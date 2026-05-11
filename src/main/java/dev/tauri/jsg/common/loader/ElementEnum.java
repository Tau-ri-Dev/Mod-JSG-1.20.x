package dev.tauri.jsg.common.loader;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.client.IModelsHolder;
import dev.tauri.jsg.core.client.LoadersHolder;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to store all models in this module
 */
public enum ElementEnum implements IModelsHolder {
    // --------------------------------------------------------------------------------------------
    // Milky Way

    MILKYWAY_GATE("milkyway/gate.obj", "milkyway/gatering", true),
    MILKYWAY_RING("milkyway/ring.obj", "milkyway/gatering", true),

    MILKYWAY_DHD_BASE("milkyway/dhd/dhd_base.obj", "milkyway/dhd/dhd_base", true),
    MILKYWAY_DHD_BUTTON_CONSOLE("milkyway/dhd/button_console.obj", "milkyway/dhd/dhd_base", true),
    MILKYWAY_DHD_CRYSTAL_HOLDER("milkyway/dhd/crystal_holder.obj", "milkyway/dhd/dhd_base", true),
    MILKYWAY_DHD_UPGRADE_CRYSTAL("milkyway/dhd/dhd_upgrade_crystal.obj", "milkyway/dhd/upgrade_crystal_base", true),
    MILKYWAY_DHD_UPGRADE_COVER("milkyway/dhd/upgrade_cover.obj", "milkyway/dhd/dhd_base", true),
    MILKYWAY_DHD_CRYSTALS("milkyway/dhd/crystals.obj", "milkyway/dhd/dhd_crystals", true),
    MILKYWAY_DHD_CONTROL_CRYSTAL("milkyway/dhd/control_crystal.obj", "milkyway/dhd/dhd_crystals", true),
    MILKYWAY_DHD_FLUID_TANK_BASE("milkyway/dhd/tank_base.obj", "milkyway/dhd/tank_base", true),
    MILKYWAY_DHD_FLUID_TANK_GLASS("milkyway/dhd/tank_glass.obj", "milkyway/dhd/tank_glass", true),
    MILKYWAY_DHD_FLUID_TANK_FLUID("milkyway/dhd/tank_inside.obj", "milkyway/dhd/tank_base", true),

    MILKYWAY_CHEVRON_LIGHT("milkyway/chevron_light.obj", "milkyway/chevron", true),
    MILKYWAY_CHEVRON_FRAME("milkyway/chevron_frame.obj", "milkyway/gatering", true),
    MILKYWAY_CHEVRON_MOVING("milkyway/chevron_moving.obj", "milkyway/chevron", true),
    MILKYWAY_CHEVRON_BACK("milkyway/chevron_back.obj", "milkyway/gatering", true),

    ORLIN_GATE("orlin/orlin_gate.obj", "orlin/orlin_gate_base", true),
    ORLIN_GATE_BURNT("orlin/orlin_gate_burnt.obj", "orlin/orlin_gate_burnt", true),
    ORLIN_STAND("orlin/orlin_stand.obj", "orlin/orlin_stand_base", true),

    // --------------------------------------------------------------------------------------------
    // Universe

    UNIVERSE_GATE("universe/universe_gate.obj", "universe/universe_gate", true),
    UNIVERSE_CHEVRON("universe/universe_chevron.obj", "universe/universe_chevron", true),
    UNIVERSE_SYMBOL("universe/universe_chevron.obj", "universe/universe_chevron_light", true),
    UNIVERSE_DIALER("universe/universe_dialer.obj", "universe/universe_dialer", true),
    UNIVERSE_DIALER_BROKEN("universe/universe_dialer.obj", "universe/universe_dialer_broken", true),

    // --------------------------------------------------------------------------------------------
    // Pegasus

    PEGASUS_GATE("pegasus/gate.obj", "pegasus/gatering", true),
    PEGASUS_RING("pegasus/ring_atlantis.obj", "pegasus/gatering", true),

