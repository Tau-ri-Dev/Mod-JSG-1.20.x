package dev.tauri.jsg.api.client;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.client.model.IModelLoader;
import dev.tauri.jsg.api.client.texture.ITextureLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.loading.progress.ProgressMeter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class LoadersHolder {
    private final ITextureLoader textureLoader;
    private final IModelLoader modelLoader;
    private final String modId;

    public LoadersHolder(String modId, ITextureLoader textureLoader, IModelLoader modelLoader) {
        this.modId = modId;
        this.textureLoader = textureLoader;
        this.modelLoader = modelLoader;
    }

    public ITextureLoader texture() {
        if (textureLoader == null)
            throw new UnsupportedOperationException("Can note get texture loader for mod " + modId);
        return textureLoader;
    }

    public IModelLoader model() {
        if (modelLoader == null) throw new UnsupportedOperationException("Can note get model loader for mod " + modId);
        return modelLoader;
    }


    // -----------------------------------------------------------------------
    // STATIC

    private static final Map<String, LoadersHolder> HOLDERS = new HashMap<>();

    public static final LoadersHolder JSG_HOLDER = getOrCreate(JSGApi.MOD_ID, JSGApi.jsgModMainClass);

    public static LoadersHolder getOrCreate(String modId, Class<?> mainModClass) {
        if (getHolder(modId).isEmpty())
            registerHolder(modId, JSGApi.loadersHolderGetter.get().apply(modId, mainModClass));
        return HOLDERS.get(modId);
    }

    public static void registerHolder(String modId, LoadersHolder holder) {
        HOLDERS.put(modId, holder);
    }

    public static Optional<LoadersHolder> getHolder(String modId) {
        return Optional.ofNullable(HOLDERS.get(modId));
    }

    public static void load(ProfilerFiller profilerFiller) {
        synchronized (HOLDERS) {
            for (var e : HOLDERS.entrySet()) {
                var id = e.getKey();
                var t = e.getValue().textureLoader;
                var m = e.getValue().modelLoader;
                var count = (t == null ? (m == null ? 0 : 1) : (m == null ? 1 : 2));
                if (count < 1) continue;
                AtomicReference<String> modName = new AtomicReference<>(id);

                ModList.get().getModContainerById(id).ifPresentOrElse(container -> modName.set(container.getModInfo().getDisplayName()), () -> {
                });

                profilerFiller.startTick();
                profilerFiller.push("jsg_resources_" + id);
                ProgressMeter progress = StartupMessageManager.addProgressBar(modName.get() + " - TESR loading", count);
                if (t != null) {
                    t.loadTextures();
                    progress.increment();
                }
                if (m != null) {
                    m.loadModels();
                    progress.increment();
                }
                progress.complete();
                profilerFiller.pop();
                profilerFiller.endTick();
            }
        }
    }
}
