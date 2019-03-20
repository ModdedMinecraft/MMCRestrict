package net.moddedminecraft.mmcrestrict.Config;

import net.moddedminecraft.mmcrestrict.Main;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Messages {

    private static Main plugin;
    public Path defaultMessage;

    private static final Pattern URL_PATTERN = Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE);


    public static ConfigurationLoader<CommentedConfigurationNode> messageLoader;
    public static CommentedConfigurationNode messages;

    public Messages(Main main) throws IOException, ObjectMappingException {
        plugin = main;
        defaultMessage = plugin.ConfigDir.resolve("messages.conf");
        messageLoader = HoconConfigurationLoader.builder().setPath(defaultMessage).build();
        messages = messageLoader.load();
        messageCheck();
    }

    public static boolean logToFile;

    public static List<String> sendToChestWhitelist;

    //banlist
    public static String bannedItemHover = "&cBanned methods &7- &6Use&7: {usebanned} &6Break&7: {breakbanned} &6Place&7: {placebanned} &6Own&7: {ownbanned} &6Craft&7: {craftbanned} &6World&7: {worldbanned}";
    public static String bannedItemExtraInfo = "&7Id: {itemid}";
    public static String bannedItemEdit = "&3Click to edit this item";
    public static String bannedItemReason = " &3- &7{banreason}";
    public static String bannedItem = "&3- &6{itemname}";
    public static String bannedItemNonSet = "&eNo banned items have been set";
    public static String bannedItemNonHidden = "&eNo banned items have been hidden";
    public static String bannedListTitle = "&6Banned List";
    public static String bannedListHiddenTitle = "&6Hidden Banned List";
    public static String bannedListPadding = "-";

    public static String bannedListHideHover = "&3Click to {hidden} this item";
    public static String bannedListHidden = "&8[&7H&8]";
    public static String bannedListHide = "&8[&6H&8]";

    //checkchunks
    public static String checkStarted = "Chunk searching has been initiated. All world banned items will be removed if found.";

    public void messageCheck() throws IOException, ObjectMappingException {

        if (!Files.exists(defaultMessage)) {
            Files.createFile(defaultMessage);
        }


        //banlist
        bannedListTitle = check(messages.getNode("list", "title"), bannedListTitle).getString();
        bannedListPadding = check(messages.getNode("list", "padding"), bannedListPadding).getString();
        bannedItemNonHidden = check(messages.getNode("list", "error", "non-hidden"), bannedItemNonHidden).getString();
        bannedItemNonSet = check(messages.getNode("list", "error", "non-set"), bannedItemNonSet).getString();
        bannedItem = check(messages.getNode("list", "formatting", "name"), bannedItem).getString();
        bannedItemReason = check(messages.getNode("list", "formatting", "reason"), bannedItemReason).getString();
        bannedItemHover = check(messages.getNode("list", "hover", "info"), bannedItemHover).getString();
        bannedItemEdit = check(messages.getNode("list", "hover", "edit"), bannedItemEdit).getString();
        bannedItemExtraInfo = check(messages.getNode("list", "hover", "extra"), bannedItemExtraInfo).getString();

        //checkchunks
        checkStarted = check(messages.getNode("commands", "checkchunks", "check-started"), checkStarted).getString();

        //hidden banlist
        bannedListHiddenTitle = check(messages.getNode("list", "hidden", "title"), bannedListHiddenTitle).getString();
        bannedListHideHover = check(messages.getNode("list", "hidden", "hover"), bannedListHideHover).getString();
        bannedListHidden = check(messages.getNode("list", "hidden", "hidden-prefix"), bannedListHidden).getString();
        bannedListHide = check(messages.getNode("list", "hidden", "hide-prefix"), bannedListHide).getString();

        messageLoader.save(messages);
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue) {
        if (node.isVirtual()) {
            node.setValue(defaultValue);
        }
        return node;
    }

    public static Text parseMessage(String message, HashMap<String, String> args) {
        for (Map.Entry<String, String> arg : args.entrySet()) {
            message = message.replace("{" + arg.getKey() + "}", arg.getValue());
        }
        Text textMessage = TextSerializers.FORMATTING_CODE.deserialize(message);

        return textMessage;
    }

}
