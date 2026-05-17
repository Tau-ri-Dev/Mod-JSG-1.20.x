package dev.tauri.jsg.datagen;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.advancements.JSGCriterions;
import dev.tauri.jsg.common.registry.*;
import dev.tauri.jsg.core.common.registry.CoreAdvancements;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class JSGAdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    @ParametersAreNonnullByDefault
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper fileHelper) {
        Advancement.Builder.advancement()
                .parent(CoreAdvancements.PAGE_EMPTY)
                .display(
                        JSGBlocks.PRINTER.get(),
                        Component.translatable("advancement.jsg.craft_printer.title"),
                        Component.translatable("advancement.jsg.craft_printer.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_printer", RecipeCraftedTrigger.TriggerInstance.craftedItem(JSGMapping.rl(JSG.MOD_ID, "printer")))
                .save(saver, JSGAdvancements.CRAFT_PRINTER, fileHelper);

        //TODO (Fredy): Fix this datagen - its wrong -> not players data, but villager data! + create rest of trades
        Advancement.Builder.advancement()
                .parent(CoreAdvancements.PAGE_EMPTY)
                .display(
                        Items.EMERALD,
                        Component.translatable("advancement.jsg.priest_trade.title"),
                        Component.translatable("advancement.jsg.priest_trade.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("priest_trade_desert", TradeTrigger.TriggerInstance.tradedWithVillager(getVillagerProfessionPredicate(Objects.requireNonNull(JSGVillagers.PRIEST_DESERT.getKey()).location())))
                .addCriterion("priest_trade_plains", TradeTrigger.TriggerInstance.tradedWithVillager(getVillagerProfessionPredicate(Objects.requireNonNull(JSGVillagers.PRIEST_PLAINS.getKey()).location())))
                .save(saver, JSGAdvancements.TRADE_WITH_PRIEST, fileHelper);


        Advancement.Builder.advancement()
                .parent(CoreAdvancements.PAGE_EMPTY)
                .display(
                        Blocks.DECORATED_POT,
                        Component.translatable("advancement.jsg.visit_abydos.title"),
                        Component.translatable("advancement.jsg.visit_abydos.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("visit_abydos", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(JSGDimensions.ABYDOS))
                .save(saver, JSGAdvancements.VISIT_ABYDOS, fileHelper);

        Advancement.Builder.advancement()
                .parent(CoreAdvancements.COPPER)
                .display(
                        JSGItems.GDO.get(),
                        Component.translatable("advancement.jsg.use_gdo.title"),
                        Component.translatable("advancement.jsg.use_gdo.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("use_gdo", JSGCriterions.GDO_USED.createInstance())
                .save(saver, JSGAdvancements.USE_GDO, fileHelper);

        Advancement toaster = Advancement.Builder.advancement()
                .parent(CoreAdvancements.COPPER)
                .display(
                        JSGBlocks.TOASTER.get(),
                        Component.translatable("advancement.jsg.obtain_toaster.title"),
                        Component.translatable("advancement.jsg.obtain_toaster.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_toaster", InventoryChangeTrigger.TriggerInstance.hasItems(JSGBlocks.TOASTER.get()))
                .save(saver, JSGAdvancements.OBTAIN_TOASTER, fileHelper);

        Advancement orlin_gate = Advancement.Builder.advancement()
                .parent(toaster)
                .display(
                        JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get(),
                        Component.translatable("advancement.jsg.activate_orlin_gate.title"),
                        Component.translatable("advancement.jsg.activate_orlin_gate.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("activate_orlin_gate", JSGCriterions.ACTIVATE_ORLIN_GATE.createInstance())
                .save(saver, JSGAdvancements.ACTIVATE_ORLIN_GATE, fileHelper);

        Advancement mw_crystal = Advancement.Builder.advancement()
                .parent(CoreAdvancements.CIRCUIT_CONTROL_CRYSTAL)
                .display(
                        JSGItems.CRYSTAL_GLYPH_MILKYWAY.get(),
                        Component.translatable("advancement.jsg.obtain_milkyway_glyph_crystal.title"),
                        Component.translatable("advancement.jsg.obtain_milkyway_glyph_crystal.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_mw_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(JSGItems.CRYSTAL_GLYPH_MILKYWAY.get()))
                .save(saver, JSGAdvancements.OBTAIN_MILKYWAY_GLYPH_CRYSTAL, fileHelper);

        Advancement pg_crystal = Advancement.Builder.advancement()
                .parent(mw_crystal)
                .display(
                        JSGItems.CRYSTAL_GLYPH_PEGASUS.get(),
                        Component.translatable("advancement.jsg.obtain_pegasus_glyph_crystal.title"),
                        Component.translatable("advancement.jsg.obtain_pegasus_glyph_crystal.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_pg_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(JSGItems.CRYSTAL_GLYPH_PEGASUS.get()))
                .save(saver, JSGAdvancements.OBTAIN_PEGASUS_GLYPH_CRYSTAL, fileHelper);

        Advancement uni_crystal = Advancement.Builder.advancement()
                .parent(pg_crystal)
                .display(
                        JSGItems.CRYSTAL_GLYPH_UNIVERSE.get(),
                        Component.translatable("advancement.jsg.obtain_universe_glyph_crystal.title"),
                        Component.translatable("advancement.jsg.obtain_universe_glyph_crystal.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_uni_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(JSGItems.CRYSTAL_GLYPH_UNIVERSE.get()))
                .save(saver, JSGAdvancements.OBTAIN_UNIVERSE_GLYPH_CRYSTAL, fileHelper);

        Advancement.Builder.advancement()
                .parent(uni_crystal)
                .display(
                        JSGItems.CRYSTAL_GLYPH_STARGATE.get(),
                        Component.translatable("advancement.jsg.obtain_stargate_glyph_crystal.title"),
                        Component.translatable("advancement.jsg.obtain_stargate_glyph_crystal.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_stargate_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(JSGItems.CRYSTAL_GLYPH_STARGATE.get()))
                .save(saver, JSGAdvancements.OBTAIN_STARGATE_GLYPH_CRYSTAL, fileHelper);

        Advancement movie_gate = Advancement.Builder.advancement()
                .parent(CoreAdvancements.TITANIUM)
                .display(
                        JSGBlocks.STARGATE_MOVIE_BASE_BLOCK.get(),
                        Component.translatable("advancement.jsg.activate_movie_gate.title"),
                        Component.translatable("advancement.jsg.activate_movie_gate.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("activate_movie_gate", JSGCriterions.ACTIVATE_MOVIE_GATE.createInstance())
                .save(saver, JSGAdvancements.ACTIVATE_MOVIE_GATE, fileHelper);

        Advancement mw_gate = Advancement.Builder.advancement()
                .parent(movie_gate)
                .display(
                        JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK.get(),
                        Component.translatable("advancement.jsg.activate_mw_gate.title"),
                        Component.translatable("advancement.jsg.activate_mw_gate.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("activate_milkyway_gate", JSGCriterions.ACTIVATE_MILKYWAY_GATE.createInstance())
                .save(saver, JSGAdvancements.ACTIVATE_MILKYWAY_GATE, fileHelper);

        Advancement.Builder.advancement()
                .parent(mw_gate)
                .display(
                        JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK.get(),
                        Component.translatable("advancement.jsg.activate_tollan_gate.title"),
                        Component.translatable("advancement.jsg.activate_tollan_gate.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("activate_tollan_gate", JSGCriterions.ACTIVATE_TOLLAN_GATE.createInstance())
                .save(saver, JSGAdvancements.ACTIVATE_TOLLAN_GATE, fileHelper);

        Advancement pg_gate = Advancement.Builder.advancement()
                .parent(mw_gate)
                .display(
                        JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.get(),
                        Component.translatable("advancement.jsg.activate_pg_gate.title"),
                        Component.translatable("advancement.jsg.activate_pg_gate.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("activate_pegasus_gate", JSGCriterions.ACTIVATE_PEGASUS_GATE.createInstance())
                .save(saver, JSGAdvancements.ACTIVATE_PEGASUS_GATE, fileHelper);

        Advancement.Builder.advancement()
                .parent(pg_gate)
                .display(
                        JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get(),
                        Component.translatable("advancement.jsg.activate_uni_gate.title"),
                        Component.translatable("advancement.jsg.activate_uni_gate.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("activate_universe_gate", JSGCriterions.ACTIVATE_UNIVERSE_GATE.createInstance())
                .save(saver, JSGAdvancements.ACTIVATE_UNIVERSE_GATE, fileHelper);

        Advancement.Builder.advancement()
                .parent(pg_gate)
                .display(
                        JSGItems.UPGRADE_SHIELD.get(),
                        Component.translatable("advancement.jsg.craft_shield.title"),
                        Component.translatable("advancement.jsg.craft_shield.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_shield", RecipeCraftedTrigger.TriggerInstance.craftedItem(JSGMapping.rl(JSG.MOD_ID, "upgrade_shield")))
                .save(saver, JSGAdvancements.CRAFT_SHIELD, fileHelper);

        Advancement.Builder.advancement()
                .parent(mw_gate)
                .display(
                        JSGItems.ICON_KAWOOSH_DEATH.get(),
                        Component.translatable("advancement.jsg.kawoosh_death.title"),
                        Component.translatable("advancement.jsg.kawoosh_death.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("kawoosh_death", JSGCriterions.KAWOOSH_CREMATION.createInstance())
                .save(saver, JSGAdvancements.KAWOOSH_DEATH, fileHelper);

        Advancement.Builder.advancement()
                .parent(mw_gate)
                .display(
                        JSGItems.UPGRADE_IRIS.get(),
                        Component.translatable("advancement.jsg.iris_impact.title"),
                        Component.translatable("advancement.jsg.iris_impact.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("iris_impact", JSGCriterions.IRIS_IMPACT.createInstance())
                .save(saver, JSGAdvancements.IRIS_IMPACT, fileHelper);

        Advancement wormhole = Advancement.Builder.advancement()
                .parent(orlin_gate)
                .display(
                        JSGItems.ICON_WORMHOLE.get(),
                        Component.translatable("advancement.jsg.wormhole_go.title"),
                        Component.translatable("advancement.jsg.wormhole_go.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("wormhole_go", JSGCriterions.WORMHOLE_GO.createInstance())
                .save(saver, JSGAdvancements.WORMHOLE_GO, fileHelper);

        Advancement.Builder.advancement()
                .parent(wormhole)
                .display(
                        Blocks.SKELETON_SKULL,
                        Component.translatable("advancement.jsg.unstable_wormhole.title"),
                        Component.translatable("advancement.jsg.unstable_wormhole.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("unstable_go", JSGCriterions.UNSTABLE_SURVIVE.createInstance())
                .save(saver, JSGAdvancements.UNSTABLE_GO, fileHelper);
        
        Advancement mw_dhd = Advancement.Builder.advancement()
                .parent(CoreAdvancements.NAQUADAH_ALLOY_REFINED)
                .display(
                        JSGBlocks.DHD_MILKYWAY.get(),
                        Component.translatable("advancement.jsg.obtain_mw_dhd.title"),
                        Component.translatable("advancement.jsg.obtain_mw_dhd.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_mw_dhd", InventoryChangeTrigger.TriggerInstance.hasItems(JSGBlocks.DHD_MILKYWAY.get()))
                .save(saver, JSGAdvancements.OBTAIN_MW_DHD, fileHelper);

        Advancement pg_dhd = Advancement.Builder.advancement()
                .parent(mw_dhd)
                .display(
                        JSGBlocks.DHD_PEGASUS.get(),
                        Component.translatable("advancement.jsg.obtain_pg_dhd.title"),
                        Component.translatable("advancement.jsg.obtain_pg_dhd.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_pg_dhd", InventoryChangeTrigger.TriggerInstance.hasItems(JSGBlocks.DHD_PEGASUS.get()))
                .save(saver, JSGAdvancements.OBTAIN_PG_DHD, fileHelper);

        Advancement.Builder.advancement()
                .parent(pg_dhd)
                .display(
                        JSGItems.CRYSTAL_GLYPH_DHD.get(),
                        Component.translatable("advancement.jsg.obtain_dhd_glyph_crystal.title"),
                        Component.translatable("advancement.jsg.obtain_dhd_glyph_crystal.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("get_dhd_glyph_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(JSGItems.CRYSTAL_GLYPH_DHD.get()))
                .save(saver, JSGAdvancements.OBTAIN_DHD_GLYPH_CRYSTAL, fileHelper);
    }


    @ParametersAreNonnullByDefault
    protected static EntityPredicate.Builder getVillagerProfessionPredicate(ResourceLocation professionLocation) {
        return EntityPredicate.Builder.entity().nbt(new NbtPredicate(Util.make(new CompoundTag(), tag -> tag.put("VillagerData", Util.make(new CompoundTag(), dataTag -> dataTag.putString("profession", professionLocation.toString()))))));
    }

    public static ForgeAdvancementProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
        return new ForgeAdvancementProvider(output, registries, helper, List.of(new JSGAdvancementProvider()));
    }
}
