package dev.tauri.jsg.api.stargate.network;

import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.core.common.symbol.SymbolType;
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
    Optional<Map.Entry<StargatePos, Map<SymbolType<?>, StargateAddress>>> getStargateByDimension(ResourceKey<Level> dimension);

    StargatePos getStargate(StargateAddress address);

    Map<SymbolType<?>, StargateAddress> getAddresses(StargatePos pos);

    Pair<StargatePos, StargateAddress> getRandomAddress(RandomSource random, SymbolType<?> symbolTypeEnum, @Nullable StargateType<?> stargateType, @Nullable Predicate<StargatePos> customTest);

    void putStargate(StargateAddress address, StargatePos stargatePos);

    void putStargate(Map<SymbolType<?>, StargateAddress> addressMap, StargatePos stargatePos);

    void removeStargate(StargatePos stargatePos);

    void renameStargate(StargatePos pos, String newName);
}
