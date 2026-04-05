package dev.tauri.jsg.screen.inventory.dialhomedevice;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.client.screen.util.DHDScreenHelper;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.dialhomedevice.DHDReactorStateEnum;
import dev.tauri.jsg.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import dev.tauri.jsg.core.client.screen.tab.tabs.Tab;
import dev.tauri.jsg.core.client.screen.tab.tabs.TabBiomeOverlay;
import dev.tauri.jsg.core.client.screen.tab.tabs.TabbedContainerInterface;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.widget.Diode;
import dev.tauri.jsg.core.client.screen.widget.Diode.DiodeStatus;
import dev.tauri.jsg.core.client.screen.widget.FluidTankElement;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import dev.tauri.jsg.core.common.item.CommonUpgrade;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.tauri.jsg.core.client.screen.util.GuiHelper.drawTexturedModalRect;
import static dev.tauri.jsg.screen.inventory.stargate.StargateContainerGui.createOverlayTab;

public class DHDContainerGui extends AbstractContainerScreen<DHDContainer> implements TabbedContainerInterface {
    private FluidTankElement tank = null;

    private final List<Diode> diodes = new ArrayList<>(3);

    private final List<Tab> tabs = new ArrayList<>();

    public static final ResourceLocation BACKGROUND = JSGMapping.rl(JSG.MOD_ID, "textures/gui/container_dhd.png");

