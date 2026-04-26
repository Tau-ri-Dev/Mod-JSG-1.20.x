package dev.tauri.jsg.common.packet.packets;

import dev.tauri.jsg.client.screen.gui.admincontroller.AdminControllerGUI;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class AdminControllerGuiOpenHandler {
    public static void openGui(@Nullable BlockPos pos, StargateNetwork network) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Minecraft.getInstance().setScreen(new AdminControllerGUI(player, (ClientLevel) player.level(), pos, network));
    }
}
