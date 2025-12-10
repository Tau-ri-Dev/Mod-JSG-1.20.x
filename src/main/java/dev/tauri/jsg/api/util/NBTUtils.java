package dev.tauri.jsg.api.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NBTUtils {
    public static <K, V> void mapToBytes(Map<K, V> map, ByteBuf buf, BiConsumer<ByteBuf, K> keyToBytes, BiConsumer<ByteBuf, V> valueToBytes) {
        buf.writeInt(map.size());
        for (var e : map.entrySet()) {
            keyToBytes.accept(buf, e.getKey());
            valueToBytes.accept(buf, e.getValue());
        }
    }

    public static <M extends Map<K, V>, K, V> M mapFromBytes(M map, ByteBuf buf, Function<ByteBuf, K> keyFromBytes, Function<ByteBuf, V> valueFromBytes) {
        var size = buf.readInt();
        map.clear();
        for (var i = 0; i < size; i++) {
            var k = keyFromBytes.apply(buf);
            var v = valueFromBytes.apply(buf);
            map.put(k, v);
        }
        return map;
    }

    public static <K, V> CompoundTag serializeMap(Map<K, V> map, BiConsumer<CompoundTag, K> serializeKey, BiConsumer<CompoundTag, V> serializeValue) {
        var mapTag = new CompoundTag();
        var entriesTag = new ListTag();
        for (var e : map.entrySet()) {
            var entryTag = new CompoundTag();
            serializeKey.accept(entryTag, e.getKey());
            serializeValue.accept(entryTag, e.getValue());
            entriesTag.add(entryTag);
        }
        mapTag.put("entries", entriesTag);
        return mapTag;
    }

    public static <M extends Map<K, V>, K, V> M deSerializeMap(M map, CompoundTag compound, Function<CompoundTag, K> deSerializeKey, Function<CompoundTag, V> deSerializeValue) {
        map.clear();
        var entries = compound.getList("entries", Tag.TAG_COMPOUND);
        for (var e : entries) {
            var k = deSerializeKey.apply((CompoundTag) e);
            var v = deSerializeValue.apply((CompoundTag) e);
            map.put(k, v);
        }
        return map;
    }
}
