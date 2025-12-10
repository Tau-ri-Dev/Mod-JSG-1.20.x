package dev.tauri.jsg.api.config.screen.entry;

import dev.tauri.jsg.api.config.values.JSGConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanConfigEntry extends AbstractConfigEntry {
    protected CycleButton<Boolean> cycleButton;
    JSGConfigValue.BooleanValue value;

    public BooleanConfigEntry(Component component, int screenWidth, JSGConfigValue.BooleanValue value) {
        super(value);
        this.value = value;
        this.cycleButton = CycleButton.booleanBuilder(
                        Component.translatable("gui.jsg.true").withStyle(ChatFormatting.GREEN),
                        Component.translatable("gui.jsg.false").withStyle(ChatFormatting.RED))
                .withInitialValue(value.get())
                .create(0, 0, 200, 20, component, (cycleButton, isTrue) ->
                {
                    value.set(isTrue);
                    this.cycleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                });
    }

    protected void reset() {
        this.value.set(this.value.getDefault());
        this.cycleButton.setValue(this.value.get());
        super.reset();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        if (this.cycleButton.isMouseOver(mouseX, mouseY))
            this.cycleButton.onPress();

        return super.mouseClicked(mouseX, mouseY, key);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        this.cycleButton.setX(x);
        this.cycleButton.setY(y + 5);
        this.cycleButton.render(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, index, y, x, width, height, mouseX, mouseY, hovered, partialTick);
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return cycleButton.isFocused() ? cycleButton : super.getFocused();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener pFocused) {
        if (cycleButton != pFocused) cycleButton.setFocused(false);
        super.setFocused(pFocused);
    }
}
