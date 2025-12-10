package dev.tauri.jsg.api.multistructure.merging;

import dev.tauri.jsg.api.multistructure.IMultiStructure;
import dev.tauri.jsg.api.util.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface IMergeHelper extends IMultiStructure, ITickable {
    void updateMemberStateAndCheck(Boolean force);

    BlockPos getTopBlock();

    BlockPos getTopBlockAboveBase();

    Direction getHorizontalFacing();

    Direction getVerticalFacing();

    List<MemberAutoBuildBlock> getAbsentBlockPositions(Level world, boolean chevron);

    class MemberAutoBuildBlock {
        public BlockPos pos;
        public BlockState state;

        public MemberAutoBuildBlock(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
        }
    }
}
