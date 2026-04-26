package dev.tauri.jsg.client.screen.provider;

import dev.tauri.jsg.client.screen.gui.GDOVirtualGui;
import net.minecraft.client.Minecraft;

public class GDOVirtualGuiProvider {
    public static void open() {
        Minecraft.getInstance().setScreen(new GDOVirtualGui());
    }
}
