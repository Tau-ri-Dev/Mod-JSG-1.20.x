package dev.tauri.jsg.client.renderer.blockentity.stargate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.client.model.IModelLoader;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import dev.tauri.jsg.core.client.renderer.LinkableRenderer;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.blockentity.CamouflageBE;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.common.multistructure.IMultiStructureRenderer;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.core.common.util.JSGColorUtil;
import dev.tauri.jsg.core.common.util.RotationUtil;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class StargateAbstractRenderer<S extends StargateAbstractRendererState> implements LinkableRenderer, BlockEntityRenderer<StargateAbstractBaseBE<?, ?>>, IMultiStructureRenderer<StargateAbstractBaseBE<?, ?>> {
    public StargateAbstractRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    public StargateAbstractBaseBE<?, ?> tileEntity;
    public PoseStack stack;
    public MultiBufferSource source;
    public int combinedLight;
    public Level level;
    public float partialTicks;
    public S rendererState;

    @Override
    @ParametersAreNonnullByDefault
    public boolean shouldRenderOffScreen(StargateAbstractBaseBE baseBE) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64 * 3;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean shouldRender(StargateAbstractBaseBE baseBE, Vec3 vec3) {
        return BlockEntityRenderer.super.shouldRender(baseBE, vec3);
    }

    public abstract float getGateDiameter();

    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void initForGui(StargateAbstractBaseBE<?, ?> tile, GuiGraphics graphics, float partialTicks) {
        tileEntity = tile;
        level = tile.getLevel();
        rendererState = (S) tile.getStateManager().getRendererStateClient();
        this.stack = graphics.pose();
        this.partialTicks = partialTicks;
        this.source = graphics.bufferSource();
        tileEntity.getStateManager().getChevronsState().update(this.partialTicks);
        StargateRendererStatic.currentStack = stack;
        StargateRendererStatic.packedLight = 0;
    }

    public int getCombinedLight() {
        double count = 0;
        double count2 = 0;
        double blockSum = 0;
        double skySum = 0;
        for (Map.Entry<BlockPos, BlockState> block : tileEntity.getMergeHelper().getBlocks().entrySet()) {
            for (var side : Direction.values()) {
                int light = LevelRenderer.getLightColor(level, block.getKey().offset(side.getNormal()));
                var blockLight = LightTexture.block(light);
                var skyLight = LightTexture.sky(light);
                var coef = (blockLight + skyLight + 1) / 32f;
                count += coef;
                count2 += (2 * coef);
                blockSum += (blockLight * 2L * coef);
                skySum += (skyLight * coef);
            }
        }
        if (count == 0) return LightTexture.FULL_BRIGHT;
        return LightTexture.pack((int) (blockSum / count2), (int) (skySum / count));
    }

    public void renderCamoBlocks(int combinedOverlay) {
        this.stack.pushPose();
        var list = new ArrayList<>(tileEntity.getMergeHelper().getBlocks().entrySet());
        list.add(new AbstractMap.SimpleEntry<>(tileEntity.getBlockPos(), tileEntity.getBlockState()));
        for (Map.Entry<BlockPos, BlockState> block : list) {
            var tile = level.getBlockEntity(block.getKey());
            if (!(tile instanceof CamouflageBE camoBE)) continue;
            var camoState = camoBE.getCamoBlock();
            if (camoState.isAir()) continue;
            int light = LevelRenderer.getLightColor(level, block.getKey());
            BlockRenderer.renderBlockShaded(tileEntity.getLevel(), block.getKey(), camoState, block.getKey().subtract(tileEntity.getBlockPos()), this.stack, this.source, light, combinedOverlay);
        }
        this.stack.popPose();
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("unchecked")
    public void render(StargateAbstractBaseBE baseBE, float partialTicks, PoseStack stack, MultiBufferSource bufferSource, int ignoredLight, int combinedOverlay) {
        renderLink(baseBE.getBlockPos(), baseBE, stack, bufferSource);
        tileEntity = baseBE;
        rendererState = (S) baseBE.getStateManager().getRendererStateClient();
        this.stack = stack;
        this.partialTicks = partialTicks;
        this.source = bufferSource;
        if (tileEntity.getLevel() == null) return;
        level = tileEntity.getLevel();
        this.combinedLight = getCombinedLight();

        if (!shouldRender()) {
            // render building helper
            renderBuildingHelper(tileEntity, stack, source, combinedLight, combinedOverlay);
        } else {
            tileEntity.getStateManager().getChevronsState().update(partialTicks);
            // render camo blocks 1
            renderCamoBlocks(combinedOverlay);

            // render stargate model
            this.stack.pushPose();
            if (JSGConfig.Debug.renderBoundingBoxes.get() || Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                this.stack.pushPose();
                ((JSGAxisAlignedBB) tileEntity.getRenderBoundingBox()).inset(tileEntity.getBlockPos()).render(Component.literal("RenderBox"), stack, bufferSource);
                tileEntity.getEventHorizonManager().getLocalTeleportBox().render(Component.literal("TeleportBox"), stack, bufferSource, 42 / 255f, 169 / 255f, 215 / 255f, 1);
                tileEntity.getEventHorizonManager().getLocalTeleportBoxBack().render(Component.literal("TeleportBox_subtickBack"), stack, bufferSource, 16 / 255f, 58 / 255f, 175 / 255f, 1);
                tileEntity.getEventHorizonManager().getLocalTeleportBoxFront().render(Component.literal("TeleportBox_subtickFront"), stack, bufferSource, 80 / 255f, 226 / 255f, 178 / 255f, 1);
                int i = 1;
                for (JSGAxisAlignedBB aabb : tileEntity.getEventHorizonManager().getLocalInnerBlockBoxes()) {
                    aabb.render(Component.literal("EHFormingBox_" + i), stack, bufferSource, 229 / 255f, 31 / 255f, 31 / 255f, 1);
                    i++;
                }
                i = 1;
                for (JSGAxisAlignedBB aabb : tileEntity.getEventHorizonManager().getLocalKillingBoxes()) {
                    aabb.render(Component.literal("KawooshBox_" + i), stack, bufferSource, 216 / 255f, 31 / 255f, 229 / 255f, 1);
                    i++;
                }
                this.stack.popPose();
            }
            this.stack.pushPose();
            this.stack.translate(0.5, 0.5, 0.5);
            this.stack.mulPose(RotationUtil.getRotation(tileEntity.getFacingVertical(), tileEntity.getFacing()));
            applyTransformations();
            this.stack.pushPose();
            StargateRendererStatic.currentStack = this.stack;
            StargateRendererStatic.packedLight = combinedLight;
            var gateRedColor = getGateColor();
            renderWholeGate();
            this.stack.popPose();
            this.stack.popPose();
            this.stack.popPose();

            // render camo blocks 2
            renderCamoBlocks(combinedOverlay);
        }
    }

    protected Vector3f getGateColor() {
        return new Vector3f(1f, 1f, 1f);
    }

    protected abstract void applyTransformations();


    // ---------------------------------------------------------------------------------------
    // Render

    protected static final String EV_HORIZON_OVERLAY_TEXTURE = "textures/tesr/event_horizon_animated_overlay.png";
    protected static final String EV_HORIZON_KAWOOSH_OVERLAY_TEXTURE = "textures/tesr/event_horizon_animated_overlay_kawoosh.png";
    private static final float VORTEX_START = 5.275f;
    private static final float SPEED_FACTOR = 6f;

    public ITextureLoader getTextureLoader() {
        return JSGApi.JSG_LOADERS_HOLDER.texture();
    }

    public IModelLoader getModelsLoader() {
        return JSGApi.JSG_LOADERS_HOLDER.model();
    }

    private static final Map<ResourceLocation, Boolean> EH_RENDERED = new HashMap<>();

    static {
        EH_RENDERED.put(JSGMapping.rl(JSG.MOD_ID, EV_HORIZON_OVERLAY_TEXTURE), false);
        EH_RENDERED.put(JSGMapping.rl(JSG.MOD_ID, EV_HORIZON_KAWOOSH_OVERLAY_TEXTURE), false);
    }

    public void renderWholeGate() {
        renderGate();
        renderIris(true);

        if (rendererState.doEventHorizonRender) {
            stack.pushPose();
            renderKawoosh();
            stack.popPose();
        } else if (JSGConfig.Stargate.renderEHifTheyNot.get()) {
            stack.pushPose();
            preRenderKawoosh();
            stack.popPose();
        }

        renderIris(false);
    }

    protected boolean shouldRender() {
        if (tileEntity == null) return false;
        if (rendererState == null) return false;
        return tileEntity.getBlockState().hasProperty(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY) && !tileEntity.getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY);
    }

    protected abstract void renderGate();

    public void scaleVortex() {
    }

    public boolean shouldRenderBackVortex() {
        return false;
    }

    protected ResourceLocation getEventHorizonTextureResource(boolean kawoosh) {
        return new ResourceLocation(JSG.MOD_ID, (kawoosh ? EV_HORIZON_KAWOOSH_OVERLAY_TEXTURE : EV_HORIZON_OVERLAY_TEXTURE));
    }

    protected void renderKawoosh() {
        renderKawoosh(rendererState, true);
    }

    protected void preRenderKawoosh() {

        StargateAbstractRendererState rs = new StargateAbstractRendererState().initClient(rendererState.pos);

        for (int i = 0; i < 2; i++) {
            rs.vortexState = (i == 0 ? EnumVortexState.STILL : EnumVortexState.FORMING);
            renderKawoosh(rs, false);
        }
    }

    @Nonnull
    public abstract Pair<Integer, Integer> getEventHorizonColor();

    protected void renderKawoosh(StargateAbstractRendererState rendererState, boolean render) {
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);

        float gateWait = (float) tileEntity.getTime() - rendererState.gateWaitStart;

        // Waiting for sound sync
        if (gateWait < (44 - 24)) {
            return;
        }

        boolean isKawoosh = (rendererState.vortexState == EnumVortexState.FORMING
                || rendererState.vortexState == EnumVortexState.DECREASING
                || rendererState.vortexState == EnumVortexState.FULL);

        ResourceLocation ehTextureRes = getEventHorizonTextureResource(isKawoosh && !rendererState.noxDialing);
        if (!render && EH_RENDERED.get(ehTextureRes)) return;

        EH_RENDERED.put(ehTextureRes, true);

        //GlStateManager.disableLighting();
        //GlStateManager.enableCull();

        stack.pushPose();
        stack.translate(0, 0, 0.06);

        if (!render) {
            stack.scale(0.0000001f, 0.0000001f, 0.0000001f);
        }

        // set default texture
        var ehTexture = getTextureLoader().getTexture(ehTextureRes);

        // bind texture
        if (ehTexture != null) ehTexture.bindTexture();

        long kawooshStart = rendererState.gateWaitStart + 44 - 24;
        float tick = (float) (level.getGameTime() - kawooshStart + partialTicks);
        float mul;

        float inner = StargateRendererStatic.EVENT_HORIZON_RADIUS - (tick / (rendererState.noxDialing ? 3.2f : 1)) / 3.957f;

        // Fading in the unstable vortex
        float tick2 = tick / 4f;
        if (tick2 <= Math.PI / 2) rendererState.whiteOverlayAlpha = MathHelper.cos(tick2);

        else {
            if (!rendererState.zeroAlphaSet) {
                rendererState.zeroAlphaSet = true;
                rendererState.whiteOverlayAlpha = 0.0f;
            }
        }

        // ----------------------------------------------------------------------------------------------
        // DO MATH - calculate EH and kawoosh

        float kawooshRadius = 0.2f; //StargateRendererStatic.kawooshRadius - 2;
        /*..if (rendererState.noxDialing) {
            kawooshRadius = 0.2f;
        }*/

        float noxAlpha = 0;
        if (rendererState.noxDialing) {
            noxAlpha = Math.min(0.8f, Math.max(0, (inner / StargateRendererStatic.EVENT_HORIZON_RADIUS)));
        }

        // Back side of the EH
        if (rendererState.vortexState != EnumVortexState.STILL && rendererState.vortexState != EnumVortexState.CLOSING) {
            if (inner >= 0.2f) {
                rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, inner - 0.2f, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
            }
        } else
            rendererState.frontStrip = null;
        // ----

        // Going center
        if (inner >= kawooshRadius) {
            rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, inner - 0.2f, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
        }
        if (inner < StargateRendererStatic.kawooshRadius) {
            if (rendererState.backStripClamp) {
                // Clamping to the desired size
                if (inner < kawooshRadius) {
                    rendererState.backStripClamp = false;
                    rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, kawooshRadius - 0.2f, StargateRendererStatic.EVENT_HORIZON_RADIUS, null);
                }

                float argState = (tick - VORTEX_START) / SPEED_FACTOR;

                if (argState < 1.342f) rendererState.vortexState = EnumVortexState.FORMING;
                else if (argState < 4.15f) rendererState.vortexState = EnumVortexState.FULL;
                else if (argState < 5.898f) rendererState.vortexState = EnumVortexState.DECREASING;
                else if (rendererState.vortexState != EnumVortexState.CLOSING)
                    rendererState.vortexState = EnumVortexState.STILL;
            }
            if (rendererState.frontStripClamp && inner < 0.2f) {
                rendererState.frontStripClamp = false;
                rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, 0, StargateRendererStatic.EVENT_HORIZON_RADIUS, null);
            }
            if (!(rendererState.vortexState == EnumVortexState.STILL)) {
                float arg = (tick - VORTEX_START) / SPEED_FACTOR;

                if (!(rendererState.vortexState == EnumVortexState.CLOSING)) {
                    if (!(rendererState.vortexState == EnumVortexState.SHRINKING)) {
                        if (rendererState.vortexState == EnumVortexState.FORMING && arg >= 1.342f) {
                            rendererState.vortexState = EnumVortexState.FULL;
                        }

                        // Offset of the end of the function domain used to generate vortex
                        float end = 0.75f;

                        if (rendererState.vortexState == (EnumVortexState.DECREASING) && arg >= 5.398 + end) {
                            rendererState.vortexState = EnumVortexState.STILL;
                        }

                        if (rendererState.vortexState == (EnumVortexState.FULL)) {
                            if (arg >= 3.65f + end) {
                                rendererState.vortexState = EnumVortexState.DECREASING;
                            }

                            // Flattening the vortex and keeping it still for a moment
                            if (arg < 2) mul = (arg - 1.5f) * (arg - 2.5f) / -10f + 0.91f;
                            else if (arg > 3 + end) mul = (arg - 2.5f - end) * (arg - 3.5f - end) / -10f + 0.91f;
                            else mul = 0.935f;
                        } else {
                            if (rendererState.vortexState == (EnumVortexState.FORMING))
                                mul = (arg * (arg - 4)) / -4.0f;

                            else mul = ((arg - 1 - end) * (arg - 5 - end)) / -5.968f + 0.29333f;
                        }

                        boolean renderWortex = true;
                        // Rendering the vortex
                        if (rendererState instanceof StargateClassicRendererState casted) {
                            // disable mul while iris/shield is closed
                            if (casted.irisState == EnumIrisState.CLOSED && casted.irisType != EnumIrisType.NULL) {
                                mul = 0;
                                renderWortex = false;
                            }
                        }
                        if (!rendererState.noxDialing && renderWortex) {
                            stack.pushPose();
                            scaleVortex();
                            float prevZ = 0;
                            float prevRad = 0;

                            int index = 0;
                            for (Map.Entry<Float, Float> e : StargateRendererStatic.Z_RadiusMap.entrySet()) {
                                var currentZ = e.getKey();
                                if (currentZ < 0) continue;
                                if (currentZ >= 0 && mul <= 0) continue;
                                float mulAbs = Math.abs(mul);
                                var currentRad = e.getValue() == 0 ? 0 : e.getValue() + StargateRendererStatic.getOffset(index, tick, 7, 1);
                                if (index != 0) {
                                    new StargateRendererStatic.QuadStrip(9, currentRad, prevRad, tick, 1 / 5f * 7).render(tick, currentZ * mulAbs, prevZ * mulAbs, false, 1.0f - rendererState.whiteOverlayAlpha, 5, false, getEventHorizonColor().first(), getEventHorizonColor().second());
                                }
                                prevZ = currentZ;
                                prevRad = currentRad;
                                index++;
                            }
                            stack.popPose();
                        }

                    } // not shrinking if

                    else {
                        // Going outwards, closing the gate 29
                        long stateChange = rendererState.gateWaitClose + 35;
                        float arg2 = ((level.getGameTime() - stateChange + partialTicks) / 3f) - 1.0f;

                        rendererState.whiteOverlayAlpha = MathHelper.sin(arg2);

                        if (arg2 < StargateRendererStatic.EVENT_HORIZON_RADIUS + 0.1f) {
                            rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
                            rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
                        } else {
                            rendererState.whiteOverlayAlpha = null;

                            if (level.getGameTime() - stateChange - 9 > 7) {
                                rendererState.doEventHorizonRender = false;
                            }
                        }
                    }
                } // not closing if

                else {
                    // Fading out the event horizon, closing the gate
                    if ((level.getGameTime() - rendererState.gateWaitClose) > 35) {
                        float arg2 = (float) ((level.getGameTime() - (rendererState.gateWaitClose + 35) + partialTicks) / SPEED_FACTOR / 2f);

                        if (arg2 <= Math.PI / 6) rendererState.whiteOverlayAlpha = MathHelper.sin(arg2);
                        else {
                            if (rendererState.backStrip == null)
                                rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);

                            if (rendererState.frontStrip == null)
                                rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);

                            rendererState.vortexState = EnumVortexState.SHRINKING;
                        }
                    }
                }
            } // not still if
        }

        // ----------------------------------------------------------------------------------------------
        // RENDER - render kawoosh and EH

        // Rendering stable wormhole EH
        if (rendererState.vortexState != null) {
            if (rendererState.vortexState == EnumVortexState.STILL || rendererState.vortexState == EnumVortexState.CLOSING) {
                if (rendererState.vortexState == EnumVortexState.CLOSING)
                    renderEventHorizon(true, rendererState.whiteOverlayAlpha, false, 1.7f);
                else
                    renderEventHorizon(false, rendererState.horizonUnstable ? (0.05f + (float) (Math.sin(tick / 4f * Math.PI) * 0.05f)) : null, false, rendererState.horizonUnstable ? 1.2f : 1);

                stack.popPose();
                //GlStateManager.enableLighting();

                return;
            }
        }

        // Render kawoosh and animations (opening, closing going to/from center)
        if (rendererState.whiteOverlayAlpha != null) {

            if (rendererState.backStrip != null)
                rendererState.backStrip.render(tick, 0f, 0f, false, Math.max(0, 1.0f - rendererState.whiteOverlayAlpha - noxAlpha), 1, getEventHorizonColor().first(), getEventHorizonColor().second());

            if (rendererState.frontStrip != null) {
                if (shouldRenderBackVortex()) {
                    renderEventHorizon(false, 0.3f, false, 1, true);
                }
                double tickFromStart = (level.getGameTime() - kawooshStart + (double) partialTicks);
                double arg2 = (((tickFromStart - VORTEX_START) / SPEED_FACTOR) - (3.65 + 0.75)) / 1.5;
                if (!shouldRenderBackVortex() || arg2 <= 0) {
                    stack.pushPose();
                    RenderSystem.enableBlend();
                    stack.mulPose(Axis.YN.rotationDegrees(180));

                    // Bind non-kawoosh texture from back
                    ehTextureRes = getEventHorizonTextureResource(false);
                    ehTexture = getTextureLoader().getTexture(ehTextureRes);
                    if (ehTexture != null) ehTexture.bindTexture();

                    Float alpha = Math.max(0, 1.0f - rendererState.whiteOverlayAlpha - 0.3f - noxAlpha);

                    rendererState.frontStrip.render(tick, 0f, 0f, false, alpha, 1, getEventHorizonColor().first(), getEventHorizonColor().second());
                    RenderSystem.disableBlend();
                    stack.popPose();
                }
            }
        }

        //GlStateManager.enableLighting();
        stack.popPose();
    }

    /**
     * Renders event horizon(white/blue flat thing)
     *
     * @param white    Are we rendering the white overlay?
     * @param alpha    Alpha channel of the white overlay
     * @param backOnly Render only the back face?(Used in kawoosh)
     * @param mul      Multiplier of the horizon waving speed
     */
    @SuppressWarnings("all")
    protected void renderEventHorizon(boolean white, Float alpha, boolean backOnly, float mul) {
        renderEventHorizon(white, alpha, backOnly, mul, false);
    }

    protected void renderEventHorizon(boolean white, Float alpha, boolean backOnly, float mul, boolean closingAnimation) {
        if (getBlackHoleVortexDepth() != 0 || getBlackHoleVortexRedMul() > 0) {
            renderBackVortex(closingAnimation, false, getBlackHoleVortexDepth(), tileEntity.getStateManager().getBlackHoleAnimationState().getBackVortexAngle(), getBlackHoleVortexRedMul());
            return;
        }
        float tick = (float) JSGMinecraftHelper.getClientTick();

        if (!closingAnimation) {
            RenderSystem.enableBlend();
            for (int k = (backOnly ? 1 : 0); k < (shouldRenderBackVortex() ? 1 : 2); k++) {
                stack.pushPose();
                if (k == 1) {
                    stack.mulPose(Axis.YN.rotationDegrees(180));
                }

                if (alpha == null) alpha = 0.0f;

                if (k == 1) alpha += 0.3f;

                var outerColor = JSGColorUtil.blendColors(getEventHorizonColor().first(), 0xffffffff, rendererState.horizonUnstable ? (0.05f + (float) (Math.sin(tick / 2f * Math.PI) * 0.1f)) : 0);


                if (white)
                    StargateRendererStatic.innerCircle.render(tick, true, alpha, mul, (byte) 0, outerColor, getEventHorizonColor().second());

                StargateRendererStatic.innerCircle.render(tick, false, 1.0f - alpha, mul, (byte) 0, outerColor, getEventHorizonColor().second());


                for (StargateRendererStatic.QuadStrip strip : StargateRendererStatic.quadStrips) {
                    if (white)
                        strip.render(tick, true, alpha, mul, (byte) 0, outerColor, getEventHorizonColor().second());

                    strip.render(tick, false, 1.0f - alpha, mul, (byte) 0, outerColor, getEventHorizonColor().second());
                }
                stack.popPose();
            }
            RenderSystem.disableBlend();
        }
        if (shouldRenderBackVortex()) {
            renderBackVortex(closingAnimation, tick);
        }
    }

    public float getBlackHoleVortexDepth() {
        return tileEntity.getStateManager().getBlackHoleAnimationState().getBackVortexDepth();
    }

    public float getBlackHoleVortexRedMul() {
        return tileEntity.getStateManager().getBlackHoleAnimationState().getBackVortexRed();
    }

    protected void renderBackVortex(boolean closingAnimation, double rotationZ) {
        long kawooshStart = rendererState.gateWaitStart + 44 - 24;
        double tickFromStart = (level.getGameTime() - kawooshStart + (double) partialTicks);
        double arg = (((tickFromStart - VORTEX_START) / SPEED_FACTOR) - (3.65 + 0.75)) / 1.5;
        if (arg < 0) return;
        float factor;
        if (arg > 1.82) factor = 1;
        else factor = (float) (-(((arg + 2.65f) * (arg + 2.65f - 4f)) / (-5.968f + 0.29333f)) + 0.63f);
        var depthMul = 0.3085f * factor * 2;
        renderBackVortex(closingAnimation, true, depthMul, rotationZ, 0);
    }

    protected void renderBackVortex(boolean closingAnimation, boolean onlyBack, float depthMul, double rotationZ, float redMul) {
        float ticks = (float) JSGMinecraftHelper.getClientTick();
        var c = getEventHorizonColor().first();
        var c2 = getEventHorizonColor().second();
        var mixColors = JSGColorUtil.blendColors(c, 0xff37044A, redMul);
        var mixColorsInner = JSGColorUtil.blendColors(c2, 0xff000000, redMul);

        RenderSystem.enableBlend();
        stack.pushPose();
        stack.last().pose().rotate(Axis.ZP.rotationDegrees((float) (rotationZ % 360f)));
        scaleVortex();
        for (int i = 0; i < (onlyBack ? 1 : 2); i++) {
            stack.pushPose();
            if (i == 1) {
                stack.mulPose(Axis.YN.rotationDegrees(180));
            }
            float prevZ = 0;
            float prevRad = 0;

            int index = 0;
            for (Map.Entry<Float, Float> e : StargateRendererStatic.Z_RadiusMap.entrySet()) {
                var currentZ = e.getKey();
                if (currentZ > 0) continue;
                currentZ *= depthMul;
                var currentRad = e.getValue() == 0 ? 0 : e.getValue() + (StargateRendererStatic.getOffset(index, ticks, 7, 1));
                float mul2 = 0.5f * (-currentZ / 5) * (Math.max(0, (1f + currentZ)) / 2);
                if (currentZ < 0.2f) mul2 = 0;
                mul2 *= Math.abs(currentZ / 10f);
                if (currentZ == 0) {
                    currentRad = StargateRendererStatic.EVENT_HORIZON_RADIUS;
                    mul2 = 0;
                }
                if (i == 1)
                    currentZ *= -1;
                if (rendererState.frontStrip != null && closingAnimation && currentRad < rendererState.frontStrip.innerRadius)
                    continue;
                if (index != 0) {
                    new StargateRendererStatic.QuadStrip(9, currentRad, prevRad, ticks, 1 / 5f * 7 * mul2)
                            .renderCurved(ticks, false, (i == 1 ? 1 : 0.7f), currentZ, currentZ, currentZ, currentZ, prevZ, mixColors, mixColorsInner);
                }
                prevZ = currentZ;
                prevRad = currentRad;
                index++;
            }
            stack.popPose();
        }
        stack.popPose();
        RenderSystem.disableBlend();
    }

    protected void renderIris(boolean backOnly) {
    }

    public enum EnumVortexState {
        FORMING(0),
        FULL(1),
        DECREASING(2),
        STILL(3),
        CLOSING(4),
        SHRINKING(5);

        private static final Map<Integer, EnumVortexState> map = new HashMap<>();

        static {
            for (EnumVortexState packet : EnumVortexState.values()) {
                map.put(packet.index, packet);
            }
        }

        public final int index;

        EnumVortexState(int index) {
            this.index = index;
        }

        public static EnumVortexState valueOf(int index) {
            return map.get(index);
        }

        public boolean equals(EnumVortexState state) {
            return this.index == state.index;
        }
    }
}
