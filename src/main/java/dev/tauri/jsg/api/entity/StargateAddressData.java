package dev.tauri.jsg.api.entity;

import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.entity.IAddressNotebookPageData;
import dev.tauri.jsg.core.common.entity.INotebookPageData;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StargateAddressData implements INBTSerializable<CompoundTag>, IAddressNotebookPageData {
    public StargateAddressDynamic address;
    public int[] symbolsToDisplay;
    public @Nullable PointOfOrigin pointOfOrigin;

    public StargateAddressData(StargateAddressDynamic address, List<Integer> symbolsToDisplay, @Nullable PointOfOrigin pointOfOrigin) {
        this(address, new int[symbolsToDisplay.size()], pointOfOrigin);
        for (int i = 0; i < symbolsToDisplay.size(); i++) {
            this.symbolsToDisplay[i] = symbolsToDisplay.get(i);
        }
    }

    public StargateAddressData(StargateAddressDynamic address, int[] symbolsToDisplay, @Nullable PointOfOrigin pointOfOrigin) {
        this.address = address;
        this.symbolsToDisplay = symbolsToDisplay;
        this.pointOfOrigin = pointOfOrigin;
    }

    public StargateAddressData(StargateAddressDynamic address) {
        this.address = address;
        this.symbolsToDisplay = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        this.pointOfOrigin = null;
    }

    public StargateAddressData(CompoundTag compound) {
        deserializeNBT(compound);
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("address", new StargateAddressDynamic(address).serializeNBT());
        compound.putIntArray("symbolsToDisplay", symbolsToDisplay);
        if (pointOfOrigin != null)
            compound.put("pointOfOrigin", pointOfOrigin.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        address = new StargateAddressDynamic(compound.getCompound("address"));
        symbolsToDisplay = compound.getIntArray("symbolsToDisplay");
        if (compound.contains("pointOfOrigin"))
            pointOfOrigin = PointOfOrigin.fromNBT(compound.getCompound("pointOfOrigin"), () -> null);
    }

    @Override
    @NotNull
    public StargateAddressDynamic getAddress() {
        return address;
    }

    @Override
    public int @NotNull [] getSymbolsToDisplay() {
        return symbolsToDisplay;
    }

    @Override
    @Nullable
    public PointOfOrigin getOrigin() {
        return pointOfOrigin;
    }

    @Override
    public void setSymbolsToDisplay(int[] symbolsToDisplay) {
        this.symbolsToDisplay = symbolsToDisplay;
    }

    @Override
    public void setOrigin(@Nullable PointOfOrigin origin) {
        this.pointOfOrigin = origin;
    }

    @Override
    public void setAddress(IAddress address) {
        if (address instanceof StargateAddressDynamic sgAddress)
            this.address = sgAddress;
    }

    @Override
    public <D extends INotebookPageData> void update(D newData) {
        if (!(newData instanceof IAddressNotebookPageData addressNotebookPageData)) return;
        this.symbolsToDisplay = addressNotebookPageData.getSymbolsToDisplay();
        this.pointOfOrigin = addressNotebookPageData.getOrigin();
        if (!(newData instanceof StargateAddressData sgAddressData)) return;
        this.address = sgAddressData.getAddress();
    }
}
