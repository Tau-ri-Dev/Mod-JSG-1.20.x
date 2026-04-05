package dev.tauri.jsg.api.util;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface IStargateGenerator {
    default GeneratedStargate generateStargate(@Nonnull IStargateGenerator.PlacementConfig conf) {
        return generateStargate(conf, true);
    }

    GeneratedStargate generateStargate(@Nonnull IStargateGenerator.PlacementConfig conf, boolean replaceMemberBlocks);

    void setStargateEnergyInternalSmart(IStargateGenerator.PlacementConfig config, int energy);

    enum StargateUpgradesEnum {
        GLYPH_CRYSTAL_TYPE,
        GLYPH_CRYSTAL_STARGATE,
        GLYPH_CRYSTAL_MW,
        GLYPH_CRYSTAL_PEG,
        GLYPH_CRYSTAL_UNI,
        UPGRADE_IRIS_TITANIUM,
        UPGRADE_IRIS_TRINIUM,
        UPGRADE_IRIS_CREATIVE,
        UPGRADE_SHIELD,

        GLYPH_CRYSTAL_DHD,
        UPGRADE_EFFICIENCY_DHD,
        UPGRADE_CAPACITY_DHD
    }

    class PlacementConfig {
        // Global
        public Level world;

        @Nonnull
        public List<StargateUpgradesEnum> upgrades = new ArrayList<>();
        @Nonnull
        public BiomeOverlayInstance overlay = CoreBiomeOverlays.NORMAL.get();
        @Nonnull
        public SymbolType<?> addressSymbolTypeToReturn = JSGSymbolTypes.MILKYWAY.get();

        // Gate
        public BlockPos gateBasePos;
        @Nonnull
        public Direction gateFacing = Direction.NORTH;
        @Nonnull
        public Direction gateVerticalFacing = Direction.SOUTH;
        @Nonnull
        public StargateType<?> gateType = StargateTypes.MILKYWAY.get();
        @Nonnull
        public List<Pair<Integer, Boolean>> capacitors = new ArrayList<>(); // List<Pair<[capacity], [isCreative]>>
        public int stargateEnergyInternal = -1;
        public Function<BEConfig, BEConfig> stargateConfig = null;
        @Nonnull
        public EnumIrisMode irisMode = EnumIrisMode.OPENED;
        @Nonnull
        public String irisCode = "";

        // DHD
        public BlockPos dhdPos;
        public int dhdRotation = -1;
        public int dhdFluid = -1;

        public boolean baseInPlace = false;
    }
}
