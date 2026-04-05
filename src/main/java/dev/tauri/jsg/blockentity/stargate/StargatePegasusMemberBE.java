package dev.tauri.jsg.blockentity.stargate;

import dev.tauri.jsg.registry.JSGBlockEntities;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StargatePegasusMemberBE extends StargateClassicMemberBE {
    public StargatePegasusMemberBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.get();
    }

    public static class StargatePegasusChevronBE extends StargatePegasusMemberBE {
        public StargatePegasusChevronBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_PEGASUS_CHEVRON_BE.get(), pos, state);
        }
    }

    public static class StargatePegasusRingBE extends StargatePegasusMemberBE {
        public StargatePegasusRingBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_PEGASUS_RING_BE.get(), pos, state);
        }
    }
}
