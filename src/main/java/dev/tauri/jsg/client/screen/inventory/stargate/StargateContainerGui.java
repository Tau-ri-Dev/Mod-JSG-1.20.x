package dev.tauri.jsg.client.screen.inventory.stargate;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.StargateUpgrade;
import dev.tauri.jsg.client.screen.element.TabIris;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.stargate.SaveIrisCodeToServer;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.core.client.screen.tab.TabSideEnum;
import dev.tauri.jsg.core.client.screen.tab.TabbedContainerScreen;
import dev.tauri.jsg.core.client.screen.tab.tabs.*;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import dev.tauri.jsg.core.common.helper.TemperatureHelper;
import dev.tauri.jsg.core.common.item.IUpgradeItem;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.SaveConfigToServer;
import dev.tauri.jsg.core.common.power.general.LargeEnergyStorage;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dev.tauri.jsg.core.client.screen.util.GuiHelper.*;

public class StargateContainerGui extends TabbedContainerScreen<StargateContainer> {
    public static final ResourceLocation BACKGROUND_TEXTURE = JSGMapping.rl(JSG.MOD_ID, "textures/gui/container_stargate.png");
    private final Map<SymbolType<?>, TabAddress> addressTabs = new LinkedHashMap<>();
    private TabIris irisTab;
    private TabConfig configTab;
    private TabInfo infoTab;
    private TabBiomeOverlay overlayTab;

    private int energyStored;
    private int maxEnergyStored;

    private final BlockPos pos;

    public StargateContainerGui(StargateContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 176, 173);

