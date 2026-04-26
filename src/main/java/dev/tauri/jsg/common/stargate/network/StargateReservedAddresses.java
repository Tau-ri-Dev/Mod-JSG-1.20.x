package dev.tauri.jsg.common.stargate.network;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.common.registry.JSGDimensions;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * CONTAINS PER-DIMENSION STARGATE ADDRESS - as in all jsg dims should be at least one stargate
 */
public class StargateReservedAddresses {

    public record ReservedStargate(ResourceKey<Level> dimension, Map<SymbolType<?>, StargateAddress> addresses) {
        public void setAddresses(Stargate<?> baseTile) {
            for (var addressEntry : addresses.entrySet()) {
                baseTile.setGateAddress(addressEntry.getKey(), addressEntry.getValue());
            }
        }

        public boolean isGenerated() {
            for (var addressEntry : addresses.entrySet()) {
                if (StargateNetwork.INSTANCE.getStargate(addressEntry.getValue()) != null) return true;
            }
            return false;
        }
    }

    public static class AddressesBuilder {
        Map<SymbolType<?>, StargateAddress> addressMap = new HashMap<>();

        public AddressesBuilder setAddress(SymbolType<?> type, SymbolInterface... symbols) {
            if (symbols.length < 8) return this;
            var address = new StargateAddress(type, symbols[0], symbols[1], symbols[2], symbols[3], symbols[4], symbols[5], symbols[6], symbols[7]);
            addressMap.put(type, address);
            return this;
        }

        public Map<SymbolType<?>, StargateAddress> build() {
            return addressMap;
        }
    }

    private static final Map<ResourceKey<Level>, ReservedStargate> REGISTRY = new HashMap<>();

    public static ReservedStargate registerStargate(ResourceKey<Level> dimension, Map<SymbolType<?>, StargateAddress> addresses) {
        var sg = new ReservedStargate(dimension, addresses);
        REGISTRY.put(dimension, sg);
        return sg;
    }

    public static Optional<ReservedStargate> getStargate(@Nonnull ServerLevel dim) {
        return getStargate(dim.dimension());
    }

    public static Optional<ReservedStargate> getStargate(@Nonnull ResourceKey<Level> dim) {
        return Optional.ofNullable(REGISTRY.get(dim));
    }

    public static void register() {
    }

