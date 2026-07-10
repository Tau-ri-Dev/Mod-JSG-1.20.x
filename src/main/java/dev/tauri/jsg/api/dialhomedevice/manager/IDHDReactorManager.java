package dev.tauri.jsg.api.dialhomedevice.manager;

import dev.tauri.jsg.api.dialhomedevice.DHDReactorState;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.util.FluidTank;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IDHDReactorManager extends INBTSerializable<CompoundTag>, ITickable {
    DHDReactorState getState();

    FluidTank getTank();
}
