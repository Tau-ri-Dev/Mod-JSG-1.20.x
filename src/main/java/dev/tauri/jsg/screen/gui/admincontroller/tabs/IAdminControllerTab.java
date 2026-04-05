package dev.tauri.jsg.screen.gui.admincontroller.tabs;

import dev.tauri.jsg.screen.gui.admincontroller.AdminControllerGUI;
import net.minecraft.client.gui.components.tabs.Tab;

public interface IAdminControllerTab extends Tab {
    AdminControllerGUI getBaseGUI();
}
