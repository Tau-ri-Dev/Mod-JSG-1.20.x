package dev.tauri.jsg.config.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.common.config.IJSONConfigEntry;
import dev.tauri.jsg.core.common.config.json.AbstractJSONConfig;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class ProgressJSON extends AbstractJSONConfig<ProgressJSON.Entry> {
    public static final ProgressJSON INSTANCE = new ProgressJSON();

    public static Entry get() {
        try {
            INSTANCE.reload(null);
        } catch (Exception ignored) {
        }
        return INSTANCE.configEntries.get("data");
    }

    public static void update() {
        INSTANCE.shouldWriteToFile = true;
        try {
            INSTANCE.update(null);
        } catch (Exception ignored) {
        }
    }

    public static ResourceLocation getNextAct() {
        return JSGMapping.rl(get().nextActId);
    }

    public static void setActs(ResourceLocation current, ResourceLocation next) {
        var e = get();
        e.currentActId = current.toString();
        e.nextActId = next.toString();
        update();
    }

    public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("currentActId").forGetter(e -> e.currentActId),
            Codec.STRING.fieldOf("nextActId").forGetter(e -> e.nextActId)
    ).apply(instance, Entry::new));


    public ProgressJSON() {
        super("data/progress", Util.make(new HashMap<>(), (map) ->
                map.put("data", new Entry(JSGMapping.rl(JSG.MOD_ID, "act1").toString(), new ResourceLocation(JSG.MOD_ID, "act2").toString()))
        ), CODEC);
    }

    public static class Entry implements IJSONConfigEntry {
        public String currentActId;
        public String nextActId;

        public Entry(String currentActId, String nextActId) {
            this.currentActId = currentActId;
            this.nextActId = nextActId;
        }

        public void toBytes(FriendlyByteBuf buf) {
            buf.writeResourceLocation(JSGMapping.rl(currentActId));
            buf.writeResourceLocation(JSGMapping.rl(nextActId));
        }

        public void fromBytes(FriendlyByteBuf buf) {
            currentActId = buf.readResourceLocation().toString();
            nextActId = buf.readResourceLocation().toString();
        }
    }
}
