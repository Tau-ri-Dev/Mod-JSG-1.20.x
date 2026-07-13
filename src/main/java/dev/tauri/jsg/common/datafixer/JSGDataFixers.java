package dev.tauri.jsg.common.datafixer;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * MissingMappingsEvent is gone on NeoForge; renamed/moved registry entries are handled
 * with registry aliases instead. Must run during mod construction (before registry events).
 * <p>
 * The 1.20.1 fixers also dynamically remapped any missing jsg:X to jsg_core:X (content
 * that moved into the core). Aliases only apply on lookup misses, so blanket-aliasing
 * every core entry reproduces that behavior safely.
 */
public class JSGDataFixers {
    public static void registerAliases() {
        var items = JSGApi.REGISTRY_HELPER.item().unwrap();

        // explicit renames within the jsg namespace
        items.addAlias(JSGMapping.rl(JSG.MOD_ID, "crystal_control_dhd"), JSGItems.MILKYWAY_DHD_MAIN_CRYSTAL.getId());
        items.addAlias(JSGMapping.rl(JSG.MOD_ID, "crystal_control_pegasus_dhd"), JSGItems.PEGASUS_DHD_MAIN_CRYSTAL.getId());
        items.addAlias(JSGMapping.rl(JSG.MOD_ID, "dhd_brb"), JSGItems.MILKYWAY_DHD_ACTIVATION_BUTTON.getId());
        items.addAlias(JSGMapping.rl(JSG.MOD_ID, "dhd_bbb"), JSGItems.PEGASUS_DHD_ACTIVATION_BUTTON.getId());
        items.addAlias(JSGMapping.rl(JSG.MOD_ID, "capacitor_block"), JSGMapping.rl(JSGCore.MOD_ID, "crystal_energy_basic"));
        items.addAlias(JSGMapping.rl(JSG.MOD_ID, "capacitor_block_creative"), JSGMapping.rl(JSGCore.MOD_ID, "crystal_energy_creative"));

        // jsg:X -> jsg_core:X fallback for everything that moved into the core
        aliasCoreEntries(items, JSGCore.REGISTRY_HELPER.item().unwrap());
        aliasCoreEntries(JSGApi.REGISTRY_HELPER.block().unwrap(), JSGCore.REGISTRY_HELPER.block().unwrap());
        aliasCoreEntries(JSGApi.REGISTRY_HELPER.be().unwrap(), JSGCore.REGISTRY_HELPER.be().unwrap());
        aliasCoreEntries(JSGApi.REGISTRY_HELPER.sound().unwrap(), JSGCore.REGISTRY_HELPER.sound().unwrap());
        aliasCoreEntries(JSGApi.REGISTRY_HELPER.fluid().unwrap(), JSGCore.REGISTRY_HELPER.fluid().unwrap());
        aliasCoreEntries(JSGApi.REGISTRY_HELPER.fluidType().unwrap(), JSGCore.REGISTRY_HELPER.fluidType().unwrap());
    }

    private static <T> void aliasCoreEntries(DeferredRegister<T> target, DeferredRegister<T> coreRegister) {
        for (var holder : coreRegister.getEntries()) {
            target.addAlias(JSGMapping.rl(JSG.MOD_ID, holder.getId().getPath()), holder.getId());
        }
    }
}
