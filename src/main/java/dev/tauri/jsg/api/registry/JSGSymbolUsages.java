package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.symbol.SymbolUsage;
import dev.tauri.jsg.core.common.registry.RegistryObject;

public class JSGSymbolUsages {
    public static final RegistryObject<SymbolUsage> STARGATES = JSGApi.REGISTRY_HELPER.symbolUsage().register("stargates", () -> new SymbolUsage("stargates", JSGNotebookPageTypes.STARGATE_ADDRESS::get));

    public static void init() {
    }
}
