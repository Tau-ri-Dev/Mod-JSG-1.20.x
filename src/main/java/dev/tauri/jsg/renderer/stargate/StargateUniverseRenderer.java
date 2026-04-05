package dev.tauri.jsg.renderer.stargate;

import com.mojang.math.Axis;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import dev.tauri.jsg.loader.ElementEnum;
import dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState;
import dev.tauri.jsg.stargate.animation.chevron.StargateUniverseChevronsState;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class StargateUniverseRenderer extends StargateClassicRenderer<StargateUniverseRendererState> {

    private static final float GATE_DIAMETER = 8.67415f;

    public StargateUniverseRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    public float getGateDiameter() {
        return GATE_DIAMETER;
    }

    @Override
    public double getScaleMultiplier() {
        return 1.14;
    }

    @Override
    protected void renderGate() {
        var angularRotation = (float) tileEntity.getDialingManager().getSpinHelper().apply(level.getGameTime() + partialTicks, true);

        stack.mulPose(Axis.ZN.rotationDegrees(angularRotation));

        // render
        stack.pushPose();
        ElementEnum.UNIVERSE_GATE.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        stack.popPose();

        renderChevrons();

        ElementEnum.UNIVERSE_SYMBOL.bindTexture(rendererState.getBiomeOverlay());
        for (SymbolUniverseEnum symbol : SymbolUniverseEnum.values()) {
            if (symbol == SymbolUniverseEnum.TOP_CHEVRON) continue;
            stack.pushPose();
            int symbolLight = (int) (((StargateUniverseChevronsState) tileEntity.getStateManager().getChevronsState()).getSymbol(symbol).getLightStage() * 15D);
            float maxLight = Math.max((LightTexture.FULL_BRIGHT * 0.15f), Math.max(combinedLight / 2, LightTexture.pack(symbolLight, symbolLight)));
            symbol.getModel(StargateTypes.UNIVERSE.get(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_GATE).render(stack, source, combinedLight, symbolLight > 2, maxLight / LightTexture.FULL_BRIGHT);
            stack.popPose();
        }
    }

    @Override
    protected void renderKawoosh() {
        stack.translate(0, -0.05f, 0);
        stack.scale(0.9f, 0.9f, 1f);
        super.renderKawoosh();
    }

    @Override
    public void scaleVortex() {
        stack.scale(1.2f, 1.2f, 1);
    }

    private static final float IRIS_DARK_COLOR = 0.6f;

    @Override
    public void renderIris(boolean backOnly) {
        stack.pushPose();
        stack.translate(0, 0, 0.06);
        var scale = (getGateDiameter() / 10.1815f) * 1.037f;
        stack.scale(scale, scale, 1f);
        super.renderIris(backOnly);
        stack.popPose();
    }

    @Override
    protected Vec3 getIrisPivot() {
        return super.getIrisPivot().add(-0.1, 0, 0);
    }

    // ----------------------------------------------------------------------------------------
    // Chevrons

    @Override
    protected void renderChevron(ChevronEnum chevron, StargateChevronsState.ChevronState state, float color, boolean onlyLight) {
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));
        getTextureLoader().getTexture(state.getTexture(rendererState.getBiomeOverlay(), onlyLight)).bindTexture();
        ElementEnum.UNIVERSE_CHEVRON.render(stack, source, combinedLight, onlyLight && state.isLocked(), color);
        stack.popPose();
    }

    @Override
    public Vector3f getIrisHeatColor(float red) {
        return new Vector3f(IRIS_DARK_COLOR + (red * 3F), IRIS_DARK_COLOR, IRIS_DARK_COLOR);
    }

    @Override
    public int getShieldColor() {
        if (tileEntity instanceof StargateClassicBaseBE<?> && ((StargateClassicBaseBE<?>) tileEntity).getConfig().getValueOrDefault(StargateConfigOptions.Universe.ORANGE_SHIELD)) {
            return 0xFFFFA659;
        }
        return super.getShieldColor();
    }

    @Override
    @Nonnull
    public Pair<Integer, Integer> getEventHorizonColor() {
        return Pair.of(0xff454545, 0xffffffff);
    }
}
