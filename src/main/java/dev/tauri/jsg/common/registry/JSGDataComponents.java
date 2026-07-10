package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.JSG;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * ItemStack data components (1.21 replacement for stack NBT that vanilla systems consume).
 */
public class JSGDataComponents {
    public static final DeferredRegister.DataComponents REGISTER =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, JSG.MOD_ID);

    /** Fluid content of the DHD fluid tank item (FluidHandlerItemStack backing store). */
    public static final Supplier<DataComponentType<SimpleFluidContent>> DHD_FLUID =
            REGISTER.registerComponentType("dhd_fluid",
                    builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));
}
