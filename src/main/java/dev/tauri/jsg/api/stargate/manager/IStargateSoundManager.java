package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.util.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStargateSoundManager extends INBTSerializable<CompoundTag>, ITickable {
    void updateRingRollSound(boolean play);
    void updateShieldHummingSound(boolean play);
    void updateWormholeSound(boolean play);
    boolean isRingRollPlaying();
}
