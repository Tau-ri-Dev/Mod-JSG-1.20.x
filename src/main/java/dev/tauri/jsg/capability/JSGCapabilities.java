package dev.tauri.jsg.capability;

import dev.tauri.jsg.jub.JUBDevice;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class JSGCapabilities {
    public static final Capability<JUBDevice> JUST_UNIVERSAL_BUS = CapabilityManager.get(new CapabilityToken<>() {
    });
}
