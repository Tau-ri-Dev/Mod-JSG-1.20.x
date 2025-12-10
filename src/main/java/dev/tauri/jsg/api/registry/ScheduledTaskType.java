package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.api.JSGApi;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ScheduledTaskType {

    private static final Map<ResourceLocation, ScheduledTaskType> ID_MAP = new HashMap<>();

    public static ScheduledTaskType valueOf(ResourceLocation id) {
        return ID_MAP.get(id);
    }

    public static final ScheduledTaskType STARGATE_OPEN_SOUND = new ScheduledTaskType("stargate_open_sound", 1, false);
    public static final ScheduledTaskType STARGATE_ENGAGE = new ScheduledTaskType("stargate_engage", 60);
    public static final ScheduledTaskType STARGATE_CLOSE = new ScheduledTaskType("stargate_close", -1);
    public static final ScheduledTaskType STARGATE_CHEVRON_OPEN = new ScheduledTaskType("stargate_chevron_open", 19, false);
    public static final ScheduledTaskType STARGATE_CHEVRON_CLOSE = new ScheduledTaskType("stargate_chevron_close", 15, false);
    public static final ScheduledTaskType HORIZON_FLASH = new ScheduledTaskType("stargate_horizon_flash", -1, false);
    public static final ScheduledTaskType STARGATE_ORLIN_BROKE_SOUND = new ScheduledTaskType("stargate_orlin_broke_sound", -1);
    public static final ScheduledTaskType STARGATE_HORIZON_LIGHT_BLOCK = new ScheduledTaskType("stargate_horizon_light_block", -1);
    public static final ScheduledTaskType STARGATE_HORIZON_WIDEN = new ScheduledTaskType("stargate_horizon_widen", -1, false);
    public static final ScheduledTaskType STARGATE_HORIZON_SHRINK = new ScheduledTaskType("stargate_horizon_shrink", -1, false);
    public static final ScheduledTaskType STARGATE_CHEVRON_LIGHT_UP = new ScheduledTaskType("stargate_chevron_light_up", -1);
    public static final ScheduledTaskType STARGATE_CHEVRON_DIM = new ScheduledTaskType("stargate_chevron_dim", -1);
    public static final ScheduledTaskType STARGATE_FAILED_SOUND = new ScheduledTaskType("stargate_fail_sound", -1);
    public static final ScheduledTaskType STARGATE_FAIL = new ScheduledTaskType("stargate_fail", -1);
    public static final ScheduledTaskType STARGATE_GIVE_PAGE = new ScheduledTaskType("stargate_give_page", -1);
    public static final ScheduledTaskType STARGATE_DIAL_NEXT = new ScheduledTaskType("stargate_dial_next", -1);
    public static final ScheduledTaskType STARGATE_CLEAR_CHEVRONS = new ScheduledTaskType("stargate_clear_chevrons", 10);
    public static final ScheduledTaskType GATE_RING_ROLL = new ScheduledTaskType("stargate_ring_roll", -1);
    public static final ScheduledTaskType LIGHT_UP_CHEVRONS = new ScheduledTaskType("stargate_light_up_chevrons", -1);
    public static final ScheduledTaskType STARGATE_RESET = new ScheduledTaskType("stargate_reset", 20); // used only for uni gates
    public static final ScheduledTaskType BEGIN_SPIN = new ScheduledTaskType("stargate_begin_spin", 35);
    public static final ScheduledTaskType STARGATE_LIGHTING_UPDATE_CLIENT = new ScheduledTaskType("stargate_light_update", 5);
    public static final ScheduledTaskType STARGATE_CHEVRON_FAIL = new ScheduledTaskType("stargate_chevron_fail", -1);
    public static final ScheduledTaskType STARGATE_SYMBOL_LOCK = new ScheduledTaskType("stargate_symbol_lock", 5);
    public static final ScheduledTaskType STARGATE_RING_ROLL_LOOP_SOUND = new ScheduledTaskType("stargate_ring_roll_loop_sound", 20);


    public final ResourceLocation id;
    public final int waitTicks;

    /**
     * Should the task be called on nearest occasion
     * even when the scheduled wait time exceeded?
     */
    public final boolean overtime;

    private ScheduledTaskType(String id, int waitTicks) {
        this(id, waitTicks, true);
    }

    public ScheduledTaskType(ResourceLocation id, int waitTicks) {
        this(id, waitTicks, true);
    }

    private ScheduledTaskType(String id, int waitTicks, boolean overtime) {
        this(new ResourceLocation(JSGApi.MOD_ID, id), waitTicks, overtime);
    }

    public ScheduledTaskType(ResourceLocation id, int waitTicks, boolean overtime) {
        this.id = id;
        this.waitTicks = waitTicks;
        this.overtime = overtime;
        ID_MAP.put(id, this);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ScheduledTaskType t)) return false;
        return this.id.equals(t.id);
    }

    @Override
    public String toString() {
        return this.id.toString() + "[time=" + this.waitTicks + "]";
    }
}
