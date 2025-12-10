package dev.tauri.jsg.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterReplacer {
    private final Map<String, String> KEYS = new HashMap<>();

    public ParameterReplacer() {
    }

    public ParameterReplacer addKey(String key, String value) {
        KEYS.put(key, value);
        return this;
    }

    public ParameterReplacer addKeys(Map<String, String> keys) {
        KEYS.putAll(keys);
        return this;
    }

    public Map<String, String> getKeys() {
        return KEYS;
    }

    public String apply(String value) {
        if (value == null) return null;
        for (Map.Entry<String, String> e : getKeys().entrySet()) {
            value = value.replaceAll("%" + e.getKey() + "%", e.getValue());
        }
        return value;
    }

    public List<String> apply(List<String> value) {
        if (value == null) return null;
        var copy = new ArrayList<>(value);
        value.clear();
        for (var s : copy) {
            for (Map.Entry<String, String> e : getKeys().entrySet()) {
                s = s.replaceAll("%" + e.getKey() + "%", e.getValue());
            }
            value.add(s);
        }
        return value;
    }

    public ParameterReplacer copy() {
        return new ParameterReplacer().addKeys(this.KEYS);
    }
}
