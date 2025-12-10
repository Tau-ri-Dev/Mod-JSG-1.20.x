package dev.tauri.jsg.api.stargate.network.address;

import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAddress extends INBTSerializable<CompoundTag> {
    SymbolInterface get(int symbolIndex);
    int getSize();

    AbstractSymbolType<?> getSymbolType();

}
