package dev.tauri.jsg.api.stargate.animation;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IChevronsState {
    void scheduleChevronActivate(int waitTicks, ChevronEnum chevron, @Nullable SymbolInterface symbolToLock, boolean setIdle);

    void scheduleChevronFinalOpenAndActivate(int waitTicks, @Nullable SymbolInterface symbolToLock, boolean setIdle);

    void scheduleChevronActivate(int waitTicks, ChevronEnum chevron, @Nullable SymbolInterface symbolToLock, boolean setIdle, boolean checkConnection, boolean animateTopChevronLock);

    void scheduleChevronActivate(int waitTicks, ChevronEnum chevron, @Nullable SymbolInterface symbolToLock, boolean setIdle, boolean checkConnection, boolean animateTopChevronLock, boolean noEnergy, boolean ignoreMaxChevrons);

    void scheduleChevronDim(int waitTicks, ChevronEnum chevron, boolean setIdle);

    void scheduleChevronOpen(int waitTicks, ChevronEnum chevron, boolean setIdle);

    void scheduleChevronClose(int waitTicks, ChevronEnum chevron, boolean setIdle);

    void scheduleChevronFail(int waitTicks, ChevronEnum chevron, boolean setIdle);

    void scheduleChevronsActivateMultiple(int waitTicks, boolean setIdle, boolean playSound, List<ChevronEnum> chevrons);

    void scheduleChevronsDimAll(int waitTicks, boolean setIdle);

    void scheduleChevronsLockAll(int waitTicks, boolean setIdle);
}
