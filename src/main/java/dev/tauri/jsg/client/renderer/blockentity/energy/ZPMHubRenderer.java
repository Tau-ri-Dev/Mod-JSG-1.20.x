package dev.tauri.jsg.client.renderer.blockentity.energy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.blockentity.energy.ZPMHubBE;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.state.energy.ZPMModelType;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMHubRenderer implements BlockEntityRenderer<ZPMHubBE> {
    public ZPMHubRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(ZPMHubBE te, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        Level level = te.getLevel();
        if (level == null) return;

        long tick = level.getGameTime();
        double stage = ((double) (tick - te.animationStart)) / te.getAnimationLength();
        if (!te.isAnimating)
            stage = (te.isSlidingUp) ? 1 : 0; // 0 = down; 1 = up
        else if (!te.isSlidingUp)
            stage = 1 - stage;

        float plusY = (float) stage * 0.3f;

        stack.pushPose();
        stack.translate(0.5, 0, 0.5);
        float angle = te.facingAngle;
        stack.mulPose(Axis.YP.rotationDegrees(angle == 0 ? 180 : ((angle == -180 || angle == 180) ? 0 : angle)));
        renderMainObject(te, stack, source, light, overlay);

        for (int i = 0; i < 3; i++) {
            renderZPM(i, te, plusY, stack, source, light, overlay);
        }
        stack.popPose();
    }

    protected void renderMainObject(ZPMHubBE tile, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        boolean zpmsDown = !tile.isAnimating && !tile.isSlidingUp;
        stack.pushPose();
        stack.translate(0, 0.6, 0);
        stack.scale(0.025f, 0.025f, 0.025f);
        ElementEnum.ZPM_HUB.bindTexture().render(stack, source, light, overlay);

        int zpmHubLights = (int) Math.round(Math.abs(Math.sin(JSGMinecraftHelper.getClientTick() / 8f)) * 5) + 1;
        if ((tile.zpm1Level == -1) && (tile.zpm2Level == -1) && (tile.zpm3Level == -1)) zpmHubLights = 0;
        if (zpmHubLights > 5) zpmHubLights = 5;
        if (zpmHubLights < 0) zpmHubLights = 0;
        if (!zpmsDown) zpmHubLights = 0;

        var textureLoader = JSGApi.JSG_LOADERS_HOLDER.texture();
        textureLoader.getTexture(textureLoader.getTextureResource("zpm/hub/pg_lights" + zpmHubLights + ".jpg")).bindTexture();
        JSGApi.JSG_LOADERS_HOLDER.model().getModel(ElementEnum.ZPM_HUB_LIGHTS.model).render(stack, source, light, zpmHubLights > 0);
        stack.popPose();
    }

    protected void renderZPM(int zpmId, ZPMHubBE te, float plusY, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        int level = -1;
        ZPMModelType type = ZPMModelType.NORMAL;
        float zx = 0;
        float zy = 0.9f;
        float zz = 0;
        switch (zpmId) {
            case 0 -> {
                level = te.zpm1Level;
                type = te.zpm1Type;
                zx = 0.2f;
                zz = 0.18f;
            }
            case 1 -> {
                level = te.zpm2Level;
                type = te.zpm2Type;
                zx = -0.33f;
                zz = 0.18f;
            }
            case 2 -> {
                level = te.zpm3Level;
                type = te.zpm3Type;
                zx = -0.07f;
                zz = -0.27f;
            }
            default -> {
            }
        }
        stack.pushPose();
        stack.translate(zx, zy + plusY, zz);
        ZPMRenderer.renderZPM(stack, source, light, overlay, level, 0.4f, (!te.isSlidingUp && !te.isAnimating), type);
        stack.popPose();
    }
}
