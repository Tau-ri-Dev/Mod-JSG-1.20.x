package dev.tauri.jsg.screen.gui.admincontroller.tabs;

import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.screen.gui.admincontroller.AdminControllerGUI;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AdminControllerTabAsWidget extends AbstractWidget implements IAdminControllerTab {
    protected final Component title;
    protected final AdminControllerGUI baseGUI;

    public AdminControllerTabAsWidget(AdminControllerGUI baseGUI, Component title) {
        super(baseGUI.guiInnerLeft, baseGUI.guiInnerTop, baseGUI.xInnerSize, baseGUI.yInnerSize, title);
        this.baseGUI = baseGUI;
        this.title = title;
    }

    @Override
    public AdminControllerGUI getBaseGUI() {
        return baseGUI;
    }

    @Override
    public Component getTabTitle() {
        return title;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> widgetConsumer) {
        widgetConsumer.accept(this);
    }

    @Override
    public void doLayout(ScreenRectangle screenRectangle) {
    }

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        var xy = GuiHelper.getCenterPos(rectWidth, rectHeight, getWidth(), getHeight());
        xy[0] += getX();
        xy[1] += getY();
        return xy;
    }
}
