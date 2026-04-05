package dev.tauri.jsg.renderer.stargate;

import com.mojang.math.Axis;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.loader.ElementEnum;
import dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class StargateMilkyWayRenderer extends StargateClassicRenderer<StargateMilkyWayRendererState> {

    public static final Vec3 RING_LOC = new Vec3(0.0, -0.122333, -0.000597);
    public static final float GATE_DIAMETER = 10.1815f;

    public StargateMilkyWayRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

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
        ElementEnum.MILKYWAY_GATE.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        stack.popPose();
        renderRing();
        renderChevrons();
    }

    // ----------------------------------------------------------------------------------------
    // Ring

    protected void renderRing() {
        stack.pushPose();
        var angularRotation = (float) tileEntity.getDialingManager().getSpinHelper().apply(level.getGameTime() + partialTicks, true);


        stack.translate(RING_LOC.x, RING_LOC.z, RING_LOC.y);
        stack.mulPose(Axis.ZP.rotationDegrees(-angularRotation));
        stack.translate(-RING_LOC.x, -RING_LOC.z, -RING_LOC.y);

        ElementEnum.MILKYWAY_RING.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        JSGSymbolTypes.MILKYWAY.get().getOrigin().getModel(tileEntity.getStargateType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_GATE).render(stack, source, combinedLight);

        stack.popPose();
    }


    // ----------------------------------------------------------------------------------------
    // Chevrons

    @Override
    protected void renderChevron(ChevronEnum chevron, StargateChevronsState.ChevronState state, float color, boolean onlyLight) {
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));

        var chevronState = tileEntity.getStateManager().getChevronsState().get(chevron);
        boolean renderEmissive = (onlyLight && chevronState.isLocked());
        float chevronOffset = chevronState.getOffset(partialTicks, 1.5f);

        getTextureLoader().getTexture(chevronState.getTexture(rendererState.getBiomeOverlay(), onlyLight)).bindTexture();

        stack.pushPose();

        stack.translate(0, chevronOffset, 0);
        ElementEnum.MILKYWAY_CHEVRON_LIGHT.render(stack, source, combinedLight, renderEmissive, color);

        stack.translate(0, -2 * chevronOffset, 0);
        ElementEnum.MILKYWAY_CHEVRON_MOVING.render(stack, source, combinedLight, renderEmissive, color);

        stack.popPose();

        if (!onlyLight) {
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
            ElementEnum.MILKYWAY_CHEVRON_BACK.render(stack, source, combinedLight);
        }


        stack.popPose();
    }

    @Override
    @Nonnull
    public Pair<Integer, Integer> getEventHorizonColor() {
        return Pair.of(0xff0e1346, 0xffffffff);
    }
}
