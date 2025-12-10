package dev.tauri.jsg.api.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EnumButton<T> extends ModeButton {
    public final List<T> entries;

    public EnumButton(int buttonId, int x, int y, List<T> entries) {
        super(buttonId, x, y, 16 * 4, 20, null, 0, 0, 0);
        this.entries = entries;
        this.states = entries.size();
    }

    @Override
    public void drawButton(GuiGraphics graphics, int mouseX, int mouseY) {
        this.setMessage(Component.literal(entries.get(getCurrentState()).toString()));
        this.width = Minecraft.getInstance().font.width(entries.get(getCurrentState()).toString()) + 20;
        super.render(graphics, mouseX, mouseY, 0);
    }
}
