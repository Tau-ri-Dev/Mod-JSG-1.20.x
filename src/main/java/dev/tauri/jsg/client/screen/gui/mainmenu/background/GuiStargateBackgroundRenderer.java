package dev.tauri.jsg.client.screen.gui.mainmenu.background;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.api.client.screen.EnumMainMenuGateType;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.client.screen.gui.mainmenu.GuiCustomMainMenu;
import dev.tauri.jsg.client.screen.gui.mainmenu.MainMenuGateRenderer;
import dev.tauri.jsg.client.screen.gui.mainmenu.MainMenuTheme;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;

import static dev.tauri.jsg.core.client.screen.util.GuiHelper.*;

public class GuiStargateBackgroundRenderer extends PanoramaRenderer {
    private static final int BACKGROUND_CHANGE_ANIMATION_LENGTH = 60; //ticks
    private static final int BACKGROUND_STAY_TIME = 200; //ticks

    @NotNull
    public final MainMenuTheme theme;
    public final EnumMainMenuGateType gateType;

    public GuiStargateBackgroundRenderer() {
        super(null);
        theme = getTheme();
        var gateType = JSGConfig.General.mainMenuGateType.get();
        if (gateType == null || gateType == EnumMainMenuGateType.BY_ACT)
            this.gateType = EnumMainMenuGateType.byGateType(theme.gateType);
        else this.gateType = gateType;
    }

    protected MainMenuTheme getTheme() {
        MainMenuTheme.load();
        var t = MainMenuTheme.THEMES.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().getPriority())).toList();
        for (var e : t) {
            if (e.getValue().canBeChosen()) {
                return e.getValue();
            }
        }
        return MainMenuTheme.ACT_1;
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int width, int height, float zoomOutCoef, float partialTick) {
        currentStack = graphics.pose();
        GuiCustomMainMenu.poseStack = graphics.pose();
        GuiCustomMainMenu.graphics = graphics;
        double tick = JSGMinecraftHelper.getGUITicks() + partialTick;
        var poseStack = graphics.pose();

        RenderSystem.enableBlend();
        double currentImgCoef = ((tick % (double) BACKGROUND_STAY_TIME) / (double) BACKGROUND_STAY_TIME);
        double backgroundProgress = ((tick % ((double) BACKGROUND_STAY_TIME * (double) theme.getBackgrounds().size())) / ((double) BACKGROUND_STAY_TIME * (double) theme.getBackgrounds().size()));

        float scale = 1f + (float) ((Math.sin((tick / 400.0) * Math.PI) / 2.0 + 0.5f) * 0.2f);

        int currentBackground = (int) (Math.floor(tick / BACKGROUND_STAY_TIME) % theme.getBackgrounds().size());
        var nextBackground = ((currentBackground + 1) % theme.getBackgrounds().size());

        var backChangeTime = (BACKGROUND_CHANGE_ANIMATION_LENGTH / (double) BACKGROUND_STAY_TIME);
        float backgroundTransition = (float) (((currentImgCoef > (1 - backChangeTime)) ? ((currentImgCoef - (1.0 - backChangeTime)) / backChangeTime) : 0));
        if (backgroundTransition > 0.98f)
            currentBackground = nextBackground;

        poseStack.pushPose();
        int[] center = getCenterPos(0, 0, width, height);
        poseStack.translate(center[0], center[1], 0);
        poseStack.scale(scale, scale, 1);

        var w = width;
        var h = height;
        if ((h / (double) w) < (1016 / 1919.0))
            h = (int) ((1016.0 * w) / 1919.0);
        else
            w = (int) ((1919.0 * h) / 1016.0);

        // current background
        ITexture.bindTextureWithMc(theme.getBackground(currentBackground));

        drawScaledCustomSizeModalRect(-(width / 2), -(height / 2), 0, 0, 1919, 1016, w, h, 1920, 1017);

        if (backgroundTransition > 0) {
            // if transitioning - render next background as overlay
            ITexture.bindTextureWithMc(theme.getBackground(nextBackground));
            drawScaledCustomSizeModalRectColor(-(width / 2), -(height / 2), 0, 0, 1919, 1016, w, h, 1920, 1017, 1, 1, 1, backgroundTransition);
        }

        poseStack.popPose();

        // Back progress
        drawRect(0, height - 2, width, height, 0xFF6E6E6E);

        drawRect(0, height - 2, (int) ((double) width * backgroundProgress), height, 0xFFEBEBEB);
        RenderSystem.disableBlend();

        MainMenuGateRenderer.renderGate(gateType, (int) (width + 20 + ((1f - zoomOutCoef) * width)), getCenterPos(0, 0, width, height)[1], 45, tick);
    }
}
