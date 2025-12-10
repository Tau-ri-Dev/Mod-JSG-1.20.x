package dev.tauri.jsg.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

public interface ILinkableBE<T> {
    boolean canLinkTo();

    void setLinkedDevice(BlockPos devicePos);

    void sendLinkedDeviceToClients(BlockPos sourcePos, PacketDistributor.TargetPoint targetPoint);

    void sendLinkedDeviceToClient(BlockPos sourcePos, ServerPlayer player);

    void requestLinkedDeviceFromServer(BlockPos sourcePos);

    default boolean isLinked(boolean savingWorld) {
        return getLinkedPos() != null && (savingWorld || getLinkedDevice() != null);
    }

    default boolean isLinked() {
        return isLinked(false);
    }

    @Nullable
    T getLinkedDevice();

    @Nullable
    BlockPos getLinkedPos();
}
