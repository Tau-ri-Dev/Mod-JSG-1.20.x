package dev.tauri.jsg.common.datagen.tag;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.registry.JSGStructures;
import dev.tauri.jsg.common.registry.tags.JSGStructureTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.tags.StructureTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class JSGStructureTagGenerator extends StructureTagsProvider {
    public JSGStructureTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, JSG.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(JSGStructureTags.HAS_MILKYWAY_STARGATE)
                .add(JSGStructures.MILKYWAY_BADLANDS)
                .add(JSGStructures.MILKYWAY_DESERT)
                .add(JSGStructures.MILKYWAY_MANGROVE)
                .add(JSGStructures.MILKYWAY_MOSSY)
                .add(JSGStructures.MILKYWAY_NETHER)
                .add(JSGStructures.MILKYWAY_PLAINS)
                .add(JSGStructures.MILKYWAY_SNOWY)
                .add(JSGStructures.ABYDOS_MAIN_PYRAMID)
                .add(JSGStructures.MILKYWAY_COMPAT);

        tag(JSGStructureTags.HAS_MOVIE_STARGATE)
                .add(JSGStructures.MOVIE_BURIED)  //After I add buried stargates structures - F.
                .add(JSGStructures.MOVIE_COMPAT);

        tag(JSGStructureTags.HAS_PEGASUS_STARGATE)
                .add(JSGStructures.PEGASUS_BADLANDS)
                .add(JSGStructures.PEGASUS_DESERT)
                .add(JSGStructures.PEGASUS_MANGROVE)
                .add(JSGStructures.PEGASUS_MOSSY)
                .add(JSGStructures.PEGASUS_OCEAN)
                .add(JSGStructures.PEGASUS_PLAINS)
                .add(JSGStructures.PEGASUS_SNOWY)
                .add(JSGStructures.PEGASUS_COMPAT);

        tag(JSGStructureTags.HAS_TOLLAN_STARGATE).add(JSGStructures.TOLLAN_COMPAT);

        tag(JSGStructureTags.HAS_UNIVERSE_STARGATE)
                .add(JSGStructures.UNIVERSE_ISLANDS)
                .add(JSGStructures.UNIVERSE_MAIN_ISLAND)
                .add(JSGStructures.UNIVERSE_COMPAT);

        tag(JSGStructureTags.HAS_COMPAT_STARGATE)
                .add(JSGStructures.MILKYWAY_COMPAT)
                .add(JSGStructures.PEGASUS_COMPAT)
                .add(JSGStructures.UNIVERSE_COMPAT)
                .add(JSGStructures.TOLLAN_COMPAT)
                .add(JSGStructures.MOVIE_COMPAT);

        tag(JSGStructureTags.HAS_STARGATE)
                .addTag(JSGStructureTags.HAS_MILKYWAY_STARGATE)
                .addTag(JSGStructureTags.HAS_PEGASUS_STARGATE)
                .addTag(JSGStructureTags.HAS_UNIVERSE_STARGATE)
                .addTag(JSGStructureTags.HAS_TOLLAN_STARGATE)
                .addTag(JSGStructureTags.HAS_MOVIE_STARGATE);

        tag(JSGStructureTags.HAS_POTENTIALLY_MILKYWAY)
                .addTag(StructureTags.VILLAGE)
                .addTag(JSGStructureTags.HAS_MILKYWAY_STARGATE);

        tag(JSGStructureTags.HAS_POTENTIALLY_MOVIE).addTag(JSGStructureTags.HAS_MOVIE_STARGATE);

        tag(JSGStructureTags.HAS_POTENTIALLY_PEGASUS)
                .addTag(StructureTags.VILLAGE)
                .addTag(JSGStructureTags.HAS_PEGASUS_STARGATE);

        tag(JSGStructureTags.HAS_POTENTIALLY_TOLLAN)
                .addTag(StructureTags.VILLAGE)
                .addTag(JSGStructureTags.HAS_TOLLAN_STARGATE);

        tag(JSGStructureTags.HAS_POTENTIALLY_UNIVERSE).addTag(JSGStructureTags.HAS_UNIVERSE_STARGATE);

        tag(JSGStructureTags.HAS_POTENTIALLY_STARGATE)
                .addTag(JSGStructureTags.HAS_POTENTIALLY_MILKYWAY)
                .addTag(JSGStructureTags.HAS_POTENTIALLY_PEGASUS)
                .addTag(JSGStructureTags.HAS_POTENTIALLY_UNIVERSE)
                .addTag(JSGStructureTags.HAS_POTENTIALLY_MOVIE)
                .addTag(JSGStructureTags.HAS_POTENTIALLY_TOLLAN);

        tag(JSGStructureTags.HAS_POTENTIALLY_ABYDOS_AMBIENT)
                .add(JSGStructures.ABYDOS_BOULDER)
                .add(JSGStructures.ABYDOS_CAMPFIRE);

        tag(JSGStructureTags.HAS_POTENTIALLY_ABYDOS_GENERIC)
                .add(JSGStructures.ABYDOS_CARTOUCHE)
                .add(JSGStructures.ABYDOS_CITY)
                .add(JSGStructures.ABYDOS_DUNGEON);

        tag(JSGStructureTags.HAS_ABYDOS_PYRAMID)
                .add(JSGStructures.ABYDOS_MAIN_PYRAMID);

        tag(JSGStructureTags.ABYDOS_MARKED_ON_MAP)
                .add(JSGStructures.ABYDOS_CARTOUCHE);

        tag(JSGStructureTags.OVERWORLD_MARKED_ON_MAP)
                .add(JSGStructures.MOVIE_BURIED);
    }
}
