package dev.tauri.jsg.common.command.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.core.common.command.JSGAbstractCommand;
import dev.tauri.jsg.core.common.command.JSGCommand;
import dev.tauri.jsg.core.common.helper.RayTraceHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

public abstract class AbstractStargateCommand extends JSGAbstractCommand {
    public AbstractStargateCommand(JSGCommand baseCommand) {
        super(baseCommand);
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> registerArguments(ArgumentBuilder<CommandSourceStack, ?> command) {
        return command.then(Commands.argument("position", BlockPosArgument.blockPos()).then(getMainCommandPart(true))).then(getMainCommandPart(false));
    }

    public abstract ArgumentBuilder<CommandSourceStack, ?> getMainCommandPart(boolean pos);


    public Stargate<?> getStargate(BlockPos position, CommandContext<CommandSourceStack> ctx) {
        var player = ctx.getSource().getPlayer();
        if (player == null) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.generic.player_only");
            return null;
        }

        if (position == null) {
            position = RayTraceHelper.rayTracePos(player, 8);
            if (position == null) {
                baseCommand.sendErrorMess(ctx.getSource(), "commands.sgsetaddress.notstargate");
                return null;
            }
        }

        var be = player.level().getBlockEntity(position);
        if (!(be instanceof Stargate<?> stargate)) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.sgsetaddress.notstargate");
            return null;
        }
        return stargate;
    }
}
