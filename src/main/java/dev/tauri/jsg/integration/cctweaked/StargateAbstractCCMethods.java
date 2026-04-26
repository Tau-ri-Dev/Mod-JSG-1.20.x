package dev.tauri.jsg.integration.cctweaked;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.core.common.integration.cctweaked.CCDevice;
import dev.tauri.jsg.core.common.integration.cctweaked.CCTweakedHelper;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.AbstractCCMethods;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class StargateAbstractCCMethods<T extends StargateAbstractBaseBE<?, ?>> extends AbstractCCMethods<T> {
    @SuppressWarnings("unchecked")
    public StargateAbstractCCMethods(BlockEntity gateTile) {
        this((T) gateTile, CCDevices.STARGATE_ABSTRACT);
    }

    public StargateAbstractCCMethods(T gateTile, CCDevice device) {
        super(gateTile, device);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getJSGVersion() {
        return new Object[]{JSG.MOD_VERSION};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
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
    @LuaFunction(mainThread = true)
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
    @LuaFunction(mainThread = true)
    public final Object[] getDialedAddress() {
        return new Object[]{deviceTile.getDialingManager().getDialedAddress().getNameList()};
    }


    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getEnergyStored() {
        return new Object[]{deviceTile.getEnergyManager().getStorage().getEnergyStored()};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getMaxEnergyStored() {
        return new Object[]{deviceTile.getEnergyManager().getStorage().getMaxEnergyStored()};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getGateType() {
        return new Object[]{deviceTile.isMerged() ? deviceTile.getStargateType().toString() : null};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getSymbolType() {
        return new Object[]{deviceTile.isMerged() ? deviceTile.getSymbolType().getId() : null};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getSymbolsMap() {
        return new Object[]{deviceTile.isMerged() ? Arrays.stream(deviceTile.getSymbolType().getValues()).map(SymbolInterface::getEnglishName).toList() : null};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getGateStatus() {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        if (deviceTile.getDialingManager().getStargateState().engaged())
            return new Object[]{true, "open", deviceTile.getDialingManager().getStargateState().initiating()};

        return new Object[]{true, deviceTile.getDialingManager().getStargateState().toString().toLowerCase()};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getSymbolsNeeded(ILuaContext ctx, IArguments args) throws LuaException {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(deviceTile.getSymbolType());

        var symbols = CCTweakedHelper.getCorrectlyOrderedTableValues(args.getTable(0));
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
    @LuaFunction(mainThread = true)
    public final Object[] getEnergyRequiredToDial(ILuaContext ctx, IArguments args) throws LuaException {
        if (!deviceTile.isMerged()) return new Object[]{false, "not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(deviceTile.getSymbolType());

        var symbols = CCTweakedHelper.getCorrectlyOrderedTableValues(args.getTable(0));
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
