package dev.tauri.jsg.screen.provider;

import dev.tauri.jsg.screen.gui.GDOVirtualGui;
import net.minecraft.client.Minecraft;

public class GDOVirtualGuiProvider {
    public static void open() {
        Minecraft.getInstance().setScreen(new GDOVirtualGui());
    }
}
