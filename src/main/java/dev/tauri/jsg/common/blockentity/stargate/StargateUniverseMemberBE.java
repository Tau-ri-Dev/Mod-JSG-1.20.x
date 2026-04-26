package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StargateUniverseMemberBE extends StargateClassicMemberBE {
    public StargateUniverseMemberBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get();
    }

    public static class StargateUniverseChevronBE extends StargateUniverseMemberBE {
        public StargateUniverseChevronBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_UNIVERSE_CHEVRON_BE.get(), pos, state);
        }
    }

    public static class StargateUniverseRingBE extends StargateUniverseMemberBE {
        public StargateUniverseRingBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_UNIVERSE_RING_BE.get(), pos, state);
        }
    }
}
