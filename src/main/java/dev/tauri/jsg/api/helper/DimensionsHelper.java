package dev.tauri.jsg.api.helper;

import dev.tauri.jsg.api.JSGApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DimensionsHelper {
    public static ServerLevel getLevel(ResourceKey<Level> dim) {
        return JSGApi.currentServer.getLevel(dim);
    }

    public static ResourceKey<Level> getDimension(ResourceLocation rl) {
        return ResourceKey.create(Registries.DIMENSION, rl);
    }

    public static List<ResourceKey<Level>> getDims() {
        var list = new ArrayList<ResourceKey<Level>>();
        JSGApi.currentServer.getAllLevels().forEach(e -> list.add(e.dimension()));
        return list;
    }

    @SuppressWarnings("deprecation")
    public static BlockPos getTopBlockWithPos(ServerLevel level, int x, int z, int structureX, int structureZ) {
        int startY = 255;
        if (level.dimension() == Level.NETHER) startY = 110;
        else {
            structureX = 1;
            structureZ = 1;
        }
        for (int offX = 0; offX < 4; offX++) {
            for (int offZ = 0; offZ < 4; offZ++) {
                boolean foundAir = false;
                int minHeight = Integer.MAX_VALUE;
                int countHeights = 0;
                int sumHeights = 0;
                int hillCoef = 0;
                for (int i = 0; i < structureX; i++) {
                    for (int j = 0; j < structureZ; j++) {
                        var currentX = x + (offX * structureX) + i;
                        var currentZ = z + (offZ * structureZ) + j;
                        level.getPoiManager().ensureLoadedAndValid(level, new BlockPos(currentX, startY, currentZ), 128);
                        for (int y = startY; y > 0; y--) {
                            if (level.getBlockState(new BlockPos(currentX, y, currentZ)).canBeReplaced()) {
                                if (level.getBlockState(new BlockPos(currentX, y, currentZ)).liquid()) break;
                                foundAir = true;
                                continue;
                            }
                            if (!foundAir) continue;
                            if (countHeights == 0)
                                sumHeights = y;
                            else
                                sumHeights += y;
                            countHeights++;
                            if (((sumHeights / (double) countHeights) - y) > 3) hillCoef++;
                            if (y < minHeight) minHeight = y;
                            break;
                        }
                    }
                }
                if (hillCoef > 5) continue;
                var currentX = x + (offX * structureX);
                var currentZ = z + (offZ * structureZ);
                return new BlockPos(currentX, minHeight + 1, currentZ);
            }
        }
        return new BlockPos(x, 255, z);
    }
}
