package dev.tauri.jsg.screen.gui.mainmenu;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.client.screen.EnumMainMenuGateType;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.config.data.ProgressJSON;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.widget.IconButton;
import dev.tauri.jsg.core.client.screen.widget.base.JSGButtonClassic;
import dev.tauri.jsg.core.client.sound.FlybySoundInstance;
import dev.tauri.jsg.core.client.sound.JSGSoundHelperClient;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.common.util.TimeUtils;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.registry.JSGPositionedSounds;
import dev.tauri.jsg.screen.element.MainMenuMusicSlider;
import dev.tauri.jsg.util.updater.GetUpdate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ModListScreen;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static dev.tauri.jsg.core.client.screen.util.GuiHelper.*;
import static dev.tauri.jsg.core.client.sound.JSGSoundHelperClient.playPositionedFlyBySound;
import static dev.tauri.jsg.util.updater.GetUpdate.DOWNLOAD_URL_USER;
import static dev.tauri.jsg.util.updater.GetUpdate.openWebsiteToClient;

@OnlyIn(Dist.CLIENT)
public class GuiCustomMainMenu extends Screen {
    public static GuiGraphics graphics;
    public static PoseStack poseStack;

    public static final String WEBSITE = "https://justsgmod.eu/";

    public static final String WIKI_RAM_ALLOCATION_URL = "https://justsgmod.eu/wiki/?type=1.20category=general&topic=start#Allocating%20more%20RAM";
    public static final String GITHUB = "https://github.com/Tau-ri-Dev";
    public static final String JSG_RUNNING_TEXT = "Just Stargate Mod v" + JSG.MOD_VERSION.replaceAll(JSG.MC_VERSION + "-", "");