    PEGASUS_DHD_BASE("milkyway/dhd/dhd_base.obj", "pegasus/dhd/dhd_base", true),
    PEGASUS_DHD_BUTTON_CONSOLE("pegasus/dhd/button_console.obj", "pegasus/dhd/dhd_base", true),
    PEGASUS_DHD_CRYSTAL_HOLDER("milkyway/dhd/crystal_holder.obj", "pegasus/dhd/dhd_base", true),
    PEGASUS_DHD_UPGRADE_CRYSTAL("milkyway/dhd/dhd_upgrade_crystal.obj", "milkyway/dhd/upgrade_crystal_base", true),
    PEGASUS_DHD_UPGRADE_COVER("milkyway/dhd/upgrade_cover.obj", "pegasus/dhd/dhd_base", true),
    PEGASUS_DHD_CRYSTALS("milkyway/dhd/crystals.obj", "pegasus/dhd/dhd_crystals", true),
    PEGASUS_DHD_CONTROL_CRYSTAL("milkyway/dhd/control_crystal.obj", "pegasus/dhd/dhd_crystals", true),
    PEGASUS_DHD_FLUID_TANK_BASE("milkyway/dhd/tank_base.obj", "milkyway/dhd/tank_base", true),
    PEGASUS_DHD_FLUID_TANK_GLASS("milkyway/dhd/tank_glass.obj", "milkyway/dhd/tank_glass", true),
    PEGASUS_DHD_FLUID_TANK_FLUID("milkyway/dhd/tank_inside.obj", "milkyway/dhd/tank_base", true),

    PEGASUS_CHEVRON_LIGHT("pegasus/chevron_light.obj", "pegasus/chevron", true),
    PEGASUS_CHEVRON_FRAME("pegasus/chevron_frame.obj", "pegasus/gatering", true),
    PEGASUS_CHEVRON_MOVING("pegasus/chevron_moving.obj", "pegasus/chevron", true),
    PEGASUS_CHEVRON_BACK("pegasus/chevron_back.obj", "pegasus/gatering", true),

    // --------------------------------------------------------------------------------------------
    // Tollan

    TOLLAN_GATE("tollan/gate.obj", "tollan/gate", true),
    TOLLAN_CHEVRON("tollan/chevron.obj", "tollan/gate", true),
    TOLLAN_CHEVRON_LIGHT("tollan/chevron_light.obj", "tollan/chevron", true),


    // --------------------------------------------------------------------------------------------
    // Irises/Shields

    SHIELD("iris/shield.obj", "iris/shield", false),
    IRIS("iris/iris_blade.obj", "iris/iris_blade", true),
    IRIS_TOLLAN("iris/iris_blade_tollan.obj", "iris/iris_blade", true),

    GDO("iris/gdo.obj", "iris/gdo", false),

    // --------------------------------------------------------------------------------------------
    // Stargate Controllers

    ADMIN_CONTROLLER("tools/admin_controller.obj", "tools/admin_controller", false),
    // --------------------------------------------------------------------------------------------
    // KINO

    // --------------------------------------------------------------------------------------------

    ;

    public final ResourceLocation model;
    public final Map<BiomeOverlayInstance, ResourceLocation> biomeTextureResourceMap = new HashMap<>();
    private final List<BiomeOverlayInstance> nonExistingReported = new ArrayList<>();

    ElementEnum(String modelPath, String texturePath, boolean byOverlay) {
        this.model = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource(modelPath);
        loadEntry(texturePath, byOverlay);
    }

    @Override
    public @NotNull LoadersHolder getLoadersHolder() {
        return JSGApi.JSG_LOADERS_HOLDER;
    }

    @Override
    public @NotNull ResourceLocation getModelLocation() {
        return model;
    }

    @Override
    public @NotNull Map<BiomeOverlayInstance, ResourceLocation> getBiomeTextureResourceMap() {
        return biomeTextureResourceMap;
    }

    @Override
    public @NotNull List<BiomeOverlayInstance> getNonExistingTexturesReported() {
        return nonExistingReported;
    }
}
