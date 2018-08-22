package net.moddedminecraft.mmcrestrict;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import net.moddedminecraft.mmcrestrict.Commands.*;
import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Data.ItemData.ItemDataSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Plugin(id = "mmcrestrict", name = "MMCRestrict", version = "1.5.1", description = "A simple item restriction plugin", authors = {"Leelawd93"})
public class Main {

    private static Main instance;
    private Game game;

    @Inject
    private Logger logger;

    @Inject
    private Metrics metrics;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConf;

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path ConfigDir;

    private CommandManager cmdManager = Sponge.getCommandManager();

    private Map<String, ItemData> items;

    private Config config;

    private Task autoPurgeTask = null;


    @Listener
    public void Init(GameInitializationEvent event) throws IOException, ObjectMappingException {
        instance = this;
        this.config = new Config(this);
        Sponge.getEventManager().registerListeners(this, new EventListener(this));

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemData.class), new ItemDataSerializer());

        loadCommands();
        loadData();
        startAutoPurge();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Banned items loaded: " + items.size());
        logger.info("MMCRestrict Loaded");
    }

    public void startAutoPurge() {
        if (autoPurgeTask != null) {
            autoPurgeTask.cancel();
        }

        if (!Config.defaultAutoPurge) {
            return;
        }

        autoPurgeTask = Task.builder()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        checkLoadedChunks();
                    }
                })
                .interval(Config.defaultAutoPurgeInterval, TimeUnit.MINUTES)
                .delay(5,TimeUnit.MINUTES)
                .async()
                .name("mmcrestrict-a-bannedItemsAutoPurge").submit(this);

        logger.info("MMCRestrict World AutoPurge Started (check chunks every " + Config.defaultAutoPurgeInterval + " minutes )");

    }

    @Listener
    public void onPluginReload(GameReloadEvent event) throws IOException, ObjectMappingException {
        this.config = new Config(this);
        loadData();
        startAutoPurge();
    }


    private void loadCommands() {

        // /restrict add
        CommandSpec itemAddHand = CommandSpec.builder()
                .description(Text.of("Add item in hand to the banned items list"))
                .executor(new Hand(this))
                .permission(Permissions.ADD_BANNED_ITEM)
                .build();

        // /restrict remove
        CommandSpec itemRemove = CommandSpec.builder()
                .description(Text.of("Remove item manually from the banned items list"))
                .executor(new Remove(this))
                .arguments(GenericArguments.string(Text.of("ItemID")))
                .permission(Permissions.REMOVE_BANNED_ITEM)
                .build();

        // /restrict search
        CommandSpec itemSearch = CommandSpec.builder()
                .description(Text.of("Search for an item from the banned items list within the world"))
                .executor(new Search(this))
                .arguments(GenericArguments.catalogedElement(Text.of("ItemID"), ItemType.class))
                .permission(Permissions.SEARCH_BANNED_ITEM)
                .build();


        Map<String, String> choices = new HashMap<String, String>() {
            {
                put("reason", "reason");
                put("use", "use");
                put("break", "break");
                put("place", "place");
                put("own", "own");
                put("drop", "drop");
                put("craft", "craft");
                put("name", "name");
                put("world", "world");
            }
        };

        // /restrict edit itemID [reason/use/break/place/own/craft/world] [true/false/reason]
        CommandSpec itemEdit = CommandSpec.builder()
                .description(Text.of("Edit values on a banned item"))
                .executor(new Edit(this))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("ItemID"))),
                        GenericArguments.optional(GenericArguments.choices(Text.of("Option"), choices)),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("True/False/Message"))))
                .permission(Permissions.EDIT_BANNED_ITEM)
                .build();

        // /restrict list | /banneditems
        CommandSpec bannedList = CommandSpec.builder()
                .description(Text.of("List the banned items"))
                .executor(new BanList(this))
                .permission(Permissions.LIST_BANNED_ITEMS)
                .build();

        // /restrict whatsthis | /whatsthis
        CommandSpec whatsThis = CommandSpec.builder()
                .description(Text.of("Get the id of an item in hand"))
                .executor(new Whatsthis(this))
                .permission(Permissions.WHATS_THIS)
                .build();

        // /restrict checkchunks
        CommandSpec checkChunks = CommandSpec.builder()
                .description(Text.of("Search loaded chunks for banned blocks"))
                .executor(new CheckChunks(this))
                .permission(Permissions.CHECK_CHUNKS)
                .build();

        // /restrict sendtochest
        CommandSpec sendToChest = CommandSpec.builder()
                .description(Text.of("Search loaded chunks for a block and put it in a chest"))
                .executor(new Sendtochest(this))
                .arguments(GenericArguments.catalogedElement(Text.of("ItemID"), ItemType.class))
                .permission(Permissions.SEND_TO_CHEST)
                .build();

        // /restrict
        CommandSpec restrict = CommandSpec.builder()
                .description(Text.of("Base restrict command"))
                .executor(new Help(this))
                .child(itemAddHand, "add")
                .child(itemRemove, "remove")
                .child(itemEdit, "edit")
                .child(bannedList, "list")
                .child(itemSearch, "search")
                .child(whatsThis, "whatsthis")
                .child(checkChunks, "checkchunks")
                .child(sendToChest, "sendtochest")
                .build();

        cmdManager.register(this, bannedList, "banneditems");
        cmdManager.register(this, whatsThis, "whatsthis");
        cmdManager.register(this, restrict, "restrict");
    }

    public HoconConfigurationLoader getItemDataLoader() {
        return HoconConfigurationLoader.builder().setPath(this.ConfigDir.resolve("Banneditems.conf")).build();
    }

    private void loadData() throws IOException, ObjectMappingException {
        HoconConfigurationLoader loader = getItemDataLoader();
        ConfigurationNode rootNode = loader.load();

        List<ItemData> itemList = rootNode.getNode("Items").getList(TypeToken.of(ItemData.class));
        this.items = new HashMap<String, ItemData>();
        for (ItemData item : itemList) {
            this.items.put(item.getItemid(), item);
        }
    }

    public void saveData() throws IOException, ObjectMappingException {
        HoconConfigurationLoader loader = getItemDataLoader();
        ConfigurationNode rootNode = loader.load();
        rootNode.getNode("Items").setValue(ItemDataSerializer.token, new ArrayList<ItemData>(this.items.values()));
        loader.save(rootNode);
    }

    public void checkChestItem(String itemID, String itemName, String playerName) {
        boolean itemExist = false;
        final List<ItemData> items = new ArrayList<>(getItemData());
        for (ItemData item : items) {
            if (item.getItemid().equals(itemID)) {
                itemExist = true;
            }
        }
        if (!itemExist) {
            addItem(new ItemData(
                    itemID,
                    itemName,
                    Config.defaultReason,
                    Config.defaultUsage,
                    Config.defaultBreaking,
                    Config.defaultPlacing,
                    Config.defaultOwnership,
                    Config.defaultDrop,
                    Config.defaultCraft,
                    Config.defaultWorld
            ));
            logToFile("ban-list", playerName + " added " +itemName+ " to the ban list");
        }
    }

    public void checkLoadedChunks() {
        Collection<World> loadedWorlds = Sponge.getServer().getWorlds();
        final java.util.List<ItemData> items = new ArrayList<ItemData>(getItemData());
        Sponge.getScheduler().createAsyncExecutor(this).execute(new Runnable() {
            public void run() {
                loadedWorlds.forEach(world -> {
                    Iterable<Chunk> loadedChunks = world.getLoadedChunks();
                    loadedChunks.forEach(chunk -> {
                        Vector3i min = chunk.getBlockMin();
                        Vector3i max = chunk.getBlockMax();
                        for (int x = min.getX(); x <= max.getX(); x++) {
                            for (int y = min.getY(); y <= max.getY(); y++) {
                                for (int z = min.getZ(); z <= max.getZ(); z++) {
                                    BlockState block = chunk.getBlock(x, y, z);
                                    Location blockLoc = chunk.getLocation(x, y, z);
                                    for (ItemData item : items) {
                                        if (item.getItemid().equals(block.getType().getId()) && item.getWorldbanned()) {
                                            int finalX = x;
                                            int finalY = y;
                                            int finalZ = z;
                                            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                                blockLoc.setBlock(BlockTypes.AIR.getDefaultState());
                                                logToFile("action-log", "Removed banned block:" +item.getItemname()+ " at x:" + finalX + " y:" + finalY + " z:" + finalZ);
                                            }).submit(Sponge.getPluginManager().getPlugin("mmcrestrict").get().getInstance().get());
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
            }
        });
    }

    public Collection<ItemData> getItemData() {
        return Collections.unmodifiableCollection(this.items.values());
    }

    public ItemData addItem(ItemData item) {
        return this.items.put(item.getItemid(), item);
    }

    public ItemData removeItem(String item) {
        return this.items.remove(item);
    }

    public Text fromLegacy(String legacy) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(legacy);
    }

    public boolean checkPerm(CommandSource src, String banType, String itemID) {
        if (!src.hasPermission(Permissions.ITEM_BYPASS + "." + banType + "." + itemID.replace(":", "."))) {
            return true;
        } else {
            return false;
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public void logToFile(String filename, String message) {
        if (Config.logToFile) {
            try {
                if (!Files.exists(ConfigDir.resolve("logs"))) {
                    Files.createDirectory(ConfigDir.resolve("logs"));
                }

                Path saveTo = ConfigDir.resolve("logs/" + filename + ".txt");

                if (!Files.exists(saveTo)) {
                    Files.createFile(saveTo);
                }

                FileWriter fw = new FileWriter(saveTo.toFile(), true);
                PrintWriter pw = new PrintWriter(fw);

                pw.println(message);
                pw.flush();
                pw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void notifyOnlineStaff(Text message) {
        if (Config.notifyStaff) {
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                if (player.hasPermission(Permissions.NOTIFY)) {
                    Text.Builder send = Text.builder();
                    send.append(message);
                    player.sendMessage(send.build());
                }
            }
        }
    }

}
