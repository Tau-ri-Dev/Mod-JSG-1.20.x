package dev.tauri.jsg.common.worldgen.generator;

import com.mojang.datafixers.util.Pair;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.client.screen.provider.SGGeneratorGuiProvider;
import dev.tauri.jsg.common.registry.JSGDimensions;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.common.registry.tags.JSGStructureTags;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.common.stargate.network.StargateReservedAddresses;
import dev.tauri.jsg.core.common.chunkloader.ChunkManager;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.util.AccessUtil;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber
public class DimensionStargateGenerator {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStarted(ServerStartedEvent e) {
        var server = e.getServer();
        long start = System.currentTimeMillis();
        JSG.logger.info("Started generating stargate in dimensions...");

        var network = StargateNetwork.INSTANCE;
        var levels = server.levelKeys();
        var lastPercentage = "";
        final int totalDimensions = levels.size();
        final ConcurrentHashMap<String, StargateGeneratorStepStatus> stats = new ConcurrentHashMap<>();
        AtomicInteger totalGenerated = new AtomicInteger();
        AtomicReference<Component> message = new AtomicReference<>(Component.empty());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SGGeneratorGuiProvider.showProgress(() -> totalDimensions, () -> stats, message::get));
        JSG.logger.info("Found {} total dimensions", totalDimensions);
        final long started = Util.getMillis();
        for (ResourceKey<Level> dimension : levels) {
            var percent = String.format("%.2f", (double) stats.size() / (double) totalDimensions * 100f);
            if (!percent.equalsIgnoreCase(lastPercentage)) {
                lastPercentage = percent;
                JSG.logger.info("SG generator progress: {}% ({}s elapsed)", percent, (double) (Util.getMillis() - started) / 1000);
            }
            var level = server.getLevel(dimension);
            if (level == null) {
                stats.put(dimension.location().toString(), StargateGeneratorDimStatus.ERROR);
                continue;
            }
            message.set(Component.translatable("createWorld.stargates_generating.trying", level.dimension().location()));
            if (!level.structureManager().shouldGenerateStructures()) {
                stats.put(dimension.location().toString(), StargateGeneratorDimStatus.SKIPPED);
                continue;
            }

            if (!JSGDimensionConfig.INSTANCE.getConfigEntrySafe(dimension).map(dimEntry -> dimEntry.getBool("hasStargate", true)).orElse(false)) {
                stats.put(dimension.location().toString(), StargateGeneratorDimStatus.SKIPPED);
                continue;
            }
            var sg = getStargateFromNetwork(network, dimension);
            if (sg.isEmpty()) {
                if (findStructureAndGenerate(level, message)) {
                    totalGenerated.getAndIncrement();
                    stats.put(dimension.location().toString(), StargateGeneratorDimStatus.GENERATED);
                } else stats.put(dimension.location().toString(), StargateGeneratorDimStatus.NO_STRUCTURE);
            } else stats.put(dimension.location().toString(), StargateGeneratorDimStatus.ALREADY_GENERATED);
        }
        message.set(Component.translatable("createWorld.stargates_generating.running_sg_regen"));

