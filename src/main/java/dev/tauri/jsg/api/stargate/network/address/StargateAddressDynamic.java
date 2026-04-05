package dev.tauri.jsg.api.stargate.network.address;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class StargateAddressDynamic extends StargateAddress implements Iterable<SymbolInterface> {

    public StargateAddressDynamic(SymbolType<?> symbolType) {
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

    public StargateAddressDynamic(SymbolType<?> symbolType, List<SymbolInterface> symbols) {
        super(symbolType);
        clear();
        addAll(symbols);
    }

    @ParametersAreNonnullByDefault
    public static StargateAddressDynamic getMinimalDialable(StargateAddress address, Stargate<?> sourceGate, @Nullable StargatePos targetGate) {
        if (targetGate == null) return new StargateAddressDynamic(address).addOriginIfMissingAndImmutable();
        var minSymbols = sourceGate.getDialingManager().getMinimalSymbolsToDial(targetGate.getGateSymbolType(), targetGate) - 1;
        return new StargateAddressDynamic(address.getSymbolType(), address.subList(0, minSymbols)).addOriginIfMissingAndImmutable();
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

    @Nullable
    public SymbolInterface popLast() {
        if (address.isEmpty()) return null;
        var symbol = getLast();
        address.remove(address.size() - 1);
        addressSize = address.size();
        return symbol;
    }

    @Override
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

    @Override
    public @NotNull Iterator<SymbolInterface> iterator() {
        return address.iterator();
    }

    @Override
    public void forEach(Consumer<? super SymbolInterface> action) {
        address.forEach(action);
    }
}
