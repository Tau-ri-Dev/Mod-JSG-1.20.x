package dev.tauri.jsg.screen.gui.admincontroller;

import dev.tauri.jsg.screen.gui.admincontroller.tabs.IAdminControllerTab;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AdminControllerTabsRegistry {
    public static final List<Function<AdminControllerGUI, IAdminControllerTab>> TABS = new ArrayList<>();

    public static void addTab(Function<AdminControllerGUI, IAdminControllerTab> tabGetter) {
        TABS.add(tabGetter);
    }
}
