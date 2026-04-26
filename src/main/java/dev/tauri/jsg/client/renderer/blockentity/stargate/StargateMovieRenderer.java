package dev.tauri.jsg.client.renderer.blockentity.stargate;

import com.mojang.math.Axis;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateChevronsState;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import javax.annotation.Nonnull;

public class StargateMovieRenderer extends StargateMilkyWayRenderer {
    public StargateMovieRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    protected void renderChevron(ChevronEnum chevron, StargateChevronsState.ChevronState state, float color, boolean onlyLight) {
        if (onlyLight) return;
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));

        getTextureLoader().getTexture(getTextureLoader().getTextureResource("movie/chevron" + rendererState.getBiomeOverlay().suffix() + ".png")).bindTexture();

        float chevronOffset = state.getOffset(partialTicks, 2);

        stack.pushPose();

        stack.translate(0, chevronOffset, 0);
        ElementEnum.MILKYWAY_CHEVRON_LIGHT.render(stack, source, combinedLight);

        stack.translate(0, -2 * chevronOffset, 0);
        ElementEnum.MILKYWAY_CHEVRON_MOVING.render(stack, source, combinedLight);

        stack.popPose();

        ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTexture(rendererState.getBiomeOverlay()).render(stack, source, combinedLight);
        ElementEnum.MILKYWAY_CHEVRON_BACK.render(stack, source, combinedLight);

        stack.popPose();
    }

    @Override
    public boolean shouldRenderBackVortex() {
        return true;
    }

    @Override
    @Nonnull
    public Pair<Integer, Integer> getEventHorizonColor() {
        return Pair.of(0xff415063, 0xffffffff);
    }
}
