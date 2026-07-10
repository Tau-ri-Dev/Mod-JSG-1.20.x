package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.core.common.packet.TargetPoint;
import dev.tauri.jsg.api.stargate.animation.AbstractBlackHoleAnimationState;
import dev.tauri.jsg.api.stargate.animation.IChevronsState;
import dev.tauri.jsg.core.common.blockentity.IStateProvider;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

public interface IStargateStateManager extends INBTSerializable<CompoundTag>, IStateProvider, ITickable {
    IChevronsState getChevronsState();

    AbstractBlackHoleAnimationState getBlackHoleAnimationState();

    TargetPoint getTargetPoint();
}
