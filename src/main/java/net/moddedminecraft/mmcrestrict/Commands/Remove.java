package net.moddedminecraft.mmcrestrict.Commands;

import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class Remove implements CommandExecutor {
    private final Main plugin;

    public Remove(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String itemType = args.<String>getOne("ItemID").get();
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        String itemName = null;

        for (ItemData item : items) {
            if (item.getItemid().equals(itemType)) {
                itemName = item.getItemname();
            }
        }

        if (plugin.removeItem(itemType) != null) {
            try {
                plugin.saveData();
            } catch (Exception e) {
                src.sendMessage(Text.of("Data was not saved correctly."));
                e.printStackTrace();
            }
        }

        if (itemName == null) {
            plugin.logToFile("ban-list", src.getName() + " removed an item from the ban list");
            src.sendMessage(Text.of("Item was removed the list."));
        } else {
            plugin.logToFile("ban-list", src.getName() + " removed " +itemName+ " from the ban list");
            src.sendMessage(Text.of(itemName + " was removed the list."));

        }
        return CommandResult.success();
    }
}
