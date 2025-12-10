package dev.tauri.jsg.api.client.screen.widget;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.client.screen.widget.base.JSGButton;
import dev.tauri.jsg.api.client.screen.util.GuiHelper;
import dev.tauri.jsg.api.client.texture.ITexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ArrowButton extends JSGButton {
    public static ResourceLocation TEXTURE = new ResourceLocation(JSGApi.MOD_ID, "textures/gui/arrow_button.png");
    public static int SIZE = 20;

    public enum ArrowType {
        UP(20, 0),
        DOWN(0, 0),
        RIGHT(40, 0),
        LEFT(60, 0),
        CROSS(80, 0),
        PLUS(100, 0);

        public final int texX;
        public final int texY;

        ArrowType(int texX, int texY) {
            this.texX = texX;
            this.texY = texY;
        }
    }

    public final ArrowType type;

    public ArrowButton(int buttonId, int x, int y, ArrowType type) {
        this(buttonId, x, y, 20, 20, type);
    }

    public ArrowButton(int buttonId, int x, int y, int sizeX, int sizeY, ArrowType type) {
        super(buttonId, x, y, sizeX, sizeY, "");
        this.type = type;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        if (visible) {
            ITexture.bindTextureWithMc(TEXTURE);
            GuiHelper.drawModalRectWithCustomSizedTexture(this.getX(), this.getY(), type.texX, type.texY, SIZE, SIZE, 120, 20);
        }
    }
}
