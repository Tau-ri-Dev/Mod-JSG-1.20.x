package dev.tauri.jsg.packet;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.common.packet.SimplePacketHandler;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.packet.packets.AdminControllerGuiOpenToClient;
import dev.tauri.jsg.packet.packets.PrinterButtonClickedToServer;
import dev.tauri.jsg.packet.packets.ProgressUpdateToClient;
import dev.tauri.jsg.packet.packets.admincontroller.ACEntryActionPacketToServer;
import dev.tauri.jsg.packet.packets.admincontroller.ACLinkedActionPacketToServer;
import dev.tauri.jsg.packet.packets.admincontroller.ACRenameGatePacketToServer;
import dev.tauri.jsg.packet.packets.admincontroller.ACResponsePacketToClient;
import dev.tauri.jsg.packet.packets.admincontroller.event.ACStargateEngageSymbolPacketToClient;
import dev.tauri.jsg.packet.packets.admincontroller.handshake.ACStargateDataPacketToClient;
import dev.tauri.jsg.packet.packets.admincontroller.handshake.ACStargateDataRequestPacketToServer;
import dev.tauri.jsg.packet.packets.effect.StargateWormholeEffectToClient;
import dev.tauri.jsg.packet.packets.linkable.GDOCodeKeyPressedToServer;
import dev.tauri.jsg.packet.packets.linkable.UniverseDialerActionPacketToServer;
import dev.tauri.jsg.packet.packets.linkable.UniverseDialerKeyPressedToServer;
import dev.tauri.jsg.packet.packets.stargate.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class JSGPacketHandler {

    private static final SimplePacketHandler HANDLER = new SimplePacketHandler(JSGMapping.rl(JSG.MOD_ID, "main"), "1.0");

    public static void sendToServer(Object packet) {
        HANDLER.sendToServer(packet);
    }

    public static void sendToClient(Object packet, PacketDistributor.TargetPoint point) {
        HANDLER.sendToClient(packet, point);
    }

    public static void sendTo(Object packet, ServerPlayer player) {
        HANDLER.sendTo(packet, player);
    }

    public static void init() {
        int index = -1;
        // to server
        HANDLER.registerPacket(DHDButtonClickedToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, DHDButtonClickedToServer::new);
        HANDLER.registerPacket(SaveIrisCodeToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, SaveIrisCodeToServer::new);
        HANDLER.registerPacket(UniverseDialerActionPacketToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, UniverseDialerActionPacketToServer::new);
        HANDLER.registerPacket(GDOCodeKeyPressedToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, GDOCodeKeyPressedToServer::new);
        HANDLER.registerPacket(UniverseDialerKeyPressedToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, UniverseDialerKeyPressedToServer::new);
        HANDLER.registerPacket(PrinterButtonClickedToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, PrinterButtonClickedToServer::new);
        HANDLER.registerPacket(StargatePlayerMotionToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, StargatePlayerMotionToServer::new);
        HANDLER.registerPacket(ACRenameGatePacketToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, ACRenameGatePacketToServer::new);
        HANDLER.registerPacket(ACEntryActionPacketToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, ACEntryActionPacketToServer::new);
        HANDLER.registerPacket(ACLinkedActionPacketToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, ACLinkedActionPacketToServer::new);
        HANDLER.registerPacket(ACStargateDataRequestPacketToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, ACStargateDataRequestPacketToServer::new);

        // to client
        HANDLER.registerPacket(AdminControllerGuiOpenToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, AdminControllerGuiOpenToClient::new);
        HANDLER.registerPacket(StargateMotionAndRotationToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, StargateMotionAndRotationToClient::new);
        HANDLER.registerPacket(StargateWormholeEffectToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, StargateWormholeEffectToClient::new);
        HANDLER.registerPacket(StargatePlayerMotionRequestToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, StargatePlayerMotionRequestToClient::new);
        HANDLER.registerPacket(ACResponsePacketToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, ACResponsePacketToClient::new);
        HANDLER.registerPacket(ACStargateEngageSymbolPacketToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, ACStargateEngageSymbolPacketToClient::new);
        HANDLER.registerPacket(ACStargateDataPacketToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, ACStargateDataPacketToClient::new);
        HANDLER.registerPacket(ProgressUpdateToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, ProgressUpdateToClient::new);
    }
}
