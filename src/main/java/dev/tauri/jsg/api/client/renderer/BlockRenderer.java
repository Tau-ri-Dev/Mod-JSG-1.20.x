package dev.tauri.jsg.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.client.renderer.LevelRenderer.DIRECTIONS;

public class BlockRenderer {

    public static void renderBlock(Level level, BlockPos absoluteWorldPos, BlockState blockState, BlockPos pos, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        renderBlock(level, absoluteWorldPos, blockState, pos, stack, source, light, overlay, 1);
    }

    @SuppressWarnings("deprecation")
    public static void renderBlock(Level level, BlockPos absoluteWorldPos, BlockState blockState, BlockPos pos, PoseStack stack, MultiBufferSource source, int light, int overlay, float alpha) {
        var rendershape = blockState.getRenderShape();
        stack.pushPose();
        stack.translate(pos.getX(), pos.getY(), pos.getZ());
        if (rendershape == RenderShape.MODEL) {
            var renderer = Minecraft.getInstance().getBlockRenderer();

            BakedModel bakedmodel = renderer.getBlockModel(blockState);
            var modelData = ModelData.EMPTY;
            int color = Minecraft.getInstance().getBlockColors().getColor(blockState, level, absoluteWorldPos, 0);
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(blockState, RandomSource.create(42), modelData))
                renderModel(stack.last(), source.getBuffer(RenderType.translucent()), blockState, bakedmodel, red, green, blue, alpha, light, overlay, modelData, rt);
        } else
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, stack, source, light, overlay);
        stack.popPose();
    }

    public static void renderModel(PoseStack.Pose pPose, VertexConsumer pConsumer, @Nullable BlockState pState, BakedModel pModel, float pRed, float pGreen, float pBlue, float alpha, int pPackedLight, int pPackedOverlay, net.minecraftforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType) {
        RandomSource randomsource = RandomSource.create();
        for (Direction direction : DIRECTIONS) {
            randomsource.setSeed(42L);
            renderQuadList(pPose, pConsumer, pRed, pGreen, pBlue, alpha, pModel.getQuads(pState, direction, randomsource, modelData, renderType), pPackedLight, pPackedOverlay);
        }

        randomsource.setSeed(42L);
        renderQuadList(pPose, pConsumer, pRed, pGreen, pBlue, alpha, pModel.getQuads(pState, null, randomsource, modelData, renderType), pPackedLight, pPackedOverlay);
    }

    private static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float alpha, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay) {
        for (BakedQuad bakedquad : pQuads) {
            float f;
            float f1;
            float f2;
            if (bakedquad.isTinted()) {
                f = Mth.clamp(pRed, 0.0F, 1.0F);
                f1 = Mth.clamp(pGreen, 0.0F, 1.0F);
                f2 = Mth.clamp(pBlue, 0.0F, 1.0F);
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }
            pConsumer.putBulkData(pPose, bakedquad, new float[]{1, 1, 1, 1}, f, f1, f2, alpha, new int[]{pPackedLight, pPackedLight, pPackedLight, pPackedLight}, pPackedOverlay, false);
        }

    }


    public enum FluidTextureType {
        STILL,
        FLOWING
    }

    @Nullable
    public static TextureAtlasSprite getFluidTexture(FluidStack fluid, FluidTextureType type) {
        try {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
            if (type == FluidTextureType.STILL)
                return getSprite(props.getStillTexture(fluid));
            return getSprite(props.getFlowingTexture(fluid));
        } catch (Exception ignored) {

        }
        return null;
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }
}
