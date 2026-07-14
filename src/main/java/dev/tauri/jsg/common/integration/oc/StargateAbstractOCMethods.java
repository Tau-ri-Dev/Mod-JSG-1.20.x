package dev.tauri.jsg.common.integration.oc;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc.methods.AbstractOCMethods;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
}
