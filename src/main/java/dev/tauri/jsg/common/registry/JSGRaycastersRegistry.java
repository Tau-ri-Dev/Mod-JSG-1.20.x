package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.raycaster.RaycasterMilkyWayDHD;
import dev.tauri.jsg.common.raycaster.RaycasterPegasusDHD;
import dev.tauri.jsg.common.raycaster.RaycasterPrinter;
import net.minecraftforge.registries.RegistryObject;

public class JSGRaycastersRegistry {
    public static final RegistryObject<RaycasterMilkyWayDHD> MILKYWAY_DHD_RAYCASTER = JSGApi.REGISTRY_HELPER.raycaster().register("milkyway_dhd", RaycasterMilkyWayDHD::new);
    public static final RegistryObject<RaycasterPegasusDHD> PEGASUS_DHD_RAYCASTER = JSGApi.REGISTRY_HELPER.raycaster().register("pegasus_dhd", RaycasterPegasusDHD::new);
    public static final RegistryObject<RaycasterPrinter> PRINTER_RAYCASTER = JSGApi.REGISTRY_HELPER.raycaster().register("printer", RaycasterPrinter::new);

    public static void init() {
    }
}
