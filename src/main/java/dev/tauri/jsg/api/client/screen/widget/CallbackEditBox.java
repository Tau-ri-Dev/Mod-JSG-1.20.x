package dev.tauri.jsg.api.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class CallbackEditBox extends EditBox {
    public CallbackEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage, Consumer<String> callback) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage);
        setResponder(callback);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int key) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(key)) {
                boolean flag = this.clicked(pMouseX, pMouseY);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(pMouseX, pMouseY);
                    setFocused(true);
                    setEditable(true);
                    return true;
                }
            }

            setFocused(false);
            return false;
        } else {
            setFocused(false);
            return false;
        }
    }
}
