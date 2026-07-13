package dev.tauri.jsg.common.packet.packets.admincontroller;

import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import dev.tauri.jsg.core.common.packet.PacketContext;

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
        ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buf, component);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        component = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(buf);
    }

    @Override
    public void handle(PacketContext ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> getGUI().ifPresent(gui -> gui.handleResponsePacket(this)));
    }
}
