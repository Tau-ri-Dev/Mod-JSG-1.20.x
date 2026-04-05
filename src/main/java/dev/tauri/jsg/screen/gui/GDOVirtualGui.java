package dev.tauri.jsg.screen.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GDOVirtualGui extends Screen {
    public GDOVirtualGui() {
        super(Component.literal("GDO"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
