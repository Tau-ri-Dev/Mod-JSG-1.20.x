package dev.tauri.jsg.renderer.machine;

import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PrinterRendererState extends State {
    public ItemStack input;
    public LinkedList<ItemStack> output;
    public StargateAddressDynamic addressDynamic;
    public int pos;
    public PointOfOrigin origin;
    public long printStarted;
    public final List<Integer> symbolsToPrint = new ArrayList<>();
    public List<ItemStack> cartridges = new ArrayList<>();

    @Override
    public void toBytes(ByteBuf buf) {
        var b = new FriendlyByteBuf(buf);
        if (input == null) input = ItemStack.EMPTY;
        b.writeItemStack(input, false);
        if (output == null) output = new LinkedList<>();
        b.writeInt(output.size());
        for (var o : output) {
            b.writeItemStack(o, false);
        }
        if (addressDynamic == null) b.writeBoolean(false);
        else {
            b.writeBoolean(true);
            addressDynamic.toBytes(b);
        }
        b.writeInt(pos);
        b.writeLong(printStarted);
        b.writeInt(symbolsToPrint.size());
        for (var s : symbolsToPrint) {
            b.writeInt(s);
        }
        if (origin != null) {
            b.writeBoolean(true);
            origin.toBytes(b);
        } else
            b.writeBoolean(false);
        b.writeInt(cartridges.size());
        for (var c : cartridges) {
            b.writeItemStack(c, false);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        cartridges = new ArrayList<>();
        var b = new FriendlyByteBuf(buf);
        input = b.readItem();
        output = new LinkedList<>();
        var size = b.readInt();
        for (var i = 0; i < size; i++) {
            output.addLast(b.readItem());
        }
        if (b.readBoolean()) {
            addressDynamic = new StargateAddressDynamic(b);
        }
        pos = b.readInt();
        printStarted = b.readLong();
        size = b.readInt();
        symbolsToPrint.clear();
        for (var i = 0; i < size; i++) {
            symbolsToPrint.add(b.readInt());
        }
        if (b.readBoolean()) {
            origin = PointOfOrigin.fromBytes(b, null);
        }
        size = b.readInt();
        for (int i = 0; i < size; i++) {
            cartridges.add(b.readItem());
        }
    }
}
