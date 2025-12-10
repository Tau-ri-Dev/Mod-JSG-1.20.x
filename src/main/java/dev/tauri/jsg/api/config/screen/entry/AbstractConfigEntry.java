package dev.tauri.jsg.api.config.screen.entry;

import dev.tauri.jsg.api.config.values.JSGConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;

public abstract class AbstractConfigEntry extends ObjectSelectionList.Entry<AbstractConfigEntry> implements ContainerEventHandler {
    public static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    protected final AbstractButton resetToDefault;
    protected final Component reset = Component.literal("Reset");

    @Nullable
    protected final JSGConfigValue configValue;

    public AbstractConfigEntry(@Nullable JSGConfigValue configValue) {
        this.configValue = configValue;
        this.resetToDefault = Button.builder(reset, (button) -> reset()).bounds(0, 0, 50, 20).build();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        if (this.resetToDefault.isMouseOver(mouseX, mouseY))
            this.resetToDefault.onPress();

        return super.mouseClicked(mouseX, mouseY, key);
    }

    protected void reset() {
        this.resetToDefault.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    public @NotNull Component getNarration() {
        return reset;
    }

    public Vector2i getResetBtnOffset() {
        return new Vector2i(0, 0);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        var backOffset = getResetBtnOffset();
        resetToDefault.setX(x + width - 55 + backOffset.x);
        resetToDefault.setY(y + 5 + backOffset.y);
        resetToDefault.render(graphics, mouseX, mouseY, partialTick);
        if (this.configValue != null && !this.configValue.comment.isEmpty() && hovered) {
            var tipX = 10;
            var tipY = OPTIONS_LIST_TOP_HEIGHT + 10;
            var tipWidth = (x - 5 - tipX);
            if (tipWidth < 75) return;

            var lines = this.configValue.comment.stream().map(t -> Component.literal(t).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY))).toList();
            var component = Component.empty();
            lines.forEach(l -> component.append(l).append("\n"));

            graphics.drawWordWrap(Minecraft.getInstance().font, component, tipX, tipY, tipWidth, 0xffffff);
        }
    }

    public void tick() {
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return resetToDefault.isFocused() ? resetToDefault : null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener pFocused) {
        if (resetToDefault != pFocused) resetToDefault.setFocused(false);
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean pIsDragging) {

    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(resetToDefault);
    }
}
