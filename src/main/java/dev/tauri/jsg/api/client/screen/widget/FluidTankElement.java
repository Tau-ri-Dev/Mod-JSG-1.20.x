package dev.tauri.jsg.api.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.api.client.texture.ITexture;
import dev.tauri.jsg.api.client.renderer.BlockRenderer;
import dev.tauri.jsg.api.client.screen.util.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Optional;

public class FluidTankElement {
    private final int xCoord;
    private final int yCoord;
    private final int maxWidth;
    private final int maxHeight;
    private final FluidTank fluidTank;

    public FluidTankElement(int xCoord, int yCoord, int maxWidth, int maxHeight, FluidTank fluidTank) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.fluidTank = fluidTank;
    }

    public void renderTank(GuiGraphics graphics) {
        if (fluidTank != null) {
            ITexture.bindTextureWithMc(InventoryMenu.BLOCK_ATLAS);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            float height = ((float) maxHeight * ((float) fluidTank.getFluidAmount()) / (float) fluidTank.getCapacity());

            TextureAtlasSprite sprite = BlockRenderer.getFluidTexture(fluidTank.getFluid(), BlockRenderer.FluidTextureType.STILL);
            if (sprite == null) return;

            GuiHelper.currentStack = graphics.pose();
            GuiHelper.drawTiledSprite(xCoord, yCoord + maxHeight, 0, maxWidth, (int) height, sprite);
        }
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (GuiHelper.isPointInRegion(xCoord, yCoord, maxWidth, maxHeight, mouseX, mouseY) && fluidTank != null) {
            String amount = String.format("%,d", fluidTank.getFluidAmount());
            String maxAmount = String.format("%,d", fluidTank.getCapacity());

            graphics.renderTooltip(Minecraft.getInstance().font, List.of(
                    fluidTank.getFluid().getDisplayName(),
                    Component.literal(amount + " / " + maxAmount + " mB").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
            ), Optional.empty(), mouseX, mouseY);
        }
    }
}
