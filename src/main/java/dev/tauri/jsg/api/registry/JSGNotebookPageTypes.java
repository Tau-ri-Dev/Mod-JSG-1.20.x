package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.client.entity.AddressPageRenderable;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.helper.DimensionsHelper;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.stargate.network.StargateNetwork;
import net.minecraftforge.registries.RegistryObject;

public class JSGNotebookPageTypes {

    public static final RegistryObject<NotebookPageType<StargateAddressData>> STARGATE_ADDRESS = JSGApi.REGISTRY_HELPER.notebookPage().register("stargate_address", () -> new NotebookPageType<>(
            AddressPageRenderable::new,
            StargateAddressData::new,
            stargateAddressData -> stargateAddressData != null ? stargateAddressData.serializeNBT() : null,
            (level, pos, random, data) -> {
                var symbolsToDisplay = Stargate.getRandomSymbolsToDisplay(8, (symbolIndex) -> {
                    if (symbolIndex == 9) return !(random.nextFloat() < 0.1f);
                    return !(random.nextFloat() < 0.05f);
                });
                var sgn = StargateNetwork.INSTANCE;
                var dim = (data.contains("regenerationForDim") ? DimensionsHelper.getDimension(JSGMapping.rl(data.getString("regenerationForDim"))) : null);
                var symbolType = (data.contains("regenerationForSymbolType") ? SymbolType.byId(JSGMapping.rl(data.getString("regenerationForSymbolType"))) : null);
                if (symbolType == null) symbolType = JSGSymbolTypes.MILKYWAY.get();
                var gate = sgn.getRandomAddress(random, symbolType, null, (sgPos) -> {
                    if (dim == null) return true;
                    return (dim == sgPos.dimension);
                });
                if (gate == null) return null;
                var savedAddress = new StargateAddressDynamic(gate.second()).addOriginIfMissingAndImmutable();
                var origin = Stargate.getOriginFor(symbolType.getPointOfOriginType(), level.dimension(), BiomeOverlayInstance.getBiomeOverlayByBlockPos(level, pos, false));
                return new StargateAddressData(savedAddress, symbolsToDisplay, origin);
            },
            (stack, level, tooltip) -> {
            }
    ));

    public static void init() {
    }
}