    public static final ResourceLocation LOGO_TAURI = JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/tauri_dev_logo.png");
    public static final boolean IS_WINTER = TimeUtils.isWinter();
    public static final ResourceLocation LOGO_JSG = IS_WINTER ? JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/jsg_logo_winter.png") : JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/jsg_logo.png");
    public static final Vector2i JSG_LOGO_SIZE = (IS_WINTER ? new Vector2i(1581, 844) : new Vector2i(1586, 603));
    public static final long FIRST_TRANSITION_LENGTH = 15 * 20; // in relative ticks
    public static final int PADDING = 10;
    public static final MainMenuNotifications NOTIFIER = MainMenuNotifications.getManager();
    private static final int BACKGROUND_CHANGE_ANIMATION_LENGTH = 60; //ticks
    private static final int BACKGROUND_STAY_TIME = 200; //ticks
    public static GetUpdate.UpdateResult UPDATER_RESULT = GetUpdate.LAST_UPDATE_RESULT;
    public static long menuDisplayed = -1;
    public static boolean menuWasDisplayed = false;
    public static double firstTransitionStart = 0;
    private static int currentButton = 0;
    private static boolean menuWasDisplayedIgnoredFPS = false;
    private static int updaterNotification = -1;

    public double tick;
    public boolean isMusicPlaying = false;
    public final EnumMainMenuGateType gateType;
    private long lastTipChange = 0;

    public List<JSGButtonClassic> buttonList = new ArrayList<>();
    public MainMenuMusicSlider musicSlider;

    private double backgroundScale = 1;
    private int currentBackground = 0;
    private float backgroundTransition = 0;
    private EnumMainMenuTips tipEnum = EnumMainMenuTips.random(null);

    @NotNull
    public final MainMenuTheme theme;

    public int lastMouseX;
    public int lastMouseY;
    public double lastMouseMove = -1;
    public boolean isZoomedOut = false;
    public double zoomInStart;
    public double zoomOutCoef = 0;

    public GuiCustomMainMenu() {
        super(Component.translatable("narrator.screen.title"));
        tick();
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

    public static Float musicVolume = null;

    public static float getMusicVolume() {
        if (musicVolume == null) {
            musicVolume = (float) JSGConfig.General.mainMenuMusicVolume.get();
        }
        return musicVolume;
    }

    public void playMusic(boolean play) {
        JSGSoundHelperClient.playMainMenuTheme(GuiCustomMainMenu::getMusicVolume, theme.getSoundTheme(), play);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void tick() {
        tick = JSGMinecraftHelper.getClientTickPrecise();
    }

    public void updateMusic() {
        if ((tick - menuDisplayed) <= FIRST_TRANSITION_LENGTH)
            return; // wait some seconds before first play

        if ((tick - menuDisplayed) > 20 * 30)
            isMusicPlaying = JSGSoundHelperClient.getRecordMainMenu(GuiCustomMainMenu::getMusicVolume, theme.getSoundTheme()).isPlaying();

        if (!isMusicPlaying && JSGConfig.General.playMusic.get()) {
            isMusicPlaying = true;
            playMusic(true);
        }
        if (!JSGConfig.General.playMusic.get() && isMusicPlaying)
            playMusic(false);
    }

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return GuiHelper.getCenterPos(rectWidth, rectHeight, width, height);
    }

    @Override
    public void init() {
        musicSlider = new MainMenuMusicSlider(width - PADDING - 20, height - PADDING - 20, 20, 20,
                Component.translatable("menu.music.volume"), getMusicVolume(), () -> musicVolume = (float) musicSlider.getValue());

        buttonList.clear();

        // buttons
        int id = -1;
        final int texSize = 128;
        buttonList.add(new IconButton(++id, 0, 0, MainMenuGateRenderer.getIconsTexture(gateType), texSize, 32, 32, 32, 32, false, Component.translatable("menu.singleplayer").getString()));
        buttonList.add(new IconButton(++id, 0, 0, MainMenuGateRenderer.getIconsTexture(gateType), texSize, 64, 32, 32, 32, false, Component.translatable("menu.multiplayer").getString()));
        buttonList.add(new IconButton(++id, 0, 0, MainMenuGateRenderer.getIconsTexture(gateType), texSize, 32, 0, 32, 32, false, Component.translatable("menu.options").getString()));
        buttonList.add(new IconButton(++id, 0, 0, MainMenuGateRenderer.getIconsTexture(gateType), texSize, 0, 32, 32, 32, false, Component.translatable("menu.quit").getString()));
        buttonList.add(new IconButton(++id, 0, 0, MainMenuGateRenderer.getIconsTexture(gateType), texSize, 0, 0, 32, 32, false, Component.translatable("menu.about").getString()));
        buttonList.add(new IconButton(++id, 0, 0, MainMenuGateRenderer.getIconsTexture(gateType), texSize, 64, 0, 32, 32, false, Component.translatable("fml.menu.mods").getString()));

        // ------------------------
        // Notifier about updates
        initUpdaterNotifier();

    }

    public int getButtonForDisplay(int offset) {
        if (offset > 0)
            return getNextButton(offset);
        if (offset < 0)
            return getPreviousButton(offset * -1);
        return currentButton;
    }

    private int getNextButton(int offset) {
        return (currentButton + offset) % buttonList.size();
    }

    private int getPreviousButton(int offset) {
        int id = currentButton;
        for (int i = 0; i < offset; i++) {
            id--;
            if (id < 0)
                id = (buttonList.size() - 1);
        }
        return id;
    }

    public void updateTip() {
        if ((tick - lastTipChange) < 30 * 20) return;
        lastTipChange = (long) tick;
        tipEnum = EnumMainMenuTips.random(tipEnum);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        GuiCustomMainMenu.graphics = graphics;
        GuiHelper.currentStack = GuiCustomMainMenu.graphics.pose();
        poseStack = graphics.pose();
        poseStack.pushPose();
        Lighting.setupForEntityInInventory();
        RenderSystem.enableDepthTest();
        font = Minecraft.getInstance().font;
        tick();
        if (menuDisplayed == -1) {
            menuDisplayed = (long) tick;
        }
        updateMusic();
        updateTip();

        if (NOTIFIER.currentDisplayed == null && ((tick - firstTransitionStart) > FIRST_TRANSITION_LENGTH)) {
            if (this.lastMouseY != mouseY || this.lastMouseX != mouseX || lastMouseMove <= 0) {
                this.lastMouseX = mouseX;
                this.lastMouseY = mouseY;
                lastMouseMove = tick;
            }

            if ((tick - lastMouseMove) > (20 * 20)) {
                isZoomedOut = true;
                var s = (((tick - lastMouseMove) - (20.0 * 20.0)) / 20.0);
                if (s > 1f) s = 1f;
                zoomOutCoef = Math.pow(Math.sin(s * (Math.PI / 2.0)), 6);
            } else if (isZoomedOut || zoomInStart > 0) {
                if (zoomInStart < 1) {
                    zoomInStart = tick;
                }
                isZoomedOut = false;

                var s = 1 - ((tick - zoomInStart) / 20.0);
                if (s > 1f) s = 1f;
                if (s < 0) {
                    s = 0;
                    zoomInStart = 0;
                }
                zoomOutCoef = Math.pow(Math.sin(s * (Math.PI / 2.0)), 6);
            }
        } else zoomOutCoef = 0;
        if (JSGConfig.General.enableLogo.get() && (tick - firstTransitionStart) <= (FIRST_TRANSITION_LENGTH + 20)) {
            var s = ((tick - (firstTransitionStart + FIRST_TRANSITION_LENGTH)) / 20.0);
            if (s > 1f) s = 1f;
            if (s < 0) s = 0;
            s = (1 - s);
            zoomOutCoef = Math.pow(Math.sin(s * (Math.PI / 2.0)), 6);
        }

        drawBackground();
        drawButtons(mouseX, mouseY);
        drawTitles();
        drawFg(mouseX, mouseY);

        if (!menuWasDisplayed) {
            firstTransitionStart = tick;
            if (Minecraft.getInstance().getFps() >= 25)
                menuWasDisplayed = true;
        }

        drawFirstAnimation();

        if (!menuWasDisplayedIgnoredFPS) {
            firstInit();
            menuWasDisplayedIgnoredFPS = true;
        }

        poseStack.popPose();
    }

    public static boolean soundIntroPlayed = false;

    public void drawFirstAnimation() {
        if (!JSGConfig.General.enableLogo.get()) return;
        double current = (tick - firstTransitionStart);
        if (current > FIRST_TRANSITION_LENGTH) {
            return;
        }

        double step = FIRST_TRANSITION_LENGTH / 5D;
        double alpha = 1 - Math.min(1, (Math.max(0, current - (4.5D * step)) / (step / 2)));

        poseStack.pushPose();
        poseStack.translate(0, 0, 52);
        poseStack.pushPose();
        RenderSystem.enableBlend();

        var introFrameInt = (int) current - (3 * 20);

        if (introFrameInt >= 0)
            drawRect(0, 0, width, height, new Color(255, 255, 255, (int) (255 * alpha)).getRGB());
        else
            drawRect(0, 0, width, height, new Color(0, 0, 0, 255).getRGB());

        if (!soundIntroPlayed && introFrameInt >= 0) {
            JSGSoundHelperClient.playMainMenuTheme(GuiCustomMainMenu::getMusicVolume, JSGPositionedSounds.MAINMENU_INTRO, true);
            soundIntroPlayed = true;
        }

        var introFrame = "";
        if (introFrameInt < 10) introFrame = "00" + introFrameInt;
        else if (introFrameInt < 100) introFrame = "0" + introFrameInt;
        else introFrame = "" + introFrameInt;

        if (introFrameInt < 1) introFrame = "001";
        if (introFrameInt <= 175 && introFrameInt > -1) {
            ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/wormhole/milkyway/ezgif-frame-" + introFrame + ".jpg"));
            drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, width, height, 1920, 1080);
        }
        poseStack.popPose();

        poseStack.pushPose();
        graphics.setColor(1, 1, 1, 1);

        var center = getCenterPos(0, 0);
        if (alpha > 0.75)
            drawCenteredString(font, "We are not associated with Mojang.", center[0], height - PADDING - 10, 0xFFFFFF, true);

        RenderSystem.disableBlend();
        poseStack.popPose();
        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return musicSlider.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return musicSlider.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean result = NOTIFIER.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && NOTIFIER.currentDisplayed == null) {
            if (musicSlider.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
            for (JSGButtonClassic guiButton : buttonList) {
                if (guiButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                    guiButton.playDownSound(getMinecraft().getSoundManager());
                    actionPerformed(guiButton);
                    result = true;
                }
            }

            // Clickable images/texts
            int sizeXTauri = width / 10;
            int sizeYTauri = (230 * sizeXTauri) / 411;
            if (isPointInRegion(PADDING, height - PADDING - sizeYTauri, sizeXTauri, sizeYTauri, (int) mouseX, (int) mouseY)) {
                openWebsiteToClient(GITHUB);
                result = true;
            }

            // JSG text
            int jsgSizeX = font.width(JSG_RUNNING_TEXT);
            int jsgSizeY = 10;

            // JSG Logo
            int sizeXJSG = (int) (width / 2.33);
            int sizeYJSG = (JSG_LOGO_SIZE.y() * sizeXJSG) / JSG_LOGO_SIZE.y();

            int[] center = getCenterPos(sizeXJSG, sizeYJSG);
            int x = (int) (center[0] * 0.25);
            int y = (int) (center[1] * 0.5);

            if (isPointInRegion(PADDING, PADDING, jsgSizeX, jsgSizeY, (int) mouseX, (int) mouseY) || isPointInRegion(x, y, sizeXJSG, sizeYJSG, (int) mouseX, (int) mouseY)) {
                openWebsiteToClient(WEBSITE);
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        super.mouseScrolled(x, y, amount);
        if (amount == 0) return false;
        int topY = Integer.MAX_VALUE;
        int leftX = Integer.MAX_VALUE;
        int bottomY = 0;
        int width = 0;
        for (JSGButtonClassic btn : buttonList) {
            if (!btn.visible) continue;
            if (btn.getY() < topY) topY = btn.getY();
            if (btn.getX() < leftX) leftX = btn.getX();

            if (btn.getWidth() > width) width = btn.getWidth();
            if ((btn.getY() + btn.getHeight()) > bottomY) bottomY = (btn.getY() + btn.getHeight());
        }
        if (isPointInRegion(leftX, topY, width, bottomY - topY, (int) x, (int) y)) {
            currentButton += ((amount < 0) ? 1 : -1);
            if (currentButton < 0) currentButton = (buttonList.size() - 1);
            if (currentButton >= buttonList.size()) currentButton = 0;
            return true;
        }
        return false;
    }


    protected void actionPerformed(@Nonnull JSGButtonClassic button) {
        if (minecraft == null) return;
        if (button.id < buttonList.size()) {
            switch (button.id) {
                // main menu screens
                case 0:
                    minecraft.setScreen(new SelectWorldScreen(this));
                    break;
                case 1:
                    minecraft.setScreen(new JoinMultiplayerScreen(this));
                    break;
                case 2:
                    minecraft.setScreen(new OptionsScreen(this, minecraft.options));
                    break;
                case 3:
                    minecraft.stop();
                    break;
                case 4:
                    openWebsiteToClient(WEBSITE);
                    break;
                case 5:
                    minecraft.setScreen(new ModListScreen(this));
                    break;
            }
        }
    }

    /**
     * Used to draw buttons
     */
    public void drawButtons(int mouseX, int mouseY) {
        poseStack.pushPose();
        poseStack.translate((zoomOutCoef * width), (zoomOutCoef * width), 50);
        musicSlider.renderWidget(graphics, mouseX, mouseY, 0);
        poseStack.popPose();

        // General buttons
        for (JSGButtonClassic button : buttonList) {
            // just make sure that Button will not be activated by any chance
            button.visible = false;
            button.active = (NOTIFIER.currentDisplayed == null);
            if (button instanceof IconButton)
                ((IconButton) button).texture = MainMenuGateRenderer.getIconsTexture(gateType);
        }
        for (int i = -2; i <= 2; i++) {
            RenderSystem.enableBlend();
            int btn = getButtonForDisplay(i);
            IconButton button = (IconButton) buttonList.get(btn);
            int x = (int) ((width - (button.width + PADDING * 2)) + (zoomOutCoef * width));
            int y = getCenterPos(button.width, button.height)[1] + (i * (button.height + 10));
            button.setX(x);
            button.setY(y);
            button.visible = true;
            button.drawButton(graphics, mouseX, mouseY);
            if (i == 0) {
                button.drawButton(graphics, mouseX, mouseY);
                button.drawButton(graphics, mouseX, mouseY);
                button.drawButton(graphics, mouseX, mouseY);

                String[] label = button.label;
                int labelHigh = label.length * 10;
                int syDefault = (button.getY() + (button.height / 2));
                int syStart = syDefault - (labelHigh / 2);
                int color = 0xffffff;
                for (int ii = 0; ii < label.length; ii++) {
                    graphics.drawString(font, label[ii], button.getX() - PADDING / 2 - font.width(label[ii]), syStart + ii * 10, color);
                    color = 0x404040;
                }
            }
            if (i == -1 || i == 1) {
                button.drawButton(graphics, mouseX, mouseY);
            }
            RenderSystem.disableBlend();
        }
    }

    public void drawBackground() {
        double startTick = tick;
        if (JSGConfig.General.enableLogo.get())
            startTick = Math.max(0, (tick - firstTransitionStart - FIRST_TRANSITION_LENGTH));

        RenderSystem.enableBlend();
        double currentImgCoef = ((startTick % (double) BACKGROUND_STAY_TIME) / (double) BACKGROUND_STAY_TIME);
        double backgroundProgress = ((startTick % ((double) BACKGROUND_STAY_TIME * (double) theme.getBackgrounds().size())) / ((double) BACKGROUND_STAY_TIME * (double) theme.getBackgrounds().size()));

        float scale = 1f + (float) ((Math.sin((startTick / 400.0) * Math.PI) / 2.0 + 0.5f) * 0.2f);

        currentBackground = (int) (Math.floor(startTick / BACKGROUND_STAY_TIME) % theme.getBackgrounds().size());
        var nextBackground = ((currentBackground + 1) % theme.getBackgrounds().size());

        var backChangeTime = (BACKGROUND_CHANGE_ANIMATION_LENGTH / (double) BACKGROUND_STAY_TIME);
        backgroundTransition = (float) (((currentImgCoef > (1 - backChangeTime)) ? ((currentImgCoef - (1.0 - backChangeTime)) / backChangeTime) : 0));
        if (backgroundTransition > 0.98f)
            currentBackground = nextBackground;

        poseStack.pushPose();
        int[] center = getCenterPos(0, 0);
        poseStack.translate(center[0], center[1], 0);
        poseStack.scale(scale, scale, 1);
        backgroundScale = scale;

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

        MainMenuGateRenderer.renderGate(gateType, (int) (width + 20 + (zoomOutCoef * width)), getCenterPos(0, 0)[1], 45, tick);
    }

    /**
     * Used to draw texts on the screen
     */
    public void drawTitles() {
        if (JSGConfig.General.mainMenuDebugMode.get()) {
            int[] center = getCenterPos(0, 0);
            center[1] -= 30;
            // Debug
            int i = 0;
            graphics.drawString(font, "FPS: " + Minecraft.getInstance().getFps(), PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "width: " + width, PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "height: " + height, PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "time: " + String.format("%.4f", tick), PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "timeHere: " + String.format("%.4f", (tick % BACKGROUND_STAY_TIME)), PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "backgroundScale: " + String.format("%.4f", backgroundScale), PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "backgroundTransition: " + String.format("%.4f", backgroundTransition), PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "currentBackground: " + currentBackground, PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "gateType: " + gateType.toString(), PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "currentButton: " + currentButton, PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "updater: (Status: " + UPDATER_RESULT.result.toString() + "; Got: " + UPDATER_RESULT.response + ")", PADDING, center[1] + (10 * (++i)), 0xffffff);
            graphics.drawString(font, "currentAct: " + ProgressJSON.get().currentActId, PADDING, center[1] + (10 * (++i)), 0xffffff);
        }

        poseStack.pushPose();
        poseStack.translate(-zoomOutCoef * width, 0, 0);
        graphics.drawString(font, JSG_RUNNING_TEXT, PADDING, PADDING, 0xffffff);
        graphics.drawString(font, "Running on Minecraft Java " + JSG.MC_VERSION, PADDING, PADDING + 10, 0xffffff);
        poseStack.popPose();


        int sizeXTauri = width / 10;
        int sizeYTauri = (230 * sizeXTauri) / 411;

        int sizeXJSG = (int) (width / 2.33);
        int sizeYJSG = (JSG_LOGO_SIZE.y() * sizeXJSG) / JSG_LOGO_SIZE.x();

        int[] center = getCenterPos(sizeXJSG, sizeYJSG);
        int x = (int) (center[0] * 0.25);
        int y = (int) (center[1] * 0.5);

        RenderSystem.enableBlend();

        // Tauri dev logo
        ITexture.bindTextureWithMc(LOGO_TAURI);
        drawScaledCustomSizeModalRect(PADDING, height - PADDING - sizeYTauri, 0, 0, 411, 230, sizeXTauri, sizeYTauri, 410, 229);

        // JSG logo - main
        poseStack.pushPose();
        poseStack.translate(-zoomOutCoef * width, 0, 0);
        ITexture.bindTextureWithMc(LOGO_JSG);
        drawScaledCustomSizeModalRect(x, y, 0, 0, JSG_LOGO_SIZE.x(), JSG_LOGO_SIZE.y(), sizeXJSG, sizeYJSG, JSG_LOGO_SIZE.x(), JSG_LOGO_SIZE.y());
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, zoomOutCoef * height, 0);
        poseStack.translate(0, 0, 51);
        String[] tip = tipEnum.text;

        int startY = -(tip.length * 10);
        int i = 0;
        center = getCenterPos(0, 0);
        for (String s : tip) {
            drawCenteredString(font, Component.translatable(s).getString(), center[0], height - PADDING + startY + i * 10, 0xCEAD28, true);
            i++;
        }
        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    /**
     * Used to draw hovered texts & updater notification
     */
    public void drawFg(int mouseX, int mouseY) {
        NOTIFIER.update();
        if (NOTIFIER.currentDisplayed == null) {
            for (JSGButtonClassic b : buttonList) {
                if (b instanceof IconButton && b.visible && b.id != currentButton)
                    ((IconButton) b).drawFg(mouseX, mouseY);
            }

            int sizeXTauri = width / 10;
            int sizeYTauri = (230 * sizeXTauri) / 411;
            if (isPointInRegion(PADDING, height - PADDING - sizeYTauri, sizeXTauri, sizeYTauri, mouseX, mouseY)) {
                List<Component> authors = List.of(Component.literal("Click to open Tau'ri Dev github").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
                graphics.renderTooltip(font, authors, Optional.empty(), mouseX, mouseY);
            }
        } else {
            poseStack.pushPose();
            poseStack.translate(0, 0, 50);
            drawGradientRect(poseStack.last().pose(), 0, 0, 0, width, height, -1072689136, -804253680);
            NOTIFIER.render(mouseX, mouseY, width, height, this);
            poseStack.popPose();
        }
    }

    public void drawCenteredString(Font fontRendererIn, @Nonnull String text, int x, int y, int color, boolean shadow) {
        if (shadow) {
            graphics.drawCenteredString(fontRendererIn, text, x, y, color);
            return;
        }
        graphics.drawString(fontRendererIn, text, (x - fontRendererIn.width(text) / 2), y, color, false);
    }

    public int kinoXStart = -1;
    public int kinoYStart = -1;

    public int kinoXEnd = -1;
    public int kinoYEnd = -1;

    public double kinoAngle = 0;

    public boolean renderKino = false;
    public double kinoRenderStart = 0;
    public static final double KINO_ANIMATION_LENGTH = 80; // ticks

    public static FlybySoundInstance kinoSound;

    @SuppressWarnings("unused")
    public void drawKinoAnimation() {
        double anim = (tick - kinoRenderStart);
        if (!renderKino) {
            if (kinoSound != null) {
                kinoSound.currentPosition = null;
                kinoSound.tick();
            }
            kinoSound = null;
            if (anim > KINO_ANIMATION_LENGTH * 5 && Math.random() < 0.003f) {
                renderKino = true;
                kinoRenderStart = tick;
                double angle = Math.pow(Math.random(), 2) * 2 * Math.PI;
                kinoXStart = (int) (Math.cos(angle) * 2.5 + 1) * (width / 2);
                kinoYStart = (int) (Math.sin(angle) * 2.5 + 1) * (height / 2);
                angle = angle + Math.PI * Math.random() * 1.5;
                kinoAngle = angle;
                kinoXEnd = (int) (Math.cos(angle) * 2.5 + 1) * (width / 2);
                kinoYEnd = (int) (Math.sin(angle) * 2.5 + 1) * (height / 2);
            }
            return;
        }
        if (anim > KINO_ANIMATION_LENGTH) {
            renderKino = false;
            return;
        }

        int kinoX = (int) (kinoXStart + (kinoXEnd - kinoXStart) * (anim / KINO_ANIMATION_LENGTH));
        int kinoY = (int) (kinoYStart + (kinoYEnd - kinoYStart) * (anim / KINO_ANIMATION_LENGTH));


        BlockPos pos = JSG.lastPlayerPosInWorld.offset(new BlockPos(kinoX, kinoY, 0));

        if (kinoSound == null) {
            kinoSound = playPositionedFlyBySound(pos, JSGPositionedSounds.KINO_FLYBY, 1, 1.3f);
            kinoSound.centerPos = JSG.lastPlayerPosInWorld;
        } else {
            kinoSound.currentPosition = pos;
            kinoSound.tick();
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) kinoAngle));
        RenderSystem.enableBlend();
        graphics.setColor(1, 1, 1, 1);
        ITexture.bindTextureWithMc(LOGO_TAURI);
        drawScaledCustomSizeModalRect(kinoX, kinoY, 0, 0, 410, 229, 100, 50, 411, 230);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    public void initUpdaterNotifier() {
        if (UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.NEWER_AVAILABLE || UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.ERROR) {
            boolean error = UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.ERROR;
            if (updaterNotification == -1 || NOTIFIER.get(updaterNotification) == null) {
                if (!error) {
                    updaterNotification = NOTIFIER.add(new MainMenuNotifications.Notification(new ArrayList<>() {{
                        // Download (20)
                        String update = Component.translatable("menu.updater.download").getString();
                        int width = font.width(update) + 20;
                        add(new JSGButtonClassic(MainMenuNotifications.BUTTONS_ID_START, -width - (PADDING / 2), 0, width, 20, update));

                        // Close (21)
                        update = Component.translatable("menu.updater.close").getString();
                        width = font.width(update) + 20;
                        add(new JSGButtonClassic(MainMenuNotifications.BUTTONS_ID_START + 1, (PADDING / 2), 0, width, 20, update));
                    }}, "New update is available!",
                            "",
                            "",
                            "You can update to version " + UPDATER_RESULT.response,
                            "It is highly recommended to update to this version!",
                            "Some dangerous bugs should be fixed in this version."
                    ) {
                        @Override
                        public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, Screen parentScreen) {
                            super.renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
                            int xCenter = getCenterPos(0, 0)[0];

                            buttons.get(0).setY(rectY + MainMenuNotifications.BACKGROUND_HEIGHT - 30);
                            buttons.get(0).setX(xCenter - buttons.get(0).getWidth() - (PADDING / 2));
                            buttons.get(0).render(graphics, mouseX, mouseY, 0);
                            buttons.get(1).setX(xCenter + (PADDING / 2));
                            buttons.get(1).setY(buttons.get(0).getY());
                            buttons.get(1).render(graphics, mouseX, mouseY, 0);
                        }

                        @Override
                        public void actionPerformed(@Nonnull JSGButtonClassic button) {
                            switch (button.id) {
                                case MainMenuNotifications.BUTTONS_ID_START:
                                    openWebsiteToClient(DOWNLOAD_URL_USER);
                                    break;
                                case MainMenuNotifications.BUTTONS_ID_START + 1:
                                    dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                } else {
                    updaterNotification = NOTIFIER.add(new MainMenuNotifications.Notification(new ArrayList<>() {{
                        // Close (21)
                        String close = Component.translatable("menu.updater.close").getString();
                        int width = font.width(close) + 20;
                        add(new JSGButtonClassic(MainMenuNotifications.BUTTONS_ID_START + 1, -width / 2, 0, width, 20, close));
                    }}, "Error while checking update!",
                            UPDATER_RESULT.response,
                            "",
                            "Can not get response from the server!",
                            "Please check your internet connection.",
                            "Problem can also be on our side."
                    ) {
                        @Override
                        public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, Screen parentScreen) {
                            super.renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
                            int xCenter = getCenterPos(0, 0)[0];

                            buttons.get(0).setY(rectY + MainMenuNotifications.BACKGROUND_HEIGHT - 30);
                            buttons.get(0).setX(xCenter - (buttons.get(0).getWidth() / 2));
                            buttons.get(0).render(graphics, mouseX, mouseY, 0);
                        }

                        @Override
                        public void actionPerformed(@Nonnull JSGButtonClassic button) {
                            if (button.id == MainMenuNotifications.BUTTONS_ID_START + 1) {
                                dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    public void firstInit() {

        if (JSG.memoryTotal < 2L * 1024 * 1024 * 1024) {
            // Insert notification about low RAM
            NOTIFIER.add(new MainMenuNotifications.Notification(new ArrayList<>() {{
                // Wiki
                String update = Component.translatable("menu.ram.help").getString();
                int width = font.width(update) + 20;
                add(new JSGButtonClassic(MainMenuNotifications.BUTTONS_ID_START + 10, -width - (PADDING / 2), 0, width, 20, update));

                // Close
                update = Component.translatable("menu.updater.close").getString();
                width = font.width(update) + 20;
                add(new JSGButtonClassic(MainMenuNotifications.BUTTONS_ID_START + 11, (PADDING / 2), 0, width, 20, update));
            }}, "Allocate more RAM!",
                    "",
                    "Recommended minimum RAM for JSG mod is 2GB!",
                    "By ignoring this fact, you can",
                    "run into troubles with this mod."
            ) {
                @Override
                public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, Screen parentScreen) {
                    super.renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
                    int xCenter = getCenterPos(0, 0)[0];

                    buttons.get(0).setY(rectY + MainMenuNotifications.BACKGROUND_HEIGHT - 30);
                    buttons.get(0).setX(xCenter - buttons.get(0).getWidth() - (PADDING / 2));
                    buttons.get(0).render(graphics, mouseX, mouseY, 0);
                    buttons.get(1).setX(xCenter + (PADDING / 2));
                    buttons.get(1).setY(buttons.get(0).getY());
                    buttons.get(1).render(graphics, mouseX, mouseY, 0);
                }

                @Override
                public void actionPerformed(@Nonnull JSGButtonClassic button) {
                    switch (button.id) {
                        case MainMenuNotifications.BUTTONS_ID_START + 10:
                            openWebsiteToClient(WIKI_RAM_ALLOCATION_URL);
                            break;
                        case MainMenuNotifications.BUTTONS_ID_START + 11:
                            dismiss();
                            break;
                        default:
                            break;
                    }
                }
            });
            NOTIFIER.update();
        }
    }
}
