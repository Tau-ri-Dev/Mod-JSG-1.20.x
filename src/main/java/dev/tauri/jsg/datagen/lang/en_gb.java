package dev.tauri.jsg.datagen.lang;

import java.util.Map;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class en_gb extends LanguageProvider {
    private final InheritableLang parent;

    static final Map<String, String> REAL_ENGLISH_CORRECTIONS = Map.of(
        "dialing", "dialling",
        "Dialing", "Dialling",
        "color", "colour",
        "Color", "Colour"
    );


    public en_gb(PackOutput output, String modid, String locale, InheritableLang parent) {
        super(output, modid, locale);
        this.parent = parent;
    }

    @Override
    protected void addTranslations() {
        parent.getTranslations().entrySet().parallelStream().filter(e -> REAL_ENGLISH_CORRECTIONS.keySet().stream().anyMatch(e.getValue()::contains)).forEach(e -> {
            String value = e.getValue();
            for (Map.Entry<String, String> correction : REAL_ENGLISH_CORRECTIONS.entrySet()) {
                value = value.replace(correction.getKey(), correction.getValue());
            }
            add(e.getKey(), value);
        });
    }
}
