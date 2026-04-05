package dev.tauri.jsg.screen.gui.admincontroller.tabs;

import dev.tauri.jsg.screen.gui.admincontroller.AdminControllerGUI;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AdminControllerTab implements IAdminControllerTab {
    protected final GridLayout layout;
    protected final Component title;
    protected final AdminControllerGUI baseGUI;

    public AdminControllerTab(AdminControllerGUI baseGUI, Component title) {
        this.baseGUI = baseGUI;
        this.title = title;
        this.layout = new GridLayout();
        createLayout();
    }

    @Override
    public AdminControllerGUI getBaseGUI() {
        return baseGUI;
    }

    public abstract void createLayout();

    @Override
    public Component getTabTitle() {
        return title;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> widgetConsumer) {
        layout.visitWidgets(widgetConsumer);
    }

    @Override
    public void doLayout(ScreenRectangle screenRectangle) {
        layout.arrangeElements();
        FrameLayout.alignInRectangle(layout, screenRectangle, 0.5F, 0.16666667F);
    }
}
