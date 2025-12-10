package dev.tauri.jsg.api.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.client.screen.util.GuiHelper;
import dev.tauri.jsg.api.client.texture.ITexture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dev.tauri.jsg.api.client.screen.util.GuiHelper.drawModalRectWithCustomSizedTexture;

public class Diode {

    public static final ResourceLocation DIODE_TEXTURE = new ResourceLocation(JSGApi.MOD_ID, "textures/gui/diodes.png");

    private final Screen screen;

    private final int x;
    private final int y;

    private final String description;
    private final Map<DiodeStatus, String> statusStringMap;
    private DiodeStatus status;
    private StatusMapperInterface statusMapper;
    private StatusStringMapperInterface statusStringMapper;

    public Diode(Screen screen, int x, int y, String description) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.description = description;
        this.statusStringMap = new HashMap<>(3);
    }

    public Diode putStatus(DiodeStatus status, String statusString) {
        statusStringMap.put(status, statusString);
        return this;
    }

    public Diode setStatusMapper(StatusMapperInterface statusMapper) {
        this.statusMapper = statusMapper;
        return this;
    }

    public Diode setStatusStringMapper(StatusStringMapperInterface statusStringMapper) {
        this.statusStringMapper = statusStringMapper;
        return this;
    }

    public Diode setDiodeStatus(DiodeStatus status) {
        this.status = status;
        return this;
    }

    public boolean render(int mouseX, int mouseY) {
        status = statusMapper.get();

        RenderSystem.enableBlend();
        ITexture.bindTextureWithMc(DIODE_TEXTURE);
        drawModalRectWithCustomSizedTexture(x, y, status.xTex, status.yTex, 8, 7, 16, 16);
        RenderSystem.disableBlend();

        return GuiHelper.isPointInRegion(x, y, 8, 8, mouseX, mouseY);
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        String statusString = null;

        if (statusStringMapper != null)
            statusString = statusStringMapper.get();

        if (statusString == null)
            statusString = statusStringMap.get(status);

        Component textComponent = Component.literal(status.color + "" + ChatFormatting.ITALIC + statusString);

        graphics.renderTooltip(Minecraft.getInstance().font, List.of(
                Component.literal(description),
                textComponent
        ), Optional.empty(), mouseX, mouseY);
    }

    public enum DiodeStatus {
        OFF(0, 0, ChatFormatting.DARK_RED),
        WARN(8, 0, ChatFormatting.YELLOW),
        ON(0, 7, ChatFormatting.GREEN);

        public final int xTex;
        public final int yTex;
        public final ChatFormatting color;

        DiodeStatus(int xTex, int yTex, ChatFormatting color) {
            this.xTex = xTex;
            this.yTex = yTex;
            this.color = color;
        }
    }

    public interface StatusMapperInterface {
        DiodeStatus get();
    }

    public interface StatusStringMapperInterface {

        /**
         * @return Custom status string or {@code null} to use {@link Map} one.
         */
        @Nullable
        String get();
    }
}
