package dev.tauri.jsg.listener;

import com.mojang.serialization.Dynamic;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.event.config.DimensionConfigPreRegisterEvent;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.registry.JSGDimensions;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = JSG.MOD_ID)
public class CoreListener {
    @SubscribeEvent
    public static void onDimensionConfigPreRegister(DimensionConfigPreRegisterEvent event) {
        event.registerField("hasStargate", (ops) -> new Dynamic<>(ops, ops.createBoolean(true)));
        event.registerField("lightingBoltChance", (ops) -> new Dynamic<>(ops, ops.createFloat(0)));
        event.registerField("isBlackHoleDim", (ops) -> new Dynamic<>(ops, ops.createBoolean(false)));

        event.registerEntry(Level.OVERWORLD.location().toString(), JSGDimensionConfig.Entry.createDefaultExcept(
                Pair.of("groups", (ops) -> new Dynamic<>(ops, ops.createList(Stream.of(ops.createString("milkyway"))))),
                Pair.of("lightingBoltChance", (ops) -> new Dynamic<>(ops, ops.createFloat(1f))),
                Pair.of("origins", (ops) -> new Dynamic<>(ops, ops.createMap(Map.of(
                        ops.createString(StargateTypes.MILKYWAY.get().getPoONamespaceIdentifier().toString()), ops.createMap(Map.of(
                                ops.createString(CoreBiomeOverlays.NORMAL.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.TAURI.toString()),
                                ops.createString(CoreBiomeOverlays.FROST.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.ANTARCTICA.toString())
                        ))
                )))))
        );

        event.registerEntry(Level.NETHER.location().toString(), JSGDimensionConfig.Entry.createDefaultExcept(
                Pair.of("groups", (ops) -> new Dynamic<>(ops, ops.createList(Stream.of(ops.createString("milkyway"))))),
                Pair.of("origins", (ops) -> new Dynamic<>(ops, ops.createMap(Map.of(
                        ops.createString(StargateTypes.MILKYWAY.get().getPoONamespaceIdentifier().toString()), ops.createMap(Map.of(
                                ops.createString(CoreBiomeOverlays.NORMAL.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.NETHER.toString()),
                                ops.createString(CoreBiomeOverlays.AGED.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.NETHER.toString()),
                                ops.createString(CoreBiomeOverlays.SOOTY.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.NETHER.toString())
                        ))
                )))))
        );

        event.registerEntry(Level.END.location().toString(), JSGDimensionConfig.Entry.createDefaultExcept(
                Pair.of("groups", (ops) -> new Dynamic<>(ops, ops.createList(Stream.of(ops.createString("milkyway"))))),
                Pair.of("origins", (ops) -> new Dynamic<>(ops, ops.createMap(Map.of(
                        ops.createString(StargateTypes.MILKYWAY.get().getPoONamespaceIdentifier().toString()), ops.createMap(Map.of(
                                ops.createString(CoreBiomeOverlays.NORMAL.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.P7J_989.toString())
                        ))
                )))))
        );

        event.registerEntry(JSGDimensions.ABYDOS.location().toString(), JSGDimensionConfig.Entry.createDefaultExcept(
                Pair.of("distance", (ops) -> new Dynamic<>(ops, ops.createInt(13))),
                Pair.of("groups", (ops) -> new Dynamic<>(ops, ops.createList(Stream.of(ops.createString("milkyway"))))),
                Pair.of("origins", (ops) -> new Dynamic<>(ops, ops.createMap(Map.of(
                        ops.createString(StargateTypes.MILKYWAY.get().getPoONamespaceIdentifier().toString()), ops.createMap(Map.of(
                                ops.createString(CoreBiomeOverlays.NORMAL.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.ABYDOS.toString()),
                                ops.createString(CoreBiomeOverlays.AGED.get().toString()), ops.createString(StargatePointOfOriginsDefaults.MilkyWay.ABYDOS.toString())
                        ))
                )))))
        );
    }
}
