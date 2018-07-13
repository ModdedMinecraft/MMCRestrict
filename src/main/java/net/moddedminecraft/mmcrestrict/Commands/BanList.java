package net.moddedminecraft.mmcrestrict.Commands;

import net.moddedminecraft.mmcrestrict.Data.ItemData;
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
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;

public class BanList implements CommandExecutor {
    private final Main plugin;

    public BanList(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        java.util.List<Text> contents = new ArrayList<>();
        for (ItemData item : items) {
            Text.Builder send = Text.builder();
            String banreason = "";
            if (!item.getBanreason().isEmpty()) {
                banreason = " &3- &7" + item.getBanreason();
            }
            send.append(plugin.fromLegacy("&3- &6" + item.getItemname() + banreason));
            if (src.hasPermission(Permissions.LIST_EXTRA) && src.hasPermission(Permissions.EDIT_BANNED_ITEM)) {
                send.onHover(TextActions.showText(plugin.fromLegacy(
                        "&cBanned methods &7- &6Use&7: " + item.getUsagebanned() +
                                " &6Break&7: " + item.getBreakingbanned() +
                                " &6Place&7: " + item.getPlacingbanned() +
                                " &6Own&7: " + item.getOwnershipbanned() +
                                " &6World&7: " + item.getWorldbanned() + ""
                                + "\n&7Id: " + item.getItemid()
                                + "\n&3Click to edit this item"
                )));
                send.onClick(TextActions.runCommand("/restrict edit " + item.getItemid()));
            } else if (src.hasPermission(Permissions.LIST_EXTRA) && !src.hasPermission(Permissions.EDIT_BANNED_ITEM)) {
                send.onHover(TextActions.showText(plugin.fromLegacy(
                        "&cBanned methods &7- &6Use&7: " + item.getUsagebanned() +
                                " &6Break&7: " + item.getBreakingbanned() +
                                " &6Place&7: " + item.getPlacingbanned() +
                                " &6Own&7: " + item.getOwnershipbanned() +
                                " &6World&7: " + item.getWorldbanned()
                                + "\n&7Id: " + item.getItemid()
                )));
            } else {
                send.onHover(TextActions.showText(plugin.fromLegacy(
                        "&cBanned methods &7- &6Use&7: " + item.getUsagebanned() +
                                " &6Break&7: " + item.getBreakingbanned() +
                                " &6Place&7: " + item.getPlacingbanned() +
                                " &6Own&7: " + item.getOwnershipbanned() +
                                " &6World&7: " + item.getWorldbanned()
                )));
            }
            contents.add(send.build());
        }

        if (contents.isEmpty()) {
            contents.add(plugin.fromLegacy("&eNo banned item have been set"));
        }

        Collections.sort(contents);

        paginationService.builder()
                .title(plugin.fromLegacy("&6Banned List"))
                .contents(contents)
                .padding(Text.of("-"))
                .sendTo(src);
        return CommandResult.success();

    }
}
