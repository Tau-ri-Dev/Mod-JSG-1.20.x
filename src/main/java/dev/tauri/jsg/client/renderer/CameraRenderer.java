package dev.tauri.jsg.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.JSG;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JSG.MOD_ID)
public class CameraRenderer {
    private static final Map<ResourceLocation, RenderedFrame> STAGED_FRAMES = new HashMap<>();

    public static void stageFrame(ResourceLocation id, Entity source, int width, int height) {
        STAGED_FRAMES.put(id, new RenderedFrame(source, width, height));
    }

    public static void onFrameRendered(ResourceLocation id, Consumer<RenderTarget> consumer) {
        Optional.ofNullable(STAGED_FRAMES.get(id)).ifPresent((frame) -> {
            if (frame.rendered && frame.renderTarget != null) {
                consumer.accept(frame.renderTarget);
                frame.renderTarget.destroyBuffers();
                frame.rendered = false;
                STAGED_FRAMES.remove(id);
            }
        });
    }

    @SubscribeEvent

    public static void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY)
            return;

        var copy = new HashMap<>(STAGED_FRAMES);
        for (var entry : copy.entrySet()) {
            entry.getValue().renderPasses++;
            if (entry.getValue().renderPasses > 3) {
                STAGED_FRAMES.remove(entry.getKey());
                continue;
            }
            renderLevel(entry.getKey(), entry.getValue());
        }
    }

    private static int renderDepth = 0;

    protected static void renderLevel(ResourceLocation target, RenderedFrame frame) {
        if (renderDepth > 0) return;
        renderDepth++;
        var mc = Minecraft.getInstance();

        int i = mc.getWindow().getWidth();
        int j = mc.getWindow().getHeight();
        if (mc.player == null) return;

        var pWidth = frame.width;
        var pHeight = frame.height;
        var source = frame.source;

        RenderTarget rendertarget = new TextureTarget(pWidth, pHeight, true, Minecraft.ON_OSX);
        mc.gameRenderer.setRenderBlockOutline(false);
        var camera = mc.getCameraEntity();
        if (camera == null) return;

        try {

            mc.gameRenderer.setPanoramicMode(true);
            mc.getWindow().setWidth(pWidth);
            mc.getWindow().setHeight(pHeight);
            mc.setCameraEntity(source);
            mc.gameRenderer.setRenderHand(false);
            RenderSystem.clear(256, Minecraft.ON_OSX);

            rendertarget.bindWrite(true);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            mc.gameRenderer.renderLevel(1.0F, 0L, new PoseStack());
            frame.renderTarget = rendertarget;
            frame.rendered = true;
        } catch (Exception exception) {
            JSG.logger.error("Error while rendering level: ", exception);
        } finally {
            mc.gameRenderer.setPanoramicMode(false);
            mc.gameRenderer.setRenderBlockOutline(true);
            mc.gameRenderer.setRenderHand(true);
            mc.getWindow().setWidth(i);
            mc.getWindow().setHeight(j);
            mc.setCameraEntity(camera);
            //mc.levelRenderer.graphicsChanged();
            mc.getMainRenderTarget().bindWrite(true);
            renderDepth--;
        }
    }

    public static class RenderedFrame {
        public final Entity source;
        public final int width;
        public final int height;
        public boolean rendered = false;
        protected int renderPasses = 0;
        protected RenderTarget renderTarget;

        public RenderedFrame(Entity source, int width, int height) {
            this.source = source;
            this.width = width;
            this.height = height;
        }
    }
}
