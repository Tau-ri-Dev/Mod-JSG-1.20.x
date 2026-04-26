package dev.tauri.jsg.common.blockentity;

import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.core.common.blockentity.ITickable;
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
