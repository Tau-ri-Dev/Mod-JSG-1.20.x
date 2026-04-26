package dev.tauri.jsg.common.packet.packets.admincontroller;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

public class ACResponsePacketToClient extends ACPacketToClient {
    public Component component;

    public ACResponsePacketToClient(Component component) {
        this.component = component;
    }

    public ACResponsePacketToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeComponent(component);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        component = buf.readComponent();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> getGUI().ifPresent(gui -> gui.handleResponsePacket(this)));
    }
}
