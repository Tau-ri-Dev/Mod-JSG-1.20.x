package dev.tauri.jsg.datagen.lang;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class InheritableLang extends LanguageProvider {
    public InheritableLang(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    protected final Map<String, String> translations = new TreeMap<>();

    @Override
    public void add(String key, String value) {
        translations.put(key, value);
        super.add(key, value);
    }

    public Map<String, String> getTranslations() {
        return Collections.unmodifiableMap(translations);
    }
}
