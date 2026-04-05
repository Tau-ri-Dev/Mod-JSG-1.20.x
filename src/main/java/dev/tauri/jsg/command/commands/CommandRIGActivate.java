package dev.tauri.jsg.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.common.command.JSGAbstractCommand;
import dev.tauri.jsg.core.common.command.JSGCommand;
import dev.tauri.jsg.core.common.helper.DimensionsHelper;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.helper.RayTraceHelper;
import dev.tauri.jsg.registry.tags.JSGBlockTags;
import dev.tauri.jsg.stargate.rig.RIGWave;
import dev.tauri.jsg.stargate.rig.StargateRIGManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class CommandRIGActivate extends JSGAbstractCommand {
    public CommandRIGActivate() {
        super(JSGCommand.JSG_COMMAND_BASE);
    }

    @Override
    public String getName() {
        return "rig";
    }

    @Override
    public String getGeneralUsage() {
        return "rig [x y z [dim]] [waveName] [opensIris]";
    }

    @Override
    public String getDescription() {
        return "Spawns new RIG on gate at target pos";
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> registerArguments(ArgumentBuilder<CommandSourceStack, ?> command) {
        return command.then(
                        Commands.argument("position", BlockPosArgument.blockPos()).then(
                                Commands.argument("dim", ResourceLocationArgument.id())

                                        .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                                            for (var dim : DimensionsHelper.getDims()) {
                                                if (Objects.equals(builder.getRemainingLowerCase(), "") || dim.location().toString().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    builder.suggest(dim.location().toString());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(
                                                Commands.argument("wave", ResourceLocationArgument.id()).then(
                                                                Commands.argument("opensIris", BoolArgumentType.bool()).executes(getMainCommand(true, true, true, true))
                                                        )
                                                        .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                                                            for (var waveId : StargateRIGManager.getWaves(context.getSource().registryAccess())) {
                                                                if (Objects.equals(builder.getRemainingLowerCase(), "") || waveId.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                                    builder.suggest(waveId);
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(getMainCommand(true, true, true, false))
                                        )
                                        .executes(getMainCommand(true, true, false, false))
                        )).then(
                        Commands.argument("wave", ResourceLocationArgument.id()).then(
                                        Commands.argument("opensIris", BoolArgumentType.bool()).executes(getMainCommand(false, false, true, true))
                                )
                                .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                                    for (var waveId : StargateRIGManager.getWaves(context.getSource().registryAccess())) {
                                        if (Objects.equals(builder.getRemainingLowerCase(), "") || waveId.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            builder.suggest(waveId);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(getMainCommand(false, false, true, false)))
                .executes(getMainCommand(false, false, false, false));
    }

    public Command<CommandSourceStack> getMainCommand(boolean pos, boolean dim, boolean wave, boolean openIris) {
        return (ctx) -> activateRIG(pos ? BlockPosArgument.getLoadedBlockPos(ctx, "position") : null, dim, wave, openIris, ctx);
    }

    private int activateRIG(@Nullable BlockPos position, boolean dim, boolean waveId, boolean openIris, @NotNull CommandContext<CommandSourceStack> ctx) {
        try {
            var player = ctx.getSource().getPlayer();
            boolean posChanged = false;
            if (player == null && (position == null || !dim)) {
                baseCommand.sendErrorMess(ctx.getSource(), "commands.rig.position_empty");
                return 0;
            }
            var dimension = (dim ? DimensionsHelper.getDimension(ResourceLocationArgument.getId(ctx, "dim")) : player.level().dimension());
            var level = ctx.getSource().getServer().getLevel(dimension);
            if (level == null) {
                baseCommand.sendErrorMess(ctx.getSource(), "commands.rig.unknown_level");
                return 0;
            }

            if (position == null) {
                posChanged = true;
                position = RayTraceHelper.rayTracePos(player, 5);
            }

            var be = (position == null ? null : level.getBlockEntity(position));
            StargateAbstractBaseBE<?, ?> stargate;
            if (!(be instanceof StargateAbstractBaseBE<?, ?> sg)) {
                if (player == null || !posChanged) {
                    baseCommand.sendErrorMess(ctx.getSource(), "commands.rig.not_stargate");
                    return 0;
                }
                stargate = LinkingHelper.findClosestTile(level, player.blockPosition(), JSGBlockTags.ALL_STARGATE_BASES, StargateAbstractBaseBE.class, 20, 20);
                if (stargate == null) {
                    baseCommand.sendErrorMess(ctx.getSource(), "commands.rig.not_stargate");
                    return 0;
                }
            } else
                stargate = sg;

            var rigManager = stargate.getRIGManager();
            if (rigManager.isActive()) {
                baseCommand.sendErrorMess(ctx.getSource(), "commands.rig.already_active");
                return 0;
            }

            Optional<RIGWave> wave = waveId ? StargateRIGManager.getWave(ctx.getSource().registryAccess(), ResourceLocationArgument.getId(ctx, "wave")) : Optional.empty();
            Boolean shouldOpenIris = (openIris ? BoolArgumentType.getBool(ctx, "opensIris") : null);
            if (wave.isPresent())
                rigManager.spawnNewIncoming(wave.get(), wave.get().chevronsToDial, shouldOpenIris);
            else
                rigManager.generateNewIncoming(shouldOpenIris);

            return 1;
        } catch (Exception e) {
            JSG.logger.error("e", e);
        }
        return 0;
    }
}
