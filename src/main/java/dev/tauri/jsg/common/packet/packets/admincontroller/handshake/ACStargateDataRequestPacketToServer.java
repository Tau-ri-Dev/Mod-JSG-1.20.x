package dev.tauri.jsg.common.packet.packets.admincontroller.handshake;

import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.common.item.admincontroller.ACStargateData;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACPacketToServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public class ACStargateDataRequestPacketToServer extends ACPacketToServer {
    public ACStargateDataRequestPacketToServer(StargatePos gatePos) {
        super(gatePos);
    }

    public ACStargateDataRequestPacketToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            var sender = ctx.getSender();
            if (sender == null) return;
            var stargate = Optional.ofNullable(gatePos.getStargate());
            if (stargate.isEmpty()) return;
            JSGPacketHandler.sendTo(new ACStargateDataPacketToClient(new ACStargateData(stargate.get())), sender);
        });
    }
}
