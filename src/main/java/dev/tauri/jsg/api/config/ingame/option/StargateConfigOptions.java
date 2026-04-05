package dev.tauri.jsg.api.config.ingame.option;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.util.StargateTimeLimitModeEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionProvider;
import dev.tauri.jsg.core.common.config.ingame.option.ConfigOptionsHolder;
import dev.tauri.jsg.core.common.config.ingame.option.type.BooleanBEConfigOption;
import dev.tauri.jsg.core.common.config.ingame.option.type.IntegerBEConfigOption;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import net.minecraft.resources.ResourceLocation;

public class StargateConfigOptions {
    public static class Common {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder();

        public static final BEConfigOptionProvider<StargateTimeLimitModeEnum> TIME_LIMIT_MODE = HOLDER.register("time_limit_mode", (onChanged) -> new dev.tauri.jsg.core.common.config.ingame.option.type.EnumBEConfigOption<>(onChanged, JSGConfig.Stargate.maxOpenedWhat.get(), StargateTimeLimitModeEnum::values, (object) -> {
            if (object instanceof StargateTimeLimitModeEnum mode)
                return mode;
            return null;
        }));
        public static final BEConfigOptionProvider<Integer> TIME_LIMIT_TIME = HOLDER.register("time_limit_time", (onChanged) -> new IntegerBEConfigOption(onChanged, JSGConfig.Stargate.maxOpenedSeconds.get(), 0));
        public static final BEConfigOptionProvider<Integer> TIME_LIMIT_POWER = HOLDER.register("time_limit_power", (onChanged) -> new IntegerBEConfigOption(onChanged, JSGConfig.Stargate.maxOpenedPowerDrawAfterLimit.get(), 0));
    }

    public static class Classic {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder(Common.HOLDER);

        public static final BEConfigOptionProvider<ResourceLocation> POINT_OF_ORIGIN = IPointOfOriginType.registerEmptyBEConfigOption(HOLDER);

        public static final BEConfigOptionProvider<Integer> SPIN_SPEED = HOLDER.register("spin_speed", (onChanged) -> new IntegerBEConfigOption(onChanged, 100, 10, 300));
        public static final BEConfigOptionProvider<Boolean> FORCE_UNSTABLE = HOLDER.register("force_unstable", (onChanged) -> new BooleanBEConfigOption(onChanged, false));
        public static final BEConfigOptionProvider<Boolean> ENABLE_BURY_STATE = HOLDER.register("enable_bury_state", (onChanged) -> new BooleanBEConfigOption(onChanged, JSGConfig.Stargate.enableBurriedState.get()));
        public static final BEConfigOptionProvider<Boolean> ALLOW_RIG = HOLDER.register("allow_rig", (onChanged) -> new BooleanBEConfigOption(onChanged, JSGConfig.Stargate.enableRandomIncoming.get()));
        public static final BEConfigOptionProvider<Integer> MAX_CAPACITORS = HOLDER.register("max_capacitors", (onChanged) -> new IntegerBEConfigOption(onChanged, 3, 0, 3));
        public static final BEConfigOptionProvider<Boolean> DHD_OC_PRESS_SOUND = HOLDER.register("dhd_oc_press_sound", (onChanged) -> new BooleanBEConfigOption(onChanged, JSGConfig.DialHomeDevice.computerDialSound.get()));
        public static final BEConfigOptionProvider<Boolean> INCOMING_ANIMATION = HOLDER.register("allow_incoming_animation", (onChanged) -> new BooleanBEConfigOption(onChanged, JSGConfig.Stargate.allowIncomingAnimations.get()));
    }

    public static class Orlin {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder(Common.HOLDER);
    }

    public static class MilkyWay {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder(Classic.HOLDER);

        // we are overriding the classic one here
        public static final BEConfigOptionProvider<ResourceLocation> POINT_OF_ORIGIN = IPointOfOriginType.registerBEConfigOption(StargateTypes.MILKYWAY, HOLDER);

        public static final BEConfigOptionProvider<Boolean> DHD_POO_LOCK = HOLDER.register("dhd_last_lock", (onChanged) -> new BooleanBEConfigOption(onChanged, JSGConfig.DialHomeDevice.dhdLastOpen.get()));
        public static final BEConfigOptionProvider<Boolean> INCOMING_RING_SPIN = HOLDER.register("spin_ring_incoming", (onChanged) -> new BooleanBEConfigOption(onChanged, true));

    }

    public static class Pegasus {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder(Classic.HOLDER);

        // we are overriding the classic one here
        public static final BEConfigOptionProvider<ResourceLocation> POINT_OF_ORIGIN = IPointOfOriginType.registerBEConfigOption(StargateTypes.PEGASUS, HOLDER);
    }

    public static class Universe {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder(Classic.HOLDER);

        // we are overriding the classic one here
        public static final BEConfigOptionProvider<ResourceLocation> POINT_OF_ORIGIN = IPointOfOriginType.registerBEConfigOption(StargateTypes.UNIVERSE, HOLDER);

        // we are overriding the classic one here
        public static final BEConfigOptionProvider<Integer> MAX_CAPACITORS = HOLDER.register("max_capacitors", (onChanged) -> new IntegerBEConfigOption(onChanged, JSGConfig.Stargate.universeCapacitors.get(), JSGConfig.Stargate.universeCapacitors.getMin(), JSGConfig.Stargate.universeCapacitors.getMax()));

        public static final BEConfigOptionProvider<Boolean> FAST_DIALING = HOLDER.register("fast_dialing", (onChanged) -> new BooleanBEConfigOption(onChanged, false));
        public static final BEConfigOptionProvider<Boolean> ORANGE_SHIELD = HOLDER.register("orange_shield", (onChanged) -> new BooleanBEConfigOption(onChanged, false));
    }

    public static class Tollan {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder(MilkyWay.HOLDER);
    }

    public static class Movie {
        public static final ConfigOptionsHolder HOLDER = new ConfigOptionsHolder(MilkyWay.HOLDER);

        // we are overriding the milkyway one here
        public static final BEConfigOptionProvider<ResourceLocation> POINT_OF_ORIGIN = IPointOfOriginType.registerBEConfigOption(StargateTypes.MOVIE, HOLDER);
    }
}
