package dev.tauri.jsg.api.config;

import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJSONConfig<E extends IJSONConfigEntry> {
    public final Collection<E> defaults;
    public final Map<String, E> configEntries = new HashMap<>();
    public final String name;
    private File file;
    protected boolean shouldWriteToFile;

    public AbstractJSONConfig(String name, Collection<E> defaults) {
        this.defaults = defaults;
        this.name = name;
    }

    @Nullable
    public E getConfigEntry(String id) {
        if (configEntries.isEmpty()) return null;
        return configEntries.get(id);
    }

    public abstract Type getJSONType();

    public void reload(@Nullable MinecraftServer server) throws IOException {
        load(null);
        update(server);
    }

    public void load(File modConfigDir) {
        configEntries.clear();
        if (modConfigDir != null)
            file = new File(modConfigDir, "jsg/" + name + ".json");
        if (file == null) return;
        try {
            Type typeOfHashMap = getJSONType();
            Map<String, E> configMap = new GsonBuilder().create().fromJson(new FileReader(file), typeOfHashMap);
            if (configMap != null) {
                configEntries.putAll(configMap);
            }
        } catch (FileNotFoundException ignored) {
        }
    }

    @SuppressWarnings("all")
    protected void update(@Nullable MinecraftServer server) throws IOException {
        if (configEntries.isEmpty()) {
            defaults.forEach(e -> configEntries.put(e.getId(), e));
            shouldWriteToFile = true;
        }
        if (!shouldWriteToFile) return;
        if (file == null) return;
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.PROTECTED).create().toJson(configEntries, getJSONType()));
        writer.close();
        shouldWriteToFile = false;
    }
}
