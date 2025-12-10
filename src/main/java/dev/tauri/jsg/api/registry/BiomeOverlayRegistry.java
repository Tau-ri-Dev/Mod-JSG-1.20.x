package dev.tauri.jsg.api.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.helper.BlockHelper;
import dev.tauri.jsg.api.util.TagFetcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BiomeOverlayRegistry {
    public static class BiomeOverlayInstance {
        public final TagKey<Item> overlayItems;
        public final TagKey<Biome> overlayBiomes;
        public final String suffix;
        public final ChatFormatting color;
        public final String unlocalizedName;
        public final ResourceLocation id;

        private BiomeOverlayInstance(String name, ChatFormatting color) {
            this(new ResourceLocation(JSGApi.MOD_ID, name), color);
        }

        public BiomeOverlayInstance(ResourceLocation id, ChatFormatting color) {
            this.id = id;
            this.suffix = (id.getPath().equalsIgnoreCase("normal") ? "" : "_" + id.getPath().toLowerCase());
            this.unlocalizedName = "gui.biome_overlay." + id.getPath().toLowerCase();
            this.color = color;
            this.overlayItems = ItemTags.create(new ResourceLocation(id.getNamespace(), "biome_overlay/" + id.getPath().toLowerCase()));
            this.overlayBiomes = TagKey.create(Registries.BIOME, new ResourceLocation(id.getNamespace(), "biome_overlay/" + id.getPath().toLowerCase()));

            INSTANCES.put(id, this);
        }

        public List<Item> getOverlayItems() {
            return TagFetcher.getItemsInTag(overlayItems);
        }

        public String getLocalizedColorizedName() {
            return color + I18n.get(unlocalizedName);
        }

        public String getSuffix() {
            return suffix;
        }
    }

    private static final Map<ResourceLocation, BiomeOverlayInstance> INSTANCES = new HashMap<>();

    public static final BiomeOverlayInstance NORMAL = new BiomeOverlayInstance("normal", ChatFormatting.GRAY);
    public static final BiomeOverlayInstance FROST = new BiomeOverlayInstance("frost", ChatFormatting.DARK_AQUA);
    public static final BiomeOverlayInstance MOSSY = new BiomeOverlayInstance("mossy", ChatFormatting.DARK_GREEN);
    public static final BiomeOverlayInstance AGED = new BiomeOverlayInstance("aged", ChatFormatting.GRAY);
    public static final BiomeOverlayInstance SOOTY = new BiomeOverlayInstance("sooty", ChatFormatting.DARK_GRAY);

    @NotNull
    public static BiomeOverlayInstance getBiomeOverlayByItem(ItemStack stack) {
        return Objects.requireNonNull(getBiomeOverlayByItem(stack, false));
    }

    public static BiomeOverlayInstance getBiomeOverlayByItem(ItemStack stack, boolean canBeNull) {
        for (var overlay : INSTANCES.values()) {
            if (stack.is(overlay.overlayItems)) return overlay;
        }
        return (canBeNull ? null : NORMAL);
    }

    @NotNull
    public static BiomeOverlayInstance getBiomeOverlayByBiome(Holder<Biome> biome) {
        for (var overlay : INSTANCES.values()) {
            if (biome.is(overlay.overlayBiomes)) return overlay;
        }
        return NORMAL;
    }

    @NotNull
    public static List<BiomeOverlayInstance> values() {
        return new ArrayList<>(INSTANCES.values());
    }

    @NotNull
    public static BiomeOverlayInstance getUpdatedBiomeOverlay(Level world, BlockPos topmostBlock, List<BiomeOverlayInstance> supportedOverlays) {
        BiomeOverlayInstance ret = getBiomeOverlayByBlockPos(world, topmostBlock);

        if (supportedOverlays.contains(ret))
            return ret;

        return NORMAL;
    }

    @NotNull
    public static BiomeOverlayInstance getBiomeOverlayByBlockPos(Level world, BlockPos topmostBlock) {
        Holder<Biome> biome = world.getBiome(topmostBlock);

        // If not Nether and block not under sky
        if (world.dimension() != Level.NETHER && !BlockHelper.isBlockDirectlyUnderSky(world, topmostBlock))
            return NORMAL;

        if (biome.value().coldEnoughToSnow(topmostBlock))
            return FROST;

        return BiomeOverlayRegistry.getBiomeOverlayByBiome(biome);

    }

    @NotNull
    public static BiomeOverlayInstance byId(ResourceLocation id) {
        return INSTANCES.getOrDefault(id, NORMAL);
    }
}
