package dev.tauri.jsg.common.capability;

import dev.tauri.jsg.common.registry.JSGDataComponents;
import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

public class DHDFluidHandlerItemStack extends FluidHandlerItemStack {
    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public DHDFluidHandlerItemStack(@NotNull ItemStack container, int capacity) {
        super(JSGDataComponents.DHD_FLUID::get, container, capacity);
        if (ItemNBT.getOrCreateTag(container).contains("savedCapacity"))
            this.capacity = ItemNBT.getOrCreateTag(container).getInt("savedCapacity");
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        ItemNBT.update(container, tag -> tag.putInt("savedCapacity", capacity));
    }

    @Override
    public void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid() == CoreFluids.MOLTEN_NAQUADAH_REFINED.get();
    }
}
