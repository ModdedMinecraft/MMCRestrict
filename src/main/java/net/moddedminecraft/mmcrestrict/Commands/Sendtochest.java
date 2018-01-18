package net.moddedminecraft.mmcrestrict.Commands;

import com.flowpowered.math.vector.Vector3i;
import net.moddedminecraft.mmcrestrict.Config;
import net.moddedminecraft.mmcrestrict.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.TileEntityInventory;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;

public class Sendtochest implements CommandExecutor {
    private final Main plugin;

    public Sendtochest(Main plugin) {
        this.plugin = plugin;
    }

    int count = 0;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(plugin.fromLegacy("Only players can run this command"));
        }

        ItemType itemType = args.<ItemType>getOne("ItemID").get();

        boolean itemFoundInWhitelist = false;
        for (String item : Config.sendToChestWhitelist) {
            if (itemType.getId().contains(item)) {
                itemFoundInWhitelist = true;
            }
        }
        if (!itemFoundInWhitelist) {
            throw new CommandException(plugin.fromLegacy("This item is not on the whitelist to send to a chest."));
        }

        Player player = (Player) src;
        Iterable<Chunk> loadedChunks = player.getWorld().getLoadedChunks();
        player.sendMessage(plugin.fromLegacy("&eSearch has started, Please wait a moment for the results."));
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(new Runnable() {
            public void run() {
                loadedChunks.forEach(chunk -> {
                    Vector3i min = chunk.getBlockMin();
                    Vector3i max = chunk.getBlockMax();
                    for (int x = min.getX(); x <= max.getX(); x++) {
                        for (int y = min.getY(); y <= max.getY(); y++) {
                            for (int z = min.getZ(); z <= max.getZ(); z++) {
                                BlockState block = chunk.getBlock(x, y, z);
                                Location blockLoc = chunk.getLocation(x, y, z);
                                if (block.getType().getId().equals(itemType.getId())) {
                                    Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                        BlockSnapshot blockSnap = blockLoc.createSnapshot();
                                        blockLoc.setBlock(BlockTypes.CHEST.getDefaultState(), BlockChangeFlags.ALL);
                                        TileEntity chest = (TileEntity) blockLoc.getTileEntity().get();
                                        TileEntityInventory inventory = (TileEntityInventory) chest;
                                        inventory.offer(ItemStack.builder().fromBlockSnapshot(blockSnap).build());
                                        updateCount();
                                    }).submit(Sponge.getPluginManager().getPlugin("mmcrestrict").get().getInstance().get());
                                }
                            }
                        }
                    }
                });
                player.sendMessage(plugin.fromLegacy("&e" +count+" blocks have been put into a chest"));
            }
        });
        return CommandResult.success();
    }

    private void updateCount() {
        count++;
    }
}
