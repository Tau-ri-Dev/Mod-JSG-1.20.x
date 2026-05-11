package dev.tauri.jsg.client.renderer.blockentity.stargate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.StargateWithIris;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateChevronsState;
import dev.tauri.jsg.common.stargate.manager.StargateIrisManager;
import dev.tauri.jsg.core.common.util.math.MathFunctionImpl;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public abstract class StargateClassicRenderer<S extends StargateClassicRendererState> extends StargateAbstractRenderer<S> {

    public StargateClassicRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    public abstract double getScaleMultiplier();

    @Override
    protected void applyTransformations() {
        float scale = (float) (0.83 * getScaleMultiplier());
        stack.translate(0, ((getGateDiameter() * scale) / 2) - 0.3, 0);
        stack.scale(scale, scale, scale);
    }

    protected abstract void renderChevron(ChevronEnum chevron, StargateChevronsState.ChevronState state, float color, boolean onlyLight);

    protected void renderChevrons() {

        for (ChevronEnum chevron : ChevronEnum.values()) {
            stack.pushPose();
            var state = tileEntity.getStateManager().getChevronsState().get(chevron);
            // not emissive
            renderChevron(chevron, state, 1f, false);

            // emissive layer
            int chevronLight = (int) (state.getState() * 1.5f);
            float maxLight = Math.max(combinedLight, LightTexture.pack(chevronLight, chevronLight));
            renderChevron(chevron, state, (state.isLocked() ? maxLight / LightTexture.FULL_BRIGHT : 1f), true);
            stack.popPose();
        }
    }

    // ----------------------------------------------------------------------------------------
    // Iris rendering

    protected static final ResourceLocation SHIELD_TEXTURE = JSGMapping.rl(JSG.MOD_ID, "textures/tesr/iris/shield.jpg");

    public static final int PHYSICAL_IRIS_ANIMATION_LENGTH = 65;
    public static final int SHIELD_IRIS_ANIMATION_LENGTH = 10;

    public static final MathFunctionImpl IRIS_ANIMATION = new MathFunctionImpl((x) -> ((x * 1.5f - 1f) * (x * 1.5f - 1f) * (x * 1.5f - 1f) + 1f + x * 1.5f / 2f) / 1.875f);
    public static final MathFunctionImpl IRIS_ANIMATION_OPENING = new MathFunctionImpl((x) -> (float) ((Math.sin(10f * x) * x) / 10f + x) / 0.9456f);

    @Override
    public void renderIris(boolean backOnly) {
        float irisAnimationStage = (level.getGameTime() - rendererState.irisAnimation + partialTicks);
        /*
         *
         * SHIELD:
         * MAX: 0.7
         * MIN: 0.0
         *
         * IRIS:
         * MAX: 1 - closed
         * MIN: 0 - open
         *
         */
        EnumIrisState irisState = rendererState.irisState;
        EnumIrisType irisType = rendererState.irisType;
        if (irisType == null || irisState == null) {
            return;
        }
        if (irisType == EnumIrisType.SHIELD) {
            if (irisState == EnumIrisState.OPENED) return;
            irisAnimationStage *= 0.7f / SHIELD_IRIS_ANIMATION_LENGTH;
            if (irisAnimationStage > 0.7f) irisAnimationStage = 0.7f;
            if (irisAnimationStage < 0) irisAnimationStage = 0;
            if (irisState == EnumIrisState.OPENING) irisAnimationStage = .7f - irisAnimationStage;
            stack.pushPose();

            getTextureLoader().getTexture(SHIELD_TEXTURE).bindTexture();

            stack.translate(0, 0, 0.13);
            StargateRendererStatic.currentStack = stack;
            StargateRendererStatic.packedLight = combinedLight;
            StargateRendererStatic.renderShield(irisAnimationStage, getShieldColor(), backOnly);
            stack.popPose();
        }
        if ((irisType == EnumIrisType.IRIS_TITANIUM || irisType == EnumIrisType.IRIS_TRINIUM || irisType == EnumIrisType.IRIS_CREATIVE) && backOnly) {
            var irisColor = getIrisHeatColor();
            irisAnimationStage /= PHYSICAL_IRIS_ANIMATION_LENGTH;
            if (irisAnimationStage > 1f) irisAnimationStage = 1f;
            if (irisAnimationStage < 0) irisAnimationStage = 0;

            if (irisState == EnumIrisState.OPENING || irisState == EnumIrisState.OPENED) {
                irisAnimationStage = IRIS_ANIMATION_OPENING.apply(irisAnimationStage);
                irisAnimationStage = 1f - irisAnimationStage;
            } else {
                irisAnimationStage = IRIS_ANIMATION.apply(irisAnimationStage);
            }

            var irisPivot = getIrisPivot();

            var irisImpactOffset = (tileEntity instanceof StargateWithIris<?> stargateWithIris) ? ((StargateIrisManager) stargateWithIris.getIrisManager()).getIrisAnimationState().getIrisImpactOffset(tileEntity.getTime(), partialTicks) : 0f;

            var irisBladesCount = getIrisBladesCount();
            for (float i = 0; i < irisBladesCount; i++) {
                float rotateIndex = (360f / (float) irisBladesCount) * i;

                stack.pushPose();
                stack.translate(0, 0, 0.025);
                stack.mulPose(Axis.ZP.rotationDegrees(rotateIndex));
                translateIrisBlade();
                var tiltCoef = (float) MathHelper.clampedLerp(0, 1f, (irisAnimationStage - 0.2f) / 0.8f);
                rotateAround(stack, Axis.YP.rotationDegrees(-0.6f + ((irisImpactOffset / 3f) + getIrisBladeTilt()) * tiltCoef), irisPivot);
                rotateAround(stack, Axis.ZP.rotationDegrees(irisAnimationStage * (getIrisCloseAngle() - irisImpactOffset * 1.5f * (level.random.nextFloat() * 0.7f + 0.3f))), irisPivot);
                getIrisModel().bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight, OverlayTexture.NO_OVERLAY, false, irisColor.x, irisColor.y, irisColor.z, 1f, false);
                stack.popPose();
            }
        }
    }

    protected float getIrisCloseAngle() {
        return 45f;
    }

    protected ElementEnum getIrisModel() {
        return ElementEnum.IRIS;
    }

    protected int getIrisBladesCount() {
        return 20;
    }

    protected void translateIrisBlade() {
    }

    protected Vec3 getIrisPivot() {
        return new Vec3(-10.1815f / 2f, 0, 0);
    }

    protected static void rotateAround(PoseStack stack, Quaternionf rotation, Vec3 pivot) {
        var pivotN = pivot.multiply(-1, -1, -1);
        stack.translate(pivotN.x(), pivotN.y(), pivotN.z());
        stack.mulPose(rotation);
        stack.translate(pivot.x(), pivot.y(), pivot.z());
    }

    @Override
    protected Vector3f getGateColor() {
        return getGateHeatColor();
    }

    public float getIrisBladeTilt() {
        return 1f;
    }

    public Vector3f getIrisHeatColor(float red) {
        return new Vector3f(1f + (red * 3f), 1f, 1f);
    }

    public int getShieldColor() {
        return 0xffffffff;
    }

    public Vector3f getIrisHeatColor() {
        if (rendererState.irisHeat == -1) {
            return getIrisHeatColor(0);
        }
        float red = (float) (rendererState.irisHeat / (rendererState.irisType == EnumIrisType.IRIS_TITANIUM ? StargateClassicBaseBE.IRIS_MAX_HEAT_TITANIUM : StargateClassicBaseBE.IRIS_MAX_HEAT_TRINIUM));
        if (rendererState.irisType == EnumIrisType.IRIS_CREATIVE) red = 0;
        return getIrisHeatColor(red);
    }

    public Vector3f getGateHeatColor() {
        if (rendererState.gateHeat == -1) return new Vector3f(1f, 1f, 1f);
        float red = (float) (rendererState.gateHeat / StargateClassicBaseBE.GATE_MAX_HEAT);
        return new Vector3f(1f + (red * 2.7f), 1f, 1f);
    }
}
