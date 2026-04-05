package dev.tauri.jsg.stargate.rig;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGRegistries;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.manager.IStargateRIGManager;
import dev.tauri.jsg.api.stargate.rig.IRIGWave;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.stargate.manager.AbstractStargateManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class StargateRIGManager extends AbstractStargateManager<Stargate<?>> implements IStargateRIGManager {
    public final LinkedList<RIGWave> waves = new LinkedList<>();
    private RIGInstance rigInstance;

    public StargateRIGManager(Stargate<?> stargate) {
        super(stargate);
    }

    public void onLoad(@NotNull Level level) {
        if (level.isClientSide) return;
        waves.clear();
        var configWaves = JSGRegistries.R_RIG_WAVES.apply(level.registryAccess()).stream().toList();
        for (var wave : configWaves) {
            if (!wave.allowedOnGates.contains(stargate.getStargateType().toString())) continue;
            for (int i = 0; i < wave.weight; i++) {
                waves.addLast(wave);
            }
        }
    }

    @Override
    public boolean isActive() {
        return rigInstance != null && rigInstance.isRunning();
    }

    @Override
    public boolean isGateActive() {
        return rigInstance != null && rigInstance.isGateActive();
    }

    @Override
    public boolean canStart() {
        if (rigInstance != null) return false;
        if (waves.isEmpty()) return false;
        var level = stargate.getStargateLevel();
        if (level == null) return false;
        if (!stargate.isRIGAllowed()) return false;
        return stargate.isMerged();
    }

    @Nullable
    @Override
    public RIGWave getRandomWave() {
        var allowedWaves = new LinkedList<RIGWave>();
        var level = stargate.getStargateLevel();
        if (level == null) return null;
        var dim = level.dimension();
        var biome = level.getBiome(stargate.blockPosition());
        for (var wave : waves) {
            if (wave.allowedInDims != null && !wave.allowedInDims.isEmpty() && !wave.allowedInDims.contains(dim.location().toString()))
                continue;
            if (wave.allowedBiomes != null && !wave.allowedBiomes.isEmpty()) {
                if (wave.allowedBiomes.stream().noneMatch(e -> {
                    if (e.startsWith("#"))
                        return biome.is(TagKey.create(Registries.BIOME, JSGMapping.rl(e.replaceFirst("#", ""))));
                    return biome.is(JSGMapping.rl(e));
                }))
                    continue;
            }
            if (wave.blacklistedBiomes != null && !wave.blacklistedBiomes.isEmpty()) {
                if (wave.blacklistedBiomes.stream().anyMatch(e -> {
                    if (e.startsWith("#"))
                        return biome.is(TagKey.create(Registries.BIOME, JSGMapping.rl(e.replaceFirst("#", ""))));
                    return biome.is(JSGMapping.rl(e));
                }))
                    continue;
            }
            if (!wave.allowedForDifficulties.contains(level.getDifficulty())) continue;
            allowedWaves.addLast(wave);
        }
        if (allowedWaves.isEmpty()) return null;

        var random = level.getRandom();
        return allowedWaves.get(random.nextInt(allowedWaves.size()));
    }

    @Override
    public void generateNewIncoming(@Nullable Boolean shouldOpenIris) {
        var wave = getRandomWave();
        if (wave == null) return;
        spawnNewIncoming(wave, wave.chevronsToDial, shouldOpenIris);
    }

    @Override
    public void spawnNewIncoming(@NotNull IRIGWave wave, int chevronCount, Boolean shouldOpenIris) {
        if (!stargate.getDialingManager().getStargateState().idle() || stargate.getStargateLevel() == null) return;
        var random = stargate.getStargateLevel().getRandom();
        var openIris = (shouldOpenIris != null && shouldOpenIris) || (random.nextFloat() < wave.shouldOpenIrisChance());
        wave.setup(random);
        if (wave.hasFinished()) return;
        if (chevronCount > 9) chevronCount = 9;
        if (chevronCount < 7) chevronCount = 7;
        var animationLength = ((random.nextInt(5 * 20) + 100.0) / 7.0) * chevronCount;
        rigInstance = new RIGInstance(this, wave, chevronCount, (int) animationLength, openIris);
    }

    public void onUnload() {
        end();
    }

    @Override
    public void end() {
        if (rigInstance == null) return;
        rigInstance.end(true);
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide()) return;
        if (rigInstance == null) {
            if (stargate.getDialingManager().getStargateState().idle() && level.getGameTime() % 40 == 0 && !stargate.isGateBurried()) {
                var rand = level.getRandom();
                if (rand.nextFloat() < (0.0005f * JSGConfig.Stargate.rigChance.get()) && canStart())
                    generateNewIncoming(null);
            }
            return;
        }
        if (rigInstance.isRunning())
            rigInstance.tick();
        else
            rigInstance = null;
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {

    }

    public static Optional<RIGWave> getWave(RegistryAccess access, ResourceLocation id) {
        return Optional.ofNullable(JSGRegistries.R_RIG_WAVES.apply(access).get(id));
    }

    public static List<String> getWaves(RegistryAccess access) {
        return JSGRegistries.R_RIG_WAVES.apply(access).holders().map(r -> r.key().location().toString()).toList();
    }
}
