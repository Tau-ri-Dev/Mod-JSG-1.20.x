package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.network.StargatePos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStargateAutoCloseManager extends INBTSerializable<CompoundTag> {

    /**
     * AutoClose update function (on server) (engaged) (receiving gate).
     * Scan for load status of the source gate every 20 ticks (1 second).
     *
     * @param sourceStargatePos of the initiating gate.
     * @return {@code True} if the gate should be closed, false otherwise.
     */
    boolean shouldClose(StargatePos sourceStargatePos);
}
