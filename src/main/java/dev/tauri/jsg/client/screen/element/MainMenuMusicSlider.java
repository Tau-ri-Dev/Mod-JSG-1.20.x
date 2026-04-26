package dev.tauri.jsg.client.screen.element;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.widget.IconButton;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class MainMenuMusicSlider extends AbstractSliderButton {
    public static final ResourceLocation BUTTON_TEX = JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/volume_on.png");
    public static final ResourceLocation BUTTON_TEX_MUTED = JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/volume_off.png");

    public final IconButton button;
    public final Runnable valueChanged;
    public final Component originalTitle;
    public boolean dragged = false;

    public MainMenuMusicSlider(int pX, int pY, int pWidth, int pHeight, Component pMessage, double pValue, Runnable valueChanged) {
        super(pX - (pWidth * 5) - 5, pY, pWidth * 5, pHeight, pMessage, pValue);
        this.button = new IconButton(-1, pX, pY, BUTTON_TEX, 64, 0, 0, pWidth, pHeight, true);
        this.valueChanged = valueChanged;
        this.originalTitle = pMessage;
        updateMessage();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.active = false;
        if (dragged || (this.visible && GuiHelper.isPointInRegion(this.getX(), this.getY(), (this.button.getX() + this.button.getWidth()) - this.getX(), this.getHeight(), pMouseX, pMouseY)) ||
                (GuiHelper.isPointInRegion(this.button.getX(), this.button.getY(), this.button.getWidth(), this.button.getHeight(), pMouseX, pMouseY))
        ) {
            this.active = true;
            this.visible = true;
            super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        } else this.visible = false;
        button.drawButton(graphics, pMouseX, pMouseY);
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        if (GuiHelper.isPointInRegion(this.button.getX(), this.button.getY(), this.button.getWidth(), this.button.getHeight(), (int) pMouseX, (int) pMouseY))
            return true;
        return this.active && GuiHelper.isPointInRegion(this.getX(), this.getY(), this.getWidth(), this.getHeight(), (int) pMouseX, (int) pMouseY);
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        if (!clicked(pMouseX, pMouseY)) return;
        dragged = false;
        super.onRelease(pMouseX, pMouseY);
    }

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        if (!clicked(pMouseX, pMouseY)) return;
        dragged = true;
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (GuiHelper.isPointInRegion(this.button.getX(), this.button.getY(), this.button.getWidth(), this.button.getHeight(), (int) pMouseX, (int) pMouseY)) {
            if (getValue() > 0) this.setValue(0);
            else this.setValue(1);
            return;
        }
        if (!clicked(pMouseX, pMouseY)) return;
        super.onClick(pMouseX, pMouseY);
    }

    public void setValue(double pValue) {
        double d0 = this.value;
        this.value = Mth.clamp(pValue, 0.0D, 1.0D);
        if (d0 != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }

    public double getValue() {
        return value;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(originalTitle.copy().append(String.format(" %.0f", getValue() * 100f) + "%"));
    }

    @Override
    protected void applyValue() {
        if (getValue() > 0)
            this.button.texture = BUTTON_TEX;
        else
            this.button.texture = BUTTON_TEX_MUTED;
        this.valueChanged.run();
    }
}
