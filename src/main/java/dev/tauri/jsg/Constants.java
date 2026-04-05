package dev.tauri.jsg;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.registry.helper.FluidHelper;
import dev.tauri.jsg.core.common.registry.helper.builder.block.BlockRegistryHelperGeneric;
import dev.tauri.jsg.core.common.registry.helper.builder.item.ItemRegistryHelperGeneric;

public class Constants {

    public static final BlockRegistryHelperGeneric JSG_BLOCK_HELPER = new BlockRegistryHelperGeneric(JSGApi.REGISTRY_HELPER::block);

    public static final ItemRegistryHelperGeneric JSG_ITEM_HELPER = new ItemRegistryHelperGeneric(JSGApi.REGISTRY_HELPER::item);

    public static final FluidHelper JSG_FLUID_HELPER = new FluidHelper(JSGApi.REGISTRY_HELPER::fluid, JSGApi.REGISTRY_HELPER::fluidType, JSGApi.REGISTRY_HELPER::item, JSGApi.REGISTRY_HELPER::block);

    public static void init() {
    }
}
