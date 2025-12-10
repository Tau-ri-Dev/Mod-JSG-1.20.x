package dev.tauri.jsg.api.client.screen.widget.base;

import net.minecraft.client.Minecraft;

public class JSGButton extends JSGButtonClassic {
    private ActionCallback actionCallback;

    public JSGButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public void performAction() {
        actionCallback.performAction();
    }

    public JSGButton setFgColor(int fgColor) {
        packedFGColor = fgColor;
        return this;
    }

    public JSGButton setActionCallback(ActionCallback callback) {
        actionCallback = callback;
        return this;
    }

    public JSGButton setEnabled(boolean enabled) {
        super.active = enabled;
        return this;
    }

    public interface ActionCallback {
        void performAction();
    }

    @Override
    public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_) {
        if (this.active && this.visible) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            performAction();
            return true;
        } else {
            return false;
        }
    }
}