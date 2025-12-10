package dev.tauri.jsg.api.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class JSGPacket {
    public JSGPacket() {
    }

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void fromBytes(FriendlyByteBuf buf);

    public abstract void handle(NetworkEvent.Context ctx);

    public boolean handleSupplier(Supplier<NetworkEvent.Context> contextSupplier) {
        handle(contextSupplier.get());
        return true;
    }

    public JSGPacket(FriendlyByteBuf buf) {
        fromBytes(buf);
    }
}
