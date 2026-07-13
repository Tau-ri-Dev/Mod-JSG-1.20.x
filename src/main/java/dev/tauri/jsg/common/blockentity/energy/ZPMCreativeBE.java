package dev.tauri.jsg.common.blockentity.energy;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.state.energy.ZPMModelType;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.power.general.SmallEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ZPMCreativeBE extends ZPMBE {
    public ZPMCreativeBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.ZPM_CREATIVE.get(), pos, state);
    }

    private final SmallEnergyStorage creativeStorage = new SmallEnergyStorage(JSGConfig.ZPM.zpmCapacity.get(), 0, Long.MAX_VALUE) {
        @Override
        public long getTrueEnergyStored() {
            return getTrueMaxEnergyStored();
        }

        @Override
        public long extractLongEnergy(long maxExtract, boolean simulate) {
            return maxExtract;
        }

        @Override
        public long setEnergy(long energy, boolean notify) {
            return super.setEnergy(getTrueMaxEnergyStored(), notify);
        }

        @Override
        public void onEnergyChanged() {
            setChanged();
        }
    };

    @Override
    public JSGEnergyStorage getEnergyStorage() {
        return creativeStorage;
    }

    @Override
    public ZPMModelType getZPMModelType() {
        return ZPMModelType.CREATIVE;
    }
}