        JSG.logger.info("SG generator progress: {}% ({}s elapsed)", String.format("%.2f", 100f), (double) (Util.getMillis() - started) / 1000);
        JSG.logger.info("Total new gates generated: {}", totalGenerated);
        JSG.logger.info("SG generator DONE");
    }

    private static Optional<StargatePos> getStargateFromNetwork(StargateNetwork network, ResourceKey<Level> dimension) {
        if (dimension == Level.OVERWORLD) {
            var sg = network.getStargate(StargateReservedAddresses.OVERWORLD.addresses().get(JSGSymbolTypes.MILKYWAY.get()));
            return Optional.ofNullable(sg);
        }
        return network.getStargateByDimension(dimension).map(Map.Entry::getKey);
    }

    private static boolean findStructureAndGenerate(ServerLevel level, AtomicReference<Component> message) {
        if (level == null)
            return false;
        var result = false;
        var maxStargates = 1;
        if (level.dimension() == Level.END)
            maxStargates = 4;
        var tag = (level.dimension() == Level.OVERWORLD ? JSGStructureTags.HAS_MILKYWAY_STARGATE : JSGStructureTags.HAS_STARGATE);
        for (var i = 0; i < maxStargates; i++) {
            var holderSet = level.registryAccess().registry(tag.registry()).map(r -> r.getOrCreateTag(tag));
            if (holderSet.isEmpty())
                return result;
            message.set(Component.translatable("createWorld.stargates_generating.searching", level.dimension().location()));
            // workaround to prevent WatchDog crashing the server...
            // Go into future...
            AccessUtil.setNextTickTime(level.getServer(), (Util.getMillis() + 5 * 60 * 1000));
            // now search for structure in max 5 minutes
            var structureEntry = level.getChunkSource().getGenerator().findNearestMapStructure(level, holderSet.get(), new BlockPos(0, 0, 0), 200, true);
            // ...and back to present.
            AccessUtil.setNextTickTime(level.getServer(), Util.getMillis());
            if (structureEntry == null) {
                JSG.logger.warn("No structure to generate for {}", level.dimension().location().toString());
                return result;
            }
            message.set(Component.translatable("createWorld.stargates_generating.generating", level.dimension().location()));
            var origin = structureEntry.getFirst().immutable();
            if (level.dimension() == JSGDimensions.ABYDOS)
                origin = new BlockPos(0, 0, 0);
            var chunksRadius = 2;
            for (var x = -chunksRadius; x < chunksRadius; x++) {
                for (var z = -chunksRadius; z < chunksRadius; z++) {
                    ChunkManager.forceChunk(level, new ChunkPos(origin.offset(x * 16, 0, z * 16)), true);
                }
            }
            origin = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin);
            JSG.logger.info("Searching for stargate in {} at {}", level.dimension().location().toString(), origin.toShortString());
            setGeneratedStargateAddress(level, origin, i == 0, structureEntry, message);
            for (var x = -chunksRadius; x < chunksRadius; x++) {
                for (var z = -chunksRadius; z < chunksRadius; z++) {
                    ChunkManager.unforceChunk(level, new ChunkPos(origin.offset(x * 16, 0, z * 16)), true);
                }
            }
            result = true;
        }
        return result;
    }

    private static void setGeneratedStargateAddress(ServerLevel level, BlockPos structureOrigin, boolean setAddress, Pair<BlockPos, Holder<Structure>> structureEntry, AtomicReference<Component> message) {
        final AtomicReference<BoundingBox> box = new AtomicReference<>(null);
        /*var structureManager = level.structureManager();
        var startPiece = structureManager.getStructureAt(structureEntry.getFirst(), structureEntry.getSecond().get());
        if (startPiece == StructureStart.INVALID_START) {
            JSG.logger.error("Failed to get bounding box of structure");
        } else {
            box.set(startPiece.getBoundingBox());
        }*/

        Stargate<?> gateBE = null;
        for (var i = 1; i <= 4; i++) {
            message.set(Component.translatable("createWorld.stargates_generating.searching_stargate", level.dimension().location(), structureOrigin.toShortString(), String.valueOf(i)));
            gateBE = LinkingHelper.findClosestTile(level, structureOrigin, JSGBlockTags.ALL_STARGATE_BASES, Stargate.class,
                    60 + 20 * i, 60 + 20 * i, (pos) -> {
                        if (box.get() == null) return true;
                        return box.get().isInside(pos);
                    }, true);
            if (gateBE != null)
                break;
            JSG.logger.warn("Getting stargate from structure is harder than usually... Maybe it's a big structure? (Attempt #{} failed)", i);
        }

        if (gateBE == null) {
            JSG.logger.error("No stargate found in stargate structure in dimension {}! (structure {})",
                    level.dimension().location().toString(),
                    structureEntry.getSecond().unwrapKey().map(ResourceKey::location).orElse(JSGMapping.rl("error")).toString()
            );
            return;
        }
        message.set(Component.translatable("createWorld.stargates_generating.running_sg_regen"));
        gateBE.tryRegenerateStargateIfNeeded();
        var reservedStargate = StargateReservedAddresses.getStargate(level);
        if (reservedStargate.isEmpty()) return;
        if (reservedStargate.get().isGenerated()) return;
        if (setAddress)
            reservedStargate.get().setAddresses(gateBE);
    }
}
