package dev.tauri.jsg.client.screen.gui.admincontroller.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateAbstractRenderer;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class StargateWidget extends AbstractWidget {
    protected final Supplier<Optional<StargateAbstractBaseBE<?, ?>>> gateSupplier;

    protected int offsetX;
    protected int offsetY;

    protected LayoutSettings layoutSettings = LayoutSettings.defaults();
    protected LayoutSettings nextLayoutSettings = null;
    protected long animationStart;

    protected int outlineWidth = 0;
    protected int outlineColor = 0xffffffff;
    protected boolean outlineShade = false;

    protected float customScale = 1f;

    public StargateWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, Supplier<Optional<StargateAbstractBaseBE<?, ?>>> gateSupplier) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.gateSupplier = gateSupplier;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (outlineWidth > 0) {
            pGuiGraphics.fill(getX() - outlineWidth - 1, getY() - outlineWidth - 1, getX() + getWidth() + outlineWidth * 2 + 1, getY() + getHeight() + outlineWidth * 2 + 1, 0xffBABABA);
        }
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
        renderStargate(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.disableScissor();
        pGuiGraphics.pose().popPose();
        if (outlineWidth > 0) {
            GuiHelper.renderOutline(pGuiGraphics, getX() - outlineWidth - 1, getY() - outlineWidth - 1, getWidth() + outlineWidth * 2 + 1, getHeight() + outlineWidth * 2 + 1, outlineColor, outlineWidth, outlineShade);
        }
    }

    public StargateWidget outline(int outlineWidth, int outlineColor, boolean outlineShade) {
        this.outlineWidth = outlineWidth;
        this.outlineColor = outlineColor;
        this.outlineShade = outlineShade;
        return this;
    }

    public StargateWidget offset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
        return this;
    }

    public StargateWidget scale(float scale) {
        this.customScale = scale;
        return this;
    }

    public StargateWidget layout(LayoutSettings layoutSettings) {
        this.layoutSettings = layoutSettings;
        return this;
    }

    public StargateWidget animateTo(LayoutSettings layoutSettings) {
        if (this.nextLayoutSettings != null) {
            var ns = nextLayoutSettings.getExposed();
            var s = layoutSettings.getExposed();
            var isSame = ns.paddingBottom == s.paddingBottom && ns.paddingLeft == s.paddingLeft && ns.paddingRight == s.paddingRight && ns.paddingTop == s.paddingTop;
            if (isSame) return this;
        }
        this.nextLayoutSettings = layoutSettings;
        this.animationStart = JSGMinecraftHelper.getPlayerTickClientSide() + 3;
        return this;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return false;
    }

    public void renderStargate(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var stargate = gateSupplier.get();
        if (stargate.isEmpty()) return;
        var gateTile = stargate.get();

        // Get and check renderer
        BlockEntityRenderer<?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(gateTile);
        if (!(renderer instanceof StargateAbstractRenderer<?> gateRenderer)) return;

        var s = layoutSettings.getExposed();
        if (nextLayoutSettings != null) {
            var progress = (JSGMinecraftHelper.getPlayerTickClientSide() + partialTick - animationStart) / 10;
            if (progress < 0) progress = 0;
            var ns = nextLayoutSettings.getExposed();
            var isSame = ns.paddingBottom == s.paddingBottom && ns.paddingLeft == s.paddingLeft && ns.paddingRight == s.paddingRight && ns.paddingTop == s.paddingTop;
            if (progress > 1f || isSame) {
                layoutSettings = nextLayoutSettings;
                nextLayoutSettings = null;
                animationStart = 0;
                s = layoutSettings.getExposed();
            } else {
                var tempLayout = LayoutSettings.defaults();
                tempLayout.paddingRight((int) (s.paddingRight + (ns.paddingRight - s.paddingRight) * progress));
                tempLayout.paddingLeft((int) (s.paddingLeft + (ns.paddingLeft - s.paddingLeft) * progress));
                tempLayout.paddingBottom((int) (s.paddingBottom + (ns.paddingBottom - s.paddingBottom) * progress));
                tempLayout.paddingTop((int) (s.paddingTop + (ns.paddingTop - s.paddingTop) * progress));
                s = tempLayout.getExposed();
            }
        }

        var d = gateRenderer.getGateDiameter();
        var scale = (float) Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight()) / (d * (float) Math.sqrt(2));
        scale *= customScale;
        var x = getX() + getWidth() / 2f + s.paddingLeft - s.paddingRight + offsetX;
        var y = getY() + getHeight() / 2f + s.paddingTop - s.paddingBottom + offsetY;

        var stack = graphics.pose();

        // ---------------------
        // RENDER THE GATE

        stack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        stack.translate(x, y, 2);
        stack.scale(-scale, -scale, -scale);

        stack.pushPose();
        stack.mulPose(Axis.YP.rotationDegrees(180));

        stack.pushPose();

        AbstractOBJModel.setLegacyRender();
        gateRenderer.initForGui(gateTile, graphics, partialTick);
        gateRenderer.renderWholeGate();
        AbstractOBJModel.resetRenderType();
        stack.popPose();

        stack.popPose();

        stack.popPose();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // ---------------------
    }
}
