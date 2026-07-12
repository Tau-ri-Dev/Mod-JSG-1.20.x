package dev.tauri.jsg.common.item.energy;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.common.registry.JSGTabs;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.power.general.ItemEnergyStorage;
import dev.tauri.jsg.core.common.util.ItemNBT;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

/**
 * BlockItem of the Zero Point Module. Carries the module's energy in the stack
 * (via {@link ItemEnergyStorage}, exposed as {@code Capabilities.EnergyStorage.ITEM}).
 */
public class ZPMItemBlock extends JSGBlockItem {
    protected final boolean creative;

    public ZPMItemBlock(Block block, boolean creative) {
        super(block, new Item.Properties().stacksTo(1), List.of(JSGTabs.TAB_MACHINES));
        this.creative = creative;
    }

    public boolean isCreative() {
        return creative;
    }

    public ItemEnergyStorage createEnergyStorage(final ItemStack stack) {
        final boolean creative = this.creative;
        return new ItemEnergyStorage(stack, JSGConfig.ZPM.zpmCapacity.get(), 0, Long.MAX_VALUE) {
            @Override
            public boolean isCreative() {
                return creative;
            }

            @Override
            public long setEnergy(long energy, boolean notify) {
                return super.setEnergy(creative ? getTrueMaxEnergyStored() : energy, notify);
            }

            @Override
            public long getTrueEnergyStored() {
                if (creative)
                    return getTrueMaxEnergyStored();
                return super.getTrueEnergyStored();
            }

            @Override
            public long extractLongEnergy(long max, boolean simulate) {
                if (creative)
                    return max;
                return super.extractLongEnergy(max, simulate);
            }

            @Override
            public long receiveLongEnergy(long max, boolean simulate) {
                return 0;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return !creative;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        if (creative) return Item.MAX_BAR_WIDTH;
        return Optional.ofNullable(itemStack.getCapability(Capabilities.EnergyStorage.ITEM))
                .map(energyStorage -> (int) (JSGEnergyStorage.getEnergyPercent(energyStorage) * Item.MAX_BAR_WIDTH))
                .orElse(0);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = getBarWidth(itemStack) / (float) Item.MAX_BAR_WIDTH;
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        if (creative) return;
        Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM)).ifPresent(energyStorage -> {
            components.add(Component.literal(ChatFormatting.GRAY + JSGEnergyStorage.energyToString(energyStorage)));
            components.add(Component.literal(ChatFormatting.GRAY + String.format("%.2f", JSGEnergyStorage.getEnergyPercent(energyStorage) * 100) + "%"));
        });
    }

    @Override
    public void addAdditional(CreativeModeTab.Output output) {
        if (creative) return;
        var stack = new ItemStack(this);
        ItemNBT.update(stack, tag -> tag.putLong("energy", JSGConfig.ZPM.zpmCapacity.get()));
        output.accept(stack);
    }
}
