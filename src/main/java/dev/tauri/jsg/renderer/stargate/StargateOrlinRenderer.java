package dev.tauri.jsg.renderer.stargate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.loader.ElementEnum;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class StargateOrlinRenderer extends StargateAbstractRenderer<StargateOrlinRendererState> {

    public static final float GATE_SCALE = 2.3f;

    public interface TranslationInterface {
        void translate(PoseStack stack);
    }

    public static final Map<Integer, TranslationInterface> LIGHT_BULBS_TRANSLATIONS = new HashMap<>() {{
        put(0, (stack) -> {
            stack.translate(0, 0, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(-51.8f));
        });
        put(1, (stack) -> {
            stack.translate(0.003f, 0, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(-103.3f));
        });
        put(2, (stack) -> {
            stack.translate(0, -0.004f, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(-154.4f));
        });
        put(3, (stack) -> {
            stack.translate(0, -0.004f, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(-206f));
        });
        put(4, (stack) -> {
            stack.translate(-0.002f, -0.004f, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(-257.5f));
        });
        put(5, (stack) -> stack.mulPose(Axis.ZP.rotationDegrees(51.3f)));
        put(6, (stack) -> {
        });
    }};


    public StargateOrlinRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    public float getGateDiameter() {
        return 3.2f / GATE_SCALE;
    }

    @Override
    protected void applyTransformations() {
        stack.translate(0, 1.2, 0);
        stack.scale(GATE_SCALE, GATE_SCALE, GATE_SCALE);
    }

    public boolean isBroken() {
        return tileEntity.getBlockState().hasProperty(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN) && tileEntity.getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN);
    }

    public boolean renderStand() {
        return tileEntity.getFacingVertical() == Direction.SOUTH && !level.getBlockState(tileEntity.getBlockPos().below()).isAir();
    }

    public boolean renderInsulator(int index) {
        var facingH = tileEntity.getFacing();
        var facingV = tileEntity.getFacingVertical();
        var insulatorSupportBlock = BlockPos.ZERO;
        if (index == 0) insulatorSupportBlock = new BlockPos(1, 3, 0);
        else if (index == 1) insulatorSupportBlock = new BlockPos(2, 0, 0);
        else if (index == 2) insulatorSupportBlock = new BlockPos(-2, 0, 0);
        else if (index == 3) insulatorSupportBlock = new BlockPos(-1, 3, 0);
        else return false;
        insulatorSupportBlock = tileEntity.relative(insulatorSupportBlock); //BlockPosHelper.rotate(insulatorSupportBlock, facingH, facingV).offset(tileEntity.getBlockPos());
        return !level.getBlockState(insulatorSupportBlock).isAir();
    }

    @Override
    protected void renderGate() {
        stack.pushPose();
        var isBroken = isBroken();
        stack.pushPose();
        if (!isBroken)
            ElementEnum.ORLIN_GATE.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        else
            ElementEnum.ORLIN_GATE_BURNT.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        stack.popPose();

        // insulators
        for (int i = 0; i < 4; i++) {
            if (renderInsulator(i)) {
                stack.pushPose();
                stack.mulPose(Axis.ZP.rotationDegrees(-90 * i));
                getModelsLoader().getModel(getModelsLoader().getModelResource("orlin/orlin_coil.obj")).render(stack, source, combinedLight);
                stack.popPose();
            }
        }
        // lights
        for (int i = 0; i < 7; i++) {
            stack.pushPose();
            LIGHT_BULBS_TRANSLATIONS.get(i).translate(stack);
            var chevron = ChevronEnum.valueOf((i == 6 ? 8 : i));
            var chevronState = tileEntity.getStateManager().getChevronsState().get(chevron);
            int chevronLight = (int) (chevronState.getState() * 1.5f);
            float maxLight = Math.max(combinedLight, LightTexture.pack(chevronLight, chevronLight));
            getModelsLoader().getModel(getModelsLoader().getModelResource("orlin/orlin_gate_light.obj")).render(stack, source, combinedLight, chevronState.getState() > 2, maxLight / LightTexture.FULL_BRIGHT);
            stack.popPose();
        }

        if (renderStand()) {
            stack.pushPose();
            stack.translate(0, -0.75, 0.21);
            ElementEnum.ORLIN_STAND.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
            stack.popPose();
        }
        stack.popPose();
    }

    @Override
    protected void renderKawoosh() {
        stack.translate(0, 3.80873f - 1.7f * GATE_SCALE + 0.11, 0);
        stack.scale(0.13f, 0.13f, 0.13f);

        super.renderKawoosh();
    }

    @Override
    @Nonnull
    public Pair<Integer, Integer> getEventHorizonColor() {
        return Pair.of(0xff0e1346, 0xffffff);
    }
}
