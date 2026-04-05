package dev.tauri.jsg.packet.packets.admincontroller;

import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import dev.tauri.jsg.screen.gui.admincontroller.AdminControllerGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

public abstract class ACPacketToClient extends JSGPacket {
    public ACPacketToClient() {
    }

    public ACPacketToClient(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public Optional<AdminControllerGUI> getGUI() {
        if (!(Minecraft.getInstance().screen instanceof AdminControllerGUI acGui)) return Optional.empty();
        return Optional.of(acGui);
    }
}
