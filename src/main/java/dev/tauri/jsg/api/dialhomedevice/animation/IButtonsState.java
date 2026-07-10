package dev.tauri.jsg.api.dialhomedevice.animation;

import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.List;

public interface IButtonsState extends INBTSerializable<CompoundTag>, ITickable {
    IButtonState get(SymbolInterface symbol);

    List<SymbolInterface> getActivatedButtons();

    interface IButtonState {
        void activate();

        void deactivate();

        boolean isActive();
    }
}
