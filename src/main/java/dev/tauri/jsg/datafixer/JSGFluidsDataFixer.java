package dev.tauri.jsg.datafixer;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

public class JSGFluidsDataFixer {
    public static void fixMappings(List<MissingMappingsEvent.Mapping<FluidType>> typesToFix, List<MissingMappingsEvent.Mapping<Fluid>> fluidsToFix) {
        typesToFix.forEach(mapping -> {
            var newKey = JSGMapping.rl(JSGCore.MOD_ID, mapping.getKey().getPath());
            var coreFT = ForgeRegistries.FLUID_TYPES.get().getValue(newKey);
            if (coreFT == null) {
                mapping.warn();
                return;
            }
            mapping.remap(coreFT);
        });

        fluidsToFix.forEach(mapping -> {
            var newKey = JSGMapping.rl(JSGCore.MOD_ID, mapping.getKey().getPath());
            var coreFluid = ForgeRegistries.FLUIDS.getValue(newKey);
            if (coreFluid == null) {
                mapping.warn();
                return;
            }
            mapping.remap(coreFluid);
        });
    }
}
