package dev.tauri.jsg.api.client.screen.widget.base;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class JSGTextFieldClassic extends EditBox {

    public final int id;

    public int getId(){
        return id;
    }

    public JSGTextFieldClassic(int id, int x, int y, int widthIn, int heightIn, String text) {
        this(id, x, y, widthIn, heightIn, Component.literal(text));
    }

    public JSGTextFieldClassic(int id, int x, int y, int widthIn, int heightIn, Component text) {
        super(Minecraft.getInstance().font, x, y, widthIn, heightIn, text);
        this.id = id;
    }


    @Override
    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(p_93643_)) {
                boolean flag = this.clicked(p_93641_, p_93642_);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(p_93641_, p_93642_);
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
    @Override
    public boolean charTyped(char c, int i) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (SharedConstants.isAllowedChatCharacter(c)) {
            if(!keyPressed(c, 0, 0)){
                insertText(String.valueOf(c));
            }
            return true;
        } else {
            return false;
        }
    }
}
