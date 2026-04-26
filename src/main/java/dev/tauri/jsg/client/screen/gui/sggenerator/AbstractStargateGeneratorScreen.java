package dev.tauri.jsg.client.screen.gui.sggenerator;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.api.client.screen.EnumMainMenuGateType;
import dev.tauri.jsg.client.screen.gui.mainmenu.GuiCustomMainMenu;
import dev.tauri.jsg.client.screen.gui.mainmenu.MainMenuGateRenderer;
import dev.tauri.jsg.common.worldgen.generator.StargateGeneratorStepStatus;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.common.util.JSGColorUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractStargateGeneratorScreen extends GenericDirtMessageScreen {
    final Supplier<String> step;
    final Supplier<Integer> total;
    final Supplier<Component> message;
    final Supplier<ConcurrentHashMap<String, StargateGeneratorStepStatus>> stats;
    final double initTick;

    public AbstractStargateGeneratorScreen(Component title, Supplier<String> step, Supplier<Integer> total, Supplier<ConcurrentHashMap<String, StargateGeneratorStepStatus>> stats, Supplier<Component> message) {
        super(title);
        this.step = step;
        this.total = total;
        this.stats = stats;
        this.message = message;
        this.initTick = System.currentTimeMillis() / 1000.0 * 20.0;
    }

    public float getProgress() {
        var t = (float) this.total.get();
        if (t == 0) t = 1;
        var stats = this.stats.get();
        return (stats.size() / t);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var ticks = (System.currentTimeMillis() / 1000.0 * 20.0) - initTick;
        renderDirtBackground(graphics);
        graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);

        var y = height - 20 - 8;
        var x = 8;
        var s = Component.translatable(step.get(), Integer.toString((int) (getProgress() * 100)));
        graphics.drawString(font, s, x, y, 0xffffffff);
        graphics.drawCenteredString(font, message.get(), width / 2, y - 10, 0xffffffff);
        y += 10;

        var progressSize = (width - 16);

        renderProgress(graphics, x, y, progressSize, mouseX, mouseY, ticks);

        graphics.pose().pushPose();
        RenderSystem.enableDepthTest();
        renderGateAssembly(graphics, mouseX, mouseY, ticks);
        RenderSystem.disableDepthTest();
        graphics.pose().popPose();
    }

    protected void renderProgress(@NotNull GuiGraphics graphics, int x, int y, int progressSize, int mouseX, int mouseY, double ticks) {
        graphics.renderOutline(x, y, progressSize, 10, 0xffffffff);

        var loaderCoef = (ticks % 100) / 100.0;
        var loaderX = Math.max(0, (-10 + (loaderCoef * progressSize)));
        var loaderXRight = Math.min(progressSize - 4, (loaderCoef * progressSize));
        graphics.fill((int) (x + 2 + loaderX), y + 2, (int) (x + 2 + loaderXRight), y + 8, 0xffffffff);

        var t = (double) this.total.get();
        var stats = this.stats.get();
        var perDim = ((progressSize - 4) / t);
        AtomicInteger i = new AtomicInteger(0);
        stats.forEach((stepName, stepStatus) -> {
            var xOffset = (perDim * (double) i.get());
            var pX = (int) (x + 2 + xOffset);
            var pXMax = (int) (x + 2 + xOffset + perDim);
            if (i.get() == (t - 1)) {
                pXMax = x + (progressSize - 2);
            }
            var pY = y + 2;
            var pYMax = y + 8;

            var color = stepStatus.getColor();
            var hover = false;
            if (GuiHelper.isPointInRegion(pX, pY, (pXMax - pX), (pYMax - pY), mouseX, mouseY)) {
                color = JSGColorUtil.blendColors(color, 0xffffffff, 0.5f);
                hover = true;
            }
            graphics.fill(pX, pY, pXMax, pYMax, color);
            if (hover) {
                graphics.renderTooltip(font, List.of(Component.literal(stepName), stepStatus.getMessage()), Optional.empty(), mouseX, mouseY);
            }
            i.incrementAndGet();
        });
    }

    protected static final double ASSEMBLY_DURATION = 20 * 20;

    protected void renderGateAssembly(@NotNull GuiGraphics graphics, int mouseX, int mouseY, double ticks) {
        var x = (width / 4);
        var y = (height / 2);
        graphics.pose().pushPose();

        var scaleX = ((float) width / 640f);
        var scaleY = ((float) height / 337f);
        var scale = Math.min(scaleX, scaleY);

        var gateDisCof = (ticks % ASSEMBLY_DURATION) / ASSEMBLY_DURATION;
        var coef = (float) -Math.pow(Math.cos(Math.max(0, (gateDisCof * 1.25f) - 0.25f) * Math.PI), 6) + 1f;
        var t = (double) total.get();
        var stats = this.stats.get();
        var percentDone = (stats.size() / t);
        GuiCustomMainMenu.poseStack = graphics.pose();
        GuiCustomMainMenu.graphics = graphics;
        MainMenuGateRenderer.disassemblyCoef = coef;
        MainMenuGateRenderer.renderGate(EnumMainMenuGateType.MILKYWAY, x, y, 1, 25 * scale, (int) Math.floor(percentDone * 9), (percentDone >= 0.99 || ((int) Math.floor(percentDone * 9)) >= 9), ticks);
        graphics.pose().pushPose();
        graphics.drawCenteredString(font, Component.translatable("createWorld.stargate_disassemble.milkyway.title"), x, (int) (y - 150 * scale), 0xffffff);
        if (coef >= 0.99f) {
            var color = 0xffffffff;
            var s = Component.translatable("createWorld.stargate_disassemble.milkyway.chevron_frame");
            drawPartTooltip(graphics, (int) (x - 55 * scale - font.width(s)), (int) (y + 25 * scale), (int) (x - 45 * scale), (int) (y - 20 * scale), s, color);
            drawPartTooltip(graphics, (int) (x + 130 * scale), (int) (y - 45 * scale), (int) (x + 105 * scale), (int) (y - 22 * scale), Component.translatable("createWorld.stargate_disassemble.milkyway.chevron_light"), color);
            drawPartTooltip(graphics, (int) (x + 75 * scale), y, (int) (x + 38 * scale), (int) (y + 25 * scale), Component.translatable("createWorld.stargate_disassemble.milkyway.glyph_ring"), color);

            drawPartTooltip(graphics, (int) (x + 130 * scale), (int) (y - 65 * scale), (int) (x + 75 * scale), (int) (y - 100 * scale), Component.translatable("createWorld.stargate_disassemble.milkyway.chevron_lock"), color);
            s = Component.translatable("createWorld.stargate_disassemble.milkyway.gate_frame");
            drawPartTooltip(graphics, (int) (x - 55 * scale) - font.width(s), (int) (y - 100 * scale), (int) (x - 15 * scale), (int) (y - 65 * scale), s, color);
        }
        graphics.pose().popPose();

        y = (height - 45);
        x = (width - 75);
        MainMenuGateRenderer.disassemblyCoef = coef;
        MainMenuGateRenderer.renderDHD(EnumMainMenuGateType.MILKYWAY, x, y, 1, 50 * scale, ticks);
        graphics.pose().pushPose();
        graphics.drawCenteredString(font, Component.translatable("createWorld.dhd_disassemble.milkyway.title"), x, y + 15, 0xffffff);
        if (coef >= 0.99f) {
            var color = 0xffffffff;
            var partY = 10;
            var s = Component.translatable("createWorld.dhd_disassemble.milkyway.dhd_table");
            drawPartTooltip(graphics, x - 50 - font.width(s), (int) (y - partY * scale - 15), x - 25, (int) (y - partY * scale), s, color);
            partY += 60;
            s = Component.translatable("createWorld.dhd_disassemble.milkyway.crystals_holder");
            drawPartTooltip(graphics, x - 50 - font.width(s), (int) (y - partY * scale - 15), x - 25, (int) (y - partY * scale), s, color);
            partY += 45;
            s = Component.translatable("createWorld.dhd_disassemble.milkyway.crystals");
            drawPartTooltip(graphics, x - 50 - font.width(s), (int) (y - partY * scale - 15), x - 25, (int) (y - partY * scale), s, color);
            partY += 40;
            s = Component.translatable("createWorld.dhd_disassemble.milkyway.control_crystal");
            drawPartTooltip(graphics, x - 50 - font.width(s), (int) (y - partY * scale - 15), x - 5, (int) (y - partY * scale), s, color);
            partY += 40;
            s = Component.translatable("createWorld.dhd_disassemble.milkyway.buttons_plate");
            drawPartTooltip(graphics, x - 50 - font.width(s), (int) (y - partY * scale - 15), x - 25, (int) (y - partY * scale), s, color);
            partY += 40;
            s = Component.translatable("createWorld.dhd_disassemble.milkyway.buttons");
            drawPartTooltip(graphics, x - 50 - font.width(s), (int) (y - partY * scale - 15), x - 25, (int) (y - partY * scale), s, color);
        }
        graphics.pose().popPose();
        graphics.pose().popPose();
    }

    protected void drawPartTooltip(@NotNull GuiGraphics graphics, int stringX, int stringY, int partX, int partY, Component text, int color) {
        var underlinedY = (partY < stringY ? -3 : 10);
        if (partY > stringY && (partY - stringY) < 11) partY = stringY + 11;

        var w = font.width(text);

        graphics.drawString(font, text, stringX, stringY, color); // text
        graphics.fill(stringX, stringY + underlinedY, stringX + w, stringY + underlinedY + 1, color); // underlined

        // line vertical
        if (partY > stringY)
            graphics.fill(stringX + (w / 2), stringY + underlinedY, stringX + (w / 2) + 1, partY, color);
        else
            graphics.fill(stringX + (w / 2), partY, stringX + (w / 2) + 1, stringY + underlinedY, color);

        // line horizontal
        if (partX > (stringX + (w / 2)))
            graphics.fill(stringX + (w / 2), partY - 1, partX, partY, color);
        else
            graphics.fill(partX, partY - 1, stringX + (w / 2), partY, color);
        graphics.fill(partX, partY - 3, partX + 1, partY + 2, color);
    }
}
