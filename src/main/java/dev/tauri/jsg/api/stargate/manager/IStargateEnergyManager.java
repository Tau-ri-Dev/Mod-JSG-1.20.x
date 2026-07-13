package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.power.JSGEnergyStorageWrapper;
import dev.tauri.jsg.core.common.power.general.EnergyRequiredToOperate;
import net.minecraft.nbt.CompoundTag;
import dev.tauri.jsg.api.nbt.LegacyNBTSerializable;

import javax.annotation.Nullable;

public interface IStargateEnergyManager<E extends JSGEnergyStorage> extends LegacyNBTSerializable, ITickable {
    E getStorage();

    JSGEnergyStorageWrapper getStorageForCaps();

    double getSecondsToClose();

    long getTransferredLastTick();

    boolean canOpenWormhole(EnergyRequiredToOperate energyRequiredToDial);

    default EnergyRequiredToOperate getEnergyRequiredToDial(Stargate<?> targetGate, StargateAddressDynamic address) {
        return getEnergyRequiredToDial(targetGate.getStargatePos(), address);
    }

    EnergyRequiredToOperate getEnergyRequiredToDial(@Nullable StargatePos targetGatePos, StargateAddressDynamic address);
}
