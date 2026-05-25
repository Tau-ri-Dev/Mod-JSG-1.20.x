package dev.tauri.jsg.client.renderer.item.dialer.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.client.renderer.item.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.common.entity.camera.KinoEntity;
import dev.tauri.jsg.common.item.linkable.dialer.modes.UDKinoMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class UDKinoScreen implements IUniverseDialerScreen {

    @Override
    @ParametersAreNonnullByDefault
    public void render(ItemStack itemStack, CompoundTag compound, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        if (!compound.contains(UDKinoMode.C_LINKED_KINO)) {
            // kino not linked
            return;
        }
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) return;

        var partialTick = mc.getPartialTick();

        var kinoId = compound.getInt(UDKinoMode.C_LINKED_KINO);

        var kino = level.getEntity(kinoId);
        if (kino == null) return;

        //var cameraScreen = KinoRenderer.CAMERAS.get(kinoId);
        //if (cameraScreen == null) return;

        var oldCamera = mc.getCameraEntity();

        if (oldCamera == null || oldCamera instanceof KinoEntity) return;

        stack.pushPose();

        mc.setCameraEntity(kino);

        int x = 0;
        int y = 0;
        int w = 300;
        int h = 200;

        RenderSystem.enableScissor(x, y, x + w, y + h);

        mc.gameRenderer.render(
                partialTick,
                System.nanoTime(),
                true
        );

        RenderSystem.disableScissor();

        mc.setCameraEntity(oldCamera);

        stack.popPose();
    }
}
