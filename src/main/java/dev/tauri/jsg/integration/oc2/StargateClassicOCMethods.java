package dev.tauri.jsg.integration.oc2;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.NearbyGate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.api.stargate.iris.codesender.ComputerCodeSender;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.AbstractOCMethods;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.stargate.network.StargateNetwork;
import li.cil.oc2.api.bus.device.object.Callback;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StargateClassicOCMethods extends AbstractOCMethods<StargateClassicBaseBE<?>> {
    public StargateClassicOCMethods(ComputerDeviceProvider deviceTile) {
        super((StargateClassicBaseBE<?>) deviceTile, OCDevices.STARGATE_CLASSIC);
    }

    // -----------------------------------------------------------------
    // OpenComputers methods

    @SuppressWarnings("unused")
    @Callback(name = "toggleIris")
    public final Object[] toggleIris() {
        if (deviceTile.getIrisManager().getIrisType() == EnumIrisType.NULL)
            return new Object[]{false, "stargate_iris_missing", "Iris is not installed!"};
        if (deviceTile.getIrisManager().getIrisMode() != EnumIrisMode.OC)
            return new Object[]{false, "stargate_iris_error_mode", "Iris mode must be set to OC"};
        boolean result = deviceTile.getIrisManager().toggleIris();
        deviceTile.setChanged();
        if (!result && (deviceTile.getIrisManager().hasShield() && deviceTile.getIrisManager().isIrisOpened() && deviceTile.getEnergyManager().getStorage().getEnergyStored() < JSGConfig.Stargate.irisShieldPowerDraw.get() * 3))
            return new Object[]{false, "stargate_iris_not_power", "Not enough power to close shield"};
        else if (!result)
            return new Object[]{false, "stargate_iris_busy", "Iris is busy"};
        else
            return new Object[]{true};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getIrisState")
    public final Object[] getIrisState() {
        return new Object[]{deviceTile.getIrisManager().getIrisState().toString()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getIrisType")
    public final Object[] getIrisType() {
        return new Object[]{deviceTile.getIrisManager().getIrisType().toString()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getIrisDurability")
    public final Object[] getIrisDurability() {
        var damage = deviceTile.getIrisManager().getIrisItem().getDamageValue();
        var irisDurability = deviceTile.getIrisManager().getIrisItem().getMaxDamage();
        return new Object[]{(irisDurability - damage) + "/" + irisDurability, irisDurability - damage, irisDurability};
    }

    @SuppressWarnings("unused")
    @Callback(name = "sendMessageToIncoming")
    public final Object[] sendMessageToIncoming(String message) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_failure_not_merged", "Stargate is not merged"};
        if (!deviceTile.getDialingManager().getStargateState().engaged())
            return new Object[]{false, "stargate_failure_not_engaged", "Stargate is not engaged"};

        if (deviceTile.getIrisManager().getCodeSender() != null && deviceTile.getIrisManager().getCodeSender().canReceiveMessage()) {
            deviceTile.getIrisManager().getCodeSender().sendMessage(Component.literal(message));
            return new Object[]{true, "success"};
        }

        return new Object[]{false, "no_listener_available"};
    }

    @SuppressWarnings("unused")
    @Callback(name = "sendIrisCode")
    public final Object[] sendIrisCode(String code) {
        if (code == null) {
            return new Object[]{false, "invalid_method_format", "You must enter code!"};
        }
        StargatePos destinationPos = StargateNetwork.INSTANCE.getStargate(deviceTile.getDialingManager().getDialedAddress());
        if (destinationPos == null) return new Object[]{false, "stargate_not_engaged"};
        var te = destinationPos.getStargate();
        if (te instanceof StargateClassicBaseBE<?> classicTile) {
            classicTile.receiveIrisCode(new ComputerCodeSender(StargateNetwork.INSTANCE.getStargate(deviceTile.getStargateAddress(JSGSymbolTypes.MILKYWAY.get()))), code);
        } else {
            return new Object[]{false, "invalid_target_gate"};
        }
        return new Object[]{true, "success"};
    }

    @SuppressWarnings("unused")
    @Callback(name = "abortDialing")
    public final Object[] abortDialing() {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_failure_not_merged", "Stargate is not merged"};

        if (deviceTile.getDialingManager().abortDialingSequence()) {
            deviceTile.setChanged();
            return new Object[]{true, "stargate_aborting", "Aborting dialing"};
        }
        return new Object[]{false, "stargate_aborting_failed", "Aborting dialing failed"};
    }

    @SuppressWarnings("unused")
    @Callback(name = "engageGate")
    public final Object[] engageGate() {
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
    @Callback(name = "disengageGate")
    public final Object[] disengageGate() {
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
    @Callback(name = "engageSymbol")
    public final Object[] engageSymbol(Object symbol) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_failure_not_merged", "Stargate is not merged"};

        if (!deviceTile.getDialingManager().getStargateState().idle()) {
            return new Object[]{false, "stargate_failure_busy", "Stargate is busy", deviceTile.getDialingManager().getStargateState().name().toLowerCase()};
        }

        if (deviceTile.getDialingManager().getDialedAddress().size() == 9) {
            return new Object[]{false, "stargate_failure_full", "Already dialed 9 chevrons"};
        }

        if (symbol == null)
            throw new IllegalArgumentException("bad argument (symbol name/index invalid)");
        SymbolInterface targetSymbol = deviceTile.getSymbolFromNameIndex(symbol);

        // disables engaging unknown symbols (gate has only 36, but dhd 38)
        if (!targetSymbol.isValidForAddress() && !targetSymbol.origin())
            throw new IllegalArgumentException("bad argument (symbol name/index invalid)");
        deviceTile.getDialingManager().engageSymbolBySpin(targetSymbol, false, false);
        deviceTile.setChanged();

        return new Object[]{true, "stargate_spin"};
    }

    @SuppressWarnings("unused")
    @Callback(name = "dialAddress")
    public final Object[] dialAddress(Object... symbols) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_failure_not_merged", "Stargate is not merged"};

        if (!deviceTile.getDialingManager().getStargateState().idle()) {
            return new Object[]{false, "stargate_failure_busy", "Stargate is busy, state: " + deviceTile.getDialingManager().getStargateState()};
        }

        if (deviceTile.getDialingManager().getDialedAddress().size() > 0) {
            return new Object[]{false, "stargate_failure_not_empty", "Dialed address is not empty"};
        }
        if (symbols.length < 7) {
            return new Object[]{false, "input_address_malformed", "Input address is malformed"};
        }
        var maxSymbols = Math.min(symbols.length, 9);
        var address = new StargateAddressDynamic(deviceTile.getSymbolType());
        for (int i = 0; i < maxSymbols; i++) {
            var symbol = deviceTile.getSymbolFromNameIndex(symbols[i]);
            if (!symbol.isValidForAddress() && !symbol.origin())
                throw new IllegalArgumentException("bad argument (symbol name/index invalid)");
            address.addSymbol(symbol);
        }
        deviceTile.getDialingManager().dialAddress(address, false, false, EnumDialingType.NORMAL);

        return new Object[]{true, "dial_begun", address.toString()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "spinRing")
    public final Object[] spinRing(boolean counterClockwise) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged", "Stargate must be merged!"};
        var state = deviceTile.getDialingManager().getStargateState();
        if (!state.idle())
            return new Object[]{false, "stargate_busy", "Stargate must be idle to spin its ring"};
        if (deviceTile.getStateManager().getChevronsState().isOpenedAny()) {
            return new Object[]{false, "stargate_locked", "Some chevrons are opened, so you cannot spin gate!"};
        }
        var direction = (counterClockwise ? EnumSpinDirection.COUNTER_CLOCKWISE : EnumSpinDirection.CLOCKWISE);
        var result = deviceTile.getDialingManager().getSpinHelper().rotateFreely(direction);
        if (!result)
            return new Object[]{false, "stargate_ring_already_spinning", "Stargate ring is already spinning!"};
        return new Object[]{true, "stargate_ring_spin", "Ring is now spinning..."};
    }

    @SuppressWarnings("unused")
    @Callback(name = "stopRingSpin")
    public final Object[] stopRingSpin() {
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
    @Callback(name = "isSpinning")
    public final Object[] isSpinning() {
        return new Object[]{deviceTile.getDialingManager().getSpinHelper().isSpinning(), deviceTile.getDialingManager().getSpinHelper().getRingVelocity()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getTopSymbol")
    public final Object[] getTopSymbol() {
        var s = deviceTile.getDialingManager().getSpinHelper().getCurrentTopSymbol();
        if (s == null)
            return new Object[]{false};
        return new Object[]{true, s.getId(), s.getEnglishName(), s.getAngle(), s.origin(), deviceTile.getDialingManager().getSpinHelper().getRingAngle()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getRingAngle")
    public final Object[] getRingAngle() {
        var angle = deviceTile.getDialingManager().getSpinHelper().getRingAngle();
        if (angle < 0)
            return new Object[]{false};
        return new Object[]{true, angle};
    }

    @SuppressWarnings("unused")
    @Callback(name = "engageTopSymbol")
    public final Object[] engageTopSymbol() {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged", "Stargate must be merged!"};
        var state = deviceTile.getDialingManager().getStargateState();
        if (!state.idle())
            return new Object[]{false, "stargate_busy", "Stargate must be idle!"};
        var result = deviceTile.getDialingManager().engageCurrentSymbol();
        if (result.ok()) {
            return new Object[]{true, "chevron_engaged", "Chevron engaged"};
        }
        return new Object[]{false, "chevron_engage_error", result.name()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "openChevron")
    public final Object[] openChevron(Optional<Integer> chevronIndex) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged", "Stargate must be merged!"};
        var state = deviceTile.getDialingManager().getStargateState();
        if (!state.idle())
            return new Object[]{false, "stargate_busy", "Stargate must be idle!"};
        var chevron = (chevronIndex.map(ChevronEnum::valueOf).orElse(deviceTile.getDialingManager().getNextChevron(deviceTile.getDialingManager().getSpinHelper().getCurrentTopSymbol(), false, false)));
        var chevronStates = deviceTile.getStateManager().getChevronsState();
        var chevronState = chevronStates.get(chevron);
        if (chevronState.isOpen()) {
            return new Object[]{false, "chevron_already_open", "Target chevron is already open!"};
        }
        deviceTile.getStateManager().getChevronsState().scheduleChevronOpen(0, chevron, false);
        return new Object[]{true, "chevron_open", "Chevron is opening..."};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getChevronStatus")
    public final Object[] getChevronStatus(int chevronIndex) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged"};
        var chevron = Optional.ofNullable(ChevronEnum.valueOf(chevronIndex));
        if (chevron.isEmpty())
            return new Object[]{false, "chevron_not_found"};
        var chevronStates = deviceTile.getStateManager().getChevronsState();
        var chevronState = chevronStates.get(chevron.get());
        return new Object[]{true, "chevron_status", chevronState.isOpen(), chevronState.isLocked()};
    }

    @SuppressWarnings("unused")
    @Callback(name = "closeChevron")
    public final Object[] closeChevron(Optional<Integer> chevronIndex) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged", "Stargate must be merged!"};
        var state = deviceTile.getDialingManager().getStargateState();
        if (!state.idle())
            return new Object[]{false, "stargate_busy", "Stargate must be idle!"};
        var chevron = (chevronIndex.map(ChevronEnum::valueOf).orElse(deviceTile.getDialingManager().getNextChevron(deviceTile.getDialingManager().getSpinHelper().getCurrentTopSymbol(), false, false)));
        var chevronStates = deviceTile.getStateManager().getChevronsState();
        var chevronState = chevronStates.get(chevron);
        if (!chevronState.isOpen()) {
            return new Object[]{false, "chevron_already_closed", "Target chevron is already closed!"};
        }
        deviceTile.getStateManager().getChevronsState().scheduleChevronClose(0, chevron, false);
        return new Object[]{true, "chevron_close", "Chevron is closing..."};
    }

    @SuppressWarnings("unused")
    @Callback(name = "activateChevron")
    public final Object[] activateChevron(Optional<Integer> chevronIndex) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged", "Stargate must be merged!"};
        var state = deviceTile.getDialingManager().getStargateState();
        if (!state.idle())
            return new Object[]{false, "stargate_busy", "Stargate must be idle!"};
        var chevron = (chevronIndex.map(ChevronEnum::valueOf).orElse(deviceTile.getDialingManager().getNextChevron(deviceTile.getDialingManager().getSpinHelper().getCurrentTopSymbol(), false, false)));
        var chevronStates = deviceTile.getStateManager().getChevronsState();
        var chevronState = chevronStates.get(chevron);
        if (chevronState.isLocked()) {
            return new Object[]{false, "chevron_already_activated", "Target chevron is already activated!"};
        }
        deviceTile.getStateManager().getChevronsState().scheduleChevronActivate(0, chevron, null, false);
        return new Object[]{true, "chevron_activate", "Chevron is activating..."};
    }

    @SuppressWarnings("unused")
    @Callback(name = "deactivateChevron")
    public final Object[] deactivateChevron(Optional<Integer> chevronIndex) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "stargate_not_merged", "Stargate must be merged!"};
        var state = deviceTile.getDialingManager().getStargateState();
        if (!state.idle())
            return new Object[]{false, "stargate_busy", "Stargate must be idle!"};
        var chevron = (chevronIndex.map(ChevronEnum::valueOf).orElse(deviceTile.getDialingManager().getNextChevron(deviceTile.getDialingManager().getSpinHelper().getCurrentTopSymbol(), false, false)));
        var chevronStates = deviceTile.getStateManager().getChevronsState();
        var chevronState = chevronStates.get(chevron);
        if (!chevronState.isLocked()) {
            return new Object[]{false, "chevron_already_deactivated", "Target chevron is already deactivated!"};
        }
        deviceTile.getStateManager().getChevronsState().scheduleChevronDim(0, chevron, false);
        return new Object[]{true, "chevron_deactivate", "Chevron is deactivating..."};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getCapacitorsInstalled")
    public final Object[] getCapacitorsInstalled() {
        return new Object[]{deviceTile.isMerged() ? deviceTile.currentPowerTier - 1 : null};
    }

    @SuppressWarnings("unused")
    @Callback(name = "getNearbyGates")
    public final Object[] getNearbyGates(String gateTypeString, boolean ignoreGateType, boolean checkAddressAndEnergy) {
        if (!deviceTile.isMerged())
            return new Object[]{false, "gate_not_merged", new HashMap<String, Object>()};

        Map<String, Map<List<String>, Integer>> map = new HashMap<>();

        var gateType = StargateType.valueOf(JSGMapping.rl(gateTypeString));

        for (NearbyGate g : deviceTile.getNearbyGates(gateType, !ignoreGateType, checkAddressAndEnergy)) {
            Map<List<String>, Integer> map2 = map.computeIfAbsent(g.gateType.toString(), k -> new HashMap<>());
            map2.put(g.address.getNameList(), g.symbolsNeeded);
            map.put(g.gateType.toString(), map2);
        }

        return new Object[]{true, "success", map};
    }
}
