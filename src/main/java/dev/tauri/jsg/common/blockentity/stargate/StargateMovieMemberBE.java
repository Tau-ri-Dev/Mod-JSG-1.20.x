package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StargateMovieMemberBE extends StargateMilkyWayMemberBE {
    public StargateMovieMemberBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_MOVIE_BASE_BLOCK.get();
    }

    public static class StargateMovieChevronBE extends StargateMovieMemberBE {
        public StargateMovieChevronBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_MOVIE_CHEVRON_BE.get(), pos, state);
        }
    }

    public static class StargateMovieRingBE extends StargateMovieMemberBE {
        public StargateMovieRingBE(BlockPos pos, BlockState state) {
            super(JSGBlockEntities.STARGATE_MOVIE_RING_BE.get(), pos, state);
        }
    }
}
