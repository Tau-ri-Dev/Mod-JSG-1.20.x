package dev.tauri.jsg.datagen.tag;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.registry.tag.CoreBiomeTags;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.registry.JSGBiomes;
import dev.tauri.jsg.registry.tags.JSGBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class JSGBiomeTagGenerator extends BiomeTagsProvider {
    public JSGBiomeTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, JSG.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {
        // abydos
        tag(Objects.requireNonNull(CoreBiomeOverlays.AGED.get().getOverlayBiomesTag())).addTag(JSGBiomeTags.IS_ABYDOS);
        tag(JSGBiomeTags.IS_ABYDOS).add(JSGBiomes.ABYDOS_DESERT, JSGBiomes.ABYDOS_NAQUADAH_DEPOSITS, JSGBiomes.ABYDOS_PLAINS);
        tag(CoreBiomeTags.IS_SANDY).addTag(JSGBiomeTags.IS_ABYDOS);
        tag(JSGBiomeTags.HAS_NAQUADAH_DEPOSITS).add(JSGBiomes.ABYDOS_NAQUADAH_DEPOSITS);
        tag(CoreBiomeTags.HAS_NAQUADAH_GENERATED).replace(true).addTag(JSGBiomeTags.IS_ABYDOS);

        //has_structure
        tag(JSGBiomeTags.HAS_STRUCTURE_ABYDOS_CAMP).add(JSGBiomes.ABYDOS_DESERT, JSGBiomes.ABYDOS_PLAINS);
        tag(JSGBiomeTags.HAS_STRUCTURE_ABYDOS_CARTOUCHE).add(JSGBiomes.ABYDOS_DESERT, JSGBiomes.ABYDOS_PLAINS);
        tag(JSGBiomeTags.HAS_STRUCTURE_ABYDOS_CITY).add(JSGBiomes.ABYDOS_DESERT, JSGBiomes.ABYDOS_PLAINS);
        tag(JSGBiomeTags.HAS_STRUCTURE_ABYDOS_DUNGEON).addTag(JSGBiomeTags.IS_ABYDOS);
        tag(JSGBiomeTags.HAS_STRUCTURE_ABYDOS_MAIN_PYRAMID).addTag(JSGBiomeTags.IS_ABYDOS);

        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_BADLANDS).addTag(CoreBiomeTags.IS_BADLANDS);
        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_DESERT).addTag(CoreBiomeTags.IS_DESERT);
        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_MANGROVE).addTag(CoreBiomeTags.IS_SWAMP);
        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_MOSSY).addTag(CoreBiomeTags.IS_SWAMP).addTag(CoreBiomeTags.IS_FORREST);
        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_NETHER).addTag(CoreBiomeTags.IS_NETHER);
        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_PLAINS).addTag(CoreBiomeTags.IS_TEMPERATE);
        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_SNOWY).addTag(CoreBiomeTags.IS_COLD_SOLID);

        tag(JSGBiomeTags.HAS_STRUCTURE_MILKYWAY_OUTPOST_COMPAT)
                .addOptional(JSGMapping.rl("ad_astra", "venus_wastelands"))
                .addOptional(JSGMapping.rl("ad_astra", "infernal_venus_barrens"))
                .addOptional(JSGMapping.rl("ad_astra", "glacio_ice_peaks"))
                .addOptional(JSGMapping.rl("ad_astra", "glacio_snowy_barrens"))
                .addOptional(JSGMapping.rl("ad_astra", "lunar_wastelands"))
                .addOptional(JSGMapping.rl("ad_astra", "martian_canyon_creek"))
                .addOptional(JSGMapping.rl("ad_astra", "martian_polar_caps"))
                .addOptional(JSGMapping.rl("ad_astra", "martian_wastelands"))

                .addOptional(JSGMapping.rl("adastraextra", "adrastea_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "amalthea_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "deimos_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "jupiter_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "metis_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "neptune_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "phobos_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "saturn_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "thebe_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "uranus_frozen_plains"))
                .addOptional(JSGMapping.rl("adastraextra", "uranus_plains"))

                .addOptional(JSGMapping.rl("ad_astra_dh", "trash_x_1_desert"))
                .addOptional(JSGMapping.rl("ad_astra_dh", "trash_x_1_snowy_desert"))
        ;

        tag(JSGBiomeTags.HAS_STRUCTURE_MOVIE_OUTPOST_COMPAT);
        tag(JSGBiomeTags.HAS_STRUCTURE_MOVIE_BURRIED).add(Biomes.DESERT);

        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_BADLANDS).add(Biomes.BADLANDS);
        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_DESERT).addTag(CoreBiomeTags.IS_DESERT);
        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_MANGROVE).addTag(CoreBiomeTags.IS_SWAMP);
        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_MOSSY).addTag(CoreBiomeTags.IS_SWAMP).addTag(CoreBiomeTags.IS_FORREST);
        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_OCEAN).addTag(CoreBiomeTags.IS_OCEAN);
        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_PLAINS).addTag(CoreBiomeTags.IS_TEMPERATE);
        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_SNOWY).addTag(CoreBiomeTags.IS_COLD_SOLID);
        tag(JSGBiomeTags.HAS_STRUCTURE_PEGASUS_OUTPOST_COMPAT);

        tag(JSGBiomeTags.HAS_STRUCTURE_TOLLAN_OUTPOST_COMPAT);

        tag(JSGBiomeTags.HAS_STRUCTURE_UNIVERSE_OUTPOST_END).add(Biomes.END_HIGHLANDS).add(Biomes.END_MIDLANDS);
        tag(JSGBiomeTags.HAS_STRUCTURE_UNIVERSE_OUTPOST_END_MAIN_ISLAND).add(Biomes.THE_END);
        tag(JSGBiomeTags.HAS_STRUCTURE_UNIVERSE_OUTPOST_COMPAT);

    }
}
