package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.nbt.CompoundTag;
import dev.tauri.jsg.api.nbt.LegacyNBTSerializable;

public interface IStargateSoundManager extends LegacyNBTSerializable, ITickable {
    void updateRingRollSound(boolean play);

    void updateShieldHummingSound(boolean play);

    void updateWormholeSound(boolean play);

    boolean isRingRollPlaying();
}
