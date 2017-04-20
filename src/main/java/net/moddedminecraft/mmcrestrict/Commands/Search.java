package net.moddedminecraft.mmcrestrict.Commands;

import com.flowpowered.math.vector.Vector3i;
import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.Chunk;

import java.util.ArrayList;
import java.util.List;

public class Search implements CommandExecutor {
    private final Main plugin;

    public Search(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(plugin.fromLegacy("Only players can run this command"));
        }
        Player player = (Player) src;
        ItemType itemType = args.<ItemType>getOne("ItemID").get();
        Iterable<Chunk> loadedChunks = player.getWorld().getLoadedChunks();
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        List<Text> contents = new ArrayList<>();
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(new Runnable() {
            public void run() {
                loadedChunks.forEach(chunk -> {
                    Vector3i min = chunk.getBlockMin();
                    Vector3i max = chunk.getBlockMax();
                    for (int x = min.getX(); x <= max.getX(); x++) {
                        for (int y = min.getY(); y <= max.getY(); y++) {
                            for (int z = min.getZ(); z <= max.getZ(); z++) {
                                BlockState block = chunk.getBlock(x, y, z);
                                if (block.getType().getId().equals(itemType.getId())) {
                                    Text.Builder send = Text.builder();
                                    send.append(plugin.fromLegacy("&6Block found: &7"+itemType.getTranslation().get()+" &6at x:&7" + x + " &6y:&7" + y + " &6z:&7" +z));
                                    send.onClick(TextActions.runCommand("/tppos "+x+ " "+y+" "+z));
                                    send.onHover(TextActions.showText(plugin.fromLegacy("Teleport to x:" + x + " y:" + y + " z:" +z)));
                                    contents.add(send.build());
                                }
                            }
                        }
                    }
                });
                if (contents.isEmpty()) {
                    contents.add(plugin.fromLegacy("No blocks found"));
                }

                paginationService.builder()
                        .title(plugin.fromLegacy("Block Search"))
                        .contents(contents)
                        .padding(Text.of("="))
                        .sendTo(player);

            }
        });
        return CommandResult.success();
    }
}
