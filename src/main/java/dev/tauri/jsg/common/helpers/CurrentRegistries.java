package dev.tauri.jsg.common.helpers;

import net.minecraft.core.HolderLookup;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

/**
 * Registry access for 1.20.1-era serialization call sites that have no
 * {@link HolderLookup.Provider} parameter (same approach as core's ItemNBT).
 */
public final class CurrentRegistries {
    private CurrentRegistries() {
    }

    @Nullable
    public static HolderLookup.Provider get() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) return server.registryAccess();
        if (FMLEnvironment.dist.isClient()) return Client.get();
        return null;
    }

    public static HolderLookup.Provider getOrThrow() {
        var access = get();
        if (access == null)
            throw new IllegalStateException("No registry access available (no server running and no client level)");
        return access;
    }

    private static class Client {
        @Nullable
        static HolderLookup.Provider get() {
            var level = net.minecraft.client.Minecraft.getInstance().level;
            return level == null ? null : level.registryAccess();
        }
    }
}
