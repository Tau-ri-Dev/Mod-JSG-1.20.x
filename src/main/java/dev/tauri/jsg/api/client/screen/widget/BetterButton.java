package dev.tauri.jsg.api.client.screen.widget;

import dev.tauri.jsg.api.client.screen.util.GuiHelper;
import dev.tauri.jsg.api.client.screen.widget.base.JSGButtonClassic;
import dev.tauri.jsg.api.client.texture.ITexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

import static dev.tauri.jsg.api.client.screen.util.GuiHelper.drawModalRectWithCustomSizedTexture;

public class BetterButton extends JSGButtonClassic {
    public final int textureWidth;
    public final int textureHeight;
    public final int texU;
    public final int texV;
    private final ResourceLocation texture;
    private boolean enabled = true;

    public BetterButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int texU, int texV) {
        super(buttonId, x, y, size, size, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.texture = texture;
        this.texU = texU;
        this.texV = texV;
    }

    public void drawButton(int mouseX, int mouseY, boolean toggled) {
        if (this.visible) {
            this.isHovered = toggled || GuiHelper.isPointInRegion(getX(), getY(), width, height, mouseX, mouseY);

            ITexture.bindTextureWithMc(texture);
            if (enabled && isHovered) {
                drawModalRectWithCustomSizedTexture(getX(), getY(), texU + width, texV, width, height, textureWidth, textureHeight);
            } else {
                drawModalRectWithCustomSizedTexture(getX(), getY(), texU, texV, width, height, textureWidth, textureHeight);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void drawButton(int mouseX, int mouseY) {
        drawButton(mouseX, mouseY, false);
    }

    public boolean isMouseOnButton(int mouseX, int mouseY) {
        return GuiHelper.isPointInRegion(getX(), getY(), width, height, mouseX, mouseY);
    }

    @Override
    public void playDownSound(@Nonnull SoundManager soundHandlerIn) {
        if (!enabled) return;
        super.playDownSound(soundHandlerIn);
    }
}
