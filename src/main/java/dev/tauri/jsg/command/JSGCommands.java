package dev.tauri.jsg.command;

import dev.tauri.jsg.command.commands.*;
import dev.tauri.jsg.core.common.command.JSGAbstractCommand;
import dev.tauri.jsg.core.common.command.JSGCommand;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.List;

public class JSGCommands {

    @SuppressWarnings("unused")
    public static final List<JSGAbstractCommand> COMMANDS = List.of(
            new CommandPrepare(),
            new CommandBuildGate(),
            new CommandSGSetAddress(),
            new CommandRIGActivate(),
            new CommandTest()
    );

    public static void registerCommands(RegisterCommandsEvent event) {
        JSGCommand.JSG_COMMAND_BASE.registerCommands(event.getDispatcher());
    }

    /**
     * Used as API
     * <p>
     * Register your sub commands to /jsg command.
     * <p>
     * ! REGISTER YOUR COMMANDS WHEN YOUR MOD IS LOADING !
     *
     * @param commandInstance - instance of JSGAbstractCommand from your mod
     */
    @SuppressWarnings("unused")
    public static void registerSubCommand(dev.tauri.jsg.core.common.command.JSGCommand baseCommand, dev.tauri.jsg.core.common.command.JSGAbstractCommand commandInstance) {
        baseCommand.registerSubCommand(commandInstance);
    }
}
