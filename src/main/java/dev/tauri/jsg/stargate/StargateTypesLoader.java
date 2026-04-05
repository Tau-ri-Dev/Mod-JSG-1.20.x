package dev.tauri.jsg.stargate;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.registry.JSGRegistries;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.blockentity.stargate.*;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.List;

public class StargateTypesLoader {
    public static final DeferredRegister<StargateType<?>> REGISTER = DeferredRegister.create(JSGRegistries.STARGATE_TYPE, JSG.MOD_ID);

    public static void load() {
        StargateTypes.MILKYWAY = REGISTER.register("milkyway", () -> new StargateType<>("MilkyWay", JSGSymbolTypes.MILKYWAY, StargateMilkyWayBaseBE.class,
                JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK, JSGBlocks.DHD_MILKYWAY, () -> StargateConfigOptions.MilkyWay.HOLDER, true,
                List.of(StargatePointOfOriginsDefaults.VARIANT_DHD, StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT, StargatePointOfOriginsDefaults.VARIANT_GATE),
                List.of(StargatePointOfOriginsDefaults.VARIANT_ICON),
                List.of(
                        JSGMapping.rl(JSG.MOD_ID, "default"),
                        JSGMapping.rl(JSG.MOD_ID, "p7j989"),
                        JSGMapping.rl(JSG.MOD_ID, "nether"),
                        JSGMapping.rl(JSG.MOD_ID, "antarctica"),
                        JSGMapping.rl(JSG.MOD_ID, "abydos"),
                        JSGMapping.rl(JSG.MOD_ID, "tauri")
                )));
        JSGApi.REGISTRY_HELPER.pooType().register("stargate/milkyway", StargateTypes.MILKYWAY);

        StargateTypes.PEGASUS = REGISTER.register("pegasus", () -> new StargateType<>("Pegasus", JSGSymbolTypes.PEGASUS, StargatePegasusBaseBE.class,
                JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK, JSGBlocks.DHD_PEGASUS, () -> StargateConfigOptions.Pegasus.HOLDER, true,
                List.of(StargatePointOfOriginsDefaults.VARIANT_DHD, StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT),
                List.of(StargatePointOfOriginsDefaults.VARIANT_ICON, StargatePointOfOriginsDefaults.VARIANT_GATE_PNG, StargatePointOfOriginsDefaults.VARIANT_GATE_OFF_PNG),
                List.of(
                        JSGMapping.rl(JSG.MOD_ID, "default")
                )));
        JSGApi.REGISTRY_HELPER.pooType().register("stargate/pegasus", StargateTypes.PEGASUS);

        StargateTypes.UNIVERSE = REGISTER.register("universe", () -> new StargateType<>("Universe", JSGSymbolTypes.UNIVERSE, StargateUniverseBaseBE.class,
                JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK, null, () -> StargateConfigOptions.Universe.HOLDER, true,
                List.of(StargatePointOfOriginsDefaults.VARIANT_GATE),
                List.of(StargatePointOfOriginsDefaults.VARIANT_ICON),
                List.of(
                        JSGMapping.rl(JSG.MOD_ID, "default")
                )));
        JSGApi.REGISTRY_HELPER.pooType().register("stargate/universe", StargateTypes.UNIVERSE);

        StargateTypes.TOLLAN = REGISTER.register("tollan", () -> new StargateType<>("Tollan", JSGSymbolTypes.MILKYWAY, StargateTollanBaseBE.class,
                JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK, JSGBlocks.DHD_MILKYWAY, () -> StargateConfigOptions.Tollan.HOLDER, true,
                List.of(StargatePointOfOriginsDefaults.VARIANT_GATE),
                List.of(StargatePointOfOriginsDefaults.VARIANT_ICON),
                List.of()));
        JSGApi.REGISTRY_HELPER.pooType().register("stargate/tollan", StargateTypes.TOLLAN);

        StargateTypes.ORLIN = REGISTER.register("orlin", () -> new StargateType<>("Orlin's", JSGSymbolTypes.MILKYWAY, StargateOrlinBaseBE.class,
                JSGBlocks.STARGATE_ORLIN_BASE_BLOCK, null, () -> StargateConfigOptions.Orlin.HOLDER, false,
                List.of(StargatePointOfOriginsDefaults.VARIANT_GATE),
                List.of(StargatePointOfOriginsDefaults.VARIANT_ICON),
                List.of()));
        JSGApi.REGISTRY_HELPER.pooType().register("stargate/orlin", StargateTypes.ORLIN);

        StargateTypes.MOVIE = REGISTER.register("movie", () -> new StargateType<>("Movie", JSGSymbolTypes.MILKYWAY, StargateMovieBaseBE.class,
                JSGBlocks.STARGATE_MOVIE_BASE_BLOCK, JSGBlocks.DHD_MILKYWAY, () -> StargateConfigOptions.Movie.HOLDER, true,
                List.of(StargatePointOfOriginsDefaults.VARIANT_GATE),
                List.of(),
                List.of(
                        JSGMapping.rl(JSG.MOD_ID, "default")
                )));
        JSGApi.REGISTRY_HELPER.pooType().register("stargate/movie", StargateTypes.MOVIE);
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
