package dev.tauri.jsg.common.packet;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.packet.packets.AdminControllerGuiOpenToClient;
import dev.tauri.jsg.common.packet.packets.PrinterButtonClickedToServer;
import dev.tauri.jsg.common.packet.packets.ProgressUpdateToClient;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACEntryActionPacketToServer;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACLinkedActionPacketToServer;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACRenameGatePacketToServer;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACResponsePacketToClient;
import dev.tauri.jsg.common.packet.packets.admincontroller.event.ACStargateEngageSymbolPacketToClient;
import dev.tauri.jsg.common.packet.packets.admincontroller.handshake.ACStargateDataPacketToClient;
import dev.tauri.jsg.common.packet.packets.admincontroller.handshake.ACStargateDataRequestPacketToServer;
import dev.tauri.jsg.common.packet.packets.dialhomedevice.DHDAssemblyClickToServer;
import dev.tauri.jsg.common.packet.packets.dialhomedevice.DHDButtonClickedToServer;
import dev.tauri.jsg.common.packet.packets.dialhomedevice.DHDFluidInsertionToServer;
import dev.tauri.jsg.common.packet.packets.dialhomedevice.DHDUpgradeAssemblyClickToServer;
import dev.tauri.jsg.common.packet.packets.effect.StargateWormholeEffectToClient;
import dev.tauri.jsg.common.packet.packets.linkable.GDOCodeKeyPressedToServer;
import dev.tauri.jsg.common.packet.packets.linkable.UniverseDialerActionPacketToServer;
import dev.tauri.jsg.common.packet.packets.linkable.UniverseDialerKeyPressedToServer;
import dev.tauri.jsg.common.packet.packets.stargate.SaveIrisCodeToServer;
import dev.tauri.jsg.common.packet.packets.stargate.StargateMotionAndRotationToClient;
import dev.tauri.jsg.common.packet.packets.stargate.StargatePlayerMotionRequestToClient;
import dev.tauri.jsg.common.packet.packets.stargate.StargatePlayerMotionToServer;
import dev.tauri.jsg.core.common.packet.SimplePacketHandler;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.server.level.ServerPlayer;
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
        // to server
        HANDLER.registerPacketToServer(DHDButtonClickedToServer.class);
        HANDLER.registerPacketToServer(DHDAssemblyClickToServer.class);
        HANDLER.registerPacketToServer(DHDFluidInsertionToServer.class);
        HANDLER.registerPacketToServer(DHDUpgradeAssemblyClickToServer.class);
        HANDLER.registerPacketToServer(SaveIrisCodeToServer.class);
        HANDLER.registerPacketToServer(UniverseDialerActionPacketToServer.class);
        HANDLER.registerPacketToServer(GDOCodeKeyPressedToServer.class);
        HANDLER.registerPacketToServer(UniverseDialerKeyPressedToServer.class);
        HANDLER.registerPacketToServer(PrinterButtonClickedToServer.class);
        HANDLER.registerPacketToServer(StargatePlayerMotionToServer.class);
        HANDLER.registerPacketToServer(ACRenameGatePacketToServer.class);
        HANDLER.registerPacketToServer(ACEntryActionPacketToServer.class);
        HANDLER.registerPacketToServer(ACLinkedActionPacketToServer.class);
        HANDLER.registerPacketToServer(ACStargateDataRequestPacketToServer.class);

        // to client
        HANDLER.registerPacketToClient(AdminControllerGuiOpenToClient.class);
        HANDLER.registerPacketToClient(StargateMotionAndRotationToClient.class);
        HANDLER.registerPacketToClient(StargateWormholeEffectToClient.class);
        HANDLER.registerPacketToClient(StargatePlayerMotionRequestToClient.class);
        HANDLER.registerPacketToClient(ACResponsePacketToClient.class);
        HANDLER.registerPacketToClient(ACStargateEngageSymbolPacketToClient.class);
        HANDLER.registerPacketToClient(ACStargateDataPacketToClient.class);
        HANDLER.registerPacketToClient(ProgressUpdateToClient.class);
    }
}
