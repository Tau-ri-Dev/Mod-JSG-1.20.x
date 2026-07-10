package dev.tauri.jsg.api.registry;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.stargate.rig.RIGWave;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(modid = JSG.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class JSGRegistries {
    private static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();

    public static final ResourceKey<Registry<StargateType<?>>> STARGATE_TYPE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGApi.MOD_ID, "stargate_type"));
    public static final ResourceKey<Registry<RIGWave>> RIG_WAVES = ResourceKey.createRegistryKey(JSGMapping.rl(JSGApi.MOD_ID, "rig_waves"));
    public static final ResourceKey<Registry<UniverseDialerMode>> UNIVERSE_DIALER_MODES = ResourceKey.createRegistryKey(JSGMapping.rl(JSGApi.MOD_ID, "universe_dialer_modes"));

    public static final Supplier<Registry<StargateType<?>>> R_STARGATE_TYPE = create(STARGATE_TYPE);
    public static final Function<RegistryAccess, Registry<RIGWave>> R_RIG_WAVES = (access) -> access.registryOrThrow(RIG_WAVES);
    public static final Supplier<Registry<UniverseDialerMode>> R_UNIVERSE_DIALER_MODES = create(UNIVERSE_DIALER_MODES);

    private static <T> Supplier<Registry<T>> create(ResourceKey<Registry<T>> id) {
        DeferredRegister<T> dr = DeferredRegister.create(id, id.location().getNamespace());
        REGISTERS.add(dr);
        // Forge custom registries were synced by default; NeoForge has no onBake -
        // UniverseDialerMode.calculateModesSequence() is invoked on FMLCommonSetupEvent instead (see JSG.java).
        Registry<T> registry = dr.makeRegistry(builder -> builder.sync(true));
        return () -> registry;
    }

    public static void init() {

    }

    public static void register(IEventBus bus) {
        REGISTERS.forEach(dr -> dr.register(bus));
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(RIG_WAVES, RIGWave.CODEC);
    }
}
