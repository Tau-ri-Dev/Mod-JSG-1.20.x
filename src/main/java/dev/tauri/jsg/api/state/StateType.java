package dev.tauri.jsg.api.state;

import dev.tauri.jsg.api.JSGApi;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Defines {@link State} of which type we want to request from the server.
 * This will be sent to server. Then, appropriate state will be serialized and
 * returned to the client(Based on {@link IStateProvider#getState(StateType)}). Deserialization will occur based on this Enum.
 * Must be unique within one TileEntity, but can be reused by multiple TileEntities.
 *
 * @author MrJake
 */
public class StateType {
    private static final Map<Integer, StateType> ID_MAP = new HashMap<>();

    public static StateType byId(int id) {
        return ID_MAP.get(id);
    }

    private static int currentId = 0;

    public static final StateType RENDERER_STATE = create("renderer_state");
    public static final StateType GUI_STATE = create("gui_state");
    public static final StateType GUI_UPDATE = create("gui_update");
    public static final StateType CAMO_STATE = create("camo_state");
    public static final StateType SPIN_STATE = create("spin_state");
    public static final StateType FLASH_STATE = create("flash_state");
    public static final StateType DHD_ACTIVATE_BUTTON = create("dhd_activate_button");
    public static final StateType RENDERER_UPDATE = create("renderer_update");
    public static final StateType STARGATE_VAPORIZE_BLOCK_PARTICLES = create("gate_vapor_block_particles");
    public static final StateType BIOME_OVERRIDE_STATE = create("biome_override_state");
    public static final StateType BLACK_HOLE_ANIMATION_UPDATE = create("black_hole_update");
    public static final StateType CHEVRONS_STATE = create("chevrons_state");
    public static final StateType SOUND_UPDATE = create("sound_update_state");

    public final int id;
    public final ResourceLocation name;

    public StateType(ResourceLocation name) {
        this.id = currentId++;
        this.name = name;
        ID_MAP.put(id, this);
    }

    public String name() {
        return name.toString();
    }

    @Override
    public String toString() {
        return name();
    }

    public boolean is(StateType otherStateType) {
        return this.id == otherStateType.id;
    }

    public StateExecutor stateExecutor() {
        return new StateExecutor(this);
    }

    public StateSupplier stateSupplier() {
        return new StateSupplier(this);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof StateType otherStateType)) return false;
        return is(otherStateType);
    }

    @Override
    public int hashCode() {
        return this.id * 31;
    }

    public static StateType create(ResourceLocation name) {
        return new StateType(name);
    }

    private static StateType create(String name) {
        return create(new ResourceLocation(JSGApi.MOD_ID, name));
    }


    @SuppressWarnings("unused")
    public static class StateSupplier {
        private Supplier<State> result = null;
        private final StateType original;

        public StateSupplier(StateType original) {
            this.original = original;
        }

        @Nullable
        public State get() {
            return result.get();
        }

        public State orElseGet(Supplier<State> defaultSupplier) {
            if (result == null) return defaultSupplier.get();
            return result.get();
        }

        public State orElseThrow(Object caller) {
            if (result == null)
                throw new UnsupportedOperationException("EnumStateType." + original.name() + " not implemented on " + caller.getClass().getName());
            return result.get();
        }

        public StateSupplier tryType(StateType otherStateType, Supplier<State> stateSupplier) {
            if (original.is(otherStateType)) result = stateSupplier;
            return this;
        }
    }

    @SuppressWarnings("unused")
    public static class StateExecutor {
        private Runnable result = null;
        private final StateType original;

        public StateExecutor(StateType original) {
            this.original = original;
        }

        public void run() {
            if (result != null)
                result.run();
        }

        public void runOrElse(Runnable defaultRunnable) {
            if (result == null) defaultRunnable.run();
            else run();
        }

        public StateExecutor tryType(StateType otherStateType, Runnable stateRunnable) {
            if (original.is(otherStateType)) result = stateRunnable;
            return this;
        }
    }
}
