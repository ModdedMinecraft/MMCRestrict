package net.moddedminecraft.mmcrestrict.Commands;

import net.moddedminecraft.mmcrestrict.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class CheckChunks implements CommandExecutor {
    private final Main plugin;

    public CheckChunks(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        plugin.checkLoadedChunks();
        src.sendMessage(plugin.fromLegacy("Chunk searching has been initiated. All world banned items will be removed if found."));
        return CommandResult.success();
    }
}