        this.pos = container.gateTile.getBlockPos();
    }

    @Override
    public void init() {
        super.init();
        int ii = 0;
        menu.slots.set(7, overlayTab.createAndSaveSlot((SlotHandler) menu.getSlot(7)));
        for (var tab : addressTabs.values()) {
            menu.slots.set(ii + 9, tab.createAndSaveSlot((SlotHandler) menu.getSlot(ii + 9)));
            ii++;
        }
    }

    @Override
    protected void initTabs(List<Tab> tabs) {
        int i = 0;
        for (SymbolType<?> type : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
            var tab = TabAddress.builder()
                    .setAddressProvider(menu.gateTile)
                    .setSymbolType(type)
                    .setProgressColor(0x98BCF9)
                    .setGuiSize(imageWidth, imageHeight)
                    .setGuiPosition(leftPos, topPos)
                    .setTabPosition(-21, 11 + 22 * i)
                    .setOpenX(-128)
                    .setHiddenX(-6)
                    .setTabSize(128, 113)
                    .setTabTitle(I18n.format("gui.stargate." + type.getId().toString().replace(":", ".") + "_address"))
                    .setTabSide(TabSideEnum.LEFT);
            tab = (Tab.TabBuilder) type.finalizeAddressTab(tab);
            addressTabs.put(type, (TabAddress) ((TabAddress) tab.build()).setMenu(menu));
            i++;
        }

        configTab = createConfigTab(menu.gateTile.getConfig(), imageWidth, imageHeight, leftPos, topPos);

        overlayTab = createOverlayTab(menu.gateTile.getSupportedOverlays(), imageWidth, imageHeight, leftPos, topPos);
        overlayTab.setMenu(menu);

        irisTab = (TabIris) TabIris.builder()
                .setCode(menu.gateTile.getIrisManager().getIrisCode())
                .setIsUniverse(menu.gateTile.getSymbolType() == JSGSymbolTypes.UNIVERSE.get())
                .setIrisMode(menu.gateTile.getIrisManager().getIrisMode())
                .setGuiSize(imageWidth, imageHeight)
                .setGuiPosition(leftPos, topPos)
                .setTabPosition(176 - 107, 2 + 22)
                .setOpenX(176)
                .setHiddenX(54)
                .setTabSize(128, 51)
                .setTabTitle(I18n.format("gui.stargate.iris_code"))
                .setTabSide(TabSideEnum.RIGHT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176 + 24, 113)
                .setIconRenderPos(107, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22 * 4).build();

        infoTab = (TabInfo) TabInfo.builder()
                .setGuiSize(imageWidth, imageHeight)
                .setGuiPosition(leftPos, topPos)
                .setTabPosition(176 - 131, 2 + 22 * 2)
                .setOpenX(176)
                .setHiddenX(30)
                .setTabSize(152, 51)
                .setTabTitle(I18n.format("gui.stargate.info"))
                .setTabSide(TabSideEnum.RIGHT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 113)
                .setIconRenderPos(131, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(326, 88).build();

        irisTab.setOnTabClose(this::saveIrisCode);
        configTab.setOnTabClose(this::saveConfig);

        tabs.addAll(addressTabs.values());
        tabs.add(configTab);

        tabs.add(overlayTab);
        tabs.add(irisTab);

        tabs.add(infoTab);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        GuiHelper.currentStack = graphics.pose();
        if (irisTab.isVisible() && !irisTab.isOpen()) {
            if (irisTab.getIrisMode() != menu.gateTile.getIrisManager().getIrisMode())
                irisTab.updateValue(menu.gateTile.getIrisManager().getIrisMode());
            if (!Objects.equals(irisTab.getCode(), menu.gateTile.getIrisManager().getIrisCode()))
                irisTab.updateValue(menu.gateTile.getIrisManager().getIrisCode());
        }
        if (menu.gateTile.getConfig().getOptions().size() != configTab.getConfig().getOptions().size())
            configTab.updateConfig(menu.gateTile.getConfig(), true);
        renderTabsBg(graphics, mouseX, mouseY);
        graphics.pose().translate(0, 0, 0.2f);

        ITexture.bindTextureWithMc(BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(leftPos, topPos, 0, 0, imageWidth, imageHeight, 512, 512);

        // Draw cross on inactive capacitors
        for (int i = 0; i < 3 - menu.gateTile.getSupportedCapacitors(); i++) {
            drawModalRectWithCustomSizedTexture(leftPos + 151 - 18 * i, topPos + 27, 24, 180, 16, 16, 512, 512);
        }

        for (int i = menu.gateTile.getPowerTier(); i < 4; i++)
            drawModalRectWithCustomSizedTexture(leftPos + 10 + 39 * i, topPos + 69, 0, 173, 39, 6, 512, 512);

        int width = Math.round((energyStored / (float) JSGConfig.Stargate.stargateEnergyStorage.get() * 156));
        drawGradientRect(graphics.pose(), leftPos + 10, topPos + 69, leftPos + 10 + width, topPos + 69 + 6, 0xffcc2828, 0xff731616);

        // Draw ancient title
        int[] pos = menu.gateTile.getSymbolType().getAncientTitlePos();
        drawModalRectWithCustomSizedTexture(leftPos + 137, topPos + 4, pos[0], pos[1], 35, 8, 512, 512);

        boolean drawICFirstCable = false;
        boolean drawICSecondCable = false;

        // Draw cables
        for (int i = 0; i < 7; i++) {
            if (menu.getSlot(i).hasItem()) {
                if (i < 4) drawICFirstCable = true;
                // render activated wires/cables
                switch (i) {
                    // upgrades
                    case 0:
                        drawModalRectWithCustomSizedTexture(leftPos + 16, topPos + 44, 18, 239, 48 - 17, 253 - 238, 512, 512);
                        break;
                    case 1:
                        drawModalRectWithCustomSizedTexture(leftPos + 34, topPos + 44, 3, 239, 15 - 2, 249 - 238, 512, 512);
                        break;
                    case 2:
                        drawModalRectWithCustomSizedTexture(leftPos + 52, topPos + 44, 0, 239, 2, 6, 512, 512);
                        break;
                    case 3:
                        drawModalRectWithCustomSizedTexture(leftPos + 59, topPos + 44, 33, 254, 45 - 32, 264 - 253, 512, 512);
                        break;

                    // capacitors
                    case 4:
                        drawModalRectWithCustomSizedTexture(leftPos + 121, topPos + 44, 0, 225, 14, 236 - 224, 512, 512);
                        break;
                    case 5:
                        drawModalRectWithCustomSizedTexture(leftPos + 139, topPos + 44, 14, 225, 4, 230 - 224, 512, 512);
                        break;
                    case 6:
                        drawModalRectWithCustomSizedTexture(leftPos + 147, topPos + 44, 18, 225, 31 - 17, 238 - 224, 512, 512);
                        break;
                    default:
                        break;
                }
            }
        }
        if (menu.getSlot(8).hasItem()) {
            drawICSecondCable = true;
            ItemStack stack = menu.getSlot(8).getItem();
            if (stack.getItem() == JSGItems.UPGRADE_SHIELD.get()) {
                // render power cable for shield
                drawModalRectWithCustomSizedTexture(leftPos + 98, topPos + 33, 0, 197, 37, 224 - 196, 512, 512);
            }
            // render activated wire for iris upgrade slot
            drawModalRectWithCustomSizedTexture(leftPos + 59, topPos + 44, 0, 254, 31, 268 - 253, 512, 512);
        }

        // render cables from 1. IC to power line
        if (drawICFirstCable)
            drawModalRectWithCustomSizedTexture(leftPos + 50, topPos + 62, 0, 239, 2, 6, 512, 512);
        if (drawICSecondCable)
            drawModalRectWithCustomSizedTexture(leftPos + 54, topPos + 62, 0, 239, 2, 6, 512, 512);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.disableDepthTest();
        if (infoTab != null) {
            infoTab.clearStrings();
            int y = 22;

            // opened time
            long openedSince = menu.gateTile.getDialingManager().getConnection().getSince();
            if (openedSince > 0) {
                long openedSeconds = menu.gateTile.getDialingManager().getConnection().getSecondsOpen();
                String format = ChatFormatting.DARK_GREEN.toString();
                int maxTime = menu.gateTile.getConfig().getValueOrDefault(StargateConfigOptions.Common.TIME_LIMIT_TIME);
                if (openedSeconds >= (maxTime * 0.75))
                    format = ChatFormatting.YELLOW.toString();
                if (openedSeconds >= maxTime)
                    format = ChatFormatting.RED.toString();
                String openedTime = I18n.format("gui.stargate.state.opened") + " " + format + menu.gateTile.getOpenedSecondsToDisplayAsMinutes();
                infoTab.addString(new TabInfo.InfoString(openedTime, 4, y));
                y += 9;
            }
            // gate temp
            double gateTemperature = menu.gateTile.gateHeat;
            // iris temp
            double irisTemperature = menu.gateTile.irisHeat;

            String format = ChatFormatting.DARK_GREEN.toString();
            if (gateTemperature >= (StargateClassicBaseBE.GATE_MAX_HEAT * 0.5))
                format = ChatFormatting.YELLOW.toString();
            if (gateTemperature >= (StargateClassicBaseBE.GATE_MAX_HEAT * 0.75))
                format = ChatFormatting.RED.toString();

            infoTab.addString(new TabInfo.InfoString(I18n.format("gui.stargate.state.gate_temp") + " " + format + JSGConfig.General.temperatureUnit.get().getTemperatureToDisplay(TemperatureHelper.asKelvins(TemperatureHelper.asCelsius(gateTemperature).toKelvins()), 0), 4, y));
            y += 9;
            menu.gateTile.getIrisManager().updateIrisType();
            if (menu.gateTile.getIrisManager().hasPhysicalIris()) {
                //irisTemperature = gateTemperature;

                double maxHeat = menu.gateTile.getMaxIrisHeat();

                format = ChatFormatting.DARK_GREEN.toString();
                if (irisTemperature > (maxHeat * 0.5))
                    format = ChatFormatting.YELLOW.toString();
                if (irisTemperature > (maxHeat * 0.75))
                    format = ChatFormatting.RED.toString();

                infoTab.addString(new TabInfo.InfoString(I18n.format("gui.stargate.state.iris_temp") + " " + format + JSGConfig.General.temperatureUnit.get().getTemperatureToDisplay(TemperatureHelper.asKelvins(TemperatureHelper.asCelsius(irisTemperature).toKelvins()), 0), 4, y));
            }
        }

        this.renderBackground(graphics);

        boolean hasAddressUpgrade = false;
        boolean hasIrisUpgrade = !menu.getSlot(11).getItem().isEmpty();

        for (var entry : addressTabs.entrySet()) {
            entry.getValue().setVisible(false);
        }

        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = menu.getSlot(i).getItem();

            if (!itemStack.isEmpty()) {
                for (var entry : addressTabs.entrySet()) {
                    if (itemStack.getItem() == entry.getKey().getGlyphUpgrade()) {
                        entry.getValue().setVisible(true);
                    }
                }
                if ((itemStack.getItem() instanceof IUpgradeItem upgradeItem) && upgradeItem.getUpgrade() == StargateUpgrade.CHEVRON_UPGRADE) {
                    hasAddressUpgrade = true;
                }
            }
        }

        for (var entry : addressTabs.entrySet()) {
            entry.getValue().setMaxSymbols(entry.getKey().getMaxSymbolsDisplay(hasAddressUpgrade));
        }
        irisTab.setVisible(hasIrisUpgrade);
        configTab.setVisible(menu.hasCreative);

        LargeEnergyStorage energyStorageInternal = (LargeEnergyStorage) menu.gateTile.getStargateCapability(ForgeCapabilities.ENERGY, null).resolve().orElseThrow();
        energyStored = energyStorageInternal.getEnergyStoredInternally();
        maxEnergyStored = energyStorageInternal.getMaxEnergyStoredInternally();

        for (int i = 4; i < 7; i++) {
            Optional<IEnergyStorage> energyStorage = menu.getSlot(i).getItem().getCapability(ForgeCapabilities.ENERGY, null).resolve();

            if (energyStorage.isEmpty())
                continue;

            energyStored += energyStorage.get().getEnergyStored();
            maxEnergyStored += energyStorage.get().getMaxEnergyStored();
        }

        for (int i = 7; i < 9 + SymbolType.values(JSGSymbolUsages.STARGATES.get()).size(); i++) {
            if (i == 8) continue; // iris slot
            Tab.SlotTab slot = ((Tab.SlotTab) menu.getSlot(i)).updatePos();
            slot.setSlotIndex(i);
            menu.slots.set(i, slot);
        }

        graphics.pose().pushPose();
        super.render(graphics, mouseX, mouseY, partialTicks);

        renderTooltip(graphics, mouseX, mouseY);
        graphics.pose().popPose();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        String caps = I18n.format("gui.stargate.capacitors");
        graphics.drawString(font, caps, this.imageWidth - 8 - font.width(caps), 16, 4210752, false);

        String energyPercent = String.format("%.2f", energyStored / (float) maxEnergyStored * 100) + " %";
        graphics.drawString(font, energyPercent, this.imageWidth - 8 - font.width(energyPercent), 79, 4210752, false);

        graphics.drawString(font, I18n.format("gui.upgrades"), 8, 16, 4210752, false);
        graphics.drawString(font, I18n.format("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);

        renderTabsFg(graphics, mouseX, mouseY);

        int transferred = menu.gateTile.getEnergyManager().getTransferredLastTick();
        ChatFormatting transferredFormatting = ChatFormatting.GRAY;
        String transferredSign = "";

        if (transferred > 0) {
            transferredFormatting = ChatFormatting.GREEN;
            transferredSign = "+";
        } else if (transferred < 0) {
            transferredFormatting = ChatFormatting.RED;
        }

        double toClose = menu.gateTile.getEnergyManager().getSecondsToClose();
        ChatFormatting toCloseFormatting = ChatFormatting.GRAY;

        if (toClose > 0) {
            if (toClose < JSGConfig.Stargate.instabilitySeconds.get())
                toCloseFormatting = ChatFormatting.DARK_RED;
            else
                toCloseFormatting = ChatFormatting.GREEN;
        }

        if (isPointInRegion(10, 69, 156, 6, mouseX - getGuiLeft(), mouseY - getGuiTop())) {
            List<String> power = new ArrayList<>();
            power.add(I18n.format("gui.energyBuffer"));
            power.add(ChatFormatting.GRAY + String.format("%,d / %,d FE", energyStored, maxEnergyStored));
            power.add(transferredFormatting + transferredSign + String.format("%,d FE/t", transferred));
            if (toClose >= 0) {
                power.add(toCloseFormatting + String.format("%.2f s", toClose));
            }

            drawHoveringText(graphics, font, power, mouseX - leftPos, mouseY - topPos);
        }
    }

    @Override
    public void onClose() {
        saveConfig();
        saveIrisCode();
        super.onClose();
    }

    private void saveConfig() {
        JSGCorePacketHandler.sendToServer(new SaveConfigToServer(pos, configTab.config));
        menu.gateTile.setConfig(configTab.getConfig());
    }

    private void saveIrisCode() {
        JSGPacketHandler.sendToServer(new SaveIrisCodeToServer(pos, irisTab.getCode(), irisTab.getIrisMode()));
        menu.gateTile.getIrisManager().setIrisCode(irisTab.getCode());
        menu.gateTile.getIrisManager().setIrisMode(irisTab.getIrisMode());
    }
}
