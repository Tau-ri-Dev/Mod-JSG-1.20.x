package dev.tauri.jsg.api.stargate.listener;

import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStargateListenerHandler extends INBTSerializable<CompoundTag> {
    void addListener(IStargateListener listener);

    void removeListener(IStargateListener listener);


    default void gateOpen() {
    }

    default void gateClose(StargateClosedReasonEnum reason) {
    }

    default void gateDisconnect() {
    }

    default void gateReset() {
    }

    default void gateStateChanged(EnumStargateState oldState, EnumStargateState newState) {
    }

    default void gateSymbolEngage(SymbolInterface symbol) {
    }

    default void gateFail(StargateOpenResult result) {
    }

    default void gateIncoming(int dialedAddressSize) {
    }

    default void gateBeginDial() {
    }

    default void irisCloses() {
    }

    default void irisOpens() {
    }

    default void irisHit() {
    }

    default boolean receiveIDC(CodeSender sender, String code) {
        return false;
    }

    default void gateRingSpin() {
    }

    default void gateDialAbort() {
    }
}
