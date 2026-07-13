package dev.tauri.jsg.client.renderer.blockentity.dialhomedevice;

import dev.tauri.jsg.api.dialhomedevice.DHDReactorState;
import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DHDAbstractRendererState extends State {
    public DHDAbstractRendererState() {
        super();
    }

    public BiomeOverlayInstance biomeOverlay = CoreBiomeOverlays.NORMAL.get();
    public final List<IDHDPartItem> assembledParts = new ArrayList<>();
    public int naquadahAmount;
    public int naquadahMaxAmount;
    public DHDReactorState reactorState = DHDReactorState.NO_CRYSTAL;

    public BiomeOverlayInstance getBiomeOverlay() {
        if (biomeOverlay == null) return CoreBiomeOverlays.NORMAL.get();
        return biomeOverlay;
    }

    public boolean isAssembled(IDHDPartItem part) {
        return assembledParts.contains(part);
    }

    @Override
    public void toBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        if (biomeOverlay != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(biomeOverlay.getId());
        } else
            buf.writeBoolean(false);
        buf.writeInt(assembledParts.size());
        assembledParts.forEach(p -> buf.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(p.self()))));
        buf.writeInt(naquadahAmount);
        buf.writeInt(naquadahMaxAmount);
        buf.writeInt(reactorState.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        if (buf.readBoolean()) {
            biomeOverlay = BiomeOverlayInstance.byId(buf.readResourceLocation());
        }
        assembledParts.clear();
        var size = buf.readInt();
        for (int i = 0; i < size; i++)
            assembledParts.add((IDHDPartItem) BuiltInRegistries.ITEM.get(buf.readResourceLocation()));
        naquadahAmount = buf.readInt();
        naquadahMaxAmount = buf.readInt();
        reactorState = DHDReactorState.values()[buf.readInt()];
    }
}