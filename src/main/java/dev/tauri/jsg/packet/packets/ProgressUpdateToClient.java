package dev.tauri.jsg.packet.packets;

import dev.tauri.jsg.config.data.ProgressJSON;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class ProgressUpdateToClient extends JSGPacket {
    public ResourceLocation act;

    public ProgressUpdateToClient(ResourceLocation act) {
        this.act = act;
    }

    public ProgressUpdateToClient(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(act);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        act = buf.readResourceLocation();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ProgressJSON.get().currentActId = act.toString();
            ProgressJSON.update();
        });
    }
}
