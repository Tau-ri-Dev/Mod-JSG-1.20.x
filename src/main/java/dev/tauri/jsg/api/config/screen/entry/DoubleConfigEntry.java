package dev.tauri.jsg.api.config.screen.entry;

import dev.tauri.jsg.api.config.values.JSGConfigValue;
import dev.tauri.jsg.api.client.screen.widget.FilteredEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public class DoubleConfigEntry extends AbstractConfigEntry {
    protected EditBox textField;
    protected JSGConfigValue.DoubleValue value;

    public DoubleConfigEntry(Component component, int screenWidth, JSGConfigValue.DoubleValue value) {
        super(value);
        this.value = value;
        this.textField = new FilteredEditBox(Minecraft.getInstance().font, 0, 0, 200, 20, component, (v) -> onChanged(), (v) -> {
            try {
                Double.parseDouble(v);
                return true;
            } catch (Exception ignored) {
            }
            return false;
        });
        textField.setValue(String.valueOf(value.get()));
    }

    protected void reset() {
        this.value.set(this.value.getDefault());
        this.textField.setValue(String.valueOf(this.value.get()));
        super.reset();
    }

    protected void onChanged() {
        if (this.textField.getValue().isEmpty()) return;
        value.set(Double.parseDouble(this.textField.getValue()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        if (textField.mouseClicked(mouseX, mouseY, key)) {
            onChanged();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, key);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int key, double dragX, double dragY) {
        if (textField.mouseDragged(mouseX, mouseY, key, dragX, dragY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, key, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int key) {
        if (textField.mouseReleased(mouseX, mouseY, key)) {
            onChanged();
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, key);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        textField.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (textField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (textField.charTyped(pCodePoint, pModifiers)) {
            return true;
        }
        return super.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public Vector2i getResetBtnOffset() {
        return new Vector2i(0, 5);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        textField.setX(x);
        textField.setY(y + 10);
        textField.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(Minecraft.getInstance().font, textField.getMessage(), x, y, 0xffffff, true);
        super.render(graphics, index, y, x, width, height, mouseX, mouseY, hovered, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        textField.tick();
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return textField.isFocused() ? textField : super.getFocused();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener pFocused) {
        if (textField != pFocused) textField.setFocused(false);
        super.setFocused(pFocused);
    }
}