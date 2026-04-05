package dev.tauri.jsg.blockentity.generator;

import dev.tauri.jsg.registry.JSGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class OrbanNaquadahGeneratorBE extends AbstractNaquadahGeneratorBE {
    public OrbanNaquadahGeneratorBE(BlockPos pPos, BlockState pBlockState) {
        super(JSGBlockEntities.ORBAN_NAQUADAH_GENERATOR.get(), pPos, pBlockState);
    }
}
