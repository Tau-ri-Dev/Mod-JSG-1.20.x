package dev.tauri.jsg.api.config.ingame;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.ingame.options.BlockConfigOptions;
import dev.tauri.jsg.api.config.ingame.options.StargateConfigOptions;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class BlockConfigOptionRegistry {
    private static final Map<ResourceLocation, Supplier<Supplier<List<? extends BlockConfigOptions>>>> reg = new HashMap<>();


    public static final ResourceLocation GATE_COMMON = register(new ResourceLocation(JSGApi.MOD_ID, "gate_common"), () -> () -> StargateConfigOptions.COMMON);
    public static final ResourceLocation GATE_MILKYWAY = register(new ResourceLocation(JSGApi.MOD_ID, "gate_milkyway"), () -> () -> StargateConfigOptions.MILKYWAY);
    public static final ResourceLocation GATE_PEGASUS = register(new ResourceLocation(JSGApi.MOD_ID, "gate_pegasus"), () -> () -> StargateConfigOptions.PEGASUS);
    public static final ResourceLocation GATE_UNIVERSE = register(new ResourceLocation(JSGApi.MOD_ID, "gate_universe"), () -> () -> StargateConfigOptions.UNIVERSE);
    public static final ResourceLocation GATE_TOLLAN = register(new ResourceLocation(JSGApi.MOD_ID, "gate_tollan"), () -> () -> StargateConfigOptions.TOLLAN);

    /**
     * Registers a list of {@link BlockConfigOptions} for a specific block type.
     *
     * <p>This method checks if the provided block type is already registered. If not, it adds the block type and its corresponding
     * list of {@link BlockConfigOptions} to the registry. If the block type is already registered, it throws an
     * {@link IllegalArgumentException} with a descriptive error message.
     *
     * @param blockType The unique identifier for the block type.
     * @param options   The list of {@link BlockConfigOptions} associated with the block type.
     * @throws IllegalArgumentException If the block type is already registered.
     */
    public static ResourceLocation register(ResourceLocation blockType, Supplier<Supplier<List<? extends BlockConfigOptions>>> options) {
        if (!reg.containsKey(blockType)) {
            reg.put(blockType, options);
            return blockType;
        } else {
            throw new IllegalArgumentException("Config options have already been registered for blockType: \"%s\"".formatted(blockType));
        }
    }

    /**
     * Retrieves a collection of {@link JSGConfigOption} objects associated with a specific block type.
     *
     * <p>This method retrieves a new immutable list of {@link JSGConfigOption} associated with the provided block type from the registry.
     * If the block type is not found in the registry, an empty list is returned.
     *
     * @param blockType The unique identifier for the block type.
     * @return A collection of {@link JSGConfigOption} objects associated with the specified block type. If the block type is not found,
     * an empty collection is returned.
     */
    public static Collection<? extends JSGConfigOption<?>> get(ResourceLocation blockType) {
        return reg.getOrDefault(blockType, () -> List::of).get().get().stream().map(BlockConfigOptions::get).toList();
    }
}

