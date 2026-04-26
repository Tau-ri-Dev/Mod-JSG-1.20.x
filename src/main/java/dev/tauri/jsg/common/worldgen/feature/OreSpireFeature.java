package dev.tauri.jsg.common.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.tauri.jsg.common.worldgen.feature.config.OreSpireConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Main part of this class is from the Stargate Jurney mod and its author Povstalec
 */
public class OreSpireFeature extends Feature<OreSpireConfig> {
    public OreSpireFeature(Codec<OreSpireConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreSpireConfig> ctx) {
        var config = ctx.config();

        var blockpos = ctx.origin();
        var randomsource = ctx.random();

        WorldGenLevel worldgenlevel = ctx.level();
        while (!worldgenlevel.getBlockState(blockpos).is(config.canGrowFrom) && blockpos.getY() > worldgenlevel.getMinBuildHeight() + 2) {
            blockpos = blockpos.below();
        }

        if (worldgenlevel.isEmptyBlock(blockpos) || !worldgenlevel.getBlockState(blockpos).is(config.canProtrudeThrough))
            return false;

        int spireLevels = config.levelsProvider.sample(randomsource);
        int totalHeight = 0;

        Map<BlockPos, BlockState> blocksToPlace = new HashMap<>();

        for (int spireLevel = 0; spireLevel < spireLevels; spireLevel++) {
            int currentCenterX = config.deformationProvider.sample(randomsource);
            int currentCenterZ = config.deformationProvider.sample(randomsource);

            int xDeformation = config.deformationProvider.sample(randomsource);
            int zDeformation = config.deformationProvider.sample(randomsource);

            int ySize = config.levelHeightProvider.sample(randomsource);

            int levelRadius = 10 + spireLevels % 5 - (spireLevel / 3);

            int xRadius = levelRadius + xDeformation;
            int zRadius = levelRadius + zDeformation;

            for (int y = 0; y <= ySize; y++) {
                xRadius = adjustRadius(xRadius, ySize, y);
                zRadius = adjustRadius(zRadius, ySize, y);

                for (int x = -xRadius; x <= xRadius; x++) {
                    for (int z = -zRadius; z <= zRadius; z++) {
                        double xScale = Math.pow((double) (x - currentCenterX) / xRadius, 2);
                        double zScale = Math.pow((double) (z - currentCenterZ) / zRadius, 2);
                        if (xScale + zScale < 1) {
                            float chance = config.oreSpawnChanceProvider.sample(randomsource);
                            // Sometimes the block placed should be the ore
                            if (randomsource.nextFloat() < chance) {
                                if (!putOre(blocksToPlace, worldgenlevel, blockpos, x, y, z, totalHeight, config, randomsource))
                                    return false;
                            } else {
                                if (!putFilling(blocksToPlace, worldgenlevel, blockpos, x, y, z, totalHeight, config, randomsource))
                                    return false;
                            }
                        }
                    }
                }
            }
            totalHeight += ySize;
        }
        for (var entry : blocksToPlace.entrySet()) {
            setBlock(worldgenlevel, entry.getKey(), entry.getValue());
        }
        return true;
    }

    protected boolean putOre(Map<BlockPos, BlockState> blocksToPlace, WorldGenLevel level, BlockPos blockpos, int x, int y, int z, int totalHeight, OreSpireConfig config, RandomSource randomsource) {
        var pos = blockpos.offset(x, y + totalHeight, z);
        if (!level.ensureCanWrite(pos)) return false;
        blocksToPlace.put(pos, config.oreProvider.getState(randomsource, blockpos));
        return true;
    }

    protected boolean putFilling(Map<BlockPos, BlockState> blocksToPlace, WorldGenLevel level, BlockPos blockpos, int x, int y, int z, int totalHeight, OreSpireConfig config, RandomSource randomsource) {
        var pos = blockpos.offset(x, y + totalHeight, z);
        if (!level.ensureCanWrite(pos)) return false;
        blocksToPlace.put(pos, config.fillingProvider.getState(randomsource, blockpos));
        return true;
    }

    protected int adjustRadius(int radius, int ySize, int y) {
        if (y == 0)
            return radius + 2;
        else if (y == ySize)
            return radius - 2;
        else if (y == 1)
            return radius + 1;
        else if (y == ySize - 1)
            return radius - 1;

        return radius;
    }
}
