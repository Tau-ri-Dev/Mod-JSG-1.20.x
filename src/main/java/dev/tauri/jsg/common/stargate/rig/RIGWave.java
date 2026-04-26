package dev.tauri.jsg.common.stargate.rig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tauri.jsg.api.stargate.rig.IRIGWave;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.util.ParameterReplacer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RIGWave implements IRIGWave {
    public static final Codec<RIGWave> CODEC = RecordCodecBuilder.create(rigWaveInstance -> rigWaveInstance.group(
            Codec.INT.fieldOf("weight").forGetter((wave) -> wave.weight),
            Codec.INT.fieldOf("chevronsToDial").forGetter((wave) -> wave.chevronsToDial),
            Codec.STRING.listOf().fieldOf("allowedOnGates").forGetter((wave) -> wave.allowedOnGates),
            Codec.STRING.listOf().optionalFieldOf("allowedInDims").forGetter((wave) -> Optional.ofNullable(wave.allowedInDims)),
            Codec.STRING.listOf().optionalFieldOf("allowedBiomes").forGetter((wave) -> Optional.ofNullable(wave.allowedBiomes)),
            Codec.STRING.listOf().optionalFieldOf("blacklistedBiomes").forGetter((wave) -> Optional.ofNullable(wave.blacklistedBiomes)),
            Codec.STRING.listOf().fieldOf("allowedForDifficulties").forGetter((wave) -> wave.allowedForDifficulties.stream().map(Enum::name).toList()),
            RIGEntity.CODEC.listOf().fieldOf("entities").forGetter((wave) -> wave.entities),
            Codec.BOOL.fieldOf("selectRandomMobs").forGetter((wave) -> wave.selectRandomMobs),
            Codec.DOUBLE.fieldOf("shouldOpenIrisChance").forGetter((wave) -> wave.shouldOpenIrisChance),
            IntProvider.CODEC.optionalFieldOf("randomMobsCount").forGetter((wave) -> Optional.ofNullable(wave.randomMobsCount))
    ).apply(rigWaveInstance, RIGWave::new));

    public int weight;
    public int chevronsToDial;
    public List<String> allowedOnGates;
    @Nullable
    public List<String> allowedInDims;
    @Nullable
    public List<String> allowedBiomes;
    @Nullable
    public List<String> blacklistedBiomes;
    public List<Difficulty> allowedForDifficulties;
    public List<RIGEntity> entities;
    public boolean selectRandomMobs;
    public double shouldOpenIrisChance;
    @Nullable
    public IntProvider randomMobsCount;

    private ParameterReplacer replacer = new ParameterReplacer();

    @SuppressWarnings("all")
    public RIGWave(int weight, int chevronsToDial, List<String> allowedOnGates, Optional<List<String>> allowedInDims, Optional<List<String>> allowedBiomes, Optional<List<String>> blacklistedBiomes, List<String> allowedForDifficulties, List<RIGEntity> entities, boolean selectRandomMobs, double shouldOpenIrisChance, Optional<IntProvider> randomMobsCount) {
        this.weight = weight;
        this.chevronsToDial = chevronsToDial;
        this.allowedOnGates = allowedOnGates;
        this.allowedBiomes = allowedBiomes.orElse(null);
        this.blacklistedBiomes = blacklistedBiomes.orElse(null);
        this.allowedInDims = allowedInDims.orElse(null);
        this.allowedForDifficulties = allowedForDifficulties.stream().map(s -> Difficulty.valueOf(s.toUpperCase())).toList();
        this.entities = entities;
        this.selectRandomMobs = selectRandomMobs;
        this.randomMobsCount = randomMobsCount.orElse(null);
        this.shouldOpenIrisChance = shouldOpenIrisChance;
    }

    private boolean finished = false;
    private LinkedList<RIGEntity> currentPool = new LinkedList<>();

    @Override
    public boolean hasFinished() {
        return finished;
    }

    @Override
    public double shouldOpenIrisChance() {
        return shouldOpenIrisChance;
    }

    @Override
    public void setup(RandomSource random) {
        finished = false;
        currentPool = new LinkedList<>();
        replacer = new ParameterReplacer();
        replacer.addKey("UUID", "[I;" + random.nextInt(2112623056) + "," + random.nextInt(2112623056) + "," + random.nextInt(2112623056) + "," + random.nextInt(2112623056) + "]");
        replacer.addKey("chance1", String.format("%.2f", random.nextFloat()));
        replacer.addKey("chance2", String.format("%.2f", random.nextFloat()));
        replacer.addKey("chance3", String.format("%.2f", random.nextFloat()));
        replacer.addKey("chance4", String.format("%.2f", random.nextFloat()));
        if (!selectRandomMobs) {
            for (var e : entities) {
                for (int i = 0; i < e.weight; i++) {
                    currentPool.addLast(e);
                }
            }
        } else if (randomMobsCount != null) {
            var all = new LinkedList<RIGEntity>();
            for (var e : entities) {
                for (int i = 0; i < e.weight; i++) {
                    all.addLast(e);
                }
            }
            if (all.isEmpty()) finished = true;
            else {
                var count = randomMobsCount.sample(random);
                for (int i = 0; i < count; i++) {
                    var index = random.nextInt(all.size());
                    currentPool.addLast(all.get(index));
                }
            }
        }
        if (currentPool.isEmpty()) finished = true;
    }

    @NotNull
    public Entity getNextEntity(ServerLevel level) {
        if (hasFinished())
            throw new UnsupportedOperationException("Tried to get next entity when wave has already finished!");
        var entity = currentPool.getFirst();
        currentPool.removeFirst();
        if (currentPool.isEmpty()) finished = true;
        return entity.get(level, replacer);
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private int weight = 1;
        private int chevronsToDial = 7;
        private List<String> allowedOnGates = new ArrayList<>(List.of(
                StargateTypes.MILKYWAY.get().getId().toString(),
                StargateTypes.PEGASUS.get().getId().toString(),
                StargateTypes.UNIVERSE.get().getId().toString(),
                StargateTypes.MOVIE.get().getId().toString(),
                StargateTypes.TOLLAN.get().getId().toString()
        ));
        private List<String> allowedInDims = null;
        private List<String> allowedBiomes = null;
        private List<String> blacklistedBiomes = null;
        private List<Difficulty> allowedForDifficulties = new ArrayList<>(List.of(
                Difficulty.HARD,
                Difficulty.NORMAL,
                Difficulty.EASY
        ));
        private List<RIGEntity> entities = new ArrayList<>();
        private boolean selectRandomMobs = false;
        private double shouldOpenIrisChance = 0;
        @Nullable
        private IntProvider randomMobsCount;

        public static Builder create() {
            return new Builder();
        }

        public Builder addEntity(RIGEntity entity) {
            this.entities.add(entity);
            return this;
        }

        public Builder addDim(String dim) {
            return addDim(dim, false);
        }

        public Builder addDim(String dim, boolean clear) {
            if (clear) this.allowedInDims.clear();
            this.allowedInDims.add(dim);
            return this;
        }

        public Builder addDifficulty(Difficulty difficulty) {
            return addDifficulty(difficulty, false);
        }

        public Builder addDifficulty(Difficulty difficulty, boolean clear) {
            if (clear) this.allowedForDifficulties.clear();
            this.allowedForDifficulties.add(difficulty);
            return this;
        }

        public Builder addGate(StargateType<?> stargateType) {
            return addGate(stargateType, false);
        }

        public Builder addGate(StargateType<?> stargateType, boolean clear) {
            if (clear) this.allowedOnGates.clear();
            this.allowedOnGates.add(stargateType.toString());
            return this;
        }

        public Builder selectRandomMobs() {
            this.selectRandomMobs = true;
            return this;
        }

        public Builder shouldOpenIrisChance(double chance) {
            this.shouldOpenIrisChance = chance;
            return this;
        }

        public Builder randomMobsCount(int min, int max) {
            this.randomMobsCount = UniformInt.of(min, max);
            return this;
        }

        public Builder entities(List<RIGEntity> entities) {
            this.entities = entities;
            return this;
        }

        public Builder allowedBiomes(List<String> allowedBiomes) {
            this.allowedBiomes = allowedBiomes;
            return this;
        }

        public Builder blacklistedBiomes(List<String> blacklistedBiomes) {
            this.blacklistedBiomes = blacklistedBiomes;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder chevronsToDial(int chevronsToDial) {
            this.chevronsToDial = chevronsToDial;
            return this;
        }

        public Builder allowedOnGates(List<String> allowedOnGates) {
            this.allowedOnGates = allowedOnGates;
            return this;
        }

        public Builder allowedInDims(List<String> allowedInDims) {
            this.allowedInDims = allowedInDims;
            return this;
        }

        public Builder allowedForDifficulties(List<Difficulty> allowedForDifficulties) {
            this.allowedForDifficulties = allowedForDifficulties;
            return this;
        }


        public RIGWave build() {
            return new RIGWave(weight, chevronsToDial, allowedOnGates, Optional.ofNullable(allowedInDims), Optional.ofNullable(allowedBiomes), Optional.ofNullable(blacklistedBiomes), allowedForDifficulties.stream().map(Enum::name).toList(), entities, selectRandomMobs, shouldOpenIrisChance, Optional.ofNullable(randomMobsCount));
        }
    }
}
