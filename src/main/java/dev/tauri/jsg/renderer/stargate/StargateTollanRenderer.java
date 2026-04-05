package dev.tauri.jsg.renderer.stargate;

import com.mojang.math.Axis;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.loader.ElementEnum;
import dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class StargateTollanRenderer extends StargateMilkyWayRenderer {
    public StargateTollanRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    public double getScaleMultiplier() {
        return 0.90;
    }

    @Override
    protected void renderGate() {
        stack.pushPose();
        stack.translate(0, 0, 0.08);
        stack.pushPose();
        ElementEnum.TOLLAN_GATE.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        stack.popPose();
        renderChevrons();
        stack.popPose();
    }

    @Override
    protected void renderChevron(ChevronEnum chevron, StargateChevronsState.ChevronState state, float color, boolean onlyLight) {
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));

        boolean renderEmissive = (onlyLight && state.isLocked());

        getTextureLoader().getTexture(getTextureLoader().getTextureResource(!state.isLocked() ? "tollan/gate.jpg" : "tollan/chevron_on.jpg")).bindTexture();
        ElementEnum.TOLLAN_CHEVRON_LIGHT.render(stack, source, combinedLight, renderEmissive);

        if (!onlyLight) {
            ElementEnum.TOLLAN_CHEVRON.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        }
        stack.popPose();
    }

    @Override
    protected void renderKawoosh() {
        stack.scale(1.12f, 1.12f, 1f);
        stack.translate(0, 0, -0.03);
        super.renderKawoosh();
    }

    @Override
    protected void translateIrisBlade() {
        stack.translate(0, 0, 0.05);
        //rotateAround(stack, Axis.YP.rotationDegrees(0f), getIrisPivot());
    }

    @Override
    protected Vec3 getIrisPivot() {
        return new Vec3(-4.2575f, 0.42757, 0);
    }

    @Override
    protected int getIrisBladesCount() {
        return 34;
    }

    @Override
    public void renderIris(boolean backOnly) {
        if (rendererState == null) return;
        EnumIrisType irisType = rendererState.irisType;
        stack.pushPose();
        if (irisType == null || irisType == EnumIrisType.SHIELD) {
            stack.scale(1.12f, 1.12f, 1.12f);
            super.renderIris(backOnly);
            stack.popPose();
            return;
        }
        super.renderIris(backOnly);
        stack.popPose();
    }

    @Override
    protected ElementEnum getIrisModel() {
        return ElementEnum.IRIS_TOLLAN;
    }

    @Override
    protected float getIrisCloseAngle() {
        return 62f;
    }
}
