package dev.tauri.jsg.packet.packets.admincontroller;

import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import dev.tauri.jsg.packet.JSGPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public abstract class ACPacketToServer extends JSGPacket {
    protected StargatePos gatePos;

    public ACPacketToServer(StargatePos gatePos) {
        this.gatePos = gatePos;
    }

    public ACPacketToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        gatePos.toBytes(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        gatePos = new StargatePos(buf);
    }

    public void response(ServerPlayer player, Component component) {
        JSGPacketHandler.sendTo(new ACResponsePacketToClient(component), player);
    }
}
