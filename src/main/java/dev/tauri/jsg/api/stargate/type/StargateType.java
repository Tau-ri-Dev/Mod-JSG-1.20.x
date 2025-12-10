package dev.tauri.jsg.api.stargate.type;


import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolTypeRegistry;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Since Tollan gate has same set of symbols as MW gate, use this to identify which gate is which...
 */
public class StargateType {
    private static final Map<Integer, StargateType> REGISTRY = new HashMap<>();
    private static final Map<String, StargateType> REGISTRY_STRING = new HashMap<>();

    private static int nextId = 0;
    public final int id;
    public final String textId;
    public final String name;
    public final AbstractSymbolType<?> symbolType;
    public final Supplier<Block> baseBlockSupplier;
    @Nullable
    public final Supplier<Block> dhdBlockSupplier;
    public final Class<? extends BlockEntity> baseBlockEntityClass;
    public final ResourceLocation configType;
    public final boolean isClassic;

    public StargateType(String textId, String name, AbstractSymbolType<?> symbolType, Class<? extends BlockEntity> baseBlockEntityClass, Supplier<Block> baseBlockSupplier, @Nullable Supplier<Block> dhdBlockSupplier, @Nonnull ResourceLocation configType, boolean isClassic) {
        this.id = nextId++;
        this.textId = textId.toLowerCase();
        this.name = name;
        this.symbolType = symbolType;
        this.baseBlockEntityClass = baseBlockEntityClass;
        this.baseBlockSupplier = baseBlockSupplier;
        this.dhdBlockSupplier = dhdBlockSupplier;
        this.configType = configType;
        this.isClassic = isClassic;

        REGISTRY.put(id, this);
        REGISTRY_STRING.put(this.textId, this);
        REGISTRY_STRING.put(this.name.toLowerCase(), this);
    }

    public String getId() {
        return textId;
    }

    @Override
    public String toString() {
        return name;
    }

    public Block getBaseBlock() {
        return baseBlockSupplier.get();
    }

    public Block getDHDBlock() {
        if (dhdBlockSupplier == null) return null;
        return dhdBlockSupplier.get();
    }


    // ------------------------------------------------------------
    // Static

    public static StargateType valueOf(int id) {
        return REGISTRY.get(id);
    }

    public static StargateType valueOf(String id) {
        return REGISTRY_STRING.get(id.toLowerCase());
    }

    public static Collection<StargateType> values() {
        return REGISTRY.values();
    }

    @Nullable
    public static StargateType getRandom(RandomSource random) {
        if (REGISTRY.isEmpty()) return null;
        return new ArrayList<>(REGISTRY.values()).get(random.nextInt(REGISTRY.size()));
    }

    @Nullable
    public static StargateType getRandomClassic(RandomSource random) {
        int i = 0;
        while (i < 100) {
            i++;
            var r = getRandom(random);
            if (r == null) return null;
            if (!r.isClassic) continue;
            return r;
        }
        return null;
    }

    public static StargateType parse(AbstractSymbolType<?> type) {
        if (type == SymbolTypeRegistry.MILKYWAY) return StargateTypes.MILKYWAY;
        if (type == SymbolTypeRegistry.PEGASUS) return StargateTypes.PEGASUS;
        if (type == SymbolTypeRegistry.UNIVERSE) return StargateTypes.UNIVERSE;
        return StargateTypes.MILKYWAY;
    }
}
