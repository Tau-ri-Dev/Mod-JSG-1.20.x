package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.common.stargate.rig.RIGWave;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = JSG.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JSGRegistries {
    private static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();

    public static final ResourceKey<Registry<StargateType<?>>> STARGATE_TYPE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGApi.MOD_ID, "stargate_type"));
    public static final ResourceKey<Registry<RIGWave>> RIG_WAVES = ResourceKey.createRegistryKey(JSGMapping.rl(JSGApi.MOD_ID, "rig_waves"));

    public static final Supplier<IForgeRegistry<StargateType<?>>> R_STARGATE_TYPE = create(STARGATE_TYPE);
    public static final Function<RegistryAccess, Registry<RIGWave>> R_RIG_WAVES = (access) -> access.registryOrThrow(RIG_WAVES);

    private static <T> Supplier<IForgeRegistry<T>> create(ResourceKey<Registry<T>> id) {
        return create(id, () -> RegistryBuilder.of(id.location()));
    }

    private static <T> Supplier<IForgeRegistry<T>> create(ResourceKey<Registry<T>> id, Supplier<RegistryBuilder<T>> builder) {
        DeferredRegister<T> dr = DeferredRegister.create(id, id.location().getNamespace());
        REGISTERS.add(dr);
        return dr.makeRegistry(builder);
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
