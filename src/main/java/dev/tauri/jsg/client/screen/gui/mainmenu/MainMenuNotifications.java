package dev.tauri.jsg.client.screen.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.widget.base.JSGButtonClassic;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

import static dev.tauri.jsg.client.screen.gui.mainmenu.GuiCustomMainMenu.poseStack;
import static dev.tauri.jsg.core.client.screen.util.GuiHelper.drawModalRectWithCustomSizedTexture;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class MainMenuNotifications {
    public static class Notification {
        public boolean dismissed;
        public final String[] lines;
        public final List<JSGButtonClassic> buttons;

        public Notification(List<JSGButtonClassic> buttons, String... text) {
            this.lines = text;
            this.buttons = buttons;
            dismissed = false;
        }

        public void dismiss() {
            dismissed = true;
        }

        public void actionPerformed(@Nonnull JSGButtonClassic button) {
        }

        public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, Screen parentScreen) {
            renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
        }

        public void renderText(int mouseX, int mouseY, int width, int height, int rectX, int rectY, Screen parentScreen) {
            int centerX = getManager().getCenterPos(0, 0)[0];
            int i = 0;
            for (String s : lines) {
                if (parentScreen instanceof GuiCustomMainMenu)
                    ((GuiCustomMainMenu) parentScreen).drawCenteredString(parentScreen.getMinecraft().font, s, centerX, rectY + 18 + (10 * i), 0x404040, false);
                else
                    GuiCustomMainMenu.graphics.drawString(parentScreen.getMinecraft().font, s, centerX - (parentScreen.getMinecraft().font.width(s) / 2), rectY + 18 + (10 * i), 0x404040);
                i++;
            }
        }
    }

    // Static
    public static final MainMenuNotifications INSTANCE = new MainMenuNotifications();
    private static final ResourceLocation NOTIFICATION_TEXTURE = JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/popup.png");
    public static final int BUTTONS_ID_START = 40;

    public static MainMenuNotifications getManager() {
        return INSTANCE;
    }

    // Relative
    private final HashMap<Integer, Notification> NOTIFICATIONS = new HashMap<>();
    private int id = 0;

    public int getId() {
        return id;
    }

    public int add(Notification notification) {
        NOTIFICATIONS.put(getId(), notification);
        return id++;
    }

    @Nullable
    public Notification get(int id) {
        return NOTIFICATIONS.get(id);
    }

    @Nullable
    public Notification getFirstToDisplay() {
        for (Notification n : NOTIFICATIONS.values()) {
            if (!n.dismissed) return n;
        }
        return null;
    }

    public Notification currentDisplayed = getFirstToDisplay();

    public int width = 0;
    public int height = 0;
    public Minecraft mc = Minecraft.getInstance();

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return new int[]{((width - rectWidth) / 2), ((height - rectHeight) / 2)};
    }

    public void update() {
        currentDisplayed = getFirstToDisplay();
    }

    public static final int BACKGROUND_WIDTH = 300;
    public static final int BACKGROUND_HEIGHT = 140;

    public void render(int mouseX, int mouseY, int winWidth, int winHeight, Screen parentScreen) {
        this.width = winWidth;
        this.height = winHeight;
        if (currentDisplayed == null) return;
        poseStack.pushPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        int[] center = getCenterPos(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        int x = center[0];
        int y = center[1];

        // Background
        ITexture.bindTextureWithMc(NOTIFICATION_TEXTURE);
        GuiHelper.currentStack = GuiCustomMainMenu.graphics.pose();
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        currentDisplayed.render(mouseX, mouseY, width, height, x, y, parentScreen);

        poseStack.popPose();

    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (currentDisplayed == null) return false;
        if (mouseButton != 0) return false;
        for (JSGButtonClassic guiButton : currentDisplayed.buttons) {
            if (guiButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                guiButton.playDownSound(mc.getSoundManager());
                currentDisplayed.actionPerformed(guiButton);
                return true;
            }
        }
        return false;
    }
}
