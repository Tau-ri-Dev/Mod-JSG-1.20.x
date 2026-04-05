package dev.tauri.jsg.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class OreSpireConfig implements FeatureConfiguration {
    public static final Codec<OreSpireConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("filling_provider").forGetter((config) -> config.fillingProvider),
            BlockStateProvider.CODEC.fieldOf("ore_provider").forGetter((config) -> config.oreProvider),
            FloatProvider.CODEC.fieldOf("ore_spawn_chance").forGetter((config) -> config.oreSpawnChanceProvider),
            IntProvider.CODEC.fieldOf("level_count_provider").forGetter((config) -> config.levelsProvider),
            IntProvider.CODEC.fieldOf("level_height_provider").forGetter((config) -> config.levelHeightProvider),
            IntProvider.CODEC.fieldOf("deformation_provider").forGetter((config) -> config.deformationProvider),
            TagKey.hashedCodec(Registries.BLOCK).fieldOf("can_protrude_through").forGetter((config) -> config.canProtrudeThrough),
            TagKey.hashedCodec(Registries.BLOCK).fieldOf("can_grow_from").forGetter((config) -> config.canGrowFrom)
    ).apply(instance, OreSpireConfig::new));

    public BlockStateProvider fillingProvider;
    public BlockStateProvider oreProvider;
    public FloatProvider oreSpawnChanceProvider;
    public IntProvider levelsProvider;
    public IntProvider levelHeightProvider;
    public IntProvider deformationProvider;
    public TagKey<Block> canProtrudeThrough;
    public TagKey<Block> canGrowFrom;

    public OreSpireConfig(BlockStateProvider fillingProvider,
                          BlockStateProvider oreProvider,
                          FloatProvider oreSpawnChanceProvider,
                          IntProvider levelsProvider,
                          IntProvider levelHeightProvider,
                          IntProvider deformationProvider,
                          TagKey<Block> canProtrudeThrough,
                          TagKey<Block> canGrowFrom) {

        this.fillingProvider = fillingProvider;
        this.oreProvider = oreProvider;
        this.oreSpawnChanceProvider = oreSpawnChanceProvider;
        this.levelsProvider = levelsProvider;
        this.levelHeightProvider = levelHeightProvider;
        this.deformationProvider = deformationProvider;
        this.canProtrudeThrough = canProtrudeThrough;
        this.canGrowFrom = canGrowFrom;
    }
}