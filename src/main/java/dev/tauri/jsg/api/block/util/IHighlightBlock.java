package dev.tauri.jsg.api.block.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IHighlightBlock {
    default boolean renderHighlight(BlockState blockState){
        return true;
    }
    default boolean renderHighlight(BlockState blockState, BlockGetter level, BlockPos pos){
        return renderHighlight(blockState);
    }
}
