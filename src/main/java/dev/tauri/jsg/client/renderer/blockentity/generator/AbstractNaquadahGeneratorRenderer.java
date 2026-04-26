package dev.tauri.jsg.client.renderer.blockentity.generator;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.common.blockentity.generator.AbstractNaquadahGeneratorBE;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import javax.annotation.ParametersAreNonnullByDefault;

public class AbstractNaquadahGeneratorRenderer<S extends AbstractNaquadahGeneratorRendererState> implements BlockEntityRenderer<AbstractNaquadahGeneratorBE> {
    public AbstractNaquadahGeneratorRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(AbstractNaquadahGeneratorBE generator, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

    }
}
