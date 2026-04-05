package dev.tauri.jsg.api.integration;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.api.stargate.iris.*;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.StargateCloseResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.core.common.integration.SignalHolder;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class StargateComputerEvents {
    public static final BiFunction<EnumSpinDirection, Float, SignalHolder> SPIN_START = (direction, speed) -> SignalHolder.of(
            "stargate_spin_start",
            direction.name(), speed.doubleValue()
    );
    public static final Function<SymbolInterface, SignalHolder> SPIN_STOP = (topSymbol) -> SignalHolder.of(
            "stargate_spin_stop",
            (topSymbol == null ? null : topSymbol.getEnglishName())
    );
    public static final ChevronEvent CHEVRON_ENGAGED = (source, symbol, chevron, dialedAddressSize) -> SignalHolder.of(
            "stargate_chevron_engaged",
            source.name(), symbol.getEnglishName(), chevron.index, dialedAddressSize
    );
    public static final Function<ChevronEnum, SignalHolder> CHEVRON_OPEN = (chevronEnum -> SignalHolder.of("stargate_chevron_open", chevronEnum.index));
    public static final Function<ChevronEnum, SignalHolder> CHEVRON_LIT = (chevronEnum -> SignalHolder.of("stargate_chevron_lit", chevronEnum.index));
    public static final Function<ChevronEnum, SignalHolder> CHEVRON_DIM = (chevronEnum -> SignalHolder.of("stargate_chevron_dim", chevronEnum.index));
    public static final Function<ChevronEnum, SignalHolder> CHEVRON_CLOSE = (chevronEnum -> SignalHolder.of("stargate_chevron_close", chevronEnum.index));

    public static final BiFunction<StargateOpenResult, StargateAddressDynamic, SignalHolder> ATTEMPT_OPEN_FAILED = (reason, address) -> SignalHolder.of(
            "stargate_attempt_open_failed",
            reason.name(), address.getNameList()
    );
    public static final TriFunction<StargateCloseResult, StargateAddressDynamic, Boolean, SignalHolder> ATTEMPT_CLOSE_FAILED = (reason, address, isInitiating) -> SignalHolder.of(
            "stargate_attempt_close_failed",
            reason.name(), address.getNameList(), isInitiating
    );

    public static final Function<Integer, SignalHolder> WORMHOLE_INCOMING = (addressSize) -> SignalHolder.of("stargate_wormhole_incoming", addressSize);
    public static final BiFunction<StargateAddressDynamic, Boolean, SignalHolder> WORMHOLE_SUBSPACE_CONNECTED = (address, isInitiating) -> SignalHolder.of("stargate_wormhole_subspace_connected", address.getNameList(), isInitiating);
    public static final BiFunction<StargateAddressDynamic, Boolean, SignalHolder> WORMHOLE_OPEN_UNSTABLE = (address, isInitiating) -> SignalHolder.of("stargate_wormhole_open_unstable", address.getNameList(), isInitiating);
    public static final BiFunction<StargateAddressDynamic, Boolean, SignalHolder> WORMHOLE_OPEN_FULLY = (address, isInitiating) -> SignalHolder.of("stargate_wormhole_open_fully", address.getNameList(), isInitiating);
    public static final TriFunction<StargateAddressDynamic, StargateClosedReasonEnum, Boolean, SignalHolder> WORMHOLE_CLOSE_UNSTABLE = (address, reason, isInitiating) -> SignalHolder.of("stargate_wormhole_close_unstable", address.getNameList(), reason.name(), isInitiating);
    public static final TriFunction<StargateAddressDynamic, StargateClosedReasonEnum, Boolean, SignalHolder> WORMHOLE_CLOSE_FULLY = (address, reason, isInitiating) -> SignalHolder.of("stargate_wormhole_close_fully", address.getNameList(), reason.name(), isInitiating);
    public static final Supplier<SignalHolder> WORMHOLE_SUBSPACE_DISCONNECTED = () -> SignalHolder.of("stargate_wormhole_subspace_disconnected");

    public static final Function<Boolean, SignalHolder> EH_UNSTABLE = (isInitiating) -> SignalHolder.of("stargate_event_horizon_unstable", isInitiating);
    public static final Function<Boolean, SignalHolder> EH_UNSTABLE_BLACK_HOLE = (isInitiating) -> SignalHolder.of("stargate_event_horizon_unstable_black_hole", isInitiating);
    public static final Function<Boolean, SignalHolder> EH_STABILIZED = (isInitiating) -> SignalHolder.of("stargate_event_horizon_stabilized", isInitiating);
    public static final BiFunction<Boolean, EntityType<?>, SignalHolder> EH_TRAVELER = (inbound, entityType) -> SignalHolder.of("stargate_event_horizon_traveler", inbound, Optional.of(entityType).map(e -> e.getDescription().getString()).orElse("unknown"));

    public static final Function<Boolean, SignalHolder> IRIS_TOGGLED = (close) -> SignalHolder.of("stargate_iris_toggled", close);
    public static final Function<IrisDestroyReason, SignalHolder> IRIS_DESTROYED = (reason) -> SignalHolder.of("stargate_iris_destroyed", reason.name());
    public static final Function<String, SignalHolder> IRIS_CODE_RECEIVED = (code) -> SignalHolder.of("stargate_iris_code_received", code);
    public static final BiFunction<EnumIrisState, EnumIrisState, SignalHolder> IRIS_STATE_CHANGED = (oldState, newState) -> SignalHolder.of("stargate_iris_state_changed", Optional.ofNullable(oldState).orElse(EnumIrisState.ERROR).name(), Optional.ofNullable(newState).orElse(EnumIrisState.ERROR).name());
    public static final BiFunction<EnumIrisType, EnumIrisType, SignalHolder> IRIS_TYPE_CHANGED = (oldState, newState) -> SignalHolder.of("stargate_iris_type_changed", Optional.ofNullable(oldState).orElse(EnumIrisType.NULL).name(), Optional.ofNullable(newState).orElse(EnumIrisType.NULL).name());
    public static final BiFunction<EnumIrisMode, EnumIrisMode, SignalHolder> IRIS_MODE_CHANGED = (oldState, newState) -> SignalHolder.of("stargate_iris_mode_changed", Optional.ofNullable(oldState).map(Enum::name).orElse("null"), Optional.ofNullable(newState).map(Enum::name).orElse("null"));
    public static final BiFunction<IrisDamageSource, Integer, SignalHolder> IRIS_DAMAGED = (source, damageAmount) -> SignalHolder.of("stargate_iris_damaged", source.name(), damageAmount);
    public static final Supplier<SignalHolder> IRIS_HIT = () -> SignalHolder.of("stargate_iris_hit");
    public static final Supplier<SignalHolder> IRIS_OUT_OF_POWER = () -> SignalHolder.of("stargate_iris_out_of_power");

    public static final Supplier<SignalHolder> PING = () -> SignalHolder.of("stargate_ping");


    public interface ChevronEvent {
        enum Source {
            DHD,
            REMOTE,
            BY_SPIN,
            INCOMING_WORMHOLE
        }

        SignalHolder apply(Source source, SymbolInterface symbol, ChevronEnum chevron, int dialedAddressSize);
    }
}
