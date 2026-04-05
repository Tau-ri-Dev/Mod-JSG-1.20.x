package dev.tauri.jsg.multistructure.mergehelper;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.blockentity.stargate.StargateOrlinMemberBE;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StargateOrlinMergeHelper extends StargateAbstractMergeHelper {
    public StargateOrlinMergeHelper(@NotNull StargateAbstractBaseBE tileEntity) {
        super(tileEntity);
    }

    @Override
    public List<BlockPos> getRings() {
        return Arrays.asList(
                new BlockPos(1, 0, 0),
                new BlockPos(1, 1, 0),
                new BlockPos(1, 2, 0),
                new BlockPos(0, 2, 0),
                new BlockPos(-1, 2, 0),
                new BlockPos(-1, 1, 0),
                new BlockPos(-1, 0, 0)
        );
    }

    public void incrementMembersOpenCount() {
        if (!checkMergeState()) return; // not merged
        var level = stargate.getLevel();
        if (level == null) return;
        for (var entry : getBlocks().entrySet()) {
            var tile = level.getBlockEntity(entry.getKey());
            if (!(tile instanceof StargateOrlinMemberBE orlinMemberTile)) continue;
            orlinMemberTile.incrementOpenCount();
        }
    }

    public int getMaxOpenCount() {
        int max = 0;
        var level = stargate.getLevel();
        if (level == null) return 0;
        for (var entry : getBlocks().entrySet()) {
            var tile = level.getBlockEntity(entry.getKey());
            if (!(tile instanceof StargateOrlinMemberBE orlinMemberTile)) return 0;
            int open = orlinMemberTile.getOpenCount();

            if (open > max)
                max = open;
        }
        return max;
    }

    @Override
    public boolean checkMergeState() {
        var level = stargate.getLevel();
        if (level == null) return false;
        boolean shouldBeMerged = true;

        var broken = stargate.getBlockState().getOptionalValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN).orElse(false);

        for (Map.Entry<BlockPos, BlockState> entry : getBlocks().entrySet()) {
            BlockState state = level.getBlockState(entry.getKey());
            if (state.getBlock() == entry.getValue().getBlock()
                    && state.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY) == entry.getValue().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY)
                    && state.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY).equals(entry.getValue().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY))
                    && state.getOptionalValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN).orElse(false).equals(broken)
            ) continue;
            shouldBeMerged = false;
            break;
        }
        return shouldBeMerged;
    }

    @Override
    public Block getRingBlock() {
        return JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get();
    }
}
