package net.moddedminecraft.mmcrestrict;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

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

    public static boolean logToFile;

    public static List<String> sendToChestWhitelist;

    public void configCheck() throws IOException, ObjectMappingException {

        if (!Files.exists(plugin.defaultConf)) {
            Files.createFile(plugin.defaultConf);
        }

        if (config.getNode("send-to-chest", "whitelist").hasListChildren()) {
            sendToChestWhitelist = check(config.getNode("send-to-chest", "whitelist"), Collections.emptyList()).getList(TypeToken.of(String.class));
        } else {
            sendToChestWhitelist = config.getNode("send-to-chest", "whitelist").setValue(Collections.emptyList()).getList(TypeToken.of(String.class));
        }

        logToFile = check(config.getNode("log-to-file"), true, "Log any banned action or banned item change to a file.").getBoolean();

        loader.save(config);

    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue, String comment) {
        if (node.isVirtual()) {
            node.setValue(defaultValue).setComment(comment);
        }
        return node;
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue) {
        if (node.isVirtual()) {
            node.setValue(defaultValue);
        }
        return node;
    }
}
