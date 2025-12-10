package dev.tauri.jsg.api.chunkloader;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.Stargate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.UUID;

public class ChunkManager {

    private static final UUID JSG_CHUNKS = UUID.fromString("ebe3ef80-f613-48a8-b10d-7a28406224d1");

    public static void forceChunk(ServerLevel world, ChunkPos chunk) {
        forceChunk(world, chunk, false);
    }

    public static void forceChunk(ServerLevel world, ChunkPos chunk, boolean quiet) {
        boolean forced = ForgeChunkManager.forceChunk(world, JSGApi.MOD_ID, JSG_CHUNKS, chunk.x, chunk.z, true, true);
        if (!quiet && forced)
            JSGApi.logger.info("Forcing chunk {}, in world {}", chunk, world.dimension().location().toString());
    }

    public static void unforceChunk(ServerLevel world, ChunkPos chunk) {
        unforceChunk(world, chunk, false);
    }

    public static void unforceChunk(ServerLevel world, ChunkPos chunk, boolean quiet) {
        boolean forced = ForgeChunkManager.forceChunk(world, JSGApi.MOD_ID, JSG_CHUNKS, chunk.x, chunk.z, false, true);
        if (!quiet && forced)
            JSGApi.logger.info("Un-forcing chunk {}, in world {}", chunk, world.dimension().location().toString());
    }

    public static void forceChunk(Stargate<?> stargate) {
        forceChunk((ServerLevel) stargate.getLevel(), new ChunkPos(stargate.getBlockPos()));
    }

    public static void unforceChunk(Stargate<?> stargate) {
        unforceChunk((ServerLevel) stargate.getLevel(), new ChunkPos(stargate.getBlockPos()));
    }
}
