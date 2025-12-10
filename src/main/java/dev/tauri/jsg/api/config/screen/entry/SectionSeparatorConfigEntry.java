package dev.tauri.jsg.api.config.screen.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SectionSeparatorConfigEntry extends AbstractConfigEntry implements IDescriptionEntry {
    public final Component component;

    public SectionSeparatorConfigEntry(Component component) {
        super(null);
        this.component = component;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        graphics.drawCenteredString(Minecraft.getInstance().font, component, x + width / 2, y + height - 14, 0xffffff);
    }
}
