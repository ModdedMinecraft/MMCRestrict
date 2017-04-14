package net.moddedminecraft.mmcrestrict.Commands;

import net.moddedminecraft.mmcrestrict.Main;
import net.moddedminecraft.mmcrestrict.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Help implements CommandExecutor {

    private final Main plugin;
    public Help(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        showHelp(src);
        return CommandResult.success();
    }

    void showHelp(CommandSource sender) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();

        List<Text> contents = new ArrayList<>();
        if (sender.hasPermission(Permissions.ADD_BANNED_ITEM)) contents.add(plugin.fromLegacy("&3/restrict &badd [itemID/hand] - &7Add an item to the ban list."));
        if (sender.hasPermission(Permissions.REMOVE_BANNED_ITEM)) contents.add(plugin.fromLegacy("&3/restrict &bremove [itemID] - &7Remove an item from the ban list."));
        if (sender.hasPermission(Permissions.EDIT_BANNED_ITEM)) contents.add(plugin.fromLegacy("&3/restrict &bedit (option) (value) - &7List options for a banned item or edit an option."));
        //if (sender.hasPermission(Permissions.SEARCH_WORLD)) contents.add(plugin.fromLegacy("&3/restrict &bsearch (itemID) - &7Search active chunks for a block"));
        if (sender.hasPermission(Permissions.LIST_BANNED_ITEMS)) contents.add(plugin.fromLegacy("&3/restrict &blist &6| &3/banneditems &b- &7List all current banned items"));

        if (contents.isEmpty()) {
            contents.add(plugin.fromLegacy("&cYou currently do not have any permissions for this plugin."));
        }
        paginationService.builder()
                .title(plugin.fromLegacy("&6MMCRestrict Help"))
                .contents(contents)
                .header(plugin.fromLegacy("&3[] = required  () = optional"))
                .padding(Text.of("="))
                .sendTo(sender);
    }
}
