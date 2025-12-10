package dev.tauri.jsg.api.helper;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class JSGMinecraftHelper {
    /**
     * Get current tick on client side.
     * !IT DOES NOT PAUSE IF YOU PAUSE THE GAME!
     *
     * @return current tick (long)
     */
    public static long getClientTick() {
        return (long) Math.floor((Util.getMillis() / (double) 1000) * 20);
    }

    public static double getClientTickPrecise() {
        return ((Util.getMillis() / 1000D) * 20D);
    }

    public static long getPlayerTickClientSide() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;
        return player.level().getGameTime();
    }
}
