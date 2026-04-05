package dev.tauri.jsg.screen.gui.admincontroller.tabs;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.widget.ScrollableWidget;
import dev.tauri.jsg.core.common.blockentity.IBELogManager;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.screen.gui.admincontroller.AdminControllerGUI;
import dev.tauri.jsg.screen.gui.admincontroller.element.StargateWidget;
import dev.tauri.jsg.stargate.animation.spinning.PegasusSpinHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class DiagnosticsTab extends AdminControllerTabAsWidget {
    @SuppressWarnings("all")
    protected final Optional<StargateAbstractBaseBE<?, ?>> stargate;

    protected StargateWidget stargateWidget;
    protected LogsWidget logsWidget;

    public DiagnosticsTab(AdminControllerGUI baseGUI) {
        super(baseGUI, Component.translatable("gui.admincontroller.tab.diagnostics.name"));
        stargate = Optional.ofNullable(baseGUI.gatePos).map(baseGUI.level::getBlockEntity).flatMap(tile -> {
            if (tile instanceof StargateAbstractBaseBE<?, ?> sg) return Optional.of(sg);
            return Optional.empty();
        });
        stargateWidget = new StargateWidget(getX() + 2, getY() + 2, 200, getHeight() - 4, Component.empty(), () -> stargate)
                .outline(1, 0xff9A9A9A, true)
                .scale(1.8f)
                .layout(LayoutSettings.defaults().padding(0).paddingRight(50));
        logsWidget = new LogsWidget(getX() + 205, getY() + 64, getWidth() - 205 - 3 - 2, getHeight() - 63 - 4, Component.empty(), () -> {
            if (baseGUI.stargateData == null) return new LinkedList<>();
            return baseGUI.stargateData.logManager.getLogs();
        });
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> widgetConsumer) {
        super.visitChildren(widgetConsumer);
        widgetConsumer.accept(stargateWidget);
        widgetConsumer.accept(logsWidget);
    }

    protected ChevronEnum getNextChevron(StargateAddressDynamic dialedAddress, @Nullable SymbolInterface symbolToLock, boolean alreadyInAddress, boolean ignoreMaxChevrons) {
        var nextChevron = ChevronEnum.valueOf(dialedAddress.size() - (alreadyInAddress ? 1 : 0));
        if (isLockChevron(dialedAddress, symbolToLock, !alreadyInAddress, ignoreMaxChevrons))
            nextChevron = ChevronEnum.getFinal();
        return nextChevron;
    }

    protected boolean isLockChevron(StargateAddressDynamic dialedAddress, @Nullable SymbolInterface symbol, boolean notAddedYet, boolean ignoreMaxChevrons) {
        if (stargate.isEmpty()) return symbol != null && symbol.origin();
        if ((dialedAddress.size() + (notAddedYet ? 1 : 0)) >= (ignoreMaxChevrons ? 9 : stargate.get().getMaxChevrons()))
            return true;
        if (symbol == null) return false;

        return (dialedAddress.size() + (notAddedYet ? 1 : 0)) >= 7 && symbol.origin();
    }

    @Override
    public void tick() {
        super.tick();
        var poses = List.of(
                LayoutSettings.defaults().paddingRight(100).paddingTop(100),
                LayoutSettings.defaults().paddingRight(150).paddingTop(40),
                LayoutSettings.defaults().paddingRight(130).paddingTop(-80),
                LayoutSettings.defaults().paddingRight(60).paddingTop(-140),
                LayoutSettings.defaults().paddingRight(-60).paddingTop(-140),
                LayoutSettings.defaults().paddingRight(-130).paddingTop(-80),
                LayoutSettings.defaults().paddingRight(-150).paddingTop(40),
                LayoutSettings.defaults().paddingRight(-100).paddingTop(100),
                LayoutSettings.defaults().paddingRight(0).paddingTop(200)
        );
        var size = poses.size();
        var index = ((int) (JSGMinecraftHelper.getPlayerTickClientSide() / 40) % size);
        if (baseGUI.stargateData != null && (!baseGUI.stargateData.stargateState.idle() || baseGUI.stargateData.dialedAddress.size() > 0)) {
            if (stargate.isPresent() && stargate.get().getDialingManager().getSpinHelper() instanceof PegasusSpinHelper) {
                stargateWidget
                        .scale(2)
                        .animateTo(poses.get((getNextChevron(baseGUI.stargateData.dialedAddress, null, false, true).rotationIndex + 8) % size));
                return;
            }
            stargateWidget
                    .scale(3.5f)
                    .animateTo(LayoutSettings.defaults().paddingRight(0).paddingTop(290));
            return;
        }
        var pose = poses.get(index);
        stargateWidget
                .scale(2f)
                .animateTo(pose);
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        var font = Minecraft.getInstance().font;
        var data = baseGUI.stargateData;
        if (data == null || stargate.isEmpty()) {
            graphics.pose().pushPose();
            var c = Component.translatable("gui.admincontroller.tab.dialing.gate.not_linked").withStyle(ChatFormatting.RED);
            var cWidth = baseGUI.getMinecraft().font.width(c);
            graphics.pose().translate(getX() + 205 + (float) (getWidth() - 205) / 2, getHeight() - 30, 0);
            graphics.pose().scale(3, 3, 3);
            graphics.pose().translate(-(float) cWidth / 2f, -5, 0);
            graphics.drawString(baseGUI.getMinecraft().font, c, 0, 0, 0xffffff);
            graphics.pose().popPose();
            return;
        }
        var x = getX() + 205;
        var y = getY() + 2;
        graphics.drawString(font,
                getStatusComponent(Component.translatable("gui.admincontroller.tab.diagnostics.state"),
                        data.stargateState.name()
                ), x, y, 0xffffff);
        y += 10;
        graphics.drawString(font,
                getStatusComponent(Component.translatable("gui.admincontroller.tab.diagnostics.subspace"),
                        data.stargateConnectionStatus.name()
                ), x, y, 0xffffff);
        y += 10;
        graphics.drawString(font,
                getStatusComponent(Component.translatable("gui.admincontroller.tab.diagnostics.energy"),
                        String.format("%d", data.energyBuffer)
                ), x, y, 0xffffff);
        y += 10;
        graphics.drawString(font,
                getStatusComponent(Component.translatable("gui.admincontroller.tab.diagnostics.energy_consumption"),
                        String.format("%d", data.energyConsumption)
                ), x, y, 0xffffff);
        y += 10;
        graphics.drawString(font,
                getStatusComponent(Component.translatable("gui.admincontroller.tab.diagnostics.seconds_open"),
                        data.secondsOpen == -1 ? "GATE CLOSED" : String.format("%d:%02d:%02d", (int) (data.secondsOpen / 3600), (int) ((data.secondsOpen % 3600) / 60), (data.secondsOpen % 60))
                ), x, y, 0xffffff);
        y += 10;
        graphics.drawString(font,
                getStatusComponent(Component.translatable("gui.admincontroller.tab.diagnostics.seconds_to_close"),
                        data.secondsToClose == -1 ? "GATE CLOSED" : String.format("%d:%02d:%02.2f", (int) (data.secondsToClose / 3600), (int) ((data.secondsToClose % 3600) / 60), (data.secondsToClose % 60))
                ), x, y, 0xffffff);
    }

    protected Component getStatusComponent(MutableComponent title, String value) {
        return Component.empty().append(title.withStyle(ChatFormatting.BOLD)).append(" ").append(Component.literal(value));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public static class LogsWidget extends ScrollableWidget {
        protected final Supplier<LinkedList<IBELogManager.ILogLine>> logs;
        protected double ySize = 0;

        public LogsWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, Supplier<LinkedList<IBELogManager.ILogLine>> logs) {
            super(pX, pY, pWidth, pHeight, pMessage);
            this.logs = logs;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            var lines = logs.get();
            GuiHelper.renderOutline(graphics, getX(), getY() - 2, getWidth() + 2, getHeight() + 4, 0xff9A9A9A, 1, true);
            graphics.fill(getX() + 1, getY() - 1, getX() + getWidth() + 1, getY() + getHeight() + 1, 0xff282828);
            var font = Minecraft.getInstance().font;
            var lastYSize = ySize;
            var s = getScrollAmount();
            var m = getMaxScrollAmount();
            var isAtBottom = (s >= (m - font.lineHeight));
            ySize = 0;
            if (lines.isEmpty()) return;
            graphics.pose().pushPose();
            graphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
            graphics.pose().pushPose();
            var scale = 0.6f;
            var dateScale = 0.6f;
            graphics.pose().scale(scale, scale, scale);
            var y = (float) getY() + getHeight() - 1;
            y += (float) (m - s);
            y /= scale;
            var x = (int) (((float) getX() + 3) / scale);
            var copy = new LinkedList<>(lines);
            while (!copy.isEmpty()) {
                var line = copy.removeLast();
                var date = Instant.ofEpochMilli(line.time()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                var dateComponent = Component.literal("[" + date.toLocalDate() + " " + date.toLocalTime().truncatedTo(ChronoUnit.SECONDS) + "] ").withStyle(ChatFormatting.GRAY);
                var dateComponentWidth = (int) (font.width(dateComponent) * dateScale);
                var maxWidth = (int) (((float) getWidth() - 6 - 8) / scale) - dateComponentWidth;
                var lineComponent = Component.empty().append(line.component()).withStyle(Style.EMPTY.withColor(line.color()));
                var height = font.wordWrapHeight(lineComponent, maxWidth);
                y -= height + 2;
                graphics.pose().pushPose();
                graphics.pose().scale(dateScale, dateScale, dateScale);
                graphics.drawString(font, dateComponent, (int) (x / dateScale), (int) ((y + height / 2f) / dateScale - (font.lineHeight / 2f)), 0xffffff);
                graphics.pose().popPose();
                graphics.drawWordWrap(font, lineComponent, x + dateComponentWidth, (int) y, maxWidth, 0xffffff);
                ySize += (height + 2) * scale;
            }
            graphics.pose().popPose();
            graphics.disableScissor();
            graphics.pose().popPose();

            if (lastYSize != ySize && isAtBottom) {
                setScrollAmount(getMaxScrollAmount());
            }

            renderDecorations(graphics);
        }

        @Override
        public int getInnerHeight() {
            return (int) ySize;
        }

        @Override
        public int innerPadding() {
            return 6;
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }
    }
}
