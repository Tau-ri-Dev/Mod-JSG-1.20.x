package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.common.stargate.network.symbol.SymbolMilkyWayProvider;
import dev.tauri.jsg.common.stargate.network.symbol.SymbolPegasusProvider;
import dev.tauri.jsg.common.stargate.network.symbol.SymbolUniverseProvider;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import net.minecraftforge.registries.RegistryObject;

public class JSGSymbolTypes {
    public static final RegistryObject<SymbolType<SymbolMilkyWayEnum>> MILKYWAY = JSGApi.REGISTRY_HELPER.symbolType()
            .register("milkyway", SymbolMilkyWayProvider::new);
    public static final RegistryObject<SymbolType<SymbolPegasusEnum>> PEGASUS = JSGApi.REGISTRY_HELPER.symbolType()
            .register("pegasus", SymbolPegasusProvider::new);
    public static final RegistryObject<SymbolType<SymbolUniverseEnum>> UNIVERSE = JSGApi.REGISTRY_HELPER.symbolType()
            .register("universe", SymbolUniverseProvider::new);

    public static void init() {
    }
}
