package dev.tauri.jsg.client.screen.gui.admincontroller.tabs;

import dev.tauri.jsg.client.screen.gui.admincontroller.AdminControllerGUI;
import net.minecraft.client.gui.components.tabs.Tab;

public interface IAdminControllerTab extends Tab {
    AdminControllerGUI getBaseGUI();
}
