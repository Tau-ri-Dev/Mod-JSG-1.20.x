package dev.tauri.jsg.api.dialhomedevice.manager;

import dev.tauri.jsg.core.common.packet.TargetPoint;
import dev.tauri.jsg.api.dialhomedevice.animation.IButtonsState;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockentity.StateProviderInterface;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

public interface IDHDStateManager extends INBTSerializable<CompoundTag>, StateProviderInterface, ITickable {
    IButtonsState getButtonsState();

    TargetPoint getTargetPoint();
}
