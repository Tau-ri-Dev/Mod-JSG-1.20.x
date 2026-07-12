package dev.tauri.jsg.common.blockentity.energy;

import dev.tauri.jsg.common.registry.JSGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ZPMSlotBE extends ZPMHubBE {
    public ZPMSlotBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.ZPM_SLOT.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public int getAnimationLength() {
        return (int) Math.round(super.getAnimationLength() * 0.75);
    }
}
