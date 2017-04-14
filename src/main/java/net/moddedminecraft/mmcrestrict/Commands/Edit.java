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
import java.util.Optional;
import java.util.function.Consumer;

public class Edit implements CommandExecutor {
    private final Main plugin;

    public Edit(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> itemIDOP = args.<String>getOne("ItemID");
        Optional<String> optionOP = args.<String>getOne("Option");
        Optional<String> valueOP = args.<String>getOne("True/False/Message");

        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        java.util.List<Text> contents = new ArrayList<>();

        if (itemIDOP.isPresent()) {
            String itemType = itemIDOP.get();
            if (optionOP.isPresent()) {
                if (!valueOP.isPresent()) {
                    throw new CommandException(plugin.fromLegacy("&cInvalid usage: /restrict edit ItemID [Option] [Value]"));
                }
                String option = optionOP.get();
                String value = valueOP.get();

                if (!items.isEmpty()) {
                    for (ItemData item : items) {
                        if (item.getItemid().equals(itemType)) {
                            switch (option) {
                                case "reason":
                                    item.setBanreason(value);
                                    src.sendMessage(plugin.fromLegacy("&2Reason set to: &6" + value));
                                    break;
                                case "use":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setUsagebanned(Boolean.parseBoolean(value));
                                        src.sendMessage(plugin.fromLegacy("&2Usage set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                                case "own":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setOwnershipbanned(Boolean.parseBoolean(value));
                                        src.sendMessage(plugin.fromLegacy("&2Ownership set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                               /* case "craft":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setCraftingbanned(Boolean.parseBoolean(value));
                                        src.sendMessage(plugin.fromLegacy("&2Crafting set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;*/
                                case "world":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setWorldbanned(Boolean.parseBoolean(value));
                                        src.sendMessage(plugin.fromLegacy("&2World set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;

                                default:
                                    throw new CommandException(plugin.fromLegacy("&cInvalid usage: /restrict edit ItemID [Option] [Value]"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                src.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return CommandResult.success();
            } else {
                boolean valid = false;
                if (!items.isEmpty()) {
                    for (ItemData item : items) {
                        if (item.getItemid().equals(itemType)) {
                            valid = true;
                            if (src.hasPermission(Permissions.REMOVE_BANNED_ITEM)) {
                                contents.add(Text.builder()
                                        .append(plugin.fromLegacy(("&3[&6Remove&3]")))
                                        .onHover(TextActions.showText(plugin.fromLegacy("Click here to remove this item from the ban list")))
                                        .onClick(TextActions.runCommand("/restrict remove " + item.getItemid()))
                                        .build());
                            }
                            contents.add(Text.builder().append(plugin.fromLegacy("&6ID: &7" + item.getItemid())).build());
                            contents.add(Text.builder().append(plugin.fromLegacy("&6Name: &7" + item.getItemname())).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Ban Reason: &7" + item.getBanreason()))
                                    .onClick(TextActions.suggestCommand("/restrict edit " + item.getItemid() + " reason [MESSAGE]"))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Reason"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Usage Banned: &7" + item.getUsagebanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "use")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Usage"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Ownership Banned: &7" + item.getOwnershipbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "own")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Ownership"))).build());

                            /*contents.add(Text.builder().append(plugin.fromLegacy("&6Crafting Banned: &7" + item.getCraftingbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "craft")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Crafting"))).build());*/

                            contents.add(Text.builder().append(plugin.fromLegacy("&6World Banned: &7" + item.getWorldbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "world")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6World"))).build());
                        }
                    }
                }
                if (!valid) {
                    throw new CommandException(plugin.fromLegacy("&cItem specified is not currently banned"));
                }

                paginationService.builder()
                        .title(plugin.fromLegacy("&6Edit"))
                        .contents(contents)
                        .padding(Text.of("-"))
                        .sendTo(src);
                return CommandResult.success();
            }
        } else {
            throw new CommandException(plugin.fromLegacy("&cInvalid usage: /restrict edit ItemID [Option] [Value]"));
        }
    }
    private Consumer<CommandSource> checkValue(String itemID, String itemValue) {
        return consumer -> {
            final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
            for (ItemData item : items) {
                if (item.getItemid().equals(itemID)) {
                    switch (itemValue) {
                        case "use":
                            if (item.getUsagebanned()) {
                                item.setUsagebanned(false);
                                consumer.sendMessage(plugin.fromLegacy("&2Usage set to &6false"));
                            } else {
                                item.setUsagebanned(true);
                                consumer.sendMessage(plugin.fromLegacy("&2Usage set to &6true"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                consumer.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                            break;
                        case "own":
                            if (item.getOwnershipbanned()) {
                                item.setOwnershipbanned(false);
                                consumer.sendMessage(plugin.fromLegacy("&2Ownership set to &6false"));
                            } else {
                                item.setOwnershipbanned(true);
                                consumer.sendMessage(plugin.fromLegacy("&2Ownership set to &6true"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                consumer.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                            break;
                        case "world":
                            if (item.getWorldbanned()) {
                                item.setWorldbanned(false);
                                consumer.sendMessage(plugin.fromLegacy("&2World set to &6false"));
                            } else {
                                item.setWorldbanned(true);
                                consumer.sendMessage(plugin.fromLegacy("&2World set to &6true"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                consumer.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                            break;

                        default:
                            consumer.sendMessage(plugin.fromLegacy("Invalid usage"));
                            break;
                    }
                }
            }
        };
    }
}
