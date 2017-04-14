package net.moddedminecraft.mmcrestrict;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import net.moddedminecraft.mmcrestrict.Commands.*;
import net.moddedminecraft.mmcrestrict.Commands.Hand;
import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Data.ItemData.ItemDataSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Plugin(id = "mmcrestrict", name = "MMCRestrict", version = "1.0", description = "A simple item restriction plugin", authors = {"Leelawd93"})
public class Main {

    private static Main instance;

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

    @Listener
    public void Init(GameInitializationEvent event) throws IOException, ObjectMappingException {
        instance = this;
        this.config = new Config(this);
        Sponge.getEventManager().registerListeners(this, new EventListener(this));

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemData.class), new ItemDataSerializer());

        loadCommands();
        loadData();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

        Sponge.getScheduler().createTaskBuilder().execute(this::checkLoadedChunks).delay(1, TimeUnit.MINUTES).interval(1, TimeUnit.MINUTES).name("mmcreboot-s-checkLoadedChunks").submit(this);

        logger.info("Banned items loaded: " + items.size());
        logger.info("MMCRestrict Loaded");
    }

    @Listener
    public void onPluginReload(GameReloadEvent event) throws IOException, ObjectMappingException {
        this.config = new Config(this);
        loadData();
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
                put("own", "own");
                put("name", "name");
                put("world", "world");
            }
        };

        // /restrict edit itemID [reason/use/own/craft/world] [true/false/reason]
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

    public void checkChestItem(String itemID, String itemName) {
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
                    "",
                    true,
                    true,
                    false
            ));
        }
    }

    public void checkLoadedChunks() {
        if (!Config.worldBanEnabled) {
            return;
        }
        Collection<World> loadedWorlds = Sponge.getServer().getWorlds();
        final java.util.List<ItemData> items = new ArrayList<ItemData>(getItemData());
        Sponge.getScheduler().createAsyncExecutor(this).execute(new Runnable() {
            public void run() {
                loadedWorlds.forEach(world -> {
                    Iterable<Chunk> loadedChunks = world.getLoadedChunks();
                    loadedChunks.forEach(chunk -> {
                        Vector3i min = chunk.getBlockMin();
                        Vector3i max = chunk.getBlockMax();
                        for (int x = min.getX(); x < max.getX(); x++) {
                            for (int y = min.getY(); y < max.getY(); y++) {
                                for (int z = min.getZ(); z < max.getZ(); z++) {
                                    BlockState block = chunk.getBlock(x, y, z);
                                    Location blockLoc = chunk.getLocation(x, y, z);
                                    for (ItemData item : items) {
                                        if (item.getItemid().equals(block.getType().getId()) && item.getWorldbanned()) {
                                            blockLoc.setBlock(BlockTypes.AIR.getDefaultState(), BlockChangeFlag.ALL, Cause.of(NamedCause.owner(Sponge.getPluginManager().getPlugin("mmcrestrict").get())));
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
        if (!src.hasPermission(Permissions.ITEM_BYPASS + "." + banType + "." + itemID)) {
            return true;
        } else {
            return false;
        }
    }

}
