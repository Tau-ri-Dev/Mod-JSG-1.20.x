package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.rig.IRIGWave;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.nbt.CompoundTag;
import dev.tauri.jsg.api.nbt.LegacyNBTSerializable;
import org.jetbrains.annotations.Nullable;

public interface IStargateRIGManager extends LegacyNBTSerializable, ITickable {
    void generateNewIncoming(@Nullable Boolean shouldOpenIris);

    void spawnNewIncoming(IRIGWave wave, int chevronCount, Boolean shouldOpenIris);

    IRIGWave getRandomWave();

    boolean canStart();

    boolean isActive();

    boolean isGateActive();

    void end();
}
