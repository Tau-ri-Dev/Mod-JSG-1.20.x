package dev.tauri.jsg.block.stargate.redstone;

import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.blockentity.stargate.StargateClassicBaseBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public class StargateRedstoneStateO extends AbstractStargateRedstoneIO {
    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.create(0, 0, 0, 1, 2 / 16f, 1);
    }

    private EnumStargateState lastSGState = null;

    @Override
    public boolean shouldUpdateNeighbours(BlockState state, ServerLevel level, BlockPos pos, StargateClassicBaseBE<?> gateTile) {
        return lastSGState != gateTile.getDialingManager().getStargateState();
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getOutputSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction, StargateClassicBaseBE<?> gateTile) {
        var gateState = gateTile.getDialingManager().getStargateState();
        lastSGState = gateState;
        return gateState.ordinal();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void processInputSignal(BlockState state, BlockGetter level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, StargateClassicBaseBE<?> gateTile) {

    }
}
