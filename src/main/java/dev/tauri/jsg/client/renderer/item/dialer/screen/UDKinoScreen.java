package dev.tauri.jsg.client.renderer.item.dialer.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.client.renderer.CameraRenderer;
import dev.tauri.jsg.client.renderer.item.dialer.IUniverseDialerScreen;
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
        if (itemDisplayContext == ItemDisplayContext.GUI) return;
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) return;

        var partialTick = mc.getPartialTick();

        var kinoId = compound.getInt(UDKinoMode.C_LINKED_KINO);

        var kino = level.getEntity(kinoId);
        if (kino == null) return;
        stack.pushPose();
        var scale = 0.1f;
        stack.scale(scale, scale, scale);
        CameraRenderer.onFrameRendered(JSG.rl("kino_" + kinoId), (frame) -> {
            frame.blitToScreen(mc.getWindow().getWidth() / 2, mc.getWindow().getHeight() / 2);
        });
        CameraRenderer.stageFrame(JSG.rl("kino_" + kinoId), kino, mc.getWindow().getWidth(), mc.getWindow().getHeight());

        stack.popPose();
    }
}
