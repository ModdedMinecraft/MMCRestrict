package net.moddedminecraft.mmcrestrict.Commands;

import net.moddedminecraft.mmcrestrict.Config.Config;
import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class Hand implements CommandExecutor {
    private final Main plugin;

    public Hand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Console users cannot use this command"));
        }
        Player player = (Player) src;
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());


        if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
            ItemStack mainHandItem = player.getItemInHand(HandTypes.MAIN_HAND).get();

            if (!items.isEmpty()) {
                for (ItemData item : items) {
                    if (item.getItemid().equals(mainHandItem.getType().getId())) {
                        throw new CommandException(Text.of("Item already exists"));
                    }
                }
            }

            DataContainer container = mainHandItem.toContainer();
            DataQuery query = DataQuery.of('/', "UnsafeDamage");

            int unsafeDamage = Integer.parseInt(container.get(query).get().toString());
            String itemId = mainHandItem.getType().getId();
            if (unsafeDamage != 0) {
                itemId = itemId + ":" + unsafeDamage;
            }

            plugin.addItem(new ItemData(
                    itemId,
                    mainHandItem.getTranslation().get(),
                    Config.defaultReason,
                    Config.defaultUsage,
                    Config.defaultBreaking,
                    Config.defaultPlacing,
                    Config.defaultOwnership,
                    Config.defaultDrop,
                    Config.defaultCraft,
                    Config.defaultWorld
            ));

            try {
                plugin.saveData();
            } catch (Exception e) {
                player.sendMessage(Text.of("Data was not saved correctly."));
                e.printStackTrace();
            }
            plugin.logToFile("ban-list", player.getName() + " added " +mainHandItem.getTranslation().get()+ " to the ban list");
            player.sendMessage(Text.of(mainHandItem.getTranslation().get() + " was added to the list."));
        } else {
            throw new CommandException(Text.of("Main hand is empty"));
        }

        return CommandResult.success();
    }
}
