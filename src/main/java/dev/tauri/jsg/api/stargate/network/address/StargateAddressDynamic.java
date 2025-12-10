package dev.tauri.jsg.api.stargate.network.address;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StargateAddressDynamic extends StargateAddress {

    public StargateAddressDynamic(AbstractSymbolType<?> symbolType) {
        super(symbolType);
    }

    public StargateAddressDynamic(CompoundTag compound) {
        super(compound);
    }

    public StargateAddressDynamic(ByteBuf buf) {
        super(buf);
    }


    public StargateAddressDynamic(StargateAddress address) {
        super(address.symbolType);
        clear();
        addAll(address);
    }

    public StargateAddressDynamic(AbstractSymbolType<?> symbolType, List<SymbolInterface> symbols) {
        super(symbolType);
        clear();
        addAll(symbols);
    }

    @Override
    protected int getSavedSymbols() {
        return Math.min(addressSize, 9);
    }

    // ---------------------------------------------------------------------------------
    // Address

    public void addSymbol(SymbolInterface symbol) {
        if (address.size() == 9) {
            JSGApi.logger.error("Tried to add symbol to already full address");
            return;
        }

        address.add(symbol);
        addressSize += 1;
    }

    public void addAll(StargateAddress stargateAddress) {
        if (address.size() + stargateAddress.address.size() > 9) {
            JSGApi.logger.error("Tried to add symbols to already populated address");
            return;
        }

        address.addAll(stargateAddress.address);
        addressSize += stargateAddress.address.size();
    }

    public void addAll(List<SymbolInterface> stargateAddress) {
        if (address.size() + stargateAddress.size() > 9) {
            JSGApi.logger.error("Tried to add symbols to already populated address");
            return;
        }

        address.addAll(stargateAddress);
        addressSize += stargateAddress.size();
    }

    public void addOrigin() {
        if (symbolType.hasOrigin()) {
            addSymbol(symbolType.getOrigin());
        }
    }

    @Nullable
    public SymbolInterface popFirst() {
        if (address.isEmpty()) return null;
        var symbol = address.get(0);
        address.remove(0);
        addressSize = address.size();
        return symbol;
    }

    public StargateAddressDynamic addOriginIfMissingAndImmutable() {
        var newAddress = new StargateAddressDynamic(this);
        if (newAddress.getSymbolType().hasOrigin() && !newAddress.contains(newAddress.getSymbolType().getOrigin())) {
            newAddress.addSymbol(newAddress.getSymbolType().getOrigin());
        }
        return newAddress;
    }

    public void clear() {
        address.clear();
        addressSize = 0;
    }

    public int size() {
        return address.size();
    }

    public boolean contains(SymbolInterface symbol) {
        return address.contains(symbol);
    }

    @SuppressWarnings("all")
    public boolean validate() {
        return symbolType.validateDialedAddress(this);
    }

    public StargateAddress toImmutable() {
        StargateAddress stargateAddress = new StargateAddress(symbolType);
        stargateAddress.address.addAll(address);
        return stargateAddress;
    }

    // ---------------------------------------------------------------------------------
    // Serialization

    private int addressSize;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = super.serializeNBT();

        compound.putInt("size", address.size());

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        addressSize = compound.getInt("size");

        super.deserializeNBT(compound);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(address.size());

        super.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        addressSize = buf.readInt();

        super.fromBytes(buf);
    }
}
