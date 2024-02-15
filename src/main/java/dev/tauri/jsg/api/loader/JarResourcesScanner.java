package dev.tauri.jsg.api.loader;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.loader.FolderLoader;

import java.util.Collections;
import java.util.List;

/**
 * Resources scanner for your mod.
 *
 * Gets all files inside resources folder (or your path)
 */
@SuppressWarnings("unused")
public class JarResourcesScanner {
    public static JarResourcesScanner createScanner(String modId, Class<?> modMainClass) {
        return new JarResourcesScanner(modId, modMainClass);
    }

    protected final Class<?> modMainClass;
    protected final String modId;
    protected String path = null;
    protected String[] fileEnds = null;

    private JarResourcesScanner(String modId, Class<?> modMainClass) {
        this.modMainClass = modMainClass;
        this.modId = modId;
        JSG.logger.info("Created FolderLoader for domain " + modId);
    }

    public List<String> getFiles(){
        if(path == null) return Collections.emptyList();
        if(fileEnds == null) return Collections.emptyList();
        try {
            return FolderLoader.getAllFiles(modMainClass, path, fileEnds);
        }catch (Exception e){
            JSG.logger.error("Error while getting files from folder " + path + " for domain " + modId);
        }
        return Collections.emptyList();
    }
}
