package dev.tauri.jsg.api.stargate.network.address.symbol;

import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTypeRegistry {
    public static int currentId = 0;
    public static final Map<Integer, AbstractSymbolType<?>> REGISTRY = new HashMap<>();
    public static final Map<SymbolUsage, Map<Integer, AbstractSymbolType<?>>> REGISTRY_USAGE = new HashMap<>();
    public static final Map<String, AbstractSymbolType<?>> REGISTRY_STRING = new HashMap<>();
    public static final Map<AbstractSymbolType<?>, Integer> REGISTRY_REVERSED = new HashMap<>();

    @ParametersAreNonnullByDefault
    public static <E extends SymbolInterface> AbstractSymbolType<E> registerSymbolType(SymbolUsage usage, AbstractSymbolType<E> provider) {
        REGISTRY.put(currentId, provider);
        REGISTRY_STRING.put(provider.getId().toLowerCase(), provider);
        REGISTRY_REVERSED.put(provider, currentId);
        REGISTRY_USAGE.computeIfAbsent(usage, (key) -> new LinkedHashMap<>());
        REGISTRY_USAGE.get(usage).put(currentId, provider);
        currentId++;
        return provider;
    }

    public static final AbstractSymbolType<SymbolMilkyWayEnum> MILKYWAY = registerSymbolType(SymbolUsage.STARGATES, SymbolMilkyWayEnum.getProvider());
    public static final AbstractSymbolType<SymbolPegasusEnum> PEGASUS = registerSymbolType(SymbolUsage.STARGATES, SymbolPegasusEnum.getProvider());
    public static final AbstractSymbolType<SymbolUniverseEnum> UNIVERSE = registerSymbolType(SymbolUsage.STARGATES, SymbolUniverseEnum.getProvider());

    public static AbstractSymbolType<?> getNext(AbstractSymbolType<?> current, boolean previous) {
        var id = AbstractSymbolType.getId(current) + (previous ? -1 : 1);
        if (id < 0) return AbstractSymbolType.byId(REGISTRY.size() - 1);
        if (id >= REGISTRY.size()) return AbstractSymbolType.byId(0);
        return AbstractSymbolType.byId(id);
    }

    @Nonnull
    public static AbstractSymbolType<?> getRandom() {
        return byId((int) (Math.random() * SymbolTypeRegistry.currentId));
    }

    public static AbstractSymbolType<?> byId(int id) {
        return SymbolTypeRegistry.REGISTRY.get(id);
    }

    public static AbstractSymbolType<?> byId(String id) {
        return SymbolTypeRegistry.REGISTRY_STRING.get(id);
    }

    public static int getId(AbstractSymbolType<?> type) {
        return SymbolTypeRegistry.REGISTRY_REVERSED.get(type);
    }

    public static AbstractSymbolType<?>[] values(@Nullable SymbolUsage usage) {
        if (usage == null)
            return SymbolTypeRegistry.REGISTRY.values().toArray(new AbstractSymbolType<?>[0]);
        else {
            var map = SymbolTypeRegistry.REGISTRY_USAGE.get(usage);
            if (map == null) return new AbstractSymbolType<?>[0];
            return map.values().toArray(new AbstractSymbolType<?>[0]);
        }
    }
}
