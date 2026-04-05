package dev.tauri.jsg.blockentity;

import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.registry.JSGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ToasterBE extends BlockEntity implements ITickable {
    public ToasterBE(BlockPos pPos, BlockState pBlockState) {
        super(JSGBlockEntities.TOASTER.get(), pPos, pBlockState);
    }

    @Override
    public void tick(@NotNull Level level) {

    }
}
