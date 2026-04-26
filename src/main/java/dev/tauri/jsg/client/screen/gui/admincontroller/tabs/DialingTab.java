package dev.tauri.jsg.client.screen.gui.admincontroller.tabs;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.client.screen.gui.admincontroller.AdminControllerGUI;
import dev.tauri.jsg.client.screen.gui.admincontroller.element.StargateWidget;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.item.admincontroller.AdminControllerAction;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACLinkedActionPacketToServer;
import dev.tauri.jsg.common.packet.packets.admincontroller.event.ACStargateEngageSymbolPacketToClient;
import dev.tauri.jsg.core.client.screen.widget.ButtonWithIcon;
import dev.tauri.jsg.core.client.screen.widget.SymbolFrame;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DialingTab extends AdminControllerTabAsWidget {
    @SuppressWarnings("all")
    protected final Optional<StargateAbstractBaseBE<?, ?>> stargate;

    public static final ResourceLocation BTN_ICONS = JSGMapping.rl(JSG.MOD_ID, "textures/gui/admin_controller/dialing_tab_icons.png");
    protected Button abortDialingButton;
    protected Button disEngageGateButton;
    protected Button toggleIrisButton;
    protected StargateWidget stargateWidget;

    protected final List<SymbolFrame> symbolFrames = new ArrayList<>();

    public DialingTab(AdminControllerGUI baseGUI) {
        super(baseGUI, Component.translatable("gui.admincontroller.tab.dialing.name"));
        stargate = Optional.ofNullable(baseGUI.gatePos).map(baseGUI.level::getBlockEntity).flatMap(tile -> {
            if (tile instanceof StargateAbstractBaseBE<?, ?> sg) return Optional.of(sg);
            return Optional.empty();
        });
        var buttonsX = baseGUI.guiInnerLeft;
        var buttonsY = baseGUI.guiInnerTop;
        abortDialingButton = ButtonWithIcon.builder(
                        Component.translatable("gui.admincontroller.tab.dialing.abort.name"),
                        (btn) -> stargate.ifPresent(stargateAbstractBaseBE -> JSGPacketHandler.sendToServer(new ACLinkedActionPacketToServer(stargateAbstractBaseBE.getStargatePos(), AdminControllerAction.ABORT_DIALING)))
                )
                .setIcon(BTN_ICONS, 0, 0, 128, 16)
                .setActive(stargate.isPresent())
                .bounds(buttonsX, buttonsY, 120, 20)
                .build();
        disEngageGateButton = ButtonWithIcon.builder(
                        Component.translatable("gui.admincontroller.tab.dialing.close.name"),
                        (btn) -> stargate.ifPresent(stargateAbstractBaseBE -> JSGPacketHandler.sendToServer(new ACLinkedActionPacketToServer(stargateAbstractBaseBE.getStargatePos(), AdminControllerAction.CLOSE_GATE)))
                )
                .setIcon(BTN_ICONS, 16, 0, 128, 16)
                .setActive(stargate.isPresent())
                .bounds(buttonsX, buttonsY + 22, 120, 20)
                .build();
        toggleIrisButton = ButtonWithIcon.builder(
                        Component.translatable("gui.admincontroller.tab.dialing.iris.name"),
                        (btn) -> stargate.ifPresent(stargateAbstractBaseBE -> JSGPacketHandler.sendToServer(new ACLinkedActionPacketToServer(stargateAbstractBaseBE.getStargatePos(), AdminControllerAction.TOGGLE_IRIS)))
                )
                .setIcon(BTN_ICONS, 32, 0, 128, 16)
                .setActive(stargate.isPresent())
                .bounds(buttonsX, buttonsY + 22 * 2, 120, 20)
                .build();

        var symbolFramePaths = List.of(
                List.of(
                        new Vector2i(0, 14),
                        new Vector2i(-100, 14),
                        new Vector2i(-165 + 13, 60)
                ),
                List.of(
                        new Vector2i(0, 14),
                        new Vector2i(-29, 14),
                        new Vector2i(-100, 85),
                        new Vector2i(-120, 85)
                ),
                List.of(
                        new Vector2i(0, 14),
                        new Vector2i(-8, 14),
                        new Vector2i(-114, 120),
                        new Vector2i(-130, 120)
                ),
                List.of(
                        new Vector2i(26, 14),
                        new Vector2i(35, 14),
                        new Vector2i(35, -86),
                        new Vector2i(-133, -86),
                        new Vector2i(-150, -70),
                        new Vector2i(-315, -70),
                        new Vector2i(-335, -50),
                        new Vector2i(-335, 120 - 27 - 10),
                        new Vector2i(-325, 120 - 27),
                        new Vector2i(-280, 120 - 27)
                ),
                List.of(
                        new Vector2i(26, 14),
                        new Vector2i(33, 14),
                        new Vector2i(33, -86 - 24),
                        new Vector2i(-133 + 1, -86 - 24),
                        new Vector2i(-150 + 1, -70 - 24),
                        new Vector2i(-300, -70 - 24),
                        new Vector2i(-320, -50 - 24),
                        new Vector2i(-320, 85 - 3 * 26 - 2 - 10),
                        new Vector2i(-310, 85 - 3 * 26 - 2),
                        new Vector2i(-290, 85 - 3 * 26 - 2)
                ),
                List.of(
                        new Vector2i(26, 14),
                        new Vector2i(31, 14),
                        new Vector2i(31, -86 - 24 * 2),
                        new Vector2i(-133 + 2, -86 - 24 * 2),
                        new Vector2i(-150 + 2, -70 - 24 * 2),
                        new Vector2i(-265, -70 - 24 * 2),
                        new Vector2i(-275, -60 - 24 * 2),
                        new Vector2i(-275, -35 - 24 * 2),
                        new Vector2i(-265, -35 - 24 * 2 + 10)
                ),
                List.of(
                        new Vector2i(0, 14),
                        new Vector2i(-60, 14),
                        new Vector2i(-60 - 50, 14 + 50),
                        new Vector2i(-169, 14 + 50),
                        new Vector2i(-179, 14 + 50 - 10)
                ),
                List.of(
                        new Vector2i(0, 14),
                        new Vector2i(-60, 14),
                        new Vector2i(-60 - 40, 14 + 40),
                        new Vector2i(-245 + 5, 14 + 40),
                        new Vector2i(-245, 14 + 35),
                        new Vector2i(-245, 14 + 30 - 10),
                        new Vector2i(-235, 14 + 30 - 20)
                ),
                List.of(
                        new Vector2i(26, 14),
                        new Vector2i(29, 14),
                        new Vector2i(29, -86 - 24 * 5 - 4),
                        new Vector2i(-133 + 3, -86 - 24 * 5 - 4),
                        new Vector2i(-150 + 3, -70 - 24 * 5 - 4),
                        new Vector2i(-207, -70 - 24 * 5 - 4),
                        new Vector2i(-207, -50 - 24 * 5 - 4)
                )
        );
        var gateCenter = getCenterPos(0, 0);
        for (int i = 1; i <= 9; i++) {
            symbolFrames.add(new SymbolFrame(
                    getX() + getWidth() - 45, getY() + 26 * (i - 1) - 3,
                    gateCenter[0], gateCenter[1],
                    i, (frame) -> {
                if (frame.offline) return 0xffa0a0a0;
                if (frame.incoming) return 0xffECD24F;
                var stargateState = Optional.ofNullable(baseGUI.stargateData).map(data -> data.stargateState).orElse(EnumStargateState.IDLE);
                if ((!frame.animating || (JSGMinecraftHelper.getPlayerTickClientSide() / 10 % 2 == 0)) && frame.symbol != null) {
                    if (stargateState.initiating())
                        return 0xff69EC4F;
                    if (stargateState.failing())
                        return 0xffEC4F4F;
                    if (stargateState.unstable())
                        return 0xffECA34F;
                    return 0xff4FECC7;
                }
                return 0xffffffff;
            }).setPath(symbolFramePaths.get(i - 1)));
        }
        stargateWidget = new StargateWidget(gateCenter[0] - (195 / 2), gateCenter[1] - (195 / 2), 195, 195, Component.empty(), () -> stargate);
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> widgetConsumer) {
        super.visitChildren(widgetConsumer);
        widgetConsumer.accept(abortDialingButton);
        widgetConsumer.accept(disEngageGateButton);
        widgetConsumer.accept(toggleIrisButton);
        widgetConsumer.accept(stargateWidget);
        symbolFrames.forEach(widgetConsumer);
    }

    @Override
    public void tick() {
        super.tick();
        if (baseGUI.stargateData == null) return;
        var address = baseGUI.stargateData.dialedAddress;
        symbolFrames.forEach(s -> {
            s.incoming = false;
            s.setSymbol(null);
        });
        for (var i = 0; i < address.size(); i++) {
            var symbol = address.get(i);
            if (symbol.origin()) i = 8;
            symbolFrames.get(i).setSymbol(symbol);
        }
        Optional.ofNullable(baseGUI.stargateData).ifPresent(data -> {
            if (data.stargateConnectionStatus.none()) return;
            if (data.stargateConnectionStatus.closing()) return;
            if (data.initiatingConnection) return;
            symbolFrames.forEach(s -> s.incoming = true);
        });
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (stargate.isEmpty()) {
            symbolFrames.forEach(s -> s.offline = true);
            graphics.pose().pushPose();
            var gateCenter = getCenterPos(0, 0);
            var c = Component.translatable("gui.admincontroller.tab.dialing.gate.not_linked").withStyle(ChatFormatting.RED);
            var cWidth = baseGUI.getMinecraft().font.width(c);
            graphics.pose().translate(gateCenter[0], gateCenter[1], 0);
            graphics.pose().scale(3, 3, 3);
            graphics.pose().translate(-(float) cWidth / 2f, -5, 0);
            graphics.drawString(baseGUI.getMinecraft().font, c, 0, 0, 0xffffff);
            graphics.pose().popPose();
        } else
            symbolFrames.forEach(s -> s.offline = false);
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public void handleChevronEngageEvent(ACStargateEngageSymbolPacketToClient packet) {
        symbolFrames.get(packet.chevron.index).engage(packet.symbolInterface);
    }
}
