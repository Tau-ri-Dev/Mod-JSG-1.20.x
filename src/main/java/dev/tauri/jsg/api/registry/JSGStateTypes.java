package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.entity.StateType;
import net.minecraftforge.registries.RegistryObject;

public class JSGStateTypes {
    public static final RegistryObject<StateType> SPIN_STATE = JSGApi.REGISTRY_HELPER.state().register("spin_state", () -> new StateType("spin_state"));
    public static final RegistryObject<StateType> FLASH_STATE = JSGApi.REGISTRY_HELPER.state().register("flash_state", () -> new StateType("flash_state"));
    public static final RegistryObject<StateType> DHD_ACTIVATE_BUTTON = JSGApi.REGISTRY_HELPER.state().register("dhd_activate_button", () -> new StateType("dhd_activate_button"));
    public static final RegistryObject<StateType> IRIS_ANIMATION = JSGApi.REGISTRY_HELPER.state().register("iris_animation", () -> new StateType("iris_animation"));
    public static final RegistryObject<StateType> STARGATE_VAPORIZE_BLOCK_PARTICLES = JSGApi.REGISTRY_HELPER.state().register("gate_vapor_block_particles", () -> new StateType("gate_vapor_block_particles"));
    public static final RegistryObject<StateType> BLACK_HOLE_ANIMATION_UPDATE = JSGApi.REGISTRY_HELPER.state().register("black_hole_update", () -> new StateType("black_hole_update"));
    public static final RegistryObject<StateType> CHEVRONS_STATE = JSGApi.REGISTRY_HELPER.state().register("chevrons_state", () -> new StateType("chevrons_state"));
    public static final RegistryObject<StateType> ORLIN_PARTICLE_STATE = JSGApi.REGISTRY_HELPER.state().register("orlin_particle_state", () -> new StateType("orlin_particle_state"));

    public static void init() {
    }
}
