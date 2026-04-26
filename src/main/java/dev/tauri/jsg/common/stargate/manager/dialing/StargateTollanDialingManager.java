package dev.tauri.jsg.common.stargate.manager.dialing;

import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.common.blockentity.stargate.StargateTollanBaseBE;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class StargateTollanDialingManager extends StargateMilkyWayDialingManager {
    public StargateTollanDialingManager(StargateTollanBaseBE stargate) {
        super(stargate);
    }

    @Override
    public StargateChevronEngageResult engageSymbolDHD(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle())
            return StargateChevronEngageResult.BUSY;
        var r = engageSymbolInternal(symbol, false, noEnergy, ignoreMaxChevrons);
        if (!r.ok()) {
            return r;
        }
        StargateComputerEvents.CHEVRON_ENGAGED.apply(StargateComputerEvents.ChevronEvent.Source.DHD, symbol, getNextChevron(symbol, true, ignoreMaxChevrons), getDialedAddressSize()).sendVia(stargate);

        setStargateState(EnumStargateState.DIALING);
        if (r == StargateChevronEngageResult.OK_CONNECTED) {
            getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), 30));
        }
        int plusTime = new Random().nextInt(5);
        stargate.getStateManager().getChevronsState().scheduleChevronActivate(10 + plusTime, getNextChevron(symbol, true, ignoreMaxChevrons), null, true);
        if (addressDialSequence != null)
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 15 + plusTime));
        stargate.setChanged();
        return r;
    }

    @Override
    public boolean engageSymbolBySpin(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle())
            return false;
        var r = engageSymbolInternal(symbol, false, noEnergy, ignoreMaxChevrons);
        if (!r.ok())
            return false;
        setStargateState(EnumStargateState.DIALING_COMPUTER);
        if (r == StargateChevronEngageResult.OK_CONNECTED) {
            getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), 30));
        }
        int plusTime = new Random().nextInt(5);
        stargate.getStateManager().getChevronsState().scheduleChevronActivate(10 + plusTime, getNextChevron(symbol, true, ignoreMaxChevrons), null, true);
        if (addressDialSequence != null)
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 15 + plusTime));
        stargate.setChanged();
        return true;
    }
}
