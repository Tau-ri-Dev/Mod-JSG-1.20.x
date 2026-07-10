package dev.tauri.jsg.common.capability;

import dev.tauri.jsg.common.jub.JUBDevice;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.capabilities.CapabilityManager;
import net.neoforged.neoforge.capabilities.CapabilityToken;

public class JSGCapabilities {
    public static final Capability<JUBDevice> JUST_UNIVERSAL_BUS = CapabilityManager.get(new CapabilityToken<>() {
    });
}
