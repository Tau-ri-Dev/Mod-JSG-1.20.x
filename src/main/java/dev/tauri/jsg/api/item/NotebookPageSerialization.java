package dev.tauri.jsg.api.item;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.network.address.IAddress;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class NotebookPageSerialization {

    public static ResourceLocation STARGATES = JSGApi.rl("stargates");

    public static void registerInternal() {
        registerDeserializer(STARGATES, StargateAddress::new);
    }


    public interface AddressDeserializer {
        IAddress deserializeAddress(CompoundTag compound);
    }

    public static final Map<ResourceLocation, AddressDeserializer> DESERIALIZER_REGISTER = new HashMap<>();

    public static void registerDeserializer(ResourceLocation id, AddressDeserializer deserializer) {
        if (DESERIALIZER_REGISTER.containsKey(id))
            throw new IllegalArgumentException("Register already contains this id: " + id);
        DESERIALIZER_REGISTER.put(id, deserializer);
    }

    public static AddressDeserializer byId(ResourceLocation id) {
        return DESERIALIZER_REGISTER.get(id);
    }

    public static IAddress getDeserializedAddress(CompoundTag tag) {
        ResourceLocation id = tag.contains("addressType") ? new ResourceLocation(tag.getString("addressType")) : STARGATES;
        return DESERIALIZER_REGISTER.get(id).deserializeAddress(tag.getCompound("address"));
    }
}
