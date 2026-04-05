package dev.tauri.jsg.stargate;

import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.animation.IAddressDialSequence;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import net.minecraft.nbt.CompoundTag;

public class StargateAddressDialSequence implements IAddressDialSequence {
    // not saved
    protected final DialNextConsumer consumeSymbol;

    // saved
    protected StargateAddressDynamic originalAddress;
    protected StargateAddressDynamic address;
    protected EnumDialingType dialingType;
    protected boolean noEnergy;
    protected boolean ignoreMaxChevrons;

    protected StargateAddressDialSequence(DialNextConsumer consumeSymbol) {
        this.consumeSymbol = consumeSymbol;
    }

    public StargateAddressDialSequence(DialNextConsumer consumeSymbol, CompoundTag tag) {
        this(consumeSymbol);
        deserializeNBT(tag);
    }

    public StargateAddressDialSequence(DialNextConsumer consumeSymbol, StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons, EnumDialingType dialingType) {
        this(consumeSymbol);
        this.originalAddress = new StargateAddressDynamic(address);
        this.address = new StargateAddressDynamic(address);
        this.noEnergy = noEnergy;
        this.dialingType = dialingType;
        this.ignoreMaxChevrons = ignoreMaxChevrons;
    }

    public StargateAddressDynamic getOriginalAddress() {
        return new StargateAddressDynamic(originalAddress);
    }

    public void dialNext() {
        consumeSymbol.accept(address.popFirst(), noEnergy, ignoreMaxChevrons, dialingType);
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("originalAddress", originalAddress.serializeNBT());
        compound.put("address", address.serializeNBT());
        compound.putInt("dialingType", dialingType.ordinal());
        compound.putBoolean("noEnergy", noEnergy);
        compound.putBoolean("ignoreMaxChevrons", ignoreMaxChevrons);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        originalAddress = new StargateAddressDynamic(compound.getCompound("originalAddress"));
        address = new StargateAddressDynamic(compound.getCompound("address"));
        dialingType = EnumDialingType.values()[compound.getInt("dialingType")];
        noEnergy = compound.getBoolean("noEnergy");
        ignoreMaxChevrons = compound.getBoolean("ignoreMaxChevrons");
    }
}
