package dev.tauri.jsg.client.renderer.blockentity.machine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.block.PrinterBlock;
import dev.tauri.jsg.common.blockentity.PrinterBE;
import dev.tauri.jsg.common.raycaster.RaycasterPrinter;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.JSGRaycasters;
import dev.tauri.jsg.core.client.renderer.IRaycasterButtonsRenderer;
import dev.tauri.jsg.core.client.renderer.item.PageRenderer;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.raycaster.util.RayCastedButton;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.List;

public class PrinterRenderer implements BlockEntityRenderer<PrinterBE>, IRaycasterButtonsRenderer {
    public PrinterRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    public List<RayCastedButton> getRaycasterButtons() {
        return RaycasterPrinter.BUTTONS;
    }

    @Override
    public Raycaster getRaycaster() {
        return JSGRaycasters.PRINTER_RAYCASTER.get();
    }


    private PoseStack stack;
    private MultiBufferSource source;
    private int light;
    private PrinterBE tile;
    private Level level;

    @Override
    @ParametersAreNonnullByDefault
    public void render(PrinterBE printerTile, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        this.tile = printerTile;
        this.level = tile.getLevel();
        if (level == null) return;
        this.stack = stack;
        this.source = source;
        this.light = light;
        if (!(level.getBlockState(tile.getBlockPos()).getBlock() instanceof PrinterBlock)) return;
        renderRaycasterButtons(printerTile, stack, source);
        GuiHelper.currentStack = stack;


        stack.pushPose();
        RenderSystem.enableDepthTest();
        var direction = level.getBlockState(tile.getBlockPos()).getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY).getOpposite();
        stack.translate(0.5f, 0.5f, 0.5f);
        stack.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));

        stack.pushPose();
        RenderSystem.enableBlend();
        // arrows
        ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/arrow_button.png"));

        stack.translate(-0.35, -0.2, 0.15);
        stack.mulPose(Axis.XP.rotationDegrees(-22));
        stack.scale(0.1f, 0.1f, 0.1f);

        stack.pushPose();
        GuiHelper.translateFor3D();
        GuiHelper.drawScaledCustomSizeModalRect(0, 0, 40, 0, 20, 20, 1, 1, 120, 20);
        stack.popPose();

        var remove = tile.symbolsToPrint.contains(tile.editPos + 1);

        stack.pushPose();
        stack.translate(2.05, 0, 0);
        GuiHelper.translateFor3D();
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCrouching()) {
            RenderSystem.setShaderColor(remove ? 1 : 0, remove ? 0 : 1, 0, 1);
            GuiHelper.drawScaledCustomSizeModalRect(0, 0, remove ? 80 : 100, 0, 20, 20, 1, 1, 120, 20);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        } else
            GuiHelper.drawScaledCustomSizeModalRect(0, 0, 20, 0, 20, 20, 1, 1, 120, 20);
        stack.popPose();

        stack.pushPose();
        stack.translate(2.05 * 2, 0, 0);
        GuiHelper.translateFor3D();
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCrouching()) {
            RenderSystem.setShaderColor(remove ? 1 : 0, remove ? 0 : 1, 0, 1);
            GuiHelper.drawScaledCustomSizeModalRect(0, 0, remove ? 80 : 100, 0, 20, 20, 1, 1, 120, 20);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        } else
            GuiHelper.drawScaledCustomSizeModalRect(0, 0, 0, 0, 20, 20, 1, 1, 120, 20);
        stack.popPose();

        stack.pushPose();
        stack.translate(2.05 * 3, 0, 0);
        RenderSystem.setShaderColor(0, 1, 0, 1);
        GuiHelper.translateFor3D();
        GuiHelper.drawScaledCustomSizeModalRect(0, 0, 100, 0, 20, 20, 1, 1, 120, 20);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        stack.popPose();

        RenderSystem.disableBlend();
        stack.popPose();

        stack.pushPose();
        stack.translate(-0.35, -0.19, 0.8);
        stack.mulPose(Axis.XP.rotationDegrees(-90));
        renderDisplay();
        stack.popPose();

        if (!tile.inputPages.isEmpty()) {
            renderPageInStack();
        }
        if (!tile.outputPages.isEmpty()) {
            renderOutputPage();
        }
        stack.popPose();
    }

    public Pair<Float, Float> getAnimation() {
        float first = 0;
        float second = 0;
        float x = ((float) Math.max(0, Math.min(PrinterBE.PRINTING_TIME, level.getGameTime() - tile.printStarted))) / 20f;

        if (x >= 0 && x <= 1.931f) {
            first = (x / (1.931f * 2f));
        } else if (x <= 2.45f) {
            first = 0.5f;
        } else if (x <= 2.66f) {
            first = (x - 2.45f) + 0.5f;
        } else if (x <= 2.88f) {
            first = 0.71f;
        } else if (x <= 3.0f) {
            first = (x - 2.88f) + 0.71f;
        } else if (x <= 3.206f) {
            first = 0.83f;
        } else if (x <= 3.302f) {
            first = (x - 3.206f) + 0.83f;
        } else if (x <= 3.5f) {
            first = 0.926f;
        } else if (x <= 3.6f) {
            first = (x - 3.5f) + 0.926f;
        } else if (x > 3f) {
            first = 1f;
        }

        if (x >= 4.05f && x <= 4.17f) {
            second = (x - 4.17f) + 0.12f;
        } else if (x <= 4.36f) {
            second = 0.12f;
        } else if (x <= 4.47f) {
            second = (x - 4.47f) + 0.23f;
        } else if (x <= 4.64f) {
            second = 0.23f;
        } else if (x <= 4.75f) {
            second = (x - 4.75f) + 0.34f;
        } else if (x <= 4.95f) {
            second = 0.34f;
        } else if (x <= 5.02f) {
            second = (x - 5.02f) + 0.412f;
        } else if (x <= 5.14f) {
            second = 0.412f;
        } else if (x <= 5.264f) {
            second = (x - 5.264f) + 0.53f;
        } else if (x <= 5.305f) {
            second = 0.53f;
        } else if (x <= 5.777f) {
            second = (x - 5.777f) + 1;
        } else if (x > 5f) {
            second = 1f;
        }

        if (first > 1) first = 1;
        if (second > 1) second = 1;
        if (first < 0) first = 0;
        if (second < 0) second = 0;

        if (tile.printStarted <= 0) {
            first = 0;
            second = 1;
        }
        return Pair.of(first, second);
    }

    protected void renderPageInStack() {
        var percent = getAnimation().first();
        stack.pushPose();
        stack.translate(-0.31, -0.625, -0.5);
        stack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        stack.translate(0, 0.05, 0);

        if (percent < 1) {
            stack.pushPose();
            stack.translate(0, -0.3 * percent, 0);
            stack.translate(0, 0, 0.005 * (tile.inputPages.getCount() - 1));
            stack.scale(0.6f, 0.6f, 0.6f);
            PageRenderer.renderPercentages = Pair.of(1 - (percent * 0.5f), false);
            PageRenderer.renderByCompound(stack, source, light, ItemDisplayContext.FIXED, null);
            PageRenderer.renderPercentages = null;
            stack.popPose();
        }

        for (var i = 0; i < (tile.inputPages.getCount() - 1); i++) {
            stack.pushPose();
            stack.translate(0, 0, 0.005 * i);
            stack.scale(0.6f, 0.6f, 0.6f);
            PageRenderer.renderByCompound(stack, source, light, ItemDisplayContext.FIXED, null);
            stack.popPose();
        }
        stack.popPose();
    }

    protected void renderOutputPage() {
        var percent = getAnimation().second();
        stack.pushPose();
        stack.translate(-0.31, -0.73, 0);

        if (percent > 0) {
            stack.pushPose();
            stack.translate(0, 0.005 * (tile.outputPages.size() - 1), ((0.24 + 0.16) * percent));
            stack.mulPose(Axis.XP.rotationDegrees(-90));
            stack.scale(0.6f, 0.6f, 0.6f);
            PageRenderer.renderPercentages = Pair.of(percent, true);
            PageRenderer.renderByCompound(stack, source, light, ItemDisplayContext.FIXED, tile.outputPages.getLast().getTag());
            PageRenderer.renderPercentages = null;
            stack.popPose();
        }

        for (var i = 0; i < (tile.outputPages.size() - 1); i++) {
            stack.pushPose();
            stack.translate(0, 0.005 * i, 0.24 + 0.16);
            stack.mulPose(Axis.XP.rotationDegrees(-90));
            stack.scale(0.6f, 0.6f, 0.6f);
            PageRenderer.renderByCompound(stack, source, light, ItemDisplayContext.FIXED, tile.outputPages.get(i).getTag());
            stack.popPose();
        }
        stack.popPose();
    }

    protected void renderDisplay() {
        if (tile.address == null) return;

        var editPos = tile.editPos;
        if (editPos < 0) editPos = 0;
        if (editPos >= tile.address.size()) editPos = tile.address.size();

        // editing symbol
        stack.pushPose();
        PageRenderer.renderSymbol(stack, source, light, 0.05f, 0, 0.2f, 0.2f, tile.address.get(editPos), tile.origin);
        stack.pushPose();
        stack.translate(0.2, 0.8, 0.01);
        stack.scale(0.003f, 0.003f, 0.003f);
        stack.pushPose();
        var title = tile.address.get(editPos).getId() + " " + tile.address.get(editPos).localize();
        var width = Minecraft.getInstance().font.width(title);
        stack.translate(-width / 2f, 0, 0);
        PageRenderer.renderText(stack, source, light, title, 0x0, false);
        stack.popPose();
        stack.popPose();
        stack.popPose();

        stack.pushPose();
        stack.translate(0.65, 0.75, 0.01);
        stack.scale(0.2f, 0.2f, 0.2f);

        // title - symbol type
        stack.pushPose();

        stack.translate(0, 1.1, 0);
        stack.scale(0.02f, 0.02f, 0.02f);
        stack.scale(0.8f, 0.8f, 0.8f);

        title = I18n.format("gui.stargate." + tile.address.getSymbolType().getId() + "_address");
        width = Minecraft.getInstance().font.width(title);
        stack.translate(-width, 0, 0);
        PageRenderer.renderText(stack, source, light, title, 0x0, false);
        stack.popPose();


        // full address
        var size = tile.address.size();
        var offsetX = -0.2f * size;
        RenderSystem.enableBlend();
        for (int i = 0; i < size; i++) {
            var underlineColor = (tile.symbolsToPrint.contains(i + 1) ? Color.GREEN : Color.RED);
            var x = offsetX + (0.2f * i);
            stack.pushPose();
            PageRenderer.renderSymbol(stack, source, light, x, 0.1f, 0.2f, 0.2f, tile.address.get(i), tile.origin, Color.BLACK, 1);

            PageRenderer.renderRect(stack, source, light, x, 0.16f, 0.2f, 0.05f, underlineColor, 0.5f);
            if (tile.editPos == i)
                PageRenderer.renderRect(stack, source, light, x, 0.16f + 0.05f, 0.2f, 0.025f, Color.CYAN, 0.5f);
            stack.popPose();
        }
        RenderSystem.disableBlend();

        // cartridges
        var status = tile.getInkStatusAmountOnly();

        stack.pushPose();
        stack.translate(-0.7, 0.1, 0);
        stack.scale(0.008f, 0.008f, 0.008f);

        PageRenderer.renderRect(stack, source, light, 0, 0, 20, 50, Color.DARK_GRAY, 1);
        PageRenderer.renderRect(stack, source, light, 25, 0, 20, 50, Color.DARK_GRAY, 1);
        PageRenderer.renderRect(stack, source, light, 50, 0, 20, 50, Color.DARK_GRAY, 1);
        PageRenderer.renderRect(stack, source, light, 75, 0, 20, 50, Color.DARK_GRAY, 1);

        stack.pushPose();
        stack.translate(0, 0, 0.01);
        PageRenderer.renderRect(stack, source, light, 2F, -2, 16, 46 * status.getOrDefault(JSGItems.CARTRIDGE_BLACK.get().renderColor, 0.0).floatValue(), JSGItems.CARTRIDGE_BLACK.get().renderColor, 1);
        PageRenderer.renderRect(stack, source, light, 2 + 25, -2, 16, 46 * status.getOrDefault(JSGItems.CARTRIDGE_CYAN.get().renderColor, 0.0).floatValue(), JSGItems.CARTRIDGE_CYAN.get().renderColor, 1);
        PageRenderer.renderRect(stack, source, light, 2 + 50, -2, 16, 46 * status.getOrDefault(JSGItems.CARTRIDGE_MAGENTA.get().renderColor, 0.0).floatValue(), JSGItems.CARTRIDGE_MAGENTA.get().renderColor, 1);
        PageRenderer.renderRect(stack, source, light, 2 + 75, -2, 16, 46 * status.getOrDefault(JSGItems.CARTRIDGE_YELLOW.get().renderColor, 0.0).floatValue(), JSGItems.CARTRIDGE_YELLOW.get().renderColor, 1);
        stack.popPose();

        stack.popPose();
        stack.popPose();
    }
}
