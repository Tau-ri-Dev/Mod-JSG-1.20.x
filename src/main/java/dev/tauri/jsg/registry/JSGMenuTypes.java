package dev.tauri.jsg.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.registry.helper.RegistryHelper;
import dev.tauri.jsg.screen.inventory.dialhomedevice.DHDContainer;
import dev.tauri.jsg.screen.inventory.dialhomedevice.DHDContainerGui;
import dev.tauri.jsg.screen.inventory.stargate.StargateContainer;
import dev.tauri.jsg.screen.inventory.stargate.StargateContainerGui;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;

public class JSGMenuTypes {

    public static final RegistryObject<MenuType<StargateContainer>> STARGATE_MENU_TYPE = JSGApi.REGISTRY_HELPER.menu().register("stargate_container", RegistryHelper.menu(StargateContainer::new));
    public static final RegistryObject<MenuType<DHDContainer>> DHD_MENU_TYPE = JSGApi.REGISTRY_HELPER.menu().register("dhd_container", RegistryHelper.menu(DHDContainer::new));

    public static void init() {
        JSGApi.REGISTRY_HELPER.guiRegister(() -> {
            RegistryHelper.bindScreenToMenu(STARGATE_MENU_TYPE.get(), StargateContainerGui::new);
            RegistryHelper.bindScreenToMenu(DHD_MENU_TYPE.get(), DHDContainerGui::new);
        });
    }
}
