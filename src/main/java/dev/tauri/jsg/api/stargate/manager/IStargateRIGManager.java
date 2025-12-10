package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.rig.IRIGWave;
import dev.tauri.jsg.api.util.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public interface IStargateRIGManager extends INBTSerializable<CompoundTag>, ITickable {
    void generateNewIncoming(@Nullable Boolean shouldOpenIris);

    void spawnNewIncoming(IRIGWave wave, int chevronCount, Boolean shouldOpenIris);

    IRIGWave getRandomWave();

    boolean canStart();

    boolean isActive();
}