    public DHDContainerGui(DHDContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 172;

        this.width = 176;
        this.height = 173;

        menu.tankNaquadah.setFluid(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.get(), 0));
    }

    @Override
    public void init() {
        super.init();

        tank = new FluidTankElement(152, 23, 16, 54, menu.tankNaquadah);

        tabs.clear();

        TabBiomeOverlay overlayTab = createOverlayTab(menu.dhdTile.getSupportedOverlays(), imageWidth, imageHeight, getGuiLeft(), getGuiTop());
        overlayTab.setMenu(menu);

        tabs.add(overlayTab);

        menu.slots.set(DHDAbstractBE.BIOME_OVERRIDE_SLOT, overlayTab.createAndSaveSlot((SlotHandler) menu.getSlot(DHDAbstractBE.BIOME_OVERRIDE_SLOT)));

        diodes.add(new Diode(this, 8, 18, I18n.format("gui.dhd.crystalStatus")).setDiodeStatus(DiodeStatus.OFF)
                .putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.no_crystal"))
                .putStatus(DiodeStatus.ON, I18n.format("gui.dhd.crystal_ok"))
                .setStatusMapper(() -> menu.slotCrystal.hasItem() ? DiodeStatus.ON : DiodeStatus.OFF));

        diodes.add(new Diode(this, 17, 18, I18n.format("gui.dhd.linkStatus")).setDiodeStatus(DiodeStatus.OFF)
                .putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.not_linked"))
                .putStatus(DiodeStatus.ON, I18n.format("gui.dhd.linked"))
                .setStatusMapper(() -> menu.dhdTile.isLinkedClient ? DiodeStatus.ON : DiodeStatus.OFF));

        diodes.add(new Diode(this, 26, 18, I18n.format("gui.dhd.reactorStatus"))
                .putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.no_fuel"))
                .putStatus(DiodeStatus.WARN, I18n.format("gui.dhd.standby"))
                .putStatus(DiodeStatus.ON, I18n.format("gui.dhd.running"))
                .setStatusMapper(() -> switch (menu.dhdTile.getReactorState()) {
                    case ONLINE -> DiodeStatus.ON;
                    case STANDBY -> DiodeStatus.WARN;
                    default -> DiodeStatus.OFF;
                })
                .setStatusStringMapper(() -> switch (menu.dhdTile.getReactorState()) {
                    case NOT_LINKED -> I18n.format("gui.dhd.not_linked");
                    case NO_CRYSTAL -> I18n.format("gui.dhd.no_crystal");
                    default -> null;
                }));
    }

    public void updateTank() {
        double capacity = JSGConfig.DialHomeDevice.fluidCapacity.get();
        if (menu.dhdTile.hasUpgrade(CommonUpgrade.CAPACITY_UPGRADE))
            capacity *= JSGConfig.DialHomeDevice.capacityUpgradeMultiplier.get();
        if (capacity != menu.tankNaquadah.getCapacity())
            menu.tankNaquadah.setCapacity((int) capacity);

        if (menu.tankNaquadah.getFluidAmount() > capacity) {
            menu.tankNaquadah.setFluid(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.get(), (int) capacity));
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.disableDepthTest();
        GuiHelper.currentStack = graphics.pose();
        updateTank();
        this.renderBackground(graphics);

        Tab.updatePositions(tabs);

        int i = DHDAbstractBE.BIOME_OVERRIDE_SLOT;
        Tab.SlotTab slot = ((Tab.SlotTab) menu.getSlot(i)).updatePos();
        slot.setSlotIndex(i);
        menu.slots.set(i, slot);

        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        for (Tab tab : tabs) {
            tab.render(graphics, mouseX, mouseY);
        }

        ITexture.bindTextureWithMc(BACKGROUND);
        drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);

        // Crystal background
        if (menu.slotCrystal.hasItem()) {
            RenderSystem.enableBlend();
            drawCrystal();
            RenderSystem.disableBlend();
        }

        int guiLeft = getGuiLeft();
        int guiTop = getGuiTop();

        // Wires for upgrades
        for (int i = 1; i < 4; i++) {
            if (menu.getSlot(i).hasItem()) {
                switch (i) {
                    case 1:
                        drawTexturedModalRect(guiLeft + 16, guiTop + 57, 121, 173, 178 - 121, 184 - 172);
                        break;
                    case 2:
                        drawTexturedModalRect(guiLeft + 34, guiTop + 57, 121, 185, 160 - 121, 193 - 184);
                        break;
                    case 3:
                        drawTexturedModalRect(guiLeft + 52, guiTop + 57, 121, 194, 142 - 121, 199 - 193);
                        break;
                    default:
                        break;
                }
            }
        }


        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (menu.dhdTile.getReactorState() == DHDReactorStateEnum.ONLINE) {
            TextureAtlasSprite sprite = BlockRenderer.getFluidTexture(menu.tankNaquadah.getFluid(), BlockRenderer.FluidTextureType.STILL);
            if (sprite != null) {
                GuiHelper.currentStack = graphics.pose();
                // Top duct Naquadah
                for (int i = 0; i < 3; i++)
                    GuiHelper.drawTexturedRectScaled(guiLeft + 103 + 16 * i, guiTop + 60 + 16, sprite, 16, 16, 1);

                // Bottom duct Naquadah
                for (int i = 0; i < 5; i++)
                    GuiHelper.drawTexturedRectScaled(guiLeft + 87 + 16 * i, guiTop + 87, sprite, 16, 16, 10.0f / 16);
            }
        }

        // Naquadah ducts
        RenderSystem.enableBlend();
        ITexture.bindTextureWithMc(BACKGROUND);
        drawTexturedModalRect(guiLeft + 103, guiTop + 60, 0, 173, 48, 16);
        drawTexturedModalRect(guiLeft + 84, guiTop + 77, 0, 189, 84, 10);
        RenderSystem.disableBlend();

        // Titles
        drawAncientTitle();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        graphics.drawString(font, I18n.format("gui.upgrades"), 8, 29, 4210752, false);
        graphics.drawString(font, I18n.format("container.inventory"), 8, this.imageHeight - 96 + 2, 4210752, false);

        tank.renderTank(graphics);

        // Tank's gauge
        RenderSystem.enableBlend();
        ITexture.bindTextureWithMc(BACKGROUND);
        drawTexturedModalRect(152, 23, 176, 32, 16, 54);
        RenderSystem.disableBlend();

        boolean[] statuses = new boolean[diodes.size()];

        for (int i = 0; i < diodes.size(); i++) {
            statuses[i] = diodes.get(i).render(mouseX - getGuiLeft(), mouseY - getGuiTop());
        }

        for (int i = 0; i < diodes.size(); i++) {
            if (statuses[i])
                diodes.get(i).renderTooltip(graphics, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }

        tank.renderTooltip(graphics, mouseX - getGuiLeft(), mouseY - getGuiTop());

        for (Tab tab : tabs) {
            tab.renderFg(graphics, mouseX, mouseY);
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);

            if (tab.isCursorOnTab((int) mouseX, (int) mouseY)) {
                Tab.tabsInteract(tabs, i);
                menu.updateTabSlots();
                break;
            }
        }
        return true;
    }

    @Override
    public List<Rect2i> getGuiExtraAreas() {
        return tabs.stream()
                .map(Tab::getArea)
                .collect(Collectors.toList());
    }

    public void drawCrystal() {
        DHDScreenHelper helper = menu.dhdTile.getScreenHelper();
        drawTexturedModalRect(getGuiLeft() + 77, getGuiTop() + 21, helper.crystalTexX, helper.crystalTexY, 24, 32);
    }

    public void drawAncientTitle() {
        DHDScreenHelper helper = menu.dhdTile.getScreenHelper();
        drawTexturedModalRect(getGuiLeft() + 136, getGuiTop() + 4, helper.titleTexX, helper.titleTexY, 35, 8);
    }
}
