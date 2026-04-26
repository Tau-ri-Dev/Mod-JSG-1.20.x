package dev.tauri.jsg.common.multistructure.mergehelper;

import dev.tauri.jsg.common.block.stargate.StargateAbstractBaseBlock;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractMemberBE;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.helper.BlockHelper;
import dev.tauri.jsg.core.common.multistructure.merging.IMergeHelper;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class StargateAbstractMergeHelper implements IMergeHelper, ITickable {
    public static final JSGAxisAlignedBB BASE_SEARCH_BOX = new JSGAxisAlignedBB(-3, -7, -0.5, 3, 7, 0.5);

    public static JSGAxisAlignedBB getSearchBox() {
        return BASE_SEARCH_BOX;
    }

    @Override
    public void tick(@NotNull Level level) {
        var state = stargate.getBlockState();
        if (state.getBlock() instanceof StargateAbstractBaseBlock) {
            if (horizontalFacing != state.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY)
                    || verticalFacing != JSGProperties.getDirectionByVerticalFacing(state.getValue(JSGProperties.FACING_VERTICAL_PROPERTY))
                    || basePos != stargate.blockPosition()) {
                // update merge helper - client and server
                horizontalFacing = state.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY);
                verticalFacing = JSGProperties.getDirectionByVerticalFacing(state.getValue(JSGProperties.FACING_VERTICAL_PROPERTY));
                basePos = stargate.blockPosition();
            }
        }
    }

    @Nullable
    public static StargateAbstractBaseBE<?, ?> findBaseTile(Level level, BlockPos memberPos, Direction facing, Direction verticalFacing, Block baseBlock) {
        JSGAxisAlignedBB globalBox = getSearchBox().rotate(facing, verticalFacing).offset(memberPos);

        for (BlockPos pos : BlockPos.betweenClosed(globalBox.getMinBlockPos(), globalBox.getMaxBlockPos())) {
            if (level.getBlockState(pos).getBlock() == baseBlock) {
                return (StargateAbstractBaseBE<?, ?>) level.getBlockEntity(pos);
            }
        }

        return null;
    }

    public StargateAbstractMergeHelper(@Nonnull StargateAbstractBaseBE<?, ?> stargate) {
        this.stargate = stargate;
    }

    public Direction horizontalFacing = Direction.SOUTH;
    public Direction verticalFacing = Direction.SOUTH;
    public BlockPos basePos;

    public final StargateAbstractBaseBE<?, ?> stargate;

    public abstract List<BlockPos> getRings();

    public abstract Block getRingBlock();

    /**
     * @return translated and rotated blocks of the gate
     */
    @Override
    public Map<BlockPos, BlockState> getBlocks() {
        return getBlocks(true);
    }

    public Map<BlockPos, BlockState> getBlocks(boolean offsetAndRotate) {
        Map<BlockPos, BlockState> map = new HashMap<>();
        for (BlockPos pos : getRings()) {
            BlockPos newPos = pos;
            if (offsetAndRotate)
                newPos = stargate.relative(pos); //BlockPosHelper.rotate(pos, horizontalFacing, verticalFacing).offset(basePos);
            map.put(newPos, getRingBlock().defaultBlockState()
                    .setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, horizontalFacing)
                    .setValue(JSGProperties.FACING_VERTICAL_PROPERTY, JSGProperties.getVerticalFacingByDirection(verticalFacing))
                    .setValue(BlockStateProperties.WATERLOGGED, offsetAndRotate && Objects.requireNonNull(stargate.getLevel()).getFluidState(newPos).getType() == Fluids.WATER)
            );
        }
        return map;
    }

    private BlockPos topBlock = null;

    @Override
    public BlockPos getTopBlock() {
        // null - not initialized
        if (topBlock == null) topBlock = BlockHelper.getHighest(getBlocks(false).keySet().stream().toList());

        if (topBlock == null) return basePos;

        return stargate.relative(topBlock); //BlockPosHelper.rotate(topBlock, horizontalFacing, verticalFacing).offset(basePos);
    }

    private BlockPos topBlockAboveBase = null;

    public BlockPos getTopBlockAboveBase() {
        // null - not initialized
        if (topBlockAboveBase == null)
            topBlockAboveBase = BlockHelper.getHighestWithXZCords(getBlocks(false).keySet().stream().toList(), 0, 0);

        if (topBlockAboveBase == null) return basePos;

        return stargate.relative(topBlockAboveBase); //BlockPosHelper.rotate(topBlockAboveBase, horizontalFacing, verticalFacing).offset(basePos);
    }

    @Override
    public boolean shouldBeMerged() {
        return stargate.isMerged();
    }

    @Override
    public boolean checkMergeState() {
        var level = stargate.getLevel();
        if (level == null) return false;
        boolean shouldBeMerged = true;

        for (Map.Entry<BlockPos, BlockState> entry : getBlocks().entrySet()) {
            BlockState state = level.getBlockState(entry.getKey());
            if (state.getBlock() == entry.getValue().getBlock()
                    && state.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY) == entry.getValue().getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY)
                    && state.getValue(JSGProperties.FACING_VERTICAL_PROPERTY).equals(entry.getValue().getValue(JSGProperties.FACING_VERTICAL_PROPERTY))
            ) continue;
            shouldBeMerged = false;
            break;
        }
        return shouldBeMerged;
    }

    @Override
    public void updateMemberStateAndCheck(Boolean force) {
        var level = stargate.getLevel();
        if (level == null) return;
        boolean shouldBeMerged = (force != null ? force : checkMergeState());
        if (shouldBeMerged != stargate.isMerged()) {
            if (shouldBeMerged)
                stargate.onGateMerged();
            else
                stargate.onGateBroken();

            stargate.setMerged(shouldBeMerged);
            stargate.setChanged();
        }

        for (Map.Entry<BlockPos, BlockState> entry : getBlocks().entrySet()) {
            BlockState state = level.getBlockState(entry.getKey());
            if (state.getBlock() == entry.getValue().getBlock()) {
                BlockEntity be = level.getBlockEntity(entry.getKey());

                if (be instanceof StargateAbstractMemberBE castedBe) {
                    castedBe.setBaseTile((shouldBeMerged ? stargate.blockPosition() : null));
                }
                if (state.getValue(JSGProperties.RENDER_BLOCK_PROPERTY) != shouldBeMerged) continue;
                level.setBlock(entry.getKey(), state.setValue(JSGProperties.RENDER_BLOCK_PROPERTY, !shouldBeMerged), 3);
            }
        }
        stargate.setChanged();
    }

    @Nonnull
    public Block getMatchBlock(boolean chevron) {
        return getRingBlock();
    }

    @Nonnull
    @Override
    public List<MemberAutoBuildBlock> getAbsentBlockPositions(Level world, boolean chevron) {
        List<MemberAutoBuildBlock> result = new ArrayList<>();
        Block matchBlock = getMatchBlock(chevron);
        for (Map.Entry<BlockPos, BlockState> e : getBlocks().entrySet()) {
            if (e.getValue().getBlock() == matchBlock) {
                BlockPos pos = e.getKey();
                if (world.getBlockState(pos).canBeReplaced() || world.getBlockState(pos).isAir()) {
                    result.add(new MemberAutoBuildBlock(pos, e.getValue()));
                }
            }
        }
        return result;
    }

    @Override
    public Direction getHorizontalFacing() {
        return horizontalFacing;
    }

    @Override
    public Direction getVerticalFacing() {
        return verticalFacing;
    }
}
