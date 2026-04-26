package dev.tauri.jsg.common.packet.packets.admincontroller.handshake;

import dev.tauri.jsg.common.item.admincontroller.ACStargateData;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACPacketToClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ACStargateDataPacketToClient extends ACPacketToClient {
    protected ACStargateData data;

    public ACStargateDataPacketToClient(ACStargateData data) {
        this.data = data;
    }

    public ACStargateDataPacketToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        data.toBytes(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        data = new ACStargateData(buf);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> getGUI().ifPresent(gui -> gui.setStargateData(data)));
    }
}
