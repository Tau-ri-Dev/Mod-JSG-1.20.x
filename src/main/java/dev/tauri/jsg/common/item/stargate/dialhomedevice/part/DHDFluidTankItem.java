package dev.tauri.jsg.common.item.stargate.dialhomedevice.part;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.item.IDHDFluidTank;
import dev.tauri.jsg.common.capability.DHDFluidHandlerItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class DHDFluidTankItem extends DHDAbstractPartItem implements IDHDFluidTank {
    public DHDFluidTankItem(Properties properties, List<RegistryObject<CreativeModeTab>> tabs, boolean mandatory, int raycasterId) {
        super(properties, tabs, mandatory, raycasterId);
    }

    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt) {
        return new DHDFluidHandlerItemStack(stack, JSGConfig.DialHomeDevice.fluidCapacity.get());
    }

    @Override
    public DHDFluidHandlerItemStack getTank(ItemStack stack) {
        return (DHDFluidHandlerItemStack) stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElseThrow(IllegalStateException::new);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, components, tooltipFlag);

    }
}
