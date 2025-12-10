package dev.tauri.jsg.api.integration;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public enum Integrations {
    OC2("OpenComputers", List.of("oc2", "oc2r")),
    CCT("ComputerCraft", List.of("computercraft")),
    CREATE("Create", List.of("create")),
    JEI("JEI", List.of("jei")),
    EMBEDDIUM("Embeddium", List.of("embeddium", "sodium", "rubiddium")),
    OCULUS("Embeddium", List.of("iris", "oculus")),
    TCONSTRUCT("Tinkers Construct", List.of("tconstruct"));

    public final String name;
    public final List<String> modNames;
    public Boolean isLoaded = null;
    public final List<Loader> onLoad = new ArrayList<>();
    public final List<Loader> onNotLoaded = new ArrayList<>();

    Integrations(String name, List<String> modNames) {
        this.name = name;
        this.modNames = modNames;
    }

    public boolean isLoaded() {
        return isLoaded != null && isLoaded;
    }

    public Integrations addOnLoad(Loader task) {
        if (isLoaded()) {
            try {
                task.run();
            } catch (Exception ignored) {
            }
            return this;
        }
        onLoad.add(task);
        return this;
    }

    public Integrations addOnNotLoaded(Loader task) {
        if (isLoaded != null && !isLoaded) {
            try {
                task.run();
            } catch (Exception ignored) {
            }
            return this;
        }
        onNotLoaded.add(task);
        return this;
    }

    public interface Loader {
        void run() throws Exception;
    }
}
