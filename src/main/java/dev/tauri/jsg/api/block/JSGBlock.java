package dev.tauri.jsg.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class JSGBlock extends Block {
    public JSGBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getBlockSupportShape(BlockState pState, BlockGetter world, BlockPos pPos) {
        return Shapes.block();
    }
}
