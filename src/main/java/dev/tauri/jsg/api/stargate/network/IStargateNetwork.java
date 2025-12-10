package dev.tauri.jsg.api.stargate.network;

import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import dev.tauri.jsg.api.stargate.type.StargateType;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;


public interface IStargateNetwork {
    @NotNull
    Optional<Map.Entry<StargatePos, Map<AbstractSymbolType<?>, StargateAddress>>> getStargateByDimension(ResourceKey<Level> dimension);

    StargatePos getStargate(StargateAddress address);

    Map<AbstractSymbolType<?>, StargateAddress> getAddresses(StargatePos pos);

    Pair<StargatePos, StargateAddress> getRandomAddress(RandomSource random, AbstractSymbolType<?> symbolTypeEnum, @Nullable StargateType stargateType, @Nullable Predicate<StargatePos> customTest);

    void putStargate(StargateAddress address, StargatePos stargatePos);

    void putStargate(Map<AbstractSymbolType<?>, StargateAddress> addressMap, StargatePos stargatePos);

    void removeStargate(StargatePos stargatePos);

    void renameStargate(StargatePos pos, String newName);
}
