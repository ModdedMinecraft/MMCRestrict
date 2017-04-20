package net.moddedminecraft.mmcrestrict;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.Files;

public class Config {

    private final Main plugin;

    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    public static CommentedConfigurationNode config;

    public Config(Main main) throws IOException, ObjectMappingException {
        plugin = main;
        loader = HoconConfigurationLoader.builder().setPath(plugin.defaultConf).build();
        config = loader.load();
        configCheck();
    }

    public static boolean worldBanEnabled;
    public static int worldBanDelay;
    public static int worldBanInterval;
    public static boolean logToFile;

    public void configCheck() throws IOException, ObjectMappingException {

        if (!Files.exists(plugin.defaultConf)) {
            Files.createFile(plugin.defaultConf);
        }

        worldBanEnabled = check(config.getNode("check-worldban-on-chunkload", "enabled"), true, "Search for and remove world banned blocks automatically.").getBoolean();
        worldBanDelay = check(config.getNode("check-worldban-on-chunkload", "delay"), 5, "Delay before chunk searching begins. (Minutes)").getInt();
        worldBanInterval = check(config.getNode("check-worldban-on-chunkload", "interval"), 5, "Time between chunk searches (Minutes)").getInt();
        logToFile = check(config.getNode("log-to-file"), true, "Log any banned action or banned item change to a file.").getBoolean();

        loader.save(config);

    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue, String comment) {
        if (node.isVirtual()) {
            node.setValue(defaultValue).setComment(comment);
        }
        return node;
    }
}
