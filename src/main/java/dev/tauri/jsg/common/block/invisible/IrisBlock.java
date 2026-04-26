package dev.tauri.jsg.common.block.invisible;

import dev.tauri.jsg.core.common.block.core.InvisibleBlock;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class IrisBlock extends InvisibleBlock implements dev.tauri.jsg.core.common.block.util.IHighlightBlock {

    public IrisBlock() {
        super();
        this.registerDefaultState(
                defaultBlockState()
                        .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, Direction.NORTH)
                        .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.rotateDir(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY), rotation));
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.flipDir(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY), mirror));
    }


    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        BlockPos min = new BlockPos(0, 0, (int) (16 * 0.45D));
        BlockPos max = new BlockPos(16, 16, (int) (16 * 0.55D));

        Direction horDir = blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        Direction verDir = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));

        if (verDir != null) {
            min = new BlockPos(0, (int) (16 * 0.45D), 0);
            max = new BlockPos(16, (int) (16 * 0.55D), 16);
        } else {
            switch (horDir) {
                case EAST:
                case WEST:
                    min = new BlockPos((int) (16 * 0.45D), 0, 0);
                    max = new BlockPos((int) (16 * 0.55D), 16, 16);
                    break;
                default:
                    break;
            }
        }

        return Shapes.box(
                Math.min(Math.abs(min.getX() / 16D), Math.abs(max.getX() / 16D)), Math.min(Math.abs(min.getY() / 16D), Math.abs(max.getY() / 16D)), Math.min(Math.abs(min.getZ() / 16D), Math.abs(max.getZ() / 16D)),
                Math.max(Math.abs(min.getX() / 16D), Math.abs(max.getX() / 16D)), Math.max(Math.abs(min.getY() / 16D), Math.abs(max.getY() / 16D)), Math.max(Math.abs(min.getZ() / 16D), Math.abs(max.getZ() / 16D))
        );
    }
}