    // -----------------------------------------------------------------------
    public static ReservedStargate OVERWORLD = registerStargate(Level.OVERWORLD,
            new AddressesBuilder()
                    .setAddress(JSGSymbolTypes.MILKYWAY.get(),
                            SymbolMilkyWayEnum.AURIGA,
                            SymbolMilkyWayEnum.CETUS,
                            SymbolMilkyWayEnum.CENTAURUS,
                            SymbolMilkyWayEnum.CANCER,
                            SymbolMilkyWayEnum.SCUTUM,
                            SymbolMilkyWayEnum.ERIDANUS,
                            SymbolMilkyWayEnum.CANISMINOR,
                            SymbolMilkyWayEnum.SERPENSCAPUT
                    )
                    .setAddress(JSGSymbolTypes.PEGASUS.get(),
                            SymbolPegasusEnum.BYDO,
                            SymbolPegasusEnum.ZEO,
                            SymbolPegasusEnum.ONCEEL,
                            SymbolPegasusEnum.DAWNRE,
                            SymbolPegasusEnum.RAMNON,
                            SymbolPegasusEnum.GILLTIN,
                            SymbolPegasusEnum.ELENAMI,
                            SymbolPegasusEnum.RECKTIC
                    )
                    .setAddress(JSGSymbolTypes.UNIVERSE.get(),
                            SymbolUniverseEnum.G10,
                            SymbolUniverseEnum.G15,
                            SymbolUniverseEnum.G20,
                            SymbolUniverseEnum.G26,
                            SymbolUniverseEnum.G28,
                            SymbolUniverseEnum.G13,
                            SymbolUniverseEnum.G18,
                            SymbolUniverseEnum.G6
                    ).build()
    );
    public static ReservedStargate ABYDOS_STARGATE = registerStargate(JSGDimensions.ABYDOS,
            new AddressesBuilder()
                    .setAddress(JSGSymbolTypes.MILKYWAY.get(),
                            SymbolMilkyWayEnum.TAURUS,
                            SymbolMilkyWayEnum.SERPENSCAPUT,
                            SymbolMilkyWayEnum.CAPRICORNUS,
                            SymbolMilkyWayEnum.MONOCEROS,
                            SymbolMilkyWayEnum.SAGITTARIUS,
                            SymbolMilkyWayEnum.ORION,
                            SymbolMilkyWayEnum.CANISMINOR,
                            SymbolMilkyWayEnum.AURIGA
                    )
                    .setAddress(JSGSymbolTypes.PEGASUS.get(),
                            SymbolPegasusEnum.ACJESIS,
                            SymbolPegasusEnum.BASELAI,
                            SymbolPegasusEnum.DANAMI,
                            SymbolPegasusEnum.ECRUMIG,
                            SymbolPegasusEnum.AAXEL,
                            SymbolPegasusEnum.GILLTIN,
                            SymbolPegasusEnum.HACEMILL,
                            SymbolPegasusEnum.RECKTIC
                    )
                    .setAddress(JSGSymbolTypes.UNIVERSE.get(),
                            SymbolUniverseEnum.G5,
                            SymbolUniverseEnum.G6,
                            SymbolUniverseEnum.G18,
                            SymbolUniverseEnum.G20,
                            SymbolUniverseEnum.G1,
                            SymbolUniverseEnum.G7,
                            SymbolUniverseEnum.G22,
                            SymbolUniverseEnum.G31
                    ).build()
    );
    public static ReservedStargate NETHER = registerStargate(Level.NETHER,
            new AddressesBuilder()
                    .setAddress(JSGSymbolTypes.MILKYWAY.get(),
                            SymbolMilkyWayEnum.LYNX,
                            SymbolMilkyWayEnum.VIRGO,
                            SymbolMilkyWayEnum.CANISMINOR,
                            SymbolMilkyWayEnum.ERIDANUS,
                            SymbolMilkyWayEnum.CENTAURUS,
                            SymbolMilkyWayEnum.EQUULEUS,
                            SymbolMilkyWayEnum.SEXTANS,
                            SymbolMilkyWayEnum.MICROSCOPIUM
                    )
                    .setAddress(JSGSymbolTypes.PEGASUS.get(),
                            SymbolPegasusEnum.BASELAI,
                            SymbolPegasusEnum.ACJESIS,
                            SymbolPegasusEnum.DANAMI,
                            SymbolPegasusEnum.ECRUMIG,
                            SymbolPegasusEnum.AAXEL,
                            SymbolPegasusEnum.GILLTIN,
                            SymbolPegasusEnum.HACEMILL,
                            SymbolPegasusEnum.RECKTIC
                    )
                    .setAddress(JSGSymbolTypes.UNIVERSE.get(),
                            SymbolUniverseEnum.G6,
                            SymbolUniverseEnum.G5,
                            SymbolUniverseEnum.G18,
                            SymbolUniverseEnum.G20,
                            SymbolUniverseEnum.G1,
                            SymbolUniverseEnum.G7,
                            SymbolUniverseEnum.G22,
                            SymbolUniverseEnum.G31
                    ).build()
    );
    public static ReservedStargate END = registerStargate(Level.END,
            new AddressesBuilder()
                    .setAddress(JSGSymbolTypes.MILKYWAY.get(),
                            SymbolMilkyWayEnum.MICROSCOPIUM,
                            SymbolMilkyWayEnum.PISCES,
                            SymbolMilkyWayEnum.PISCISAUSTRINUS,
                            SymbolMilkyWayEnum.SCUTUM,
                            SymbolMilkyWayEnum.ORION,
                            SymbolMilkyWayEnum.SERPENSCAPUT,
                            SymbolMilkyWayEnum.GEMINI,
                            SymbolMilkyWayEnum.NORMA
                    )
                    .setAddress(JSGSymbolTypes.PEGASUS.get(),
                            SymbolPegasusEnum.OLAVII,
                            SymbolPegasusEnum.BASELAI,
                            SymbolPegasusEnum.CAPO,
                            SymbolPegasusEnum.ZAMILLOZ,
                            SymbolPegasusEnum.TAHNAN,
                            SymbolPegasusEnum.ILLUME,
                            SymbolPegasusEnum.ZEO,
                            SymbolPegasusEnum.POCORE
                    )
                    .setAddress(JSGSymbolTypes.UNIVERSE.get(),
                            SymbolUniverseEnum.G3,
                            SymbolUniverseEnum.G30,
                            SymbolUniverseEnum.G11,
                            SymbolUniverseEnum.G14,
                            SymbolUniverseEnum.G31,
                            SymbolUniverseEnum.G9,
                            SymbolUniverseEnum.G1,
                            SymbolUniverseEnum.G22
                    ).build()
    );
}
