package dev.tauri.jsg.client.renderer.blockentity.stargate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateChevronsState;
import dev.tauri.jsg.common.stargate.animation.chevron.StargatePegasusChevronsState;
import dev.tauri.jsg.common.stargate.animation.spinning.PegasusSpinHelper;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.math.NumberUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StargatePegasusRenderer extends StargateClassicRenderer<StargatePegasusRendererState> {

    public StargatePegasusRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    public static final float GATE_DIAMETER = 10.1815f;

    public static final float GLYPHS_RING_RADIUS = (float) ((GATE_DIAMETER / 2f) - 0.845);

    private static final int GLYPHS_COUNT = 36;

    @Override
    public float getGateDiameter() {
        return GATE_DIAMETER;
    }

    @Override
    public double getScaleMultiplier() {
        return 1;
    }

    @Override
    protected void renderGate() {
        stack.pushPose();
        ElementEnum.PEGASUS_GATE.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        stack.popPose();
        renderRing();
        renderChevrons();
    }

    // ----------------------------------------------------------------------------------------
    // Ring

    private void renderRing() {
        stack.pushPose();
        if (ElementEnum.PEGASUS_RING.model != null && ElementEnum.PEGASUS_RING.biomeTextureResourceMap.get(rendererState.getBiomeOverlay()) != null)
            ElementEnum.PEGASUS_RING.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);

        stack.pushPose();
        var spinHelper = tileEntity.getDialingManager().getSpinHelper();
        var chevronsState = (StargatePegasusChevronsState) tileEntity.getStateManager().getChevronsState();

        if (spinHelper.isSpinning()) {
            tileEntity.getDialingManager().getSpinHelper().correctClientRingStartTime(level.getGameTime(), Minecraft.getInstance().gui.getGuiTicks());
            double tick = (Minecraft.getInstance().gui.getGuiTicks() + partialTicks);
            int slot = (int) Math.floor(spinHelper.apply(tick, true));
            if (!chevronsState.isSlotActive(slot)) {
                renderGlyph(((PegasusSpinHelper) spinHelper).getTargetSymbol(), slot, false);
            }
        }

        var allDim = !chevronsState.isAnySlotActive();
        if (allDim && !spinHelper.isSpinning()) {
            Arrays.stream(JSGSymbolTypes.PEGASUS.get().getValues()).forEach(symbol -> {
                var slot = symbol.getAngle() / 10f;
                if (slot < 0) return;
                renderGlyph(symbol, (int) slot, true);
            });
        }
        for (int i = 0; i < GLYPHS_COUNT; i++) {
            if (!chevronsState.isSlotActive(i)) continue;
            renderGlyph(chevronsState.getSymbolAtSlot(i).orElse(JSGSymbolTypes.PEGASUS.get().getOrigin()), i, false);
        }
        stack.popPose();

        stack.popPose();
    }


    // ----------------------------------------------------------------------------------------
    // Chevrons

    @Override
    protected void renderChevron(ChevronEnum chevron, StargateChevronsState.ChevronState state, float color, boolean onlyLight) {
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));
        float chevronOffset = state.getOffset(partialTicks, 1.5f);
        boolean renderEmissive = (onlyLight && state.isLocked());

        var chevronTexture = getTextureLoader().getTexture(state.getTexture(rendererState.getBiomeOverlay(), onlyLight));
        if (chevronTexture != null) {
            chevronTexture.bindTexture();
            stack.pushPose();

            stack.translate(0, chevronOffset, 0);
            ElementEnum.PEGASUS_CHEVRON_MOVING.render(stack, source, combinedLight, renderEmissive);

            stack.translate(0, -2 * chevronOffset, 0);
            ElementEnum.PEGASUS_CHEVRON_LIGHT.render(stack, source, combinedLight, renderEmissive);

            stack.popPose();

            if (!onlyLight) {
                ElementEnum.PEGASUS_CHEVRON_FRAME.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
                ElementEnum.PEGASUS_CHEVRON_BACK.render(stack, source, combinedLight);
            }


            stack.popPose();
        }
    }

    public static double[] getPositionInRingAtIndex(double radius, int index) {
        double deg = -((360.0 / GLYPHS_COUNT) * index) + 90;
        double rad = Math.toRadians(deg);
        return new double[]{radius * Math.cos(rad), radius * Math.sin(rad), (360.0 / GLYPHS_COUNT) * index};
    }

    protected void renderGlyph(SymbolInterface glyph, int slot, boolean deactivated) {
        renderGlyph(glyph, slot, deactivated, false);
        if (deactivated) {
            renderGlyph(glyph, slot, false, true);
        }
    }


    private static final Map<SymbolInterface, VertexBuffer> SYMBOLS_MODEL_CACHE = new HashMap<>();

    @SuppressWarnings("all")
    protected void renderGlyph(SymbolInterface glyph, int slot, boolean deactivated, boolean translatePos) {
        if (AbstractOBJModel.getRenderMethod() != AbstractOBJModel.EnumOBJRenderMethod.NORMAL) {
            renderGlyphGUI(glyph, slot, deactivated, translatePos);
            return;
        }
        var vertexBuffer = SYMBOLS_MODEL_CACHE.get(glyph);
        float tileSize = 0.270f;

        if (vertexBuffer == null) {
            vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);

            buffer.vertex(-tileSize, 0, -tileSize)
                    .color(1f, 1f, 1f, 1f)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(0, 0, 1)
                    .endVertex();
            buffer.vertex(-tileSize, 0, tileSize)
                    .color(1f, 1f, 1f, 1f)
                    .uv(0, 1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(0, 0, 1)
                    .endVertex();
            buffer.vertex(tileSize, 0, tileSize)
                    .color(1f, 1f, 1f, 1f)
                    .uv(1, 1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(0, 0, 1)
                    .endVertex();
            buffer.vertex(tileSize, 0, -tileSize)
                    .color(1f, 1f, 1f, 1f)
                    .uv(1, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(0, 0, 1)
                    .endVertex();

            BufferBuilder.RenderedBuffer rb = buffer.end();
            vertexBuffer.bind();
            vertexBuffer.upload(rb);
            VertexBuffer.unbind();
        }

        VertexBuffer finalVertexBuffer = vertexBuffer;
        double[] slotPos = getPositionInRingAtIndex(GLYPHS_RING_RADIUS, slot + 1);
        EmissiveRenderer.renderWithLightOverlay(stack, LightTexture.FULL_BRIGHT, true, () -> {
            var variant = (deactivated ? StargatePointOfOriginsDefaults.VARIANT_GATE_OFF_PNG : StargatePointOfOriginsDefaults.VARIANT_GATE_PNG);
            bindSymbolTextureForRing(glyph, variant);
            finalVertexBuffer.bind();
        }, () -> {

            // Round is necessary here, since Minecraft doesn't handle many decimal places very well in this case,
            // so that the texture just ceases to exist.
            stack.translate(NumberUtils.round(slotPos[0], 3), NumberUtils.round(slotPos[1], 3), translatePos ? -0.105 : 0.205);
            stack.mulPose(Axis.XP.rotationDegrees(90));

            stack.mulPose(Axis.YN.rotationDegrees((float) slotPos[2]));
            Matrix4f projection = RenderSystem.getProjectionMatrix();
            Matrix4f matrix = stack.last().pose();
            finalVertexBuffer.drawWithShader(matrix, projection, Objects.requireNonNull(RenderSystem.getShader()));
            VertexBuffer.unbind();
        });
        SYMBOLS_MODEL_CACHE.put(glyph, vertexBuffer);
    }

    protected void bindSymbolTextureForRing(SymbolInterface glyph, String variant) {
        if (glyph instanceof SymbolPegasusEnum symbolPegasusEnum)
            symbolPegasusEnum.bindIconTexture(tileEntity.getPointOfOrigin(), variant);
        else
            glyph.bindIconTexture(tileEntity.getPointOfOrigin());
    }

    @SuppressWarnings("all")
    protected void renderGlyphGUI(SymbolInterface glyph, int slot, boolean deactivated, boolean translatePos) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        float tileSize = 0.270f;
        double[] slotPos = getPositionInRingAtIndex(GLYPHS_RING_RADIUS, slot + 1);
        EmissiveRenderer.renderWithLightOverlay(stack, combinedLight, false, () -> {
            var variant = (deactivated ? StargatePointOfOriginsDefaults.VARIANT_GATE_OFF_PNG : StargatePointOfOriginsDefaults.VARIANT_GATE_PNG);
            bindSymbolTextureForRing(glyph, variant);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        }, () -> {
            // Round is necessary here, since Minecraft doesn't handle many decimal places very well in this case,
            // so that the texture just ceases to exist.
            stack.translate(NumberUtils.round(slotPos[0], 3), NumberUtils.round(slotPos[1], 3), translatePos ? -0.105 : 0.205);
            stack.mulPose(Axis.XP.rotationDegrees(90));

            stack.mulPose(Axis.YN.rotationDegrees((float) slotPos[2]));
            Matrix4f matrix = stack.last().pose();
            buffer.vertex(matrix, -tileSize, 0, -tileSize).uv(0, 0).endVertex();
            buffer.vertex(matrix, -tileSize, 0, tileSize).uv(0, 1).endVertex();
            buffer.vertex(matrix, tileSize, 0, tileSize).uv(1, 1).endVertex();
            buffer.vertex(matrix, tileSize, 0, -tileSize).uv(1, 0).endVertex();

            tessellator.end();
        }, GameRenderer::getPositionTexShader);
    }

    @Override
    @Nonnull
    public Pair<Integer, Integer> getEventHorizonColor() {
        return Pair.of(0xff2e578d, 0xffffffff);
    }
}
