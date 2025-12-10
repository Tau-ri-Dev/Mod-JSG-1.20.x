package dev.tauri.jsg.api.multistructure;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.api.client.renderer.BlockRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IMultiStructureRenderer<T extends IMultiStructureBE<? extends IMultiStructure>> {
    default void renderBuildingHelper(T tileEntity, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay) {
        if (tileEntity.getLevel() == null) return;
        stack.pushPose();
        RenderSystem.enableBlend();
        for (Map.Entry<BlockPos, BlockState> block : tileEntity.getMergeHelper().getBlocks().entrySet()) {
            if (!tileEntity.getLevel().getBlockState(block.getKey()).canBeReplaced()) continue;
            var pos = block.getKey();
            stack.pushPose();
            var newPos = pos.subtract(tileEntity.getBlockPos());
            BlockRenderer.renderBlock(tileEntity.getLevel(), pos, block.getValue(), newPos, stack, source, combinedLight, combinedOverlay, 0.5f);
            stack.popPose();
        }
        stack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
