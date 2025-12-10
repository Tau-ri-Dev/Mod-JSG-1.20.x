package dev.tauri.jsg.api.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.api.client.screen.widget.base.JSGButton;
import dev.tauri.jsg.api.client.screen.util.GuiHelper;
import dev.tauri.jsg.api.client.texture.ITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static dev.tauri.jsg.api.client.screen.util.GuiHelper.drawModalRectWithCustomSizedTexture;

public class ModeButton extends JSGButton {
    public final int textureWidth;
    public final int textureHeight;
    protected final ResourceLocation texture;
    public int states;
    public Map<Integer, Boolean> enabled = new HashMap<>();
    protected int currentState = 0;

    public ModeButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int states) {
        this(buttonId, x, y, size, size, texture, textureWidth, textureHeight, states);
    }

    public ModeButton(int buttonId, int x, int y, int width, int height, ResourceLocation texture, int textureWidth, int textureHeight, int states) {
        super(buttonId, x, y, width, height, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.states = states;
        this.texture = texture;
        for (int i = 0; i < states; i++) {
            enabled.put(i, true);
        }
    }

    public void setStates(int i) {
        this.states = i;
    }


    public void drawButton(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            graphics.pose().pushPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.enableBlend();

            ITexture.bindTextureWithMc(texture);
            if (!isEnabledCurrent()) {
                drawModalRectWithCustomSizedTexture(getX(), getY(), width * 2, height, width, height, textureWidth, textureHeight);
            } else if (isHovered) {
                drawModalRectWithCustomSizedTexture(getX(), getY(), width, height, width, height, textureWidth, textureHeight);
            } else {
                drawModalRectWithCustomSizedTexture(getX(), getY(), 0, height, width, height, textureWidth, textureHeight);
            }
            drawModalRectWithCustomSizedTexture(getX(), getY(), currentState * width, 0, width, height, textureWidth, textureHeight);

            RenderSystem.disableBlend();
            graphics.pose().popPose();
        }
    }

    public void nextState() {
        if (currentState == states - 1) currentState = 0;
        else currentState++;
    }

    public void previousState() {
        if (currentState == 0) currentState = states - 1;
        else currentState--;
    }

    /*
     * left = 0
     * right = 1
     * middle = 2
     *
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiHelper.isPointInRegion(this.getX(), this.getY(),
                this.width, this.height, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    this.nextState();
                    break;
                case 1:
                    this.previousState();
                    break;
                case 2:
                    this.setCurrentState(0);
                    break;

            }
            this.playDownSound(Minecraft.getInstance().getSoundManager());
        }
    }

    public boolean isEnabledCurrent(){
        return enabled.get(currentState);
    }

    public void setEnabled(int state, boolean enabled) {
        this.enabled.put(state, enabled);
    }

    public void mouseClickedPerformAction(int mouseX, int mouseY, int mouseButton) {
        if (GuiHelper.isPointInRegion(this.getX(), this.getY(),
                this.width, this.height, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    if (isEnabledCurrent())
                        performAction();
                    break;
                case 1:
                    this.nextState();
                    break;
                case 2:
                    this.setCurrentState(0);
                    break;

            }
            this.playDownSound(Minecraft.getInstance().getSoundManager());
        }
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        if (currentState < 0) currentState = 0;
        if (currentState >= states) currentState = (states - 1);
        this.currentState = currentState;
    }
}
