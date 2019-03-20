package net.moddedminecraft.mmcrestrict.Commands;

import net.moddedminecraft.mmcrestrict.Config.Messages;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class BanList implements CommandExecutor {
    private final Main plugin;

    public BanList(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        Optional<String> hidden = args.<String>getOne("hidden");

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        java.util.List<Text> contents = new ArrayList<>();

        if (hidden.isPresent()) {
            for (ItemData item : items) {
                if (!item.getHidden()) {
                    continue;
                }
                HashMap<String, String> arguments = new HashMap<>();
                arguments.put("banreason", item.getBanreason());
                arguments.put("itemid", item.getItemid());
                arguments.put("usebanned", item.getUsagebanned().toString());
                arguments.put("breakbanned", item.getBreakingbanned().toString());
                arguments.put("placebanned", item.getPlacingbanned().toString());
                arguments.put("ownbanned", item.getOwnershipbanned().toString());
                arguments.put("craftbanned", item.getCraftbanned().toString());
                arguments.put("worldbanned", item.getWorldbanned().toString());
                arguments.put("itemname", item.getItemname());
                arguments.put("hidden", item.getHidden() ? "show" : "hide");

                Text.Builder send = Text.builder();

                String banreason = "";
                if (!item.getBanreason().isEmpty()) {
                    banreason = Messages.bannedItemReason;
                }

                if (src.hasPermission(Permissions.LIST_HIDDEN_EDIT)) {
                    send.append(Text.builder().append(Messages.parseMessage(Messages.bannedListHidden, arguments))
                            .onClick(TextActions.executeCallback(changeHidden(item.getItemid())))
                            .onHover(TextActions.showText(Messages.parseMessage(Messages.bannedListHideHover, arguments))).build());
                    send.append(plugin.fromLegacy(" "));
                }

                String banInfo = Messages.bannedItemHover;
                if ((src.hasPermission(Permissions.LIST_EXTRA) && src.hasPermission(Permissions.EDIT_BANNED_ITEM))) {
                    banInfo = banInfo + "\n" + Messages.bannedItemExtraInfo + "\n" + Messages.bannedItemEdit;
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onClick(TextActions.runCommand("/restrict edit " + item.getItemid()))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                } else if (src.hasPermission(Permissions.LIST_EXTRA)) {
                    banInfo = banInfo + "\n" + Messages.bannedItemExtraInfo;
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                } else if (src.hasPermission(Permissions.EDIT_BANNED_ITEM)) {
                    banInfo = banInfo + "\n" + Messages.bannedItemEdit;
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onClick(TextActions.runCommand("/restrict edit " + item.getItemid()))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                } else {
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                }

                contents.add(send.build());
            }
        } else {
            for (ItemData item : items) {
                if (item.getHidden()) {
                    continue;
                }
                HashMap<String, String> arguments = new HashMap<>();
                arguments.put("banreason", item.getBanreason());
                arguments.put("itemid", item.getItemid());
                arguments.put("usebanned", item.getUsagebanned().toString());
                arguments.put("breakbanned", item.getBreakingbanned().toString());
                arguments.put("placebanned", item.getPlacingbanned().toString());
                arguments.put("ownbanned", item.getOwnershipbanned().toString());
                arguments.put("craftbanned", item.getCraftbanned().toString());
                arguments.put("worldbanned", item.getWorldbanned().toString());
                arguments.put("itemname", item.getItemname());
                arguments.put("hidden", item.getHidden() ? "show" : "hide");

                Text.Builder send = Text.builder();

                String banreason = "";
                if (!item.getBanreason().isEmpty()) {
                    banreason = Messages.bannedItemReason;
                }

                if (src.hasPermission(Permissions.LIST_HIDDEN_EDIT)) {
                    send.append(Text.builder().append(Messages.parseMessage(Messages.bannedListHide, arguments))
                            .onClick(TextActions.executeCallback(changeHidden(item.getItemid())))
                            .onHover(TextActions.showText(Messages.parseMessage(Messages.bannedListHideHover, arguments))).build());
                    send.append(plugin.fromLegacy(" "));
                }

                String banInfo = Messages.bannedItemHover;
                if ((src.hasPermission(Permissions.LIST_EXTRA) && src.hasPermission(Permissions.EDIT_BANNED_ITEM))) {
                    banInfo = banInfo + "\n" + Messages.bannedItemExtraInfo + "\n" + Messages.bannedItemEdit;
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onClick(TextActions.runCommand("/restrict edit " + item.getItemid()))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                } else if (src.hasPermission(Permissions.LIST_EXTRA)) {
                    banInfo = banInfo + "\n" + Messages.bannedItemExtraInfo;
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                } else if (src.hasPermission(Permissions.EDIT_BANNED_ITEM)) {
                    banInfo = banInfo + "\n" + Messages.bannedItemEdit;
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onClick(TextActions.runCommand("/restrict edit " + item.getItemid()))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                } else {
                    send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments))
                            .onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));
                }

                contents.add(send.build());
            }
        }

        Text title = plugin.fromLegacy(Messages.bannedListTitle);
        if (contents.isEmpty()) {
            if (hidden.isPresent()) {
                contents.add(plugin.fromLegacy(Messages.bannedItemNonHidden));
                title = plugin.fromLegacy(Messages.bannedListHiddenTitle);
            } else {
                contents.add(plugin.fromLegacy(Messages.bannedItemNonSet));
            }
        }

        Collections.sort(contents);

        paginationService.builder()
                .title(title)
                .contents(contents)
                .padding(plugin.fromLegacy(Messages.bannedListPadding))
                .sendTo(src);
        return CommandResult.success();

    }

    private Consumer<CommandSource> changeHidden(String itemID) {
        return consumer -> {
            final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
            for (ItemData item : items) {
                if (item.getItemid().equals(itemID)) {
                    item.setHidden(!item.getHidden());
                    String hidden = item.getHidden() ? "true" : "false";
                    consumer.sendMessage(plugin.fromLegacy("&6" +itemID + " &2hidden set to: &6" + hidden));
                }
            }
        };
    }
}
