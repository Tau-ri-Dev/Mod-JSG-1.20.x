package dev.tauri.jsg.common.integration.oc;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc.methods.AbstractOCMethods;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

public class StargateClassicOCMethods extends AbstractOCMethods<StargateClassicBaseBE<?>> {
    public StargateClassicOCMethods(ComputerDeviceProvider deviceTile) {
        super((StargateClassicBaseBE<?>) deviceTile, OCDevices.STARGATE_CLASSIC);
    }

    // -----------------------------------------------------------------
    // OpenComputers methods

    @SuppressWarnings("unused")
    @Callback(value = "toggleIris")
    public final Object[] toggleIris(Context context, Arguments args) {
        if (deviceTile.getIrisManager().getIrisType() == EnumIrisType.NULL)
            return new Object[]{false, "stargate_iris_missing", "Iris is not installed!"};
        if (deviceTile.getIrisManager().getIrisMode() != EnumIrisMode.OC)
            return new Object[]{false, "stargate_iris_error_mode", "Iris mode must be set to OC"};
        boolean result = deviceTile.getIrisManager().toggleIris();
        deviceTile.setChanged();
        if (!result && (deviceTile.getIrisManager().hasShield() && deviceTile.getIrisManager().isIrisOpened() && deviceTile.getEnergyManager().getStorage().getTrueEnergyStored() < JSGConfig.Stargate.irisShieldPowerDraw.get() * 3L))
            return new Object[]{false, "stargate_iris_not_power", "Not enough power to close shield"};
        else if (!result)
            return new Object[]{false, "stargate_iris_busy", "Iris is busy"};
        else
            return new Object[]{true};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getIrisState")
    public final Object[] getIrisState(Context context, Arguments args) {
        return new Object[]{deviceTile.getIrisManager().getIrisState().toString()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getIrisType")
    public final Object[] getIrisType(Context context, Arguments args) {
        return new Object[]{deviceTile.getIrisManager().getIrisType().toString()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getIrisDurability")
    public final Object[] getIrisDurability(Context context, Arguments args) {
        var damage = deviceTile.getIrisManager().getIrisItem().getDamageValue();
        var irisDurability = deviceTile.getIrisManager().getIrisItem().getMaxDamage();
        return new Object[]{(irisDurability - damage) + "/" + irisDurability, irisDurability - damage, irisDurability};
    }

    @SuppressWarnings("unused")
    @Callback(value = "abortDialing")
    public final Object[] abortDialing(Context context, Arguments args) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_failure_not_merged", "Stargate is not merged"};

        if (deviceTile.getDialingManager().abortDialingSequence()) {
            deviceTile.setChanged();
            return new Object[]{true, "stargate_aborting", "Aborting dialing"};
        }
        return new Object[]{false, "stargate_aborting_failed", "Aborting dialing failed"};
    }

    @SuppressWarnings("unused")
    @Callback(value = "engageGate")
    public final Object[] engageGate(Context context, Arguments args) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_failure_not_merged", "Stargate is not merged"};

        if (deviceTile.getDialingManager().getStargateState().idle()) {
            StargateOpenResult gateState = deviceTile.getDialingManager().attemptOpenDialed();

            if (gateState.ok()) {
                return new Object[]{true, "stargate_engage"};
            } else {
                return new Object[]{false, "stargate_failure_opening", "Stargate failed to open", gateState.toString()};
            }
        } else {
            return new Object[]{false, "stargate_failure_busy", "Stargate is busy", deviceTile.getDialingManager().getStargateState().toString()};
        }
    }

    @SuppressWarnings("unused")
    @Callback(value = "disengageGate")
    public final Object[] disengageGate(Context context, Arguments args) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_failure_not_merged", "Stargate is not merged"};

        if (deviceTile.getDialingManager().getStargateState().engaged()) {
            if (deviceTile.getDialingManager().getStargateState().initiating()) {
                deviceTile.getDialingManager().attemptClose(StargateClosedReasonEnum.REQUESTED);
                return new Object[]{true, "stargate_disengage", "Stargate closed!"};
            } else return new Object[]{false, "stargate_failure_wrong_end", "Unable to close the gate on this end"};
        } else {
            return new Object[]{false, "stargate_failure_not_open", "The gate is closed"};
        }
    }

    @SuppressWarnings("unused")
    @Callback(value = "stopRingSpin")
    public final Object[] stopRingSpin(Context context, Arguments args) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged", "Stargate must be merged!"};
        var state = deviceTile.getDialingManager().getStargateState();
        if (!state.idle())
            return new Object[]{false, "stargate_busy", "Stargate must be idle to spin its ring"};
        var result = deviceTile.getDialingManager().getSpinHelper().stopSpinning(true);
        if (!result)
            return new Object[]{false, "stargate_ring_not_spinning", "Stargate ring is not spinning!"};
        return new Object[]{true, "stargate_ring_spin_stop", "Ring is now stopping..."};
    }

    @SuppressWarnings("unused")
    @Callback(value = "isSpinning")
    public final Object[] isSpinning(Context context, Arguments args) {
        return new Object[]{deviceTile.getDialingManager().getSpinHelper().isSpinning(), deviceTile.getDialingManager().getSpinHelper().getRingVelocity()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getTopSymbol")
    public final Object[] getTopSymbol(Context context, Arguments args) {
        var s = deviceTile.getDialingManager().getSpinHelper().getCurrentTopSymbol();
        if (s == null)
            return new Object[]{false};
        return new Object[]{true, s.getId(), s.getEnglishName(deviceTile.getPointOfOrigin()), s.getAngle(), s.origin(), deviceTile.getDialingManager().getSpinHelper().getRingAngle()};
    }

    @SuppressWarnings("unused")
    @Callback(value = "getRingAngle")
    public final Object[] getRingAngle(Context context, Arguments args) {
        var angle = deviceTile.getDialingManager().getSpinHelper().getRingAngle();
        if (angle < 0)
            return new Object[]{false};
        return new Object[]{true, angle};
    }
}
