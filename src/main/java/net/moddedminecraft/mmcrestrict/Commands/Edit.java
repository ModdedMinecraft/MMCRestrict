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
                                case "name":
                                    item.setItemname(value);
                                    plugin.logToFile("ban-list", "World for " +item.getItemname()+ " was changed to " +value);
                                    src.sendMessage(plugin.fromLegacy("&2Name set to: &6" + value));
                                    break;
                                case "reason":
                                    item.setBanreason(value);
                                    plugin.logToFile("ban-list", "Reason for " +item.getItemname()+ " was changed to " +value);
                                    src.sendMessage(plugin.fromLegacy("&2Reason set to: &6" + value));
                                    break;
                                case "use":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setUsagebanned(Boolean.parseBoolean(value));
                                        plugin.logToFile("ban-list", "Usage for " +item.getItemname()+ " was changed to " +value);
                                        src.sendMessage(plugin.fromLegacy("&2Usage set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                                case "own":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setOwnershipbanned(Boolean.parseBoolean(value));
                                        plugin.logToFile("ban-list", "Ownership for " +item.getItemname()+ " was changed to " +value);
                                        src.sendMessage(plugin.fromLegacy("&2Ownership set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                                case "break":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setBreakingbanned(Boolean.parseBoolean(value));
                                        plugin.logToFile("ban-list", "Breaking for " +item.getItemname()+ " was changed to " +value);
                                        src.sendMessage(plugin.fromLegacy("&2Breaking set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                                case "place":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setPlacingbanned(Boolean.parseBoolean(value));
                                        plugin.logToFile("ban-list", "Placing for " +item.getItemname()+ " was changed to " +value);
                                        src.sendMessage(plugin.fromLegacy("&2Placing set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                                case "world":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setWorldbanned(Boolean.parseBoolean(value));
                                        plugin.logToFile("ban-list", "World for " +item.getItemname()+ " was changed to " +value);
                                        src.sendMessage(plugin.fromLegacy("&2World set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                                case "drop":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setDropbanned(Boolean.parseBoolean(value));
                                        plugin.logToFile("ban-list", "Drop for " +item.getItemname()+ " was changed to " +value);
                                        src.sendMessage(plugin.fromLegacy("&2Drop set to: &6" + value));
                                    } else {
                                        src.sendMessage(plugin.fromLegacy("&cInvalid value: " + value + ". Must be true or false"));
                                    }
                                    break;
                                case "craft":
                                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                        item.setCraftbanned(Boolean.parseBoolean(value));
                                        plugin.logToFile("ban-list", "Craft for " +item.getItemname()+ " was changed to " +value);
                                        src.sendMessage(plugin.fromLegacy("&2Craft set to: &6" + value));
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

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Name: &7" + item.getItemname()))
                                    .onClick(TextActions.suggestCommand("/restrict edit " + item.getItemid() + " name " +item.getItemname()))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Name"))).build());


                            contents.add(Text.builder().append(plugin.fromLegacy("&6Ban Reason: &7" + item.getBanreason()))
                                    .onClick(TextActions.suggestCommand("/restrict edit " + item.getItemid() + " reason [MESSAGE]"))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Reason"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Usage Banned: &7" + item.getUsagebanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "use")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Usage"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Breaking Banned: &7" + item.getBreakingbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "break")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Breaking"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Placing Banned: &7" + item.getPlacingbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "place")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Placing"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Ownership Banned: &7" + item.getOwnershipbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "own")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Ownership"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Drop Banned: &7" + item.getDropbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "drop")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Drop"))).build());

                            contents.add(Text.builder().append(plugin.fromLegacy("&6Craft Banned: &7" + item.getCraftbanned()))
                                    .onClick(TextActions.executeCallback(checkValue(item.getItemid(), "craft")))
                                    .onHover(TextActions.showText(plugin.fromLegacy("&3Click here to change the value of &6Craft"))).build());

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
                                plugin.logToFile("ban-list", "Usage for " +item.getItemname()+ " was changed to false");
                                consumer.sendMessage(plugin.fromLegacy("&2Usage set to &6false"));
                            } else {
                                item.setUsagebanned(true);
                                plugin.logToFile("ban-list", "Usage for " +item.getItemname()+ " was changed to true");
                                consumer.sendMessage(plugin.fromLegacy("&2Usage set to &6true"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                consumer.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                            break;
                        case "break":
                            if (item.getBreakingbanned()) {
                                item.setBreakingbanned(false);
                                plugin.logToFile("ban-list", "Breaking for " +item.getItemname()+ " was changed to false");
                                consumer.sendMessage(plugin.fromLegacy("&2Breaking set to &6false"));
                            } else {
                                item.setBreakingbanned(true);
                                plugin.logToFile("ban-list", "Breaking for " +item.getItemname()+ " was changed to true");
                                consumer.sendMessage(plugin.fromLegacy("&2Breaking set to &6true"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                consumer.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                            break;
                        case "place":
                            if (item.getPlacingbanned()) {
                                item.setPlacingbanned(false);
                                plugin.logToFile("ban-list", "Placing for " +item.getItemname()+ " was changed to false");
                                consumer.sendMessage(plugin.fromLegacy("&2Placing set to &6false"));
                            } else {
                                item.setPlacingbanned(true);
                                plugin.logToFile("ban-list", "Placing for " +item.getItemname()+ " was changed to true");
                                consumer.sendMessage(plugin.fromLegacy("&2Placing set to &6true"));
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
                                plugin.logToFile("ban-list", "Ownership for " +item.getItemname()+ " was changed to false");
                                consumer.sendMessage(plugin.fromLegacy("&2Ownership set to &6false"));
                            } else {
                                item.setOwnershipbanned(true);
                                plugin.logToFile("ban-list", "Ownership for " +item.getItemname()+ " was changed to true");
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
                                plugin.logToFile("ban-list", "World for " +item.getItemname()+ " was changed to false");
                                consumer.sendMessage(plugin.fromLegacy("&2World set to &6false"));
                            } else {
                                item.setWorldbanned(true);
                                plugin.logToFile("ban-list", "World for " +item.getItemname()+ " was changed to true");
                                consumer.sendMessage(plugin.fromLegacy("&2World set to &6true"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                consumer.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                            break;
                        case "drop":
                            if (item.getWorldbanned()) {
                                item.setDropbanned(false);
                                plugin.logToFile("ban-list", "Drop for " +item.getItemname()+ " was changed to false");
                                consumer.sendMessage(plugin.fromLegacy("&2Drop set to &6false"));
                            } else {
                                item.setDropbanned(true);
                                plugin.logToFile("ban-list", "Drop for " +item.getItemname()+ " was changed to true");
                                consumer.sendMessage(plugin.fromLegacy("&2Drop set to &6true"));
                            }
                            try {
                                plugin.saveData();
                            } catch (Exception e) {
                                consumer.sendMessage(Text.of("Data was not saved correctly."));
                                e.printStackTrace();
                            }
                            break;
                        case "craft":
                            if (item.getCraftbanned()) {
                                item.setCraftbanned(false);
                                plugin.logToFile("ban-list", "Craft for " +item.getItemname()+ " was changed to false");
                                consumer.sendMessage(plugin.fromLegacy("&2Craft set to &6false"));
                            } else {
                                item.setCraftbanned(true);
                                plugin.logToFile("ban-list", "Craft for " +item.getItemname()+ " was changed to true");
                                consumer.sendMessage(plugin.fromLegacy("&2Craft set to &6true"));
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
