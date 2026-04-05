package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import net.minecraftforge.registries.RegistryObject;

public class JSGScheduledTaskTypes {
    public static final RegistryObject<ScheduledTaskType> STARGATE_OPEN_SOUND = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_open_sound", () -> new ScheduledTaskType("stargate_open_sound", 1, false));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CLOSE_SOUND = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_close_sound", () -> new ScheduledTaskType("stargate_close_sound", 0, false));
    public static final RegistryObject<ScheduledTaskType> STARGATE_ENGAGE = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_engage", () -> new ScheduledTaskType("stargate_engage", 60));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CLOSE = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_close", () -> new ScheduledTaskType("stargate_close"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CHEVRON_OPEN = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_chevron_open", () -> new ScheduledTaskType("stargate_chevron_open", 19, false));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CHEVRON_CLOSE = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_chevron_close", () -> new ScheduledTaskType("stargate_chevron_close", 15, false));
    public static final RegistryObject<ScheduledTaskType> HORIZON_FLASH = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_horizon_flash", () -> new ScheduledTaskType("stargate_horizon_flash", false));
    public static final RegistryObject<ScheduledTaskType> STARGATE_ORLIN_BROKE_SOUND = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_orlin_broke_sound", () -> new ScheduledTaskType("stargate_orlin_broke_sound"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_ORLIN_OPEN = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_orlin_open", () -> new ScheduledTaskType("stargate_orlin_open"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_HORIZON_LIGHT_BLOCK = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_horizon_light_block", () -> new ScheduledTaskType("stargate_horizon_light_block"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_HORIZON_WIDEN = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_horizon_widen", () -> new ScheduledTaskType("stargate_horizon_widen", false));
    public static final RegistryObject<ScheduledTaskType> STARGATE_HORIZON_SHRINK = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_horizon_shrink", () -> new ScheduledTaskType("stargate_horizon_shrink", false));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CHEVRON_LIGHT_UP = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_chevron_light_up", () -> new ScheduledTaskType("stargate_chevron_light_up"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CHEVRON_DIM = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_chevron_dim", () -> new ScheduledTaskType("stargate_chevron_dim"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_FAIL = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_fail", () -> new ScheduledTaskType("stargate_fail"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_GIVE_PAGE = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_give_page", () -> new ScheduledTaskType("stargate_give_page"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_DIAL_NEXT = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_dial_next", () -> new ScheduledTaskType("stargate_dial_next"));
    public static final RegistryObject<ScheduledTaskType> GATE_RING_ROLL = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_ring_roll", () -> new ScheduledTaskType("stargate_ring_roll"));
    public static final RegistryObject<ScheduledTaskType> LIGHT_UP_CHEVRONS = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_light_up_chevrons", () -> new ScheduledTaskType("stargate_light_up_chevrons"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CHEVRON_FAIL = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_chevron_fail", () -> new ScheduledTaskType("stargate_chevron_fail"));
    public static final RegistryObject<ScheduledTaskType> STARGATE_SYMBOL_LOCK = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_symbol_lock", () -> new ScheduledTaskType("stargate_symbol_lock", 5));
    public static final RegistryObject<ScheduledTaskType> STARGATE_LIGHTING_UPDATE_CLIENT = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_light_update", () -> new ScheduledTaskType("stargate_light_update", 5));
    public static final RegistryObject<ScheduledTaskType> STARGATE_CLEAR_CHEVRONS = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_clear_chevrons", () -> new ScheduledTaskType("stargate_clear_chevrons", 10));
    public static final RegistryObject<ScheduledTaskType> STARGATE_RESET = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_reset", () -> new ScheduledTaskType("stargate_reset", 20));
    public static final RegistryObject<ScheduledTaskType> BEGIN_SPIN = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_begin_spin", () -> new ScheduledTaskType("stargate_begin_spin", 35));
    public static final RegistryObject<ScheduledTaskType> STARGATE_RING_ROLL_LOOP_SOUND = JSGApi.REGISTRY_HELPER.scheduledTask().register("stargate_ring_roll_loop_sound", () -> new ScheduledTaskType("stargate_ring_roll_loop_sound", 20));

    public static void init() {
    }
}
