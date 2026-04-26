package dev.tauri.jsg.common.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tauri.jsg.core.common.blockentity.IPreparable;
import dev.tauri.jsg.core.common.command.JSGAbstractCommand;
import dev.tauri.jsg.core.common.command.JSGCommand;
import dev.tauri.jsg.core.common.helper.RayTraceHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CommandPrepare extends JSGAbstractCommand {
    public CommandPrepare() {
        super(JSGCommand.JSG_COMMAND_BASE);
    }

    @Override
    public String getName() {
        return "prepare";
    }

    @Override
    public String getGeneralUsage() {
        return "prepare [argument] [x y z]";
    }

    @Override
    public String getDescription() {
        return "Prepares block entity for saving to NBT structure.";
    }


    @Override
    public ArgumentBuilder<CommandSourceStack, ?> registerArguments(ArgumentBuilder<CommandSourceStack, ?> command) {
        return command
                .then(Commands.argument("argument", StringArgumentType.string())
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(getMainCommand(true, true)))
                        .executes(getMainCommand(false, true)))
                .then(Commands.argument("position", BlockPosArgument.blockPos())
                        .executes(getMainCommand(true, false)))
                .executes(getMainCommand(false, false));
    }


    public Command<CommandSourceStack> getMainCommand(boolean pos, boolean argument) {
        return (ctx) -> prepareBE(pos ? BlockPosArgument.getLoadedBlockPos(ctx, "position") : null, argument ? StringArgumentType.getString(ctx, "argument") : null, ctx);
    }


    private int prepareBE(@Nullable BlockPos position, @Nullable String argument, @NotNull CommandContext<CommandSourceStack> ctx) {
        var player = ctx.getSource().getPlayer();
        if (player == null) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.generic.player_only");
            return 0;
        }

        if (position == null) {
            position = RayTraceHelper.rayTracePos(player, 8);
            if (position == null) {
                baseCommand.sendErrorMess(ctx.getSource(), "commands.prepare.not_preparable");
                return 0;
            }
        }

        var be = player.level().getBlockEntity(position);
        if (!(be instanceof IPreparable preparable)) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.prepare.not_preparable");
            return 0;
        }

        if (!preparable.prepareBE(argument)) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.prepare.error");
            return 0;
        }
        baseCommand.sendSuccessMess(ctx.getSource(), "commands.prepare.success");
        return 1;
    }
}
