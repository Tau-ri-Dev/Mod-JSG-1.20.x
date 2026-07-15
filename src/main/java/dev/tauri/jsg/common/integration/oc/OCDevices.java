package dev.tauri.jsg.common.integration.oc;

import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.common.integration.oc.OCDevice;

public class OCDevices {
    public static final OCDevice STARGATE_ABSTRACT = new OCDevice("STARGATE_ABSTRACT", "stargate", (tile) -> new StargateAbstractOCMethods<>((StargateAbstractBaseBE<?, ?>) tile));
    public static final OCDevice STARGATE_CLASSIC = new OCDevice("STARGATE_CLASSIC", "stargate", StargateClassicOCMethods::new);
    public static final OCDevice PRINTER = new OCDevice("PRINTER", "printer", PrinterOCMethods::new);


    public static void load() {

    }
}
