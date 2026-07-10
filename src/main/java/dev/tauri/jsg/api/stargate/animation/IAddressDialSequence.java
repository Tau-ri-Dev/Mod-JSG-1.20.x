package dev.tauri.jsg.api.stargate.animation;

import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IAddressDialSequence extends INBTSerializable<CompoundTag> {
    interface DialNextConsumer {
        void accept(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons, EnumDialingType dialingType);
    }

    IAddress getOriginalAddress();
}
