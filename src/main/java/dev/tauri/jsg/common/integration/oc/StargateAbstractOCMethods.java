package dev.tauri.jsg.common.integration.oc;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.cctweaked.CCTweakedHelper;
import dev.tauri.jsg.core.common.integration.oc.methods.AbstractOCMethods;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import java.util.*;

public class StargateAbstractOCMethods extends AbstractOCMethods<StargateAbstractBaseBE<?, ?>> {
    public StargateAbstractOCMethods(ComputerDeviceProvider gateTile) {
        super((StargateAbstractBaseBE<?, ?>) gateTile, OCDevices.STARGATE_ABSTRACT);
    }

    @SuppressWarnings("unused")
    @Callback(value = "getJSGVersion")
    public final Object[] getJSGVersion(Context context, Arguments args) {
        return new Object[]{JSG.MOD_VERSION};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getOpenedTime")
    public final Object[] getOpenedTime(Context context, Arguments args) {
        if (deviceTile.getDialingManager().getStargateState().engaged()) {
            float openedSeconds = deviceTile.getDialingManager().getConnection().getSecondsOpen();
            int minutes = ((int) Math.floor(openedSeconds / 60));
            int seconds = ((int) (openedSeconds - (60 * minutes)));
            if (openedSeconds > 0) return new Object[]{true, "stargate_time", minutes, seconds};
            return new Object[]{false, "stargate_not_connected"};
        }
        return new Object[]{false, "stargate_not_connected"};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getStargateAddress")
    public final Object[] getStargateAddress(Context context, Arguments args) {
        var a = new HashMap<String, List<String>>();
        for (var symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
            var address = deviceTile.getStargateAddress(symbolType);
            if (address == null) continue;
            a.put(symbolType.getId().toString(), address.getNameList());
        }
        return new Object[]{a};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getDialedAddress")
    public final Object[] getDialedAddress(Context context, Arguments args) {
        return new Object[]{deviceTile.getDialingManager().getDialedAddress().getNameList()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getEnergyStored")
    public final Object[] getEnergyStored(Context context, Arguments args) {
        return new Object[]{deviceTile.getEnergyManager().getStorage().getTrueEnergyStored()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getMaxEnergyStored")
    public final Object[] getMaxEnergyStored(Context context, Arguments args) {
        return new Object[]{deviceTile.getEnergyManager().getStorage().getTrueMaxEnergyStored()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getGateType")
    public final Object[] getGateType(Context context, Arguments args) {
        return new Object[]{deviceTile.isMerged() ? deviceTile.getStargateType().toString() : null};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getSymbolType")
    public final Object[] getSymbolType(Context context, Arguments args) {
        return new Object[]{deviceTile.isMerged() ? deviceTile.getSymbolType().getId() : null};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getSymbolsMap")
    public final Object[] getSymbolsMap(Context context, Arguments args) {
        return new Object[]{deviceTile.isMerged() ? Arrays.stream(deviceTile.getSymbolType().getValues()).map(s -> s.getEnglishName(deviceTile.getPointOfOrigin())).toList() : null};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getGateStatus")
    public final Object[] getGateStatus(Context context, Arguments args) {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        if (deviceTile.getDialingManager().getStargateState().engaged())
            return new Object[]{true, "open", deviceTile.getDialingManager().getStargateState().initiating()};

        return new Object[]{true, deviceTile.getDialingManager().getStargateState().toString().toLowerCase()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getSymbolsNeeded")
    public final Object[] getSymbolsNeeded(Context context, Arguments args) {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(deviceTile.getSymbolType());

        var symbols = CCTweakedHelper.getCorrectlyOrderedTableValues(args.checkTable(0));
        for (Object symbolObj : symbols) {
            if (stargateAddress.size() == 9) {
                throw new IllegalArgumentException("Too much glyphs");
            }

            SymbolInterface symbol = deviceTile.getSymbolFromNameIndex(symbolObj);
            if (stargateAddress.contains(symbol)) {
                throw new IllegalArgumentException("Duplicate glyph");
            }

            stargateAddress.addSymbol(symbol);
        }

        if (!stargateAddress.getLast().origin() && stargateAddress.size() < 9) stargateAddress.addOrigin();

        if (!stargateAddress.validate()) return new Object[]{false, "address_malformed"};

        if (!deviceTile.getDialingManager().canDialAddress(stargateAddress, false))
            return new Object[]{false, "address_malformed"};

        StargatePos pos = StargateNetwork.INSTANCE.getStargate(stargateAddress);
        if (pos == null) return new Object[]{false, "gate_not_found"};

        int symbolsCount = deviceTile.getDialingManager().getMinimalSymbolsToDial(Objects.requireNonNull(pos.getGateSymbolType()), pos);

        return new Object[]{true, "symbols_needed", symbolsCount};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getEnergyRequiredToDial")
    public final Object[] getEnergyRequiredToDial(Context context, Arguments args) {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(deviceTile.getSymbolType());

        var symbols = CCTweakedHelper.getCorrectlyOrderedTableValues(args.checkTable(0));
        for (Object symbolObj : symbols) {
            if (stargateAddress.size() == 9) {
                throw new IllegalArgumentException("Too much glyphs");
            }

            SymbolInterface symbol = deviceTile.getSymbolFromNameIndex(symbolObj);
            if (stargateAddress.contains(symbol)) {
                throw new IllegalArgumentException("Duplicate glyph");
            }

            stargateAddress.addSymbol(symbol);
        }

        if (!stargateAddress.getLast().origin() && stargateAddress.size() < 9) stargateAddress.addOrigin();

        if (!stargateAddress.validate()) return new Object[]{false, "address_malformed"};

        if (!deviceTile.getDialingManager().canDialAddress(stargateAddress, false))
            return new Object[]{false, "address_malformed"};

        var energyRequired = deviceTile.getEnergyManager().getEnergyRequiredToDial(Objects.requireNonNull(StargateNetwork.INSTANCE.getStargate(stargateAddress)), stargateAddress);
        Map<String, Object> energyMap = new HashMap<>();

        energyMap.put("open", energyRequired.energyToOpen);
        energyMap.put("keepAlive", energyRequired.keepAlive);
        energyMap.put("canOpen", deviceTile.getEnergyManager().getStorage().getTrueEnergyStored() >= energyRequired.energyToOpen);

        return new Object[]{true, "energy_map", energyMap};
    }
}
