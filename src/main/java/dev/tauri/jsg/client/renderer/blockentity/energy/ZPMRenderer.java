package dev.tauri.jsg.client.renderer.blockentity.energy;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.blockentity.energy.ZPMBE;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.state.energy.ZPMModelType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMRenderer implements BlockEntityRenderer<ZPMBE> {
    public ZPMRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(ZPMBE te, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        stack.pushPose();
        int level = te.getPowerLevel();
        if (level > 5) level = 5;
        if (level < 0) level = 0;
        stack.translate(0.32, 0.27, 0.43);
        renderZPM(stack, source, light, overlay, level, 0.8f, false, te.getZPMModelType());
        stack.popPose();
    }

    public static void renderZPM(@NotNull PoseStack stack, @NotNull MultiBufferSource source, int light, int overlay, int powerLevel, float size, boolean on, @Nullable ZPMModelType type) {
        if (powerLevel < 0) return;
        if (type == null) type = ZPMModelType.NORMAL;
        if (type == ZPMModelType.CREATIVE) powerLevel = 5;
        if (powerLevel > 5) powerLevel = 5;

        stack.pushPose();
        stack.scale(size, size, size);
        var textureLoader = JSGApi.JSG_LOADERS_HOLDER.texture();
        textureLoader.getTexture(textureLoader.getTextureResource("zpm/zpm" + powerLevel + (on ? "" : "_off") + type.suffix + ".png")).bindTexture();
        JSGApi.JSG_LOADERS_HOLDER.model().getModel(ElementEnum.ZPM.model).render(stack, source, light, true);
        stack.popPose();
    }
}
