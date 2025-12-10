package dev.tauri.jsg.api.client.screen.widget.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import static dev.tauri.jsg.api.client.screen.util.GuiHelper.drawRect;

public class JSGGuiButton extends JSGButtonClassic {
    public JSGGuiButton(int id, int x, int y, int w, int h, String string) {
        super(id, x, y, w, h, string);
    }

    public JSGGuiButton(int id, int x, int y, String text) {
        this(id, x, y, 200, 20, text);
    }

    public GuiGraphics graphics;

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.graphics = graphics;
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            int fgcolor = 0xCCCCCC;
            int bgcolor = 0xFF1D2026;

            if (!this.isActive()) {
                fgcolor = 10526880;
            } else if (this.isHovered()) {
                fgcolor = 0xFFFFFF;
                bgcolor = 0xFF313640;
            }

            drawRect(getX(), getY(), getX() + width, getY() + height, JSGGuiBase.FRAME_COLOR);
            drawRect(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, bgcolor);

            graphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, fgcolor);
        }
    }
}
