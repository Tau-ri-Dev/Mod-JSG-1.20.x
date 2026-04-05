package dev.tauri.jsg.effect;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.sound.JSGSoundHelperClient;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.registry.JSGPositionedSounds;
import dev.tauri.jsg.registry.JSGSoundEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.tauri.jsg.core.client.screen.util.GuiHelper.drawScaledCustomSizeModalRect;

/**
 * CLIENT SIDE
 */

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class StargateWormholeEffect {
    public static long playingStarted = -1;
    public static long playingWhiteStarted = -1;
    public static boolean isPlaying = false;
    public static boolean isStopping = false;
    public static boolean isPlayingWhite = false;
    public static float lastSpeed = -1;

    public static boolean isFlying = false;

    public static StargateWormholeType type = StargateWormholeType.MILKYWAY;
    public static int length;

    private static BlockPos travelSoundPos = null;

    public static void play(StargateWormholeType type, int length) {
        if (Minecraft.getInstance().player == null) return;
        StargateWormholeEffect.type = type;
        StargateWormholeEffect.length = length;
        var p = Minecraft.getInstance().player;
        //Minecraft.getInstance().getSoundManager().stop();
        isFlying = p.getAbilities().flying;
        lastSpeed = p.getAbilities().getFlyingSpeed();
        p.getAbilities().setFlyingSpeed(0);
        playingStarted = JSGMinecraftHelper.getPlayerTickClientSide();
        Vec3 v = p.position();
        travelSoundPos = new BlockPos((int) v.x, (int) v.y, (int) v.z);
        JSGSoundHelperClient.playPositionedSoundClientSide(travelSoundPos, JSGPositionedSounds.WORMHOLE_TRAVEL, true);
        isPlaying = true;
        isPlayingWhite = true;
        isStopping = false;
        playingWhiteStarted = playingStarted;
    }

    public static void stop() {
        isStopping = true;
        playingStarted = -1;
        playingWhiteStarted = JSGMinecraftHelper.getPlayerTickClientSide();
        isPlaying = false;
        isPlayingWhite = true;
        if (Minecraft.getInstance().player == null) return;
        var p = Minecraft.getInstance().player;
        if (lastSpeed > -1)
            p.getAbilities().setFlyingSpeed(lastSpeed);
        p.getAbilities().flying = isFlying;
        p.onUpdateAbilities();
        Vec3 v = p.position();
        JSGSoundHelperClient.playPositionedSoundClientSide(travelSoundPos, JSGPositionedSounds.WORMHOLE_TRAVEL, false);
        JSGSoundHelperClient.playSoundEventClientSide(new BlockPos((int) v.x, (int) v.y, (int) v.z), JSGSoundEvents.WORMHOLE_GO, 1, 1);
    }

    public static int getCurrentAnimationState() {
        if (playingStarted == -1)
            return 0;
        int tick = (int) (JSGMinecraftHelper.getPlayerTickClientSide() - playingStarted);
        int l = (length * 20);
        var framesCount = (type.endFrame - type.startFrame);
        var startFrameOffset = (framesCount - l);
        while (startFrameOffset < 0) {
            startFrameOffset += framesCount;
        }
        var frameOffset = (startFrameOffset + tick) % framesCount;
        var frame = type.startFrame + frameOffset;
        if (tick > l) {
            stop();
        }
        return Math.min(frame, type.endFrame);
    }

    @SubscribeEvent
    public static void onMouseWheel(InputEvent.MouseScrollingEvent event) {
        if (isPlaying) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (isPlaying) {
            InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());
            KeyMapping.set(key, false);
        }
    }

    @SubscribeEvent
    public static void onSoundPlay(PlaySoundEvent event) {
        if (!isPlaying) return;
        if (event.getOriginalSound().getLocation() == JSGPositionedSounds.WORMHOLE_TRAVEL.event.get().getLocation())
            return;
        event.setSound(null);
    }

    @SuppressWarnings("unused")
    public static void render(ForgeGui forgeGui, GuiGraphics graphics, float partialTicks, int packedLight, int packedOverlay) {
        if (!isPlayingWhite && !isPlaying) return;
        PoseStack stack = graphics.pose();
        GuiHelper.currentStack = graphics.pose();
        RenderSystem.enableBlend();
        stack.pushPose();
        stack.translate(0, 0, 100);
        float whiteCoef = getWhiteCoef();
        if (isPlaying && (!isPlayingWhite || whiteCoef >= 0.29289f)) {
            renderWormholeFrame(graphics, getCurrentAnimationState());
        }
        if (isStopping && isPlayingWhite && whiteCoef <= 0.29289f) {
            renderWormholeFrame(graphics, type.endFrame);
        }
        if (isPlayingWhite) {
            renderWhiteFlash(graphics, getWhiteAlpha(whiteCoef));
        }
        stack.popPose();
    }

    private static void renderWormholeFrame(GuiGraphics graphics, int frame) {
        String introFrame;
        if (frame < 10) introFrame = "00" + frame;
        else if (frame < 100) introFrame = "0" + frame;
        else introFrame = "" + frame;

        ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/wormhole/" + type.name().toLowerCase() + "/ezgif-frame-" + introFrame + ".jpg"));
        drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, graphics.guiWidth(), graphics.guiHeight(), 1920, 1080);
    }

    private static float getWhiteCoef() {
        if (!isPlayingWhite) return 0;
        var alphaCoef = (JSGMinecraftHelper.getPlayerTickClientSide() - playingWhiteStarted) / 20f;
        if (alphaCoef > 1f) {
            alphaCoef = 1f;
            isPlayingWhite = false;
        }
        return alphaCoef;
    }

    private static float getWhiteAlpha(float alphaCoef) {
        return (float) Math.sin((1 - alphaCoef) * (1 - alphaCoef) * Math.PI);
    }

    private static void renderWhiteFlash(GuiGraphics graphics, float alpha) {
        GuiHelper.currentStack = graphics.pose();
        GuiHelper.drawRect(0, 0, graphics.guiWidth(), graphics.guiHeight(), 0x00ffffff | ((int) (alpha * 0xff) << 24));
    }
}
