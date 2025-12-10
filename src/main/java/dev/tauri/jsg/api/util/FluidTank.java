package dev.tauri.jsg.api.util;

import net.minecraftforge.fluids.FluidStack;

public class FluidTank extends net.minecraftforge.fluids.capability.templates.FluidTank {
    public FluidTank(FluidStack stack, int capacity) {
        super(capacity, (e) -> e.getFluid() == stack.getFluid());
    }
}
