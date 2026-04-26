package dev.tauri.jsg.common.command.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.tauri.jsg.core.common.command.JSGAbstractCommand;
import dev.tauri.jsg.core.common.command.JSGCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import javax.annotation.ParametersAreNonnullByDefault;

public class CommandTest extends JSGAbstractCommand {
    public CommandTest() {
        super(JSGCommand.JSG_COMMAND_BASE);
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getGeneralUsage() {
        return "test <x> <y> <z> <size> <rotX> <rotY> <rotZ>";
    }

    @Override
    public String getDescription() {
        return "Dev command to manipulate with render offsets in runtime - useless in public versions";
    }

    public static double x;
    public static double y;
    public static double z;
    public static float size;
    public static float rotx;
    public static float roty;
    public static float rotz;

    @Override
    @ParametersAreNonnullByDefault
    public ArgumentBuilder<CommandSourceStack, ?> registerArguments(ArgumentBuilder<CommandSourceStack, ?> command) {
        return command.then(Commands.argument("x", DoubleArgumentType.doubleArg())
                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("size", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("rotx", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("roty", DoubleArgumentType.doubleArg())
                                                        .then(Commands.argument("rotz", DoubleArgumentType.doubleArg())
                                                                .executes(ctx -> {
                                                                    x = DoubleArgumentType.getDouble(ctx, "x");
                                                                    y = DoubleArgumentType.getDouble(ctx, "y");
                                                                    z = DoubleArgumentType.getDouble(ctx, "z");
                                                                    size = (float) DoubleArgumentType.getDouble(ctx, "size");
                                                                    rotx = (float) DoubleArgumentType.getDouble(ctx, "rotx");
                                                                    roty = (float) DoubleArgumentType.getDouble(ctx, "roty");
                                                                    rotz = (float) DoubleArgumentType.getDouble(ctx, "rotz");
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
