package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.animation.IAddressDialSequence;
import dev.tauri.jsg.api.stargate.animation.ISpinHelper;
import dev.tauri.jsg.api.stargate.network.IStargateConnection;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import dev.tauri.jsg.api.stargate.result.StargateAddressCheckResult;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.api.stargate.result.StargateCloseResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.api.util.ITickable;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("all")
public interface IStargateDialingManager extends INBTSerializable<CompoundTag>, ITickable {
    ISpinHelper getSpinHelper();

    EnumStargateState getStargateState();

    IStargateConnection getConnection();

    int getDialedAddressSize();

    StargateAddressDynamic getDialedAddress();

    boolean isFinalActive();

    Optional<IAddressDialSequence> getDialingSequence();

    ChevronEnum getNextChevron(@Nullable SymbolInterface symbolToLock, boolean alreadyInAddress, boolean ignoreMaxChevrons);

    StargateAddressCheckResult checkEnergyRequirementsOnDialed();

    StargateAddressCheckResult checkAddressAndEnergyRequirements(StargateAddressDynamic address, boolean noEnergy);

    boolean canDialAddress(StargateAddressDynamic address);

    Pair<StargateAddressCheckResult, Stargate<?>> getTargetByAddress(StargateAddressDynamic address);

    Optional<StargatePos> getDialableStargatePos(StargateAddressDynamic address);

    boolean isAddressLengthOk(StargateAddressDynamic address, StargatePos targetGatePosition);

    StargateChevronEngageResult canAddSymbol(SymbolInterface symbol, boolean ignoreMaxChevrons);

    int getMinimalSymbolsToDial(AbstractSymbolType<?> symbolType, StargatePos targetGatePos);

    boolean canAcceptConnectionFrom(@Nullable StargatePos targetGatePos);

    /*
     * ===============================
     * Functional methods - controller
     * ===============================
     */
    StargateOpenResult attemptOpenDialed();

    StargateChevronEngageResult engageCurrentSymbol();

    boolean engageSymbolBySpin(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons);

    StargateChevronEngageResult dialAddress(StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons, EnumDialingType dialingType);

    StargateChevronEngageResult engageSymbolDHD(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons);

    StargateChevronEngageResult engageAddressByNox(StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons);

    StargateCloseResult disconnectSafe(StargateClosedReasonEnum reason);

    StargateCloseResult attemptClose(StargateClosedReasonEnum reason);

    boolean abortDialingSequence();

    boolean canAbortDialing();
}
