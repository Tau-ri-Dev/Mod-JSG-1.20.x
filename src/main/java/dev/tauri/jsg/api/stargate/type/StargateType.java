package dev.tauri.jsg.api.stargate.type;


import dev.tauri.jsg.api.registry.JSGRegistries;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.core.common.config.ingame.option.ConfigOptionsHolder;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Since Tollan gate has same set of symbols as MW gate, use this to identify which gate is which...
 */
public class StargateType<T extends SymbolType<?>> implements IPointOfOriginType {
    public final String name;
    public final Supplier<T> symbolType;
    public final Supplier<Block> baseBlockSupplier;
    @Nullable
    public final Supplier<Block> dhdBlockSupplier;
    public final Class<? extends BlockEntity> baseBlockEntityClass;
    public final Supplier<ConfigOptionsHolder> configOptionsHolder;
    public final boolean isClassic;
    public final List<String> pooModelsTypes;
    public final List<String> pooTexturesTypes;
    public final List<ResourceLocation> defaultPointOfOrigins;

    public StargateType(String name, Supplier<T> symbolType, Class<? extends BlockEntity> baseBlockEntityClass, Supplier<Block> baseBlockSupplier, @Nullable Supplier<Block> dhdBlockSupplier, @Nonnull Supplier<ConfigOptionsHolder> configOptionsHolder, boolean isClassic, List<String> pooModelsTypes, List<String> pooTexturesTypes, List<ResourceLocation> defaultPointOfOrigins) {
        this.name = name;
        this.symbolType = symbolType;
        this.baseBlockEntityClass = baseBlockEntityClass;
        this.baseBlockSupplier = baseBlockSupplier;
        this.dhdBlockSupplier = dhdBlockSupplier;
        this.configOptionsHolder = configOptionsHolder;
        this.isClassic = isClassic;
        this.defaultPointOfOrigins = defaultPointOfOrigins;
        this.pooModelsTypes = pooModelsTypes;
        this.pooTexturesTypes = pooTexturesTypes;
    }

    public ResourceLocation getId() {
        return JSGRegistries.R_STARGATE_TYPE.get().getKey(this);
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

    public static StargateType<?> valueOf(ResourceLocation id) {
        return JSGRegistries.R_STARGATE_TYPE.get().getValue(id);
    }

    public static Collection<StargateType<?>> values() {
        return JSGRegistries.R_STARGATE_TYPE.get().getValues();
    }

    @Nullable
    public static StargateType<?> getRandom(RandomSource random) {
        return new ArrayList<>(values()).get(random.nextInt(values().size()));
    }

    @Nullable
    public static StargateType<?> getRandomClassic(RandomSource random) {
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

    public static StargateType<?> parse(SymbolType<?> type) {
        if (type == JSGSymbolTypes.MILKYWAY.get()) return StargateTypes.MILKYWAY.get();
        if (type == JSGSymbolTypes.PEGASUS.get()) return StargateTypes.PEGASUS.get();
        if (type == JSGSymbolTypes.UNIVERSE.get()) return StargateTypes.UNIVERSE.get();
        return StargateTypes.MILKYWAY.get();
    }

    @Override
    public List<String> getPoOModelsTypes() {
        return pooModelsTypes;
    }

    @Override
    public List<String> getPoOTexturesTypes() {
        return pooTexturesTypes;
    }

    @Override
    public List<ResourceLocation> getPoODefaults() {
        return defaultPointOfOrigins;
    }

    @Override
    public @Nullable PointOfOrigin getDefaultPoO() {
        if (getPoODefaults().isEmpty()) return null;
        return StargatePointOfOriginsDefaults.get(this, getPoODefaults().get(0));
    }
}
