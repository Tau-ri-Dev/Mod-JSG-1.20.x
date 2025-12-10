package dev.tauri.jsg.api.stargate.network.address;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

public class StargateAddress implements IAddress {

    public StargateAddress(AbstractSymbolType<?> symbolType, SymbolInterface symbol1, SymbolInterface symbol2, SymbolInterface symbol3, SymbolInterface symbol4, SymbolInterface symbol5, SymbolInterface symbol6, SymbolInterface symbol7, SymbolInterface symbol8) {
        this.symbolType = symbolType;
        address.addAll(List.of(
                symbol1,
                symbol2,
                symbol3,
                symbol4,
                symbol5,
                symbol6,
                symbol7,
                symbol8
        ));
    }

    public StargateAddress(AbstractSymbolType<?> symbolType) {
        this.symbolType = symbolType;
    }

    public StargateAddress(ByteBuf byteBuf) {
        fromBytes(byteBuf);
    }

    public StargateAddress(CompoundTag compound) {
        deserializeNBT(compound);
    }

    protected int getSavedSymbols() {
        return 8;
    }


    // ---------------------------------------------------------------------------------
    // Address

    protected AbstractSymbolType<?> symbolType;
    protected List<SymbolInterface> address = new ArrayList<>(8);

    public AbstractSymbolType<?> getSymbolType() {
        return symbolType;
    }

    /**
     * Generates new 8 chevron random address.
     *
     * @param random {@link Random} instance.
     */
    public StargateAddress generate(Random random) {
        if (!address.isEmpty()) {
            JSGApi.logger.error("Tried to regenerate address already containing symbols", new ConcurrentModificationException());
            for (var s : address)
                JSGApi.logger.error(s.getEnglishName());
            return this;
        }

        while (address.size() < 8) {
            SymbolInterface symbol = symbolType.getRandomSymbol(random);

            if (!address.contains(symbol))
                address.add(symbol);
        }

        return this;
    }

    /**
     * Get glyph at position {@code position}
     *
     * @param pos Position of the glyph
     */
    public SymbolInterface get(int pos) {
        return address.get(pos);
    }

    public void clear() {
        address.clear();
    }

    public void set(int index, SymbolInterface symbol) {
        address.set(index, symbol);
    }

    public SymbolInterface getLast() {
        if (address.isEmpty())
            return null;

        return address.get(address.size() - 1);
    }

    public int getSize() {
        return address.size();
    }

    public List<String> getNameList() {
        List<String> out = new ArrayList<>(address.size());

        for (SymbolInterface symbol : address) {
            out.add(symbol.getEnglishName());
        }

        return out;
    }

    public List<SymbolInterface> subList(int start, int end) {
        return address.subList(start, end);
    }

    /**
     * Get 7th and 8th symbols, as they're not saved by this implementation.
     */
    public List<SymbolInterface> getAdditional() {
        return address.subList(6, 8);
    }


    // ---------------------------------------------------------------------------------
    // Serialization

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putString("symbolType", symbolType.getId());

        for (int i = 0; i < getSavedSymbols(); i++)
            compound.putInt("symbol" + i, address.get(i).getId());

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        if (!address.isEmpty()) {
            JSGApi.logger.error("Tried to deserialize address already containing symbols", new ConcurrentModificationException());
            for (var s : address)
                JSGApi.logger.error(s.getEnglishName());
            return;
        }

        if (compound.contains("symbolType", CompoundTag.TAG_INT)) {
            // old format - used int as id
            symbolType = AbstractSymbolType.byId(compound.getInt("symbolType"));
        } else {
            // new format use string as id
            symbolType = AbstractSymbolType.byId(compound.getString("symbolType"));
        }

        for (int i = 0; i < getSavedSymbols(); i++)
            address.add(symbolType.valueOf(compound.getInt("symbol" + i)));
    }

    public void toBytes(ByteBuf buf) {
        buf.writeByte(AbstractSymbolType.getId(symbolType));

        for (int i = 0; i < getSavedSymbols(); i++)
            buf.writeByte(address.get(i).getId());
    }

    public void fromBytes(ByteBuf buf) {
        if (!address.isEmpty()) {
            JSGApi.logger.error("Tried to deserialize address already containing symbols");
            return;
        }

        symbolType = AbstractSymbolType.byId(buf.readByte());

        for (int i = 0; i < getSavedSymbols(); i++)
            address.add(symbolType.valueOf(buf.readByte()));
    }


    // ---------------------------------------------------------------------------------
    // Hashing

    @Override
    public String toString() {
        var stringAddress = new StringBuilder();
        for (var symbol : address) {
            stringAddress.append(symbol.getEnglishName()).append(", ");
        }
        return "{symbolType: " + symbolType + ", address: [" + stringAddress.toString() + "]}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.subList(0, 6).hashCode());
        result = prime * result + ((symbolType == null) ? 0 : symbolType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof StargateAddress other))
            return false;
        if (address == null)
            return other.address == null;
        if (address.size() < 6) return false;
        if (other.address.size() < 6) return false;
        if (!address.subList(0, 6).equals(other.address.subList(0, 6))) return false;
        var noPoO = trimOrigin();
        var otherNoPoO = other.trimOrigin();

        if (noPoO.size() >= 7 && otherNoPoO.size() >= 7) {
            if (!noPoO.get(6).equals(otherNoPoO.get(6))) return false;
            if (noPoO.size() >= 8 && otherNoPoO.size() >= 8) {
                if (!noPoO.get(7).equals(otherNoPoO.get(7))) return false;
            }
        }
        return symbolType == other.symbolType;
    }

    public List<SymbolInterface> trimOrigin() {
        if (address == null) return null;
        if (address.isEmpty()) return address;
        if (address.get(address.size() - 1).origin()) return address.subList(0, address.size() - 1);
        return address;
    }

    public boolean equalsV2(StargateAddressDynamic address) {
        return equalsV2(address, address.getSize());
    }

    public boolean equalsV2(StargateAddressDynamic address, int checkLength) {
        for (int i = 0; i < address.getSize(); i++) {
            if (i + 1 > checkLength) break;
            if (this.address.size() >= i + 1) {
                if (this.address.get(i) != address.get(i))
                    return false;
            } else return false;
        }
        return true;
    }

    public boolean contains(SymbolInterface symbol) {
        for (SymbolInterface s : address) {
            if (s.equals(symbol))
                return true;
        }
        return false;
    }
}
