package dev.tauri.jsg.api;

import dev.tauri.jsg.api.client.LoadersHolder;
import dev.tauri.jsg.api.stargate.network.IStargateNetwork;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JSGApi {
    public static final String MOD_ID = "jsg";
    public static String MOD_VERSION;
    public static LoggerWrapper logger;
    public static final Map<String, JSGAddon> ADDONS = new HashMap<>();
    public static Function<ServerLevel, IStargateNetwork> SGNGetter = null;
    public static MinecraftServer currentServer = null;
    public static Supplier<BiFunction<String, Class<?>, LoadersHolder>> loadersHolderGetter;
    public static Class<?> jsgModMainClass;

    /**
     * Contains las pos of player (client side) - helps to debug sound in main menu.
     */
    public static BlockPos lastPlayerPosInWorld = new BlockPos(0, 0, 0);

    public static Optional<JSGAddon> getAddon(String id) {
        return Optional.ofNullable(ADDONS.get(id));
    }

    public static void registerAddon(JSGAddon addon) {
        ADDONS.put(addon.getId(), addon);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static IStargateNetwork getStargateNetwork(ServerLevel level) {
        return SGNGetter.apply(level);
    }

    public static boolean isDevBuild() {
        if (MOD_VERSION == null) return false;
        return MOD_VERSION.toLowerCase().contains("-dev");
    }
}
