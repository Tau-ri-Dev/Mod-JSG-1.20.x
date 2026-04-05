package dev.tauri.jsg.loader;

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

    MILKYWAY_GATE("milkyway/gate.obj", "milkyway/gatering.jpg", true),
    MILKYWAY_RING("milkyway/ring.obj", "milkyway/gatering.jpg", true),

    MILKYWAY_DHD_BASE("milkyway/dhd/dhd_base.obj", "milkyway/dhd/dhd_base.jpg", false),
    MILKYWAY_DHD_BUTTON_CONSOLE("milkyway/dhd/button_console.obj", "milkyway/dhd/dhd_base.jpg", false),
    MILKYWAY_DHD_CRYSTAL_HOLDER("milkyway/dhd/crystal_holder.obj", "milkyway/dhd/dhd_base.jpg", false),
    MILKYWAY_DHD_UPGRADE_CRYSTAL("milkyway/dhd/dhd_upgrade_crystal.obj", "milkyway/dhd/upgrade_crystal_base.png", false),
    MILKYWAY_DHD_UPGRADE_COVER("milkyway/dhd/upgrade_cover.obj", "milkyway/dhd/dhd_base.jpg", false),
    MILKYWAY_DHD_CRYSTALS("milkyway/dhd/crystals.obj", "milkyway/dhd/dhd_crystals.png", false),
    MILKYWAY_DHD_CONTROL_CRYSTAL("milkyway/dhd/control_crystal.obj", "milkyway/dhd/dhd_crystals.png", false),
    MILKYWAY_DHD_FLUID_TANK_BASE("milkyway/dhd/tank_base.obj", "milkyway/dhd/tank_base.png", false),
    MILKYWAY_DHD_FLUID_TANK_GLASS("milkyway/dhd/tank_glass.obj", "milkyway/dhd/tank_glass.png", false),
    MILKYWAY_DHD_FLUID_TANK_FLUID("milkyway/dhd/tank_inside.obj", "milkyway/dhd/tank_base.png", false),

    MILKYWAY_CHEVRON_LIGHT("milkyway/chevron_light.obj", "milkyway/chevron.png", true),
    MILKYWAY_CHEVRON_FRAME("milkyway/chevron_frame.obj", "milkyway/gatering.jpg", true),
    MILKYWAY_CHEVRON_MOVING("milkyway/chevron_moving.obj", "milkyway/chevron.png", true),
    MILKYWAY_CHEVRON_BACK("milkyway/chevron_back.obj", "milkyway/gatering.jpg", true),

    ORLIN_GATE("orlin/orlin_gate.obj", "orlin/orlin_gate_base.jpg", false),
    ORLIN_GATE_BURNT("orlin/orlin_gate_burnt.obj", "orlin/orlin_gate_burnt.jpg", false),
    ORLIN_STAND("orlin/orlin_stand.obj", "orlin/orlin_stand_base.jpg", false),

    // --------------------------------------------------------------------------------------------
    // Universe

    UNIVERSE_GATE("universe/universe_gate.obj", "universe/universe_gate.jpg", true),
    UNIVERSE_CHEVRON("universe/universe_chevron.obj", "universe/universe_chevron.png", true),
    UNIVERSE_SYMBOL("universe/universe_chevron.obj", "universe/universe_chevron_light.png", true),
    UNIVERSE_DIALER("universe/universe_dialer.obj", "universe/universe_dialer.jpg", true),
    UNIVERSE_DIALER_BROKEN("universe/universe_dialer.obj", "universe/universe_dialer_broken.jpg", true),

    // --------------------------------------------------------------------------------------------
    // Pegasus

    PEGASUS_GATE("pegasus/gate.obj", "pegasus/gatering.jpg", true),
    PEGASUS_RING("pegasus/ring_atlantis.obj", "pegasus/gatering.jpg", true),

    PEGASUS_DHD_BASE("milkyway/dhd/dhd_base.obj", "pegasus/dhd/dhd_base.jpg", false),
    PEGASUS_DHD_BUTTON_CONSOLE("pegasus/dhd/button_console.obj", "pegasus/dhd/dhd_base.jpg", false),
    PEGASUS_DHD_CRYSTAL_HOLDER("milkyway/dhd/crystal_holder.obj", "pegasus/dhd/dhd_base.jpg", false),
    PEGASUS_DHD_UPGRADE_CRYSTAL("milkyway/dhd/dhd_upgrade_crystal.obj", "milkyway/dhd/upgrade_crystal_base.png", false),
    PEGASUS_DHD_UPGRADE_COVER("milkyway/dhd/upgrade_cover.obj", "pegasus/dhd/dhd_base.jpg", false),
    PEGASUS_DHD_CRYSTALS("milkyway/dhd/crystals.obj", "pegasus/dhd/dhd_crystals.png", false),
    PEGASUS_DHD_CONTROL_CRYSTAL("milkyway/dhd/control_crystal.obj", "pegasus/dhd/dhd_crystals.png", false),
    PEGASUS_DHD_FLUID_TANK_BASE("milkyway/dhd/tank_base.obj", "milkyway/dhd/tank_base.png", false),
    PEGASUS_DHD_FLUID_TANK_GLASS("milkyway/dhd/tank_glass.obj", "milkyway/dhd/tank_glass.png", false),
    PEGASUS_DHD_FLUID_TANK_FLUID("milkyway/dhd/tank_inside.obj", "milkyway/dhd/tank_base.png", false),

    PEGASUS_CHEVRON_LIGHT("pegasus/chevron_light.obj", "pegasus/chevron.png", true),
    PEGASUS_CHEVRON_FRAME("pegasus/chevron_frame.obj", "pegasus/gatering.jpg", true),
    PEGASUS_CHEVRON_MOVING("pegasus/chevron_moving.obj", "pegasus/chevron.png", true),
    PEGASUS_CHEVRON_BACK("pegasus/chevron_back.obj", "pegasus/gatering.jpg", true),

    // --------------------------------------------------------------------------------------------
    // Tollan

    TOLLAN_GATE("tollan/gate.obj", "tollan/gate.jpg", false),
    TOLLAN_CHEVRON("tollan/chevron.obj", "tollan/gate.jpg", false),
    TOLLAN_CHEVRON_LIGHT("tollan/chevron_light.obj", "tollan/chevron.jpg", false),


    // --------------------------------------------------------------------------------------------
    // Irises/Shields

    SHIELD("iris/shield.obj", "iris/shield.jpg", false),
    IRIS("iris/iris_blade.obj", "iris/iris_blade.jpg", false),
    IRIS_TOLLAN("iris/iris_blade_tollan.obj", "iris/iris_blade.jpg", false),

    GDO("iris/gdo.obj", "iris/gdo.png", false),

    // --------------------------------------------------------------------------------------------
    // Stargate Controllers

    ADMIN_CONTROLLER("tools/admin_controller.obj", "tools/admin_controller.png", false),
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
