package dev.tauri.jsg.common.worldgen.generator;

import net.neoforged.fml.common.EventBusSubscriber;
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
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.util.AccessUtil;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@EventBusSubscriber
public class DimensionStargateGenerator {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStarted(ServerStartedEvent e) {
        var server = e.getServer();
        JSG.logger.info("Started generating stargate in dimensions...");

        var network = StargateNetwork.INSTANCE;
        var levels = server.levelKeys();
        var lastPercentage = "";
        final int totalDimensions = levels.size();
        final LinkedHashMap<String, StargateGeneratorStepStatus> stats = new LinkedHashMap<>();
        AtomicInteger totalGenerated = new AtomicInteger();
        AtomicReference<Component> message = new AtomicReference<>(Component.empty());
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient())
            SGGeneratorGuiProvider.showProgress(() -> totalDimensions, () -> stats, message::get);
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
            message.set(Component.translatable("createWorld.stargates_generating.trying", level.dimension().location().toString()));
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
                boolean generated;
                try {
                    generated = findStructureAndGenerate(level, message);
                } catch (Exception ex) {
                    // Generating a stargate here forces chunk generation. Another mod's world
                    // generation may throw while those chunks generate (e.g. a placed feature
                    // reading a biome outside its allowed region -> "Requested chunk unavailable
                    // during world generation"). In 1.21 that stores a fatal exception on the
                    // server (MinecraftServer.setFatalException) which findStructureAndGenerate
                    // surfaces to us via managedBlock. Clear it so the server survives, then skip
                    // this dimension and carry on instead of aborting world creation.
                    MinecraftServer.fatalException.set(null);
                    JSG.logger.error("Failed to generate stargate in dimension {} - skipping it. "
                            + "World creation continues without a gate there.",
                            dimension.location(), ex);
                    stats.put(dimension.location().toString(), StargateGeneratorDimStatus.ERROR);
                    continue;
                }
                if (generated) {
                    totalGenerated.getAndIncrement();
                    stats.put(dimension.location().toString(), StargateGeneratorDimStatus.GENERATED);
                } else stats.put(dimension.location().toString(), StargateGeneratorDimStatus.NO_STRUCTURE);
            } else stats.put(dimension.location().toString(), StargateGeneratorDimStatus.ALREADY_GENERATED);
        }
        message.set(Component.translatable("createWorld.stargates_generating.running_sg_regen"));

        JSG.logger.info("SG generator progress: {}% ({}s elapsed)", String.format("%.2f", 100f), (double) (Util.getMillis() - started) / 1000);
        JSG.logger.info("Total new gates generated: {}", totalGenerated);
        // Per-dimension summary, cross-checked against the stargate network. The stats map is
        // our own bookkeeping; the network is the ground truth for "a gate really exists here",
        // so a GENERATED dimension without a registered gate means generation silently failed.
        for (ResourceKey<Level> dimension : levels) {
            var status = stats.get(dimension.location().toString());
            var registered = getStargateFromNetwork(network, dimension).isPresent();
            if ((status == StargateGeneratorDimStatus.GENERATED || status == StargateGeneratorDimStatus.ALREADY_GENERATED) && !registered)
                JSG.logger.error("SG generator summary: {} -> {} but NO stargate is registered in the network!", dimension.location(), status);
            else if (status == StargateGeneratorDimStatus.ERROR)
                JSG.logger.warn("SG generator summary: {} -> {} (no gate was generated there, see errors above)", dimension.location(), status);
            else
                JSG.logger.info("SG generator summary: {} -> {}{}", dimension.location(), status, registered ? " (gate registered in network)" : "");
        }
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
            message.set(Component.translatable("createWorld.stargates_generating.searching", level.dimension().location().toString()));
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
            message.set(Component.translatable("createWorld.stargates_generating.generating", level.dimension().location().toString()));
            var origin = structureEntry.getFirst().immutable();
            if (level.dimension() == JSGDimensions.ABYDOS)
                origin = new BlockPos(0, 0, 0);
            // Force-load a small region around the structure's start piece. The stargate lives
            // in that start piece (at the located origin), so this is all the scan needs; the
            // scan itself (setGeneratedStargateAddress -> LinkingHelper) only reads already-loaded
            // chunks, so it never generates the distant chunks its search radius would reach.
            //
            // We must NOT use a blocking forceChunk here: if generating one of these chunks fails
            // (another mod's worldgen throwing), 1.21 stores the error via
            // MinecraftServer.setFatalException and leaves the chunk future incomplete, which
            // hangs the plain chunk-source managedBlock forever. Instead we ticket the region and
            // drive generation through the server's own managedBlock, which both pumps chunk
            // generation and re-throws that fatal exception to us (caught in onServerStarted),
            // so a bad dimension is skipped instead of hanging world creation.
            var chunksRadius = 3;
            var originChunk = new ChunkPos(origin);
            var chunkSource = level.getChunkSource();
            // Push the next-tick deadline far out so the watchdog stays quiet and haveTime() keeps
            // returning true (otherwise the server would not pump chunk-generation tasks for us).
            AccessUtil.setNextTickTime(level.getServer(), Util.getMillis() + 5 * 60 * 1000);
            chunkSource.addRegionTicket(TicketType.FORCED, originChunk, chunksRadius, originChunk);
            var escalated = false;
            try {
                waitForChunks(level, originChunk, chunksRadius);
                origin = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin);
                JSG.logger.info("Searching for stargate in {} at {}", level.dimension().location().toString(), origin.toShortString());
                var found = setGeneratedStargateAddress(level, origin, i == 0, structureEntry, message);
                if (!found) {
                    // The gate is normally in the structure's start piece right at the located
                    // origin, but some structures place it in a piece further out. The scan never
                    // loads chunks itself, so widen the loaded region to the scan's full 140-block
                    // range and scan once more before declaring failure.
                    JSG.logger.warn("No stargate within the initially loaded region of {} - loading the full scan range and retrying", level.dimension().location());
                    escalated = true;
                    chunkSource.addRegionTicket(TicketType.FORCED, originChunk, ESCALATED_CHUNKS_RADIUS, originChunk);
                    waitForChunks(level, originChunk, ESCALATED_CHUNKS_RADIUS);
                    found = setGeneratedStargateAddress(level, origin, i == 0, structureEntry, message);
                }
                if (!found)
                    throw new IllegalStateException("Structure generated at " + origin.toShortString() + " but no stargate was found inside it");
                result = true;
            } finally {
                // Always release the region tickets, even if generation threw above, so an aborted
                // dimension does not leak forced chunks.
                chunkSource.removeRegionTicket(TicketType.FORCED, originChunk, chunksRadius, originChunk);
                if (escalated)
                    chunkSource.removeRegionTicket(TicketType.FORCED, originChunk, ESCALATED_CHUNKS_RADIUS, originChunk);
                AccessUtil.setNextTickTime(level.getServer(), Util.getMillis());
            }
        }
        return result;
    }

    /**
     * Covers the widest gate scan in {@link #setGeneratedStargateAddress} (140 blocks): a block
     * 140 blocks from the origin is at most 9 chunks from the origin's chunk.
     */
    private static final int ESCALATED_CHUNKS_RADIUS = 9;

    /**
     * Pumps the server's task queue (which drives chunk generation) until every chunk within
     * {@code radius} of {@code center} is fully generated. The server's own managedBlock re-throws
     * worldgen failures stored via {@link MinecraftServer#setFatalException}, and the deadline
     * turns a stall into an exception too - both are handled per-dimension in onServerStarted,
     * so a bad dimension is skipped instead of hanging world creation or scanning a half-loaded
     * region.
     */
    private static void waitForChunks(ServerLevel level, ChunkPos center, int radius) {
        var chunkSource = level.getChunkSource();
        final long deadlineNanos = Util.getNanos() + 120L * 1_000_000_000L;
        level.getServer().managedBlock(() -> Util.getNanos() > deadlineNanos
                || allChunksFull(chunkSource, center, radius));
        if (!allChunksFull(chunkSource, center, radius))
            throw new IllegalStateException("Timed out waiting for chunks around " + center + " (radius " + radius + ") to generate");
    }

    /**
     * @return true once every chunk in the (2*radius+1) square around {@code center} is fully
     * generated and loaded. Never triggers generation itself - {@link ServerChunkCache#getChunkNow}
     * returns null for chunks that are not yet at FULL status.
     */
    private static boolean allChunksFull(ServerChunkCache chunkSource, ChunkPos center, int radius) {
        for (var x = -radius; x <= radius; x++) {
            for (var z = -radius; z <= radius; z++) {
                if (chunkSource.getChunkNow(center.x + x, center.z + z) == null)
                    return false;
            }
        }
        return true;
    }

    /**
     * @return true if a stargate was found (and registered) in the generated structure. The scan
     * only sees loaded chunks, so a false return may just mean the gate's chunk is not loaded yet.
     */
    private static boolean setGeneratedStargateAddress(ServerLevel level, BlockPos structureOrigin, boolean setAddress, Pair<BlockPos, Holder<Structure>> structureEntry, AtomicReference<Component> message) {
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
            message.set(Component.translatable("createWorld.stargates_generating.searching_stargate", level.dimension().location().toString(), structureOrigin.toShortString(), String.valueOf(i)));
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
            return false;
        }
        message.set(Component.translatable("createWorld.stargates_generating.running_sg_regen"));
        gateBE.tryRegenerateStargateIfNeeded();
        var reservedStargate = StargateReservedAddresses.getStargate(level);
        if (reservedStargate.isEmpty()) return true;
        if (reservedStargate.get().isGenerated()) return true;
        if (setAddress)
            reservedStargate.get().setAddresses(gateBE);
        return true;
    }
}
