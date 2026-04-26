package dev.tauri.jsg.client.screen.gui.admincontroller.element;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ACTabButton extends TabButton {
    public static final ResourceLocation BACKGROUND_TEXTURE = JSGMapping.rl(JSG.MOD_ID, "textures/gui/admin_controller/gui_admin_controller.png");
    public static final int BACKGROUND_W = 512;
    public static final int BACKGROUND_H = 512;
    public static final int HEIGHT = 12;
    public static final int START_X = 4;
    public static final int END_X = 130;
    public static final int START_WIDTH = 24;
    public static final int END_WIDTH = START_WIDTH;
    public static final int WIDTH = END_X + END_WIDTH - START_X;

    public ACTabButton(TabManager pTabManager, Tab pTab, int pWidth) {
        super(pTabManager, pTab, pWidth, HEIGHT);
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        blitButton(pGuiGraphics, BACKGROUND_TEXTURE, this.getX(), this.getY(), this.width, this.height,
                START_WIDTH, END_WIDTH, WIDTH, HEIGHT, START_X, getTextureY());
        Font font = Minecraft.getInstance().font;
        int i = this.active ? -1 : -6250336;
        this.renderString(pGuiGraphics, font, i);
    }

    @Override
    public void renderString(GuiGraphics pGuiGraphics, Font pFont, int pColor) {
        int x = this.getX() + START_WIDTH + 1;
        int y = this.getY();
        int maxX = this.getX() + this.getWidth() - 1 - END_WIDTH;
        int maxY = this.getY() + this.getHeight() - 3;
        renderScrollingString(pGuiGraphics, pFont, this.getMessage(), x, y, maxX, maxY, pColor);
    }

    @Override
    protected int getTextureY() {
        int i = 0;
        if (this.isSelected() && this.isHoveredOrFocused()) {
            i = 3;
        } else if (this.isSelected()) {
            i = 1;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        return i * HEIGHT + 256;
    }

    public void blitButton(GuiGraphics graphics, ResourceLocation pAtlasLocation, int pX, int pY, int pWidth, int pHeight, int pLeftSliceWidth, int pRightSliceWidth, int pUWidth, int pVHeight, int pTextureX, int pTextureY) {
        pLeftSliceWidth = Math.min(pLeftSliceWidth, pWidth / 2);
        pRightSliceWidth = Math.min(pRightSliceWidth, pWidth / 2);
        if (pWidth == pUWidth && pHeight == pVHeight) {
            graphics.blit(pAtlasLocation, pX, pY, pTextureX, pTextureY, pWidth, pHeight, BACKGROUND_W, BACKGROUND_H);
        } else {
            graphics.blit(pAtlasLocation, pX, pY, pTextureX, pTextureY, pLeftSliceWidth, pHeight, BACKGROUND_W, BACKGROUND_H);
            graphics.blitRepeating(pAtlasLocation, pX + pLeftSliceWidth, pY, pWidth - pRightSliceWidth - pLeftSliceWidth, pHeight, pTextureX + pLeftSliceWidth, pTextureY, pUWidth - pRightSliceWidth - pLeftSliceWidth, pVHeight, BACKGROUND_W, BACKGROUND_H);
            graphics.blit(pAtlasLocation, pX + pWidth - pRightSliceWidth, pY, pTextureX + pUWidth - pRightSliceWidth, pTextureY, pRightSliceWidth, pHeight, BACKGROUND_W, BACKGROUND_H);
        }
    }
}
