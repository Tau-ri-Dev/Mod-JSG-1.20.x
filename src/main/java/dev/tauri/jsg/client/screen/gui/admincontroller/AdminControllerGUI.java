package dev.tauri.jsg.client.screen.gui.admincontroller;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.client.screen.gui.admincontroller.element.ACResponseBar;
import dev.tauri.jsg.client.screen.gui.admincontroller.element.ACTabNavigationBar;
import dev.tauri.jsg.client.screen.gui.admincontroller.tabs.DialingTab;
import dev.tauri.jsg.common.item.admincontroller.ACStargateData;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACResponsePacketToClient;
import dev.tauri.jsg.common.packet.packets.admincontroller.event.ACStargateEngageSymbolPacketToClient;
import dev.tauri.jsg.common.packet.packets.admincontroller.handshake.ACStargateDataRequestPacketToServer;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

import static dev.tauri.jsg.core.client.screen.util.GuiHelper.drawModalRectWithCustomSizedTexture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdminControllerGUI extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = JSGMapping.rl(JSG.MOD_ID, "textures/gui/admin_controller/gui_admin_controller.png");

    @NotNull
    public final Player player;
    @NotNull
    public final ClientLevel level;
    @Nullable
    public final BlockPos gatePos;
    @NotNull
    public final StargateNetwork sgNetwork;

    private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);

    public final int xSize;
    public final int ySize;
    public final int xInnerSize;
    public final int yInnerSize;

    public int guiLeft;
    public int guiInnerLeft;
    public int guiTop;
    public int guiInnerTop;

    protected ACTabNavigationBar tabNavigationBar;
    protected ACResponseBar responseBar;

    public ACStargateData stargateData = null;

    public AdminControllerGUI(Player player, ClientLevel level, @Nullable BlockPos gatePos, StargateNetwork sgNetwork) {
        super(Component.translatable("gui.admincontroller.name"));
        this.player = player;
        this.level = level;
        this.gatePos = gatePos;
        this.sgNetwork = sgNetwork;
        this.xSize = 512;
        this.xInnerSize = xSize - 6;
        this.ySize = 256;
        this.yInnerSize = ySize - 4 - 16;
        requestStargateData();
    }

    public void requestStargateData() {
        if (gatePos != null) {
            Optional.of(gatePos).map(level::getBlockEntity).flatMap(tile -> {
                if (tile instanceof Stargate<?> sg) return Optional.of(sg);
                return Optional.empty();
            }).ifPresent(linkedGate -> JSGPacketHandler.sendToServer(new ACStargateDataRequestPacketToServer(linkedGate.getStargatePos())));
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.tabManager.tickCurrent();
        if (JSGMinecraftHelper.getPlayerTickClientSide() % 20 == 0)
            requestStargateData();
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        clearWidgets();
        init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.guiInnerLeft = guiLeft + 4;
        this.guiInnerTop = guiTop + 16;
        var selectedTab = Optional.ofNullable(tabNavigationBar).map(t -> t.selectedTab).orElse(0);
        var builder = ACTabNavigationBar.builder(tabManager, guiInnerLeft, guiTop + ySize, xInnerSize);
        for (var tab : AdminControllerTabsRegistry.TABS)
            builder.addTabs(tab.apply(this));
        this.tabNavigationBar = builder.build();

        this.responseBar = new ACResponseBar(guiLeft + 323 - 200, guiTop, 200, 12, BACKGROUND_TEXTURE, 0, 316, 512, 512, 24, 16);

        addRenderableWidget(tabNavigationBar);
        tabNavigationBar.selectTab(selectedTab, false);
        repositionElements();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        GuiHelper.currentStack = pGuiGraphics.pose();
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(graphics);
        if (responseBar != null)
            responseBar.render(graphics, pMouseX, pMouseY, pPartialTick);
        ITexture.bindTextureWithMc(BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
    }

    @Override
    public void repositionElements() {
        if (this.tabNavigationBar != null) {
            this.tabNavigationBar.setWidth(xInnerSize);
            this.tabNavigationBar.arrangeElements();
            ScreenRectangle screenrectangle = new ScreenRectangle(guiInnerLeft, guiInnerTop, xInnerSize, yInnerSize);
            this.tabManager.setTabArea(screenrectangle);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (tabNavigationBar != null && this.tabNavigationBar.keyPressed(pKeyCode)) {
            return true;
        } else if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        return pKeyCode == 257 || pKeyCode == 335;
    }

    public void handleResponsePacket(ACResponsePacketToClient packet) {
        if (responseBar == null) return;
        responseBar.showTemp(packet.component);
    }

    public void handleChevronEngageEvent(ACStargateEngageSymbolPacketToClient packet) {
        if (!(tabManager.getCurrentTab() instanceof DialingTab dialingTab)) return;
        dialingTab.handleChevronEngageEvent(packet);
    }

    public void setStargateData(ACStargateData data) {
        this.stargateData = data;
    }
}
