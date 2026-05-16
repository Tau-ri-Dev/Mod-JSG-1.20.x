package dev.tauri.jsg.datagen;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.tag.CoreItemTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class JSGRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public JSGRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        //Machine and Devices
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGBlocks.TOASTER.get())
                .group("jsg:toaster")
                .pattern("###")
                .pattern("SCS")
                .pattern("III")
                .define('#', Blocks.SMOOTH_STONE_SLAB)
                .define('S', Tags.Items.STONE)
                .define('C', Tags.Items.INGOTS_COPPER)
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_stone_slab", has(Blocks.SMOOTH_STONE_SLAB))
                .unlockedBy("has_stone", has(Tags.Items.STONE))
                .unlockedBy("has_copper_ingot", has(Tags.Items.INGOTS_COPPER))
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGBlocks.PRINTER.get())
                .group("jsg:printer")
                .pattern("PGP")
                .pattern("RHT")
                .pattern("BDB")
                .define('P', CoreItemTags.PLATE_TITANIUM)
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('H', Items.HOPPER)
                .define('T', CoreItems.GEAR_TITANIUM.get())
                .define('B', Tags.Items.DYES_CYAN)
                .define('D', Tags.Items.DYES_GRAY)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_TITANIUM))
                .unlockedBy("has_cyan_dye", has(Tags.Items.DYES_CYAN))
                .unlockedBy("has_gray_dye", has(Tags.Items.DYES_GRAY))
                .save(pWriter);

        //Cartridges
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CARTRIDGE_BLACK.get())
                .group("jsg:cartridge")
                .pattern(" X ")
                .pattern("XBX")
                .pattern("GRG")
                .define('X', Tags.Items.DYES_BLACK)
                .define('B', Items.GLASS_BOTTLE)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_color", has(Tags.Items.DYES_BLACK))
                .unlockedBy("has_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_nugget", has(Tags.Items.NUGGETS_GOLD))
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CARTRIDGE_CYAN.get())
                .group("jsg:cartridge")
                .pattern(" X ")
                .pattern("XBX")
                .pattern("GRG")
                .define('X', Tags.Items.DYES_CYAN)
                .define('B', Items.GLASS_BOTTLE)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_color", has(Tags.Items.DYES_CYAN))
                .unlockedBy("has_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_nugget", has(Tags.Items.NUGGETS_GOLD))
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CARTRIDGE_MAGENTA.get())
                .group("jsg:cartridge")
                .pattern(" X ")
                .pattern("XBX")
                .pattern("GRG")
                .define('X', Tags.Items.DYES_MAGENTA)
                .define('B', Items.GLASS_BOTTLE)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_color", has(Tags.Items.DYES_MAGENTA))
                .unlockedBy("has_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_nugget", has(Tags.Items.NUGGETS_GOLD))
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CARTRIDGE_YELLOW.get())
                .group("jsg:cartridge")
                .pattern(" X ")
                .pattern("XBX")
                .pattern("GRG")
                .define('X', Tags.Items.DYES_YELLOW)
                .define('B', Items.GLASS_BOTTLE)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_color", has(Tags.Items.DYES_YELLOW))
                .unlockedBy("has_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_nugget", has(Tags.Items.NUGGETS_GOLD))
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(pWriter);

        //Stargate blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get())
                .group("jsg:orlin_member_block")
                .pattern("PCP")
                .pattern("CRC")
                .pattern("PCP")
                .define('P', Items.PAPER)
                .define('C', Tags.Items.INGOTS_COPPER)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .unlockedBy("has_paper", has(Items.PAPER))
                .unlockedBy("has_copper", has(Tags.Items.INGOTS_COPPER))
                .unlockedBy("has_redstone", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK.get())
                .group("jsg:milkyway_base_block")
                .pattern("RFE")
                .pattern("NBC")
                .pattern("GFG")
                .define('R', CoreItemTags.GEM_RED)
                .define('F', JSGItems.FRAGMENT_MILKYWAY.get())
                .define('E', CoreItemTags.GEM_ENDER)
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('B', CoreBlocks.CAPACITOR_BLOCK.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('G', CoreItemTags.GEAR_NAQUADAH_ALLOY)
                .unlockedBy("has_red_crystal", has(CoreItemTags.GEM_RED))
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_MILKYWAY.get()))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER))
                .unlockedBy("has_circuit_naquadah", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_capacitor", has(CoreBlocks.CAPACITOR_BLOCK.get()))
                .unlockedBy("has_circuit_crystal", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_MILKYWAY_CHEVRON_BLOCK.get())
                .group("jsg:milkyway_chevron_block")
                .pattern("CGC")
                .pattern("CPC")
                .pattern("PCP")
                .define('C', CoreItemTags.GEM_RED)
                .define('G', Tags.Items.GLASS_RED)
                .define('P', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_RED))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_RED))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_MILKYWAY_RING_BLOCK.get())
                .group("jsg:milkyway_ring_block")
                .pattern("###")
                .pattern("PPP")
                .pattern("###")
                .define('#', JSGItems.FRAGMENT_MILKYWAY.get())
                .define('P', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_MILKYWAY.get()))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.get())
                .group("jsg:pegasus_base_block")
                .pattern("PFE")
                .pattern("NBC")
                .pattern("GFG")
                .define('P', CoreItemTags.GEM_PEGASUS)
                .define('F', JSGItems.FRAGMENT_PEGASUS.get())
                .define('E', CoreItemTags.GEM_ENDER)
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('B', CoreBlocks.CAPACITOR_BLOCK.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('G', CoreItemTags.GEAR_NAQUADAH_ALLOY)
                .unlockedBy("has_pegasus_crystal", has(CoreItemTags.GEM_PEGASUS))
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_PEGASUS.get()))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER))
                .unlockedBy("has_circuit_naquadah", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_capacitor", has(CoreBlocks.CAPACITOR_BLOCK.get()))
                .unlockedBy("has_circuit_crystal", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_PEGASUS_CHEVRON_BLOCK.get())
                .group("jsg:pegasus_chevron_block")
                .pattern("CGC")
                .pattern("CPC")
                .pattern("PCP")
                .define('C', CoreItemTags.GEM_PEGASUS)
                .define('G', Tags.Items.GLASS_LIGHT_BLUE)
                .define('P', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_PEGASUS))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_LIGHT_BLUE))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_PEGASUS_RING_BLOCK.get())
                .group("jsg:pegasus_ring_block")
                .pattern("###")
                .pattern("PPP")
                .pattern("###")
                .define('#', JSGItems.FRAGMENT_PEGASUS.get())
                .define('P', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_PEGASUS.get()))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get())
                .group("jsg:universe_base_block")
                .pattern("WFE")
                .pattern("NBC")
                .pattern("GFG")
                .define('W', CoreItemTags.GEM_WHITE)
                .define('F', JSGItems.FRAGMENT_UNIVERSE.get())
                .define('E', CoreItemTags.GEM_ENDER)
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('B', CoreBlocks.CAPACITOR_BLOCK.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('G', CoreItemTags.GEAR_NAQUADAH)
                .unlockedBy("has_white_crystal", has(CoreItemTags.GEM_WHITE))
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_UNIVERSE.get()))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER))
                .unlockedBy("has_circuit_naquadah", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_capacitor", has(CoreBlocks.CAPACITOR_BLOCK.get()))
                .unlockedBy("has_circuit_crystal", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_NAQUADAH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_UNIVERSE_CHEVRON_BLOCK.get())
                .group("jsg:universe_chevron_block")
                .pattern("CGC")
                .pattern("CNC")
                .pattern("TCT")
                .define('C', CoreItemTags.GEM_WHITE)
                .define('G', Tags.Items.GLASS_WHITE)
                .define('N', CoreItemTags.PLATE_NAQUADAH)
                .define('T', CoreItemTags.PLATE_TITANIUM)
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_WHITE))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_LIGHT_BLUE))
                .unlockedBy("has_naquadah_plate", has(CoreItemTags.PLATE_NAQUADAH))
                .unlockedBy("has_titanium_plate", has(CoreItemTags.PLATE_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_UNIVERSE_RING_BLOCK.get())
                .group("jsg:universe_ring_block")
                .pattern("###")
                .pattern("PCP")
                .pattern("###")
                .define('#', JSGItems.FRAGMENT_UNIVERSE.get())
                .define('P', CoreItemTags.PLATE_NAQUADAH)
                .define('C', CoreItemTags.GEM_WHITE)
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_UNIVERSE.get()))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH))
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_WHITE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK.get())
                .group("jsg:tollan_base_block")
                .pattern("LFE")
                .pattern("NBC")
                .pattern("GFG")
                .define('L', CoreItemTags.GEM_BLUE)
                .define('F', JSGItems.FRAGMENT_TOLLAN.get())
                .define('E', CoreItemTags.GEM_ENDER)
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('B', CoreBlocks.CAPACITOR_BLOCK.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('G', CoreItemTags.GEAR_TITANIUM)
                .unlockedBy("has_blue_crystal", has(CoreItemTags.GEM_BLUE))
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_TOLLAN.get()))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER))
                .unlockedBy("has_circuit_naquadah", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_capacitor", has(CoreBlocks.CAPACITOR_BLOCK.get()))
                .unlockedBy("has_circuit_crystal", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_TOLLAN_CHEVRON_BLOCK.get())
                .group("jsg:tollan_chevron_block")
                .pattern("#B#")
                .pattern("#B#")
                .pattern("###")
                .define('#', CoreItemTags.GEM_BLUE)
                .define('B', CoreItemTags.PLATE_TITANIUM)
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_WHITE))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_TOLLAN_RING_BLOCK.get())
                .group("jsg:tollan_ring_block")
                .pattern("###")
                .pattern("PPP")
                .pattern("###")
                .define('#', JSGItems.FRAGMENT_TOLLAN.get())
                .define('P', CoreItemTags.PLATE_NAQUADAH)
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_TOLLAN.get()))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_MOVIE_BASE_BLOCK.get())
                .group("jsg:movie_base_block")
                .pattern("WFE")
                .pattern("NBC")
                .pattern("GFG")
                .define('W', CoreItemTags.GEM_WHITE)
                .define('F', JSGItems.FRAGMENT_MILKYWAY.get())
                .define('E', CoreItemTags.GEM_ENDER)
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('B', CoreBlocks.CAPACITOR_BLOCK.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('G', CoreItemTags.GEAR_NAQUADAH)
                .unlockedBy("has_white_crystal", has(CoreItemTags.GEM_WHITE))
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_MILKYWAY.get()))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER))
                .unlockedBy("has_circuit_naquadah", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_capacitor", has(CoreBlocks.CAPACITOR_BLOCK.get()))
                .unlockedBy("has_circuit_crystal", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_NAQUADAH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_MOVIE_CHEVRON_BLOCK.get())
                .group("jsg:movie_chevron_block")
                .pattern("RGR")
                .pattern("RNR")
                .pattern("NRN")
                .define('R', CoreItemTags.PLATE_NAQUADAH)
                .define('G', Tags.Items.GLASS_BLACK)
                .define('N', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLACK))
                .unlockedBy("has_raw_naquadah_plate", has(CoreItemTags.PLATE_NAQUADAH))
                .unlockedBy("has_naquadah_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.STARGATE_MOVIE_RING_BLOCK.get())
                .group("jsg:movie_ring_block")
                .pattern("###")
                .pattern("PPP")
                .pattern("###")
                .define('#', JSGItems.FRAGMENT_MILKYWAY.get())
                .define('P', CoreItemTags.PLATE_NAQUADAH)
                .unlockedBy("has_fragment", has(JSGItems.FRAGMENT_MILKYWAY.get()))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH))
                .save(pWriter);

        //Redstone IO
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, JSGBlocks.SG_REDSTONE_DIALER_I_BLOCK.get())
                .group("jsg:redstone_stargate_input")
                .pattern("SPS")
                .pattern("PCP")
                .pattern("RON")
                .define('S', Tags.Items.STONE)
                .define('P', Tags.Items.GLASS_PANES_COLORLESS)
                .define('C', CoreItemTags.GEM_PEGASUS)
                .define('R', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('O', Items.COMPARATOR)
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .unlockedBy("has_stone", has(Tags.Items.STONE))
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_PEGASUS))
                .unlockedBy("has_crystal_circuit", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_comparator", has(Items.COMPARATOR))
                .unlockedBy("has_naquadah_circuit", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, JSGBlocks.SG_REDSTONE_STATE_O_BLOCK.get())
                .group("jsg:redstone_stargate_output")
                .pattern(" E ")
                .pattern("RCY")
                .define('E', CoreItemTags.GEM_ENDER_SMALL)
                .define('R', CoreItemTags.GEM_RED_SMALL)
                .define('C', Items.COMPARATOR)
                .define('Y', CoreItemTags.GEM_YELLOW_SMALL)
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER_SMALL))
                .unlockedBy("has_red_crystal", has(CoreItemTags.GEM_RED_SMALL))
                .unlockedBy("has_yellow_crystal", has(CoreItemTags.GEM_YELLOW_SMALL))
                .unlockedBy("has_comparator", has(Items.COMPARATOR))
                .save(pWriter);

        //DHDs
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.DHD_MILKYWAY.get())
                .group("jsg:milkyway_dhd")
                .pattern("PBP")
                .pattern("CHN")
                .pattern("III")
                .define('P', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .define('B', JSGItems.DHD_BRB.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('H', JSGItems.HOLDER_CRYSTAL.get())
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('I', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .unlockedBy("has_button", has(JSGItems.DHD_BRB.get()))
                .unlockedBy("has_crystal_circuit", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_crystal_holder", has(JSGItems.HOLDER_CRYSTAL.get()))
                .unlockedBy("has_naquadah_circuit", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_naquadah_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, JSGBlocks.DHD_PEGASUS.get())
                .group("jsg:pegasus_dhd")
                .pattern("PBP")
                .pattern("CHN")
                .pattern("III")
                .define('P', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .define('B', JSGItems.DHD_BBB.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('H', JSGItems.HOLDER_CRYSTAL_PEGASUS.get())
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('I', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .unlockedBy("has_button", has(JSGItems.DHD_BBB.get()))
                .unlockedBy("has_crystal_circuit", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_crystal_holder", has(JSGItems.HOLDER_CRYSTAL_PEGASUS.get()))
                .unlockedBy("has_naquadah_circuit", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_naquadah_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD.get())
                .group("jsg:control_crystal_dhd_milkyway")
                .pattern(" BS")
                .pattern("BCB")
                .pattern("SB ")
                .define('B', CoreItemTags.GEM_RED)
                .define('S', CoreItemTags.GEM_RED_SMALL)
                .define('C', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .unlockedBy("has_small_crystal", has(CoreItemTags.GEM_RED_SMALL))
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_RED))
                .unlockedBy("has_circuit", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD.get())
                .group("jsg:control_crystal_dhd_pegasus")
                .pattern(" BS")
                .pattern("BCB")
                .pattern("SB ")
                .define('B', CoreItemTags.GEM_PEGASUS)
                .define('S', CoreItemTags.GEM_PEGASUS_SMALL)
                .define('C', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .unlockedBy("has_small_crystal", has(CoreItemTags.GEM_PEGASUS_SMALL))
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_PEGASUS))
                .unlockedBy("has_circuit", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .save(pWriter);

        //Handheld devices
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, JSGItems.UNIVERSE_DIALER.get())
                .group("jsg:universe_dialer")
                .pattern("21B")
                .pattern("3PB")
                .pattern("2CB")
                .define('1', CoreItems.COPPER_INGOT_EXPOSED.get())
                .define('2', CoreItems.COPPER_INGOT_WEATHERED.get())
                .define('3', CoreItems.COPPER_INGOT_OXIDIZED.get())
                .define('P', Tags.Items.GLASS_PANES_BLACK)
                .define('C', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('B', Items.STONE_BUTTON)
                .unlockedBy("has_exposed_copper", has(CoreItems.COPPER_INGOT_EXPOSED.get()))
                .unlockedBy("has_weathered_copper", has(CoreItems.COPPER_INGOT_WEATHERED.get()))
                .unlockedBy("has_oxidized_copper", has(CoreItems.COPPER_INGOT_OXIDIZED.get()))
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_BLACK))
                .unlockedBy("has_circuit", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_button", has(Items.STONE_BUTTON))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, JSGItems.GDO.get())
                .group("jsg:gdo")
                .pattern("IDL")
                .pattern("BPI")
                .pattern("RDI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('D', Tags.Items.DYES_BLACK)
                .define('L', Items.LIGHTNING_ROD)
                .define('B', Items.STONE_BUTTON)
                .define('P', Tags.Items.GLASS_PANES)
                .define('R', Items.REPEATER)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_black_dye", has(Tags.Items.DYES_BLACK))
                .unlockedBy("has_rod", has(Items.LIGHTNING_ROD))
                .unlockedBy("has_button", has(Items.STONE_BUTTON))
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES))
                .unlockedBy("has_repeater", has(Items.REPEATER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, JSGItems.SHIELD_EMITTER.get())
                .group("jsg:shield_emitter")
                .pattern("TET")
                .pattern("ECE")
                .pattern("TET")
                .define('T', CoreItemTags.INGOT_TRINIUM)
                .define('E', CoreItemTags.GEM_ENDER)
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_TRINIUM))
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_ENDER))
                .unlockedBy("has_circuit", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .save(pWriter);

        //upgrades
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CRYSTAL_GLYPH_DHD.get())
                .group("jsg:crystal_glyph_dhd")
                .pattern("# #")
                .pattern("PRE")
                .pattern("###")
                .define('#', Tags.Items.GLASS_PANES_COLORLESS)
                .define('P', CoreItemTags.GEM_PEGASUS_SMALL)
                .define('R', CoreItemTags.GEM_RED_SMALL)
                .define('E', CoreItemTags.GEM_ENDER_SMALL)
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES))
                .unlockedBy("has_pegasus_crystal", has(CoreItemTags.GEM_PEGASUS_SMALL))
                .unlockedBy("has_red_crystal", has(CoreItemTags.GEM_RED_SMALL))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER_SMALL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CRYSTAL_GLYPH_STARGATE.get())
                .group("jsg:stargate_glyph_crystal")
                .pattern("#B#")
                .pattern("YRE")
                .pattern("###")
                .define('#', Tags.Items.GLASS_PANES_COLORLESS)
                .define('B', CoreItemTags.GEM_BLUE_SMALL)
                .define('Y', CoreItemTags.GEM_YELLOW)
                .define('R', CoreItemTags.GEM_RED_SMALL)
                .define('E', CoreItemTags.GEM_ENDER_SMALL)
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_blue_crystal", has(CoreItemTags.GEM_BLUE_SMALL))
                .unlockedBy("has_yellow_crystal", has(CoreItemTags.GEM_YELLOW))
                .unlockedBy("has_red_crystal", has(CoreItemTags.GEM_RED_SMALL))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CRYSTAL_GLYPH_MILKYWAY.get())
                .group("jsg:stargate_milkyway_glyph_crystal")
                .pattern("#S#")
                .pattern("BSB")
                .pattern("###")
                .define('#', Tags.Items.GLASS_PANES_COLORLESS)
                .define('S', CoreItemTags.GEM_RED_SMALL)
                .define('B', CoreItemTags.GEM_RED)
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_small_red_crystal", has(CoreItemTags.GEM_RED_SMALL))
                .unlockedBy("has_red_crystal", has(CoreItemTags.GEM_RED))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CRYSTAL_GLYPH_PEGASUS.get())
                .group("jsg:stargate_pegasus_glyph_crystal")
                .pattern("#S#")
                .pattern("BML")
                .pattern("###")
                .define('#', Tags.Items.GLASS_PANES_COLORLESS)
                .define('S', CoreItemTags.GEM_BLUE_SMALL)
                .define('B', CoreItemTags.GEM_BLUE)
                .define('M', CoreItemTags.GEM_PEGASUS_SMALL)
                .define('L', CoreItemTags.GEM_PEGASUS)
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_small_blue_crystal", has(CoreItemTags.GEM_BLUE_SMALL))
                .unlockedBy("has_blue_crystal", has(CoreItemTags.GEM_BLUE))
                .unlockedBy("has_small_pegasus_crystal", has(CoreItemTags.GEM_PEGASUS_SMALL))
                .unlockedBy("has_pegasus_crystal", has(CoreItemTags.GEM_PEGASUS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.CRYSTAL_GLYPH_UNIVERSE.get())
                .group("jsg:stargate_universe_glyph_crystal")
                .pattern("#S#")
                .pattern("BSB")
                .pattern("###")
                .define('#', Tags.Items.GLASS_PANES_COLORLESS)
                .define('S', CoreItemTags.GEM_WHITE_SMALL)
                .define('B', CoreItemTags.GEM_WHITE)
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_small_white_crystal", has(CoreItemTags.GEM_WHITE_SMALL))
                .unlockedBy("has_white_crystal", has(CoreItemTags.GEM_WHITE))
                .save(pWriter);

        //IRIS
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.UPGRADE_IRIS.get())
                .group("jsg:iris_upgrade")
                .pattern("#X#")
                .pattern("X X")
                .pattern("#X#")
                .define('#', CoreItemTags.GEAR_TITANIUM)
                .define('X', JSGItems.QUAD_IRIS_BLADE.get())
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_TITANIUM))
                .unlockedBy("has_quad_blade", has(JSGItems.QUAD_IRIS_BLADE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.UPGRADE_IRIS_TRINIUM.get())
                .group("jsg:iris_upgrade")
                .pattern("#X#")
                .pattern("X X")
                .pattern("#X#")
                .define('#', CoreItemTags.GEAR_TRINIUM)
                .define('X', JSGItems.QUAD_IRIS_BLADE_TRINIUM.get())
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_TRINIUM))
                .unlockedBy("has_quad_blade", has(JSGItems.QUAD_IRIS_BLADE_TRINIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.UPGRADE_SHIELD.get())
                .group("jsg:iris_upgrade")
                .pattern("IEI")
                .pattern("CSN")
                .pattern("OHO")
                .define('I', Tags.Items.INGOTS_NETHERITE)
                .define('E', JSGItems.SHIELD_EMITTER.get())
                .define('C', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('S', Tags.Items.NETHER_STARS)
                .define('N', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .define('O', Items.COMPARATOR)
                .define('H', Items.ECHO_SHARD)
                .unlockedBy("has_netherite_ingot", has(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy("has_shield_emmiter", has(JSGItems.SHIELD_EMITTER.get()))
                .unlockedBy("has_circuit_crystal", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .unlockedBy("has_nether_star", has(Tags.Items.NETHER_STARS))
                .unlockedBy("has_circuit_naquadah", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .unlockedBy("has_comparator", has(Items.COMPARATOR))
                .unlockedBy("has_echo_shard", has(Items.ECHO_SHARD))
                .save(pWriter);

        //Crafting elements
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.FRAGMENT_MILKYWAY.get())
                .group("jsg:stargate_fragments")
                .pattern("###")
                .pattern("XXX")
                .pattern("###")
                .define('#', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .define('X', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.FRAGMENT_PEGASUS.get())
                .group("jsg:stargate_fragments")
                .pattern("###")
                .pattern("XXX")
                .pattern("###")
                .define('#', CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .define('X', CoreItemTags.INGOT_TRINIUM)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH_ALLOY))
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_TRINIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.FRAGMENT_UNIVERSE.get())
                .group("jsg:stargate_fragments")
                .pattern("###")
                .pattern("XXX")
                .pattern("###")
                .define('#', CoreItemTags.PLATE_NAQUADAH)
                .define('X', CoreItemTags.INGOT_TITANIUM)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_NAQUADAH))
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.FRAGMENT_TOLLAN.get())
                .group("jsg:stargate_fragments")
                .pattern("###")
                .pattern("XXX")
                .pattern("###")
                .define('#', CoreItemTags.PLATE_TITANIUM)
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_ingot", has(Tags.Items.INGOTS_IRON))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.IRIS_BLADE.get())
                .group("jsg:iris_blades")
                .pattern("#X ")
                .pattern(" # ")
                .pattern(" # ")
                .define('#', CoreItemTags.PLATE_TITANIUM)
                .define('X', CoreItemTags.NUGGET_TITANIUM)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.QUAD_IRIS_BLADE.get())
                .group("jsg:iris_quad_blades")
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .define('#', JSGItems.IRIS_BLADE.get())
                .define('X', CoreItemTags.GEAR_TITANIUM)
                .unlockedBy("has_blade", has(JSGItems.IRIS_BLADE.get()))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.IRIS_BLADE_TRINIUM.get())
                .group("jsg:iris_blades")
                .pattern("#X ")
                .pattern(" # ")
                .pattern(" # ")
                .define('#', CoreItemTags.PLATE_TRINIUM)
                .define('X', CoreItemTags.NUGGET_TRINIUM)
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TRINIUM))
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TRINIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.QUAD_IRIS_BLADE_TRINIUM.get())
                .group("jsg:iris_quad_blades")
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .define('#', JSGItems.IRIS_BLADE_TRINIUM.get())
                .define('X', CoreItemTags.GEAR_TRINIUM)
                .unlockedBy("has_blade", has(JSGItems.IRIS_BLADE_TRINIUM.get()))
                .unlockedBy("has_gear", has(CoreItemTags.GEAR_TRINIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.HOLDER_CRYSTAL.get())
                .group("jsg:crystal_holder")
                .pattern("NCN")
                .pattern("PIP")
                .pattern(" P ")
                .define('N', CoreItemTags.NUGGET_TITANIUM)
                .define('C', CoreItemTags.GEM_RED)
                .define('P', CoreItemTags.PLATE_TITANIUM)
                .define('I', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_RED))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.HOLDER_CRYSTAL_PEGASUS.get())
                .group("jsg:crystal_holder")
                .pattern("NCN")
                .pattern("PIP")
                .pattern(" P ")
                .define('N', CoreItemTags.NUGGET_TITANIUM)
                .define('C', CoreItemTags.GEM_PEGASUS)
                .define('P', CoreItemTags.PLATE_TITANIUM)
                .define('I', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .unlockedBy("has_crystal", has(CoreItemTags.GEM_PEGASUS))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.DHD_BRB.get())
                .group("jsg:dhd_main_buttons")
                .pattern("X#X")
                .pattern("#G#")
                .pattern("X#X")
                .define('X', CoreItemTags.NUGGET_TITANIUM)
                .define('#', CoreItemTags.PLATE_TITANIUM)
                .define('G', Tags.Items.GLASS_PANES_RED)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_RED))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JSGItems.DHD_BBB.get())
                .group("jsg:dhd_main_buttons")
                .pattern("X#X")
                .pattern("#G#")
                .pattern("X#X")
                .define('X', CoreItemTags.NUGGET_TITANIUM)
                .define('#', CoreItemTags.PLATE_TITANIUM)
                .define('G', Tags.Items.GLASS_PANES_CYAN)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .unlockedBy("has_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES_CYAN))
                .save(pWriter);

        //Food
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, JSGItems.FOOD_CHOCOLATE_BAR.get())
                .group("jsg:chocolate_bar")
                .pattern("###")
                .pattern("COC")
                .pattern("###")
                .define('#', Items.PAPER)
                .define('C', Items.COCOA_BEANS)
                .define('O', Items.COOKIE)
                .unlockedBy("has_paper", has(Items.PAPER))
                .unlockedBy("has_cocoa", has(Items.COCOA_BEANS))
                .unlockedBy("has_cookie", has(Items.COOKIE))
                .save(pWriter);
    }


    @ParametersAreNonnullByDefault
    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    @ParametersAreNonnullByDefault
    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    @ParametersAreNonnullByDefault
    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for (ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, JSG.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
