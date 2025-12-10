package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.animation.AbstractBlackHoleAnimationState;
import dev.tauri.jsg.api.stargate.animation.IChevronsState;
import dev.tauri.jsg.api.state.IStateProvider;
import dev.tauri.jsg.api.util.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStargateStateManager extends INBTSerializable<CompoundTag>, IStateProvider, ITickable {
    IChevronsState getChevronsState();

    AbstractBlackHoleAnimationState getBlackHoleAnimationState();
}
