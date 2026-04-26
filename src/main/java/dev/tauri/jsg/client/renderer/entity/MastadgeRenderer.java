package dev.tauri.jsg.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.entity.animal.MastadgeEntity;
import dev.tauri.jsg.common.entity.client.JSGEntityModelLayer;
import dev.tauri.jsg.common.entity.client.MastadgeModel;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MastadgeRenderer extends MobRenderer<MastadgeEntity, MastadgeModel<MastadgeEntity>> {
    public MastadgeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MastadgeModel<>(pContext.bakeLayer(JSGEntityModelLayer.MASTADGE_LAYER)), 2f);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public ResourceLocation getTextureLocation(MastadgeEntity pEntity) {
        return JSGMapping.rl(JSG.MOD_ID, "textures/entity/mastadge.png");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MastadgeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack stack, MultiBufferSource buffer, int pPackedLight) {
        stack.pushPose();
        if (pEntity.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, stack, buffer, pPackedLight);
        stack.popPose();
    }
}
