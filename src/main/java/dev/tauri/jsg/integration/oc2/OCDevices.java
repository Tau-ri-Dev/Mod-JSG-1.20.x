package dev.tauri.jsg.integration.oc2;

import dev.tauri.jsg.core.common.integration.oc2.OCDevice;

public class OCDevices {
    public static final OCDevice STARGATE_ABSTRACT = new OCDevice("STARGATE_ABSTRACT", "stargate", StargateAbstractOCMethods::new);
    public static final OCDevice STARGATE_CLASSIC = new OCDevice("STARGATE_CLASSIC", "stargate", StargateClassicOCMethods::new);
    public static final OCDevice PRINTER = new OCDevice("PRINTER", "printer", PrinterOCMethods::new);


    public static void load() {

    }
}
