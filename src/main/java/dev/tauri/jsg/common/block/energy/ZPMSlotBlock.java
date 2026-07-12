package dev.tauri.jsg.common.block.energy;

import dev.tauri.jsg.common.blockentity.energy.ZPMSlotBE;
import dev.tauri.jsg.common.container.ZPMSlotContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMSlotBlock extends ZPMHubBlock {
    @Override
    protected AbstractContainerMenu createMenu(int containerID, Inventory playerInventory, BlockEntity blockEntity) {
        return new ZPMSlotContainer(containerID, playerInventory, blockEntity);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ZPMSlotBE(pos, state);
    }
}
