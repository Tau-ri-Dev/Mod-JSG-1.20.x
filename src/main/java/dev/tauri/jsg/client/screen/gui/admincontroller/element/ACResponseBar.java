package dev.tauri.jsg.client.screen.gui.admincontroller.element;

import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ACResponseBar extends AbstractWidget {
    public static final int ANIMATION_LENGTH = 10;

    protected final ResourceLocation backgroundTexture;
    protected final int u;
    protected final int v;
    protected final int texWidth;
    protected final int texHeight;

    protected final int startPadding;
    protected final int endPadding;

    protected final int maxWidth;
    protected final int visibleX;

    protected long animationStart;
    protected long stayTime = -1;
    protected boolean hide;

    protected Component title;

    public ACResponseBar(int visibleX, int y, int maxWidth, int height, ResourceLocation backgroundTexture, int u, int v, int texWidth, int texHeight, int startPadding, int endPadding) {
        super(visibleX, y, maxWidth, height, Component.empty());
        this.maxWidth = maxWidth;
        this.visibleX = visibleX;
        this.hide = true;
        this.backgroundTexture = backgroundTexture;
        this.u = u;
        this.v = v;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.startPadding = startPadding;
        this.endPadding = endPadding;
    }

    public void show(Component title) {
        this.title = title;
        if (!hide) return;
        hide = false;
        animationStart = JSGMinecraftHelper.getPlayerTickClientSide();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_IN, 1.0F, 1.5f));
    }

    public void showTemp(Component title) {
        show(title);
        stayTime = ANIMATION_LENGTH + JSGMinecraftHelper.getPlayerTickClientSide() + 20 * 5;
    }

    public void hide() {
        if (hide) return;
        hide = true;
        animationStart = JSGMinecraftHelper.getPlayerTickClientSide();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_OUT, 1.0F, 1.5f));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var tick = (JSGMinecraftHelper.getPlayerTickClientSide() + partialTick) - animationStart;
        float coef = (float) Math.min(1, Math.max(0, (tick / (double) ANIMATION_LENGTH)));
        if (hide)
            coef = 1 - coef;
        var currentWidth = (int) ((double) this.maxWidth * coef);
        var currentX = this.visibleX + maxWidth - currentWidth;
        setX(currentX);
        setWidth(currentWidth);
        if (!hide && stayTime > 0 && (JSGMinecraftHelper.getPlayerTickClientSide() + partialTick) >= stayTime) {
            hide();
        }
        if (currentWidth <= 0) return;
        graphics.blit(backgroundTexture, getX(), getY(), u, v, getWidth(), getHeight(), texWidth, texHeight);
        var minX = getX() + startPadding;
        var maxX = getX() + getWidth() - endPadding;
        if (minX >= maxX) return;
        GuiHelper.renderScrollingStringLeftAligned(graphics, Minecraft.getInstance().font, title, minX, maxX, getY() + 2, getY() + getHeight(), 0xffffff, true);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
