package dev.tauri.jsg.api.config.screen.entry;

import dev.tauri.jsg.api.config.values.JSGConfigValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SliderConfigEntry extends AbstractConfigEntry {
    protected ForgeSlider sliderButton;
    protected JSGConfigValue.DoubleValue value;

    public SliderConfigEntry(Component component1, Component component2, int screenWidth, JSGConfigValue.DoubleValue value) {
        super(value);
        this.value = value;
        this.sliderButton = new ForgeSlider(0, 0, 200, 20,
                component1.copy().append(": "), component2,
                value.getMin(), value.getMax(), value.get(), 0.05, 1, true);
    }

    protected void reset() {
        this.value.set(this.value.getDefault());
        this.sliderButton.setValue(this.value.get());
        super.reset();
    }

    protected void onChanged() {
        value.set((int) this.sliderButton.getValue());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        if (sliderButton.isMouseOver(mouseX, mouseY))
            sliderButton.mouseClicked(mouseX, mouseY, key);
        onChanged();

        return super.mouseClicked(mouseX, mouseY, key);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int key, double dragX, double dragY) {
        if (sliderButton.isMouseOver(mouseX, mouseY))
            sliderButton.mouseDragged(mouseX, mouseY, key, dragX, dragY);

        return super.mouseDragged(mouseX, mouseY, key, dragX, dragY);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int key) {
        if (sliderButton.isMouseOver(mouseX, mouseY))
            sliderButton.mouseReleased(mouseX, mouseY, key);
        onChanged();

        return super.mouseReleased(mouseX, mouseY, key);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (sliderButton.isMouseOver(mouseX, mouseY))
            sliderButton.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        sliderButton.setX(x);
        sliderButton.setY(y + 5);
        sliderButton.render(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, index, y, x, width, height, mouseX, mouseY, hovered, partialTick);
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return sliderButton.isFocused() ? sliderButton : super.getFocused();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener pFocused) {
        if (sliderButton != pFocused) sliderButton.setFocused(false);
        super.setFocused(pFocused);
    }

    public boolean dragging = false;

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }
}