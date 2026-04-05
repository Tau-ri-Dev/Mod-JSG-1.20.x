package dev.tauri.jsg.api;

import dev.tauri.jsg.api.stargate.network.IStargateNetwork;
import dev.tauri.jsg.core.LoggerWrapper;
import dev.tauri.jsg.core.client.LoadersHolder;
import dev.tauri.jsg.core.common.registry.helper.RegistryHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JSGApi {
    public static final String MOD_ID = "jsg";
    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(JSGApi.MOD_ID);
    public static final LoadersHolder JSG_LOADERS_HOLDER = LoadersHolder.getOrCreate(JSGApi.MOD_ID, JSGApi.class);
    public static String MOD_VERSION;
    public static LoggerWrapper logger;
    public static Function<ServerLevel, IStargateNetwork> SGNGetter = null;
    public static Class<?> jsgModMainClass;

    /**
     * Contains las pos of player (client side) - helps to debug sound in main menu.
     */
    public static BlockPos lastPlayerPosInWorld = new BlockPos(0, 0, 0);

    public static ResourceLocation rl(String path) {
        return JSGMapping.rl(MOD_ID, path);
    }

    public static IStargateNetwork getStargateNetwork(ServerLevel level) {
        return SGNGetter.apply(level);
    }

    public static boolean isDevBuild() {
        if (MOD_VERSION == null) return false;
        return MOD_VERSION.toLowerCase().contains("-dev");
    }

    public static void init() {
    }
}
