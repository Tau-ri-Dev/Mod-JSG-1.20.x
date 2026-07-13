package dev.tauri.jsg.client.screen.inventory;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.container.ZPMHubContainer;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.ZPMHubAnimationToServer;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ZPMHubContainerGui<T extends ZPMHubContainer> extends AbstractContainerScreen<T> {

    public ZPMHubContainerGui(T container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 179;
        this.titleLabelX = 8;
        this.titleLabelY = 16;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    protected ResourceLocation getBackground() {
        return JSGMapping.rl(JSG.MOD_ID, "textures/gui/container_zpmhub.png");
    }

    protected int getButtonY() {
        return 51;
    }

    protected int getBarY() {
        return 75;
    }

    protected int getPercentTextY() {
        return 85;
    }

    protected long[] computeEnergy() {
        long energyStored = 0;
        long maxEnergyStored = 0;
        for (int i = 0; i < menu.getZPMSlotCount(); i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (stack.isEmpty()) continue;
            var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (storage instanceof JSGEnergyStorage jsgStorage) {
                energyStored += jsgStorage.getTrueEnergyStored();
                maxEnergyStored += jsgStorage.getTrueMaxEnergyStored();
            }
        }
        return new long[]{energyStored, maxEnergyStored};
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        long[] energy = computeEnergy();
        if (isHovering(10, getBarY(), 156, 6, mouseX, mouseY)) {
            long transferred = menu.hubTile.getEnergyTransferredLastTick();
            ChatFormatting transferredFormatting = ChatFormatting.GRAY;
            String transferredSign = "";
            if (transferred > 0) {
                transferredFormatting = ChatFormatting.GREEN;
                transferredSign = "+";
            } else if (transferred < 0) {
                transferredFormatting = ChatFormatting.RED;
            }
            List<Component> power = new ArrayList<>();
            power.add(Component.literal(I18n.format("gui.energyBuffer")));
            power.add(Component.literal(ChatFormatting.GRAY + String.format("%,d / %,d FE", energy[0], energy[1])));
            power.add(Component.literal(transferredFormatting + transferredSign + String.format("%,d FE/t", transferred)));
            graphics.renderComponentTooltip(font, power, mouseX, mouseY);
        } else if (isHovering(9, getButtonY(), 16, 16, mouseX, mouseY)) {
            String key;
            if (menu.hubTile.isAnimating) key = "gui.zpmhub.inProgress";
            else if (menu.hubTile.isSlidingUp) key = "gui.zpmhub.slideDown";
            else key = "gui.zpmhub.slideUp";
            graphics.renderTooltip(font, Component.literal(I18n.format(key)), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(getBackground(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

        long[] energy = computeEnergy();
        int width = energy[1] == 0 ? 0 : Math.round(energy[0] / ((float) energy[1]) * 156);
        graphics.fillGradient(leftPos + 10, topPos + getBarY(), leftPos + 10 + width, topPos + getBarY() + 6, 0xffcc2828, 0xff731616);

        // Slide button
        graphics.blit(getBackground(), leftPos + 9, topPos + getButtonY(), 176, 0, 16, 16);
        if (menu.hubTile.isAnimating || !menu.hubTile.isSlidingUp) {
            // Alert icon: ZPM slots are locked
            graphics.blit(getBackground(), leftPos + 9 + 7, topPos + getButtonY() + 6, 176, 16, 16, 16);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        long[] energy = computeEnergy();
        String energyPercent = String.format("%.2f", (energy[1] != 0 ? (energy[0] / (float) energy[1] * 100) : 0)) + " %";
        graphics.drawString(font, energyPercent, imageWidth - 8 - font.width(energyPercent), getPercentTextY(), 4210752, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(9, getButtonY(), 16, 16, mouseX, mouseY)) {
            if (!menu.hubTile.isAnimating) {
                JSGPacketHandler.sendToServer(new ZPMHubAnimationToServer(menu.hubTile.getBlockPos()));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
