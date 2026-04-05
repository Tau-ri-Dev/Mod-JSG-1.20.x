package dev.tauri.jsg.blockentity.stargate;

import dev.tauri.jsg.registry.JSGBlockEntities;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StargateTollanMemberBE extends StargateMilkyWayMemberBE {
    public StargateTollanMemberBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK.get();
    }

    public static class StargateTollanChevronBE extends StargateTollanMemberBE {
        public StargateTollanChevronBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_TOLLAN_CHEVRON_BE.get(), pos, state);
        }
    }

    public static class StargateTollanRingBE extends StargateTollanMemberBE {
        public StargateTollanRingBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_TOLLAN_RING_BE.get(), pos, state);
        }
    }
}
