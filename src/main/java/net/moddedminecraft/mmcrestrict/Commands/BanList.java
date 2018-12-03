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
            HashMap<String, String> arguments = new HashMap<>();
            arguments.put("banreason", item.getBanreason());
            arguments.put("itemid", item.getItemid());
            arguments.put("usebanned", item.getUsagebanned().toString());
            arguments.put("breakbanned", item.getBreakingbanned().toString());
            arguments.put("placebanned", item.getPlacingbanned().toString());
            arguments.put("ownbanned", item.getOwnershipbanned().toString());
            arguments.put("craftbanned", item.getCraftbanned().toString());
            arguments.put("worldbanned", item.getWorldbanned().toString());

            Text.Builder send = Text.builder();

            String banreason = "";
            if (!item.getBanreason().isEmpty()) {
                banreason = Messages.bannedItemReason;
            }

            send.append(Messages.parseMessage(Messages.bannedItem + banreason, arguments));

            String banInfo = Messages.bannedItemHover;
            if (src.hasPermission(Permissions.LIST_EXTRA)) {
                banInfo = banInfo + "\n" + Messages.bannedItemExtraInfo;
            }
            if (src.hasPermission(Permissions.EDIT_BANNED_ITEM)) {
                banInfo = banInfo + "\n" + Messages.bannedItemEdit;
                send.onClick(TextActions.runCommand("/restrict edit " + item.getItemid()));
            }

            send.onHover(TextActions.showText(Messages.parseMessage(banInfo, arguments)));

            contents.add(send.build());
        }

        if (contents.isEmpty()) {
            contents.add(plugin.fromLegacy(Messages.bannedItemNonSet));
        }

        Collections.sort(contents);

        paginationService.builder()
                .title(plugin.fromLegacy(Messages.bannedListTitle))
                .contents(contents)
                .padding(plugin.fromLegacy(Messages.bannedListPadding))
                .sendTo(src);
        return CommandResult.success();

    }
}
