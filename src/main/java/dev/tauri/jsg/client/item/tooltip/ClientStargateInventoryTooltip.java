package dev.tauri.jsg.client.item.tooltip;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.power.PowerUtils;
import dev.tauri.jsg.common.item.tooltips.ServerStargateInventoryTooltip;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.power.general.SmallEnergyStorage;
import dev.tauri.jsg.core.common.util.JSGItemStackHandler;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClientStargateInventoryTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = JSGMapping.rl(JSG.MOD_ID, "textures/gui/itemstack_container_stargate.png");
    protected final ItemStack stack;

    public ClientStargateInventoryTooltip(ServerStargateInventoryTooltip serverTooltip) {
        stack = serverTooltip.stack();
    }

    @Override
    public int getHeight() {
        if (!ItemNBT.hasTag(stack)) return 0;
        return 57;
    }

    @Override
    public int getWidth(Font font) {
        if (!ItemNBT.hasTag(stack)) return 0;
        return 90;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        if (!ItemNBT.hasTag(stack)) return;
        GuiHelper.currentStack = graphics.pose();
        var compound = ItemNBT.getOrCreateTag(stack);
        int offsetY = 0;
        var energyResult = new InventoryRenderEnergyResult(0, 0, 0);
        if (compound.contains("itemHandler")) {
            var handler = new JSGItemStackHandler(1);
            handler.deserializeNBT(dev.tauri.jsg.common.helpers.CurrentRegistries.getOrThrow(), compound.getCompound("itemHandler"));
            energyResult = renderInventory(font, x, y, graphics, handler, compound);
            offsetY += 44;
        }
        var energyStorage = PowerUtils.getSmall(() -> {});
        if (compound.contains("stargateEnergyManager"))
            energyStorage.deserializeNBT(compound.getCompound("stargateEnergyManager").getCompound("energyStorage"));
        renderEnergyStorage(font, x, y + offsetY, graphics, energyResult, energyStorage);
    }

    protected void renderEnergyStorage(Font font, int x, int y, GuiGraphics graphics, InventoryRenderEnergyResult inventoryRenderEnergyResult, SmallEnergyStorage energyStorage) {
        ITexture.bindTextureWithMc(TEXTURE_LOCATION);
        GuiHelper.drawModalRectWithCustomSizedTexture(x, y, 0, 44, 90, 7, 128, 128);

        var energyStored = energyStorage.getTrueEnergyStored() + inventoryRenderEnergyResult.additionalEnergy();
        var maxEnergyStored = energyStorage.getTrueMaxEnergyStored() + inventoryRenderEnergyResult.additionalCapacity();
        var blockedSlots = inventoryRenderEnergyResult.blockedOutSlots();

        var maxWidth = 87;
        for (int i = 2; i >= (3 - blockedSlots); i--) {
            maxWidth -= 22;
            GuiHelper.drawModalRectWithCustomSizedTexture(x + 23 + (22 * i), y + 1, 0, 52, 22, 5, 128, 128);
        }
        int width = Math.round((energyStored / (float) maxEnergyStored * maxWidth));
        GuiHelper.drawGradientRect(graphics.pose(), x + 1, y + 1, x + 1 + width, y + 6, 0xffcc2828, 0xff731616);
    }

    protected InventoryRenderEnergyResult renderInventory(Font font, int x, int y, GuiGraphics graphics, JSGItemStackHandler itemStackHandler, CompoundTag compound) {
        int capacitorsCount = 3;
        int maxCapacitors = 3;
        long additionalEnergy = 0;
        long additionalCapacity = 0;
        if (compound.contains("config")) {
            try {
                var config = new BEConfig(() -> {
                }, StargateConfigOptions.Classic.HOLDER);
                config.deserializeNBT(compound.getCompound("config"));
                var capsMaybe = config.getValue(StargateConfigOptions.Classic.MAX_CAPACITORS);
                if (capsMaybe.isPresent())
                    maxCapacitors = capsMaybe.get();
            } catch (Exception ignored) {
            }
        }
        if (itemStackHandler.getSize() > 0) {
            capacitorsCount = 0;
            // inventory background
            ITexture.bindTextureWithMc(TEXTURE_LOCATION);
            GuiHelper.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 90, 40, 128, 128);
            for (int i = 2; i >= maxCapacitors; i--) {
                // blocked slots
                GuiHelper.drawModalRectWithCustomSizedTexture(x + 1 + (18 * i), y + 23, 0, 58, 16, 16, 128, 128);
            }
            for (int slot = 0; slot < itemStackHandler.getSize(); slot++) {
                var item = itemStackHandler.getStackInSlot(slot);
                // pages slots are ignored here - not rendered
                if (slot < 4) {
                    // upgrades
                    renderItemStack(item, x + 18 * slot, y, graphics, font, slot);
                } else if (slot < 7) {
                    // capacitors
                    renderItemStack(item, x + 18 * (slot - 4), y + 22, graphics, font, slot);
                    var energyCap = java.util.Optional.ofNullable(item.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM));
                    if (energyCap.isPresent() && energyCap.get() instanceof JSGEnergyStorage energyStorage) {
                        additionalEnergy += energyStorage.getTrueEnergyStored();
                        additionalCapacity += energyStorage.getTrueMaxEnergyStored();
                        capacitorsCount++;
                    }
                } else if (slot == 7) {
                    // biome overlay slot
                    renderItemStack(item, x + 72, y + 22, graphics, font, slot);
                } else if (slot == 8) {
                    // iris slot
                    renderItemStack(item, x + 72, y, graphics, font, slot);
                }
            }
        }
        if (capacitorsCount > 3) capacitorsCount = 3;
        return new InventoryRenderEnergyResult(additionalEnergy, additionalCapacity, 3 - capacitorsCount);
    }

    protected void renderItemStack(ItemStack item, int x, int y, GuiGraphics graphics, Font font, int slot) {
        graphics.renderItem(item, x + 1, y + 1, slot);
        graphics.renderItemDecorations(font, item, x + 1, y + 1);
    }

    public record InventoryRenderEnergyResult(long additionalEnergy, long additionalCapacity, int blockedOutSlots) {
    }
}
