package dev.tauri.jsg.api.stargate;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface StargatePointOfOriginsDefaults {
    String VARIANT_DHD = "dhd.obj";
    String VARIANT_DHD_LIGHT = "dhd_light.obj";
    String VARIANT_GATE = "stargate.obj";
    String VARIANT_ICON = "icon.png";
    String VARIANT_GATE_PNG = "stargate.png";
    String VARIANT_GATE_OFF_PNG = "stargate_off.png";

    @Nullable
    static PointOfOrigin get(IPointOfOriginType type, ResourceLocation location) {
        return PointOfOriginsLoader.INSTANCE.getOriginByIdOrElse(type, location, null);
    }

    interface MilkyWay {
        ResourceLocation DEFAULT = JSGMapping.rl(JSGApi.MOD_ID, "default");
        ResourceLocation P7J_989 = JSGMapping.rl(JSGApi.MOD_ID, "p7j989");
        ResourceLocation NETHER = JSGMapping.rl(JSGApi.MOD_ID, "nether");
        ResourceLocation ANTARCTICA = JSGMapping.rl(JSGApi.MOD_ID, "antarctica");
        ResourceLocation ABYDOS = JSGMapping.rl(JSGApi.MOD_ID, "abydos");
        ResourceLocation TAURI = JSGMapping.rl(JSGApi.MOD_ID, "tauri");
    }
}
