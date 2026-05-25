package dev.tauri.jsg.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.common.entity.camera.KinoEntity;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KinoRenderer extends EntityRenderer<KinoEntity> {

    public KinoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public boolean shouldRender(KinoEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ);
    }

    @Override
    public void render(KinoEntity kino, float yaw, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light) {
        stack.pushPose();

        stack.translate(0, kino.getBbHeight() / 2f, 0);

        var scale = 0.0125f;
        stack.scale(scale, scale, scale);
        ElementEnum.KINO.bindTexture().render(stack, bufferSource, light);

        stack.popPose();

        // render name tag
        super.render(kino, yaw, partialTick, stack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(KinoEntity pEntity) {
        return JSGMapping.rl("empty");
    }
}
