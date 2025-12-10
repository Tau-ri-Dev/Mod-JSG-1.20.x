package dev.tauri.jsg.api.client.screen;

import net.minecraft.resources.ResourceLocation;

public interface ITab {
    interface ITabBuilder {
        ITabBuilder setGuiSize(int xSize, int ySize);

        ITabBuilder setGuiPosition(int guiLeft, int guiTop);

        ITabBuilder setTabPosition(int defaultX, int defaultY);

        ITabBuilder setOpenX(int openX);

        ITabBuilder setHiddenX(int hiddenX);

        ITabBuilder setTabSize(int width, int height);

        ITabBuilder setTabTitle(String tabTitle);

        ITabBuilder setTabSide(TabSideEnum side);

        ITabBuilder setTexture(ResourceLocation bgTexLocation, int texureSize);

        ITabBuilder setBackgroundTextureLocation(int bgTexX, int bgTexY);

        ITabBuilder setIconRenderPos(int iconX, int iconY);

        ITabBuilder setIconSize(int iconWidth, int iconHeight);

        ITabBuilder setIconTextureLocation(int iconTexX, int iconTexY);

        ITab build();
    }
}
