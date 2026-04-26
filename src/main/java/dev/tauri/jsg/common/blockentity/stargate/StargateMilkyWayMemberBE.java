package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StargateMilkyWayMemberBE extends StargateClassicMemberBE {
    public StargateMilkyWayMemberBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK.get();
    }

    public static class StargateMilkyWayChevronBE extends StargateMilkyWayMemberBE {
        public StargateMilkyWayChevronBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_MILKYWAY_CHEVRON_BE.get(), pos, state);
        }
    }

    public static class StargateMilkyWayRingBE extends StargateMilkyWayMemberBE {
        public StargateMilkyWayRingBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_MILKYWAY_RING_BE.get(), pos, state);
        }
    }
}
