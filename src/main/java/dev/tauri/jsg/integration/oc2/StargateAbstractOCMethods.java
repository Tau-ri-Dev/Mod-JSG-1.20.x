package dev.tauri.jsg.integration.oc2;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.AbstractOCMethods;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import li.cil.oc2.api.bus.device.object.Callback;

import java.util.*;

public class StargateAbstractOCMethods extends AbstractOCMethods<StargateAbstractBaseBE<?, ?>> {
    public StargateAbstractOCMethods(ComputerDeviceProvider gateTile) {
        super((StargateAbstractBaseBE<?, ?>) gateTile, OCDevices.STARGATE_ABSTRACT);
    }

    @SuppressWarnings("unused")
    @Callback(name = "getJSGVersion")
    public final Object[] getJSGVersion() {
        return new Object[]{JSG.MOD_VERSION};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getOpenedTime")
    public final Object[] getOpenedTime() {
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
    @Callback(name = "getStargateAddress")
    public final Object[] getStargateAddress() {
        var a = new HashMap<String, List<String>>();
        for (var symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
            var address = deviceTile.getStargateAddress(symbolType);
            if (address == null) continue;
            a.put(symbolType.getId().toString(), address.getNameList());
        }
        return new Object[]{a};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getDialedAddress")
    public final Object[] getDialedAddress() {
        return new Object[]{deviceTile.getDialingManager().getDialedAddress().getNameList()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getEnergyStored")
    public final Object[] getEnergyStored() {
        return new Object[]{deviceTile.getEnergyManager().getStorage().getEnergyStored()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getMaxEnergyStored")
    public final Object[] getMaxEnergyStored() {
        return new Object[]{deviceTile.getEnergyManager().getStorage().getMaxEnergyStored()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getGateType")
    public final Object[] getGateType() {
        return new Object[]{deviceTile.isMerged() ? deviceTile.getStargateType().toString() : null};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getSymbolType")
    public final Object[] getSymbolType() {
        return new Object[]{deviceTile.isMerged() ? deviceTile.getSymbolType().getId() : null};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getSymbolsMap")
    public final Object[] getSymbolsMap() {
        return new Object[]{deviceTile.isMerged() ? Arrays.stream(deviceTile.getSymbolType().getValues()).map(SymbolInterface::getEnglishName).toList() : null};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getGateStatus")
    public final Object[] getGateStatus() {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        if (deviceTile.getDialingManager().getStargateState().engaged())
            return new Object[]{true, "open", deviceTile.getDialingManager().getStargateState().initiating()};

        return new Object[]{true, deviceTile.getDialingManager().getStargateState().toString().toLowerCase()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getSymbolsNeeded")
    public final Object[] getSymbolsNeeded(Object... symbols) {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(deviceTile.getSymbolType());
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

        int symbolsCount = deviceTile.getDialingManager().getMinimalSymbolsToDial(pos.getGateSymbolType(), pos);

        return new Object[]{true, "symbols_needed", symbolsCount};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getEnergyRequiredToDial")
    public final Object[] getEnergyRequiredToDial(Object... symbols) {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(deviceTile.getSymbolType());
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
        energyMap.put("canOpen", deviceTile.getEnergyManager().getStorage().getEnergyStored() >= energyRequired.energyToOpen);

        return new Object[]{true, "energy_map", energyMap};
    }
}
