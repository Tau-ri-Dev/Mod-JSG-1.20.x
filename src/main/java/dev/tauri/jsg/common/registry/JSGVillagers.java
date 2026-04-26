package dev.tauri.jsg.common.registry;

import com.google.common.collect.ImmutableSet;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.entity.VillagerTradesBuilder;
import dev.tauri.jsg.common.registry.tags.JSGBiomeTags;
import dev.tauri.jsg.common.registry.tags.JSGStructureTags;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.CoreItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class JSGVillagers {
    private static final DeferredRegister<VillagerProfession> PROFESSION_REGISTER = JSGApi.REGISTRY_HELPER.villagerProfession();
    private static final DeferredRegister<VillagerType> TYPE_REGISTER = JSGApi.REGISTRY_HELPER.villagerType();
    private static final DeferredRegister<PoiType> POI_REGISTER = JSGApi.REGISTRY_HELPER.poi();

    public static final RegistryObject<PoiType> NAQUADAH_CAULDRON_POI = POI_REGISTER.register("naquadah_cauldron", () -> new PoiType(ImmutableSet.copyOf(CoreFluids.MOLTEN_NAQUADAH_RAW.cauldron.get().getStateDefinition().getPossibleStates()), 4, 1));

    public static final RegistryObject<VillagerProfession> PRIEST_DESERT = PROFESSION_REGISTER.register("priest_desert", () -> new VillagerProfession("priest_desert", holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> PRIEST_JUNGLE = PROFESSION_REGISTER.register("priest_jungle", () -> new VillagerProfession("priest_jungle", holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> PRIEST_PLAINS = PROFESSION_REGISTER.register("priest_plains", () -> new VillagerProfession("priest_plains", holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> PRIEST_SAVANNA = PROFESSION_REGISTER.register("priest_savanna", () -> new VillagerProfession("priest_savanna", holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> PRIEST_SNOW = PROFESSION_REGISTER.register("priest_snow", () -> new VillagerProfession("priest_snow", holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> PRIEST_SWAMP = PROFESSION_REGISTER.register("priest_swamp", () -> new VillagerProfession("priest_swamp", holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> PRIEST_TAIGA = PROFESSION_REGISTER.register("priest_taiga", () -> new VillagerProfession("priest_taiga", holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), null));

    public static final RegistryObject<VillagerProfession> SLAVE_MINER = PROFESSION_REGISTER.register("slave_miner", () -> new VillagerProfession("slave_miner", holder -> holder.is(Objects.requireNonNull(NAQUADAH_CAULDRON_POI.getKey())), holder ->
            holder.is(Objects.requireNonNull(NAQUADAH_CAULDRON_POI.getKey())),
            ImmutableSet.of(Items.IRON_PICKAXE, CoreItems.NAQUADAH_ORE_RAW.get()),
            ImmutableSet.copyOf(CoreBlocks.ORE_NAQUADAH.values().stream().map(RegistryObject::get).collect(Collectors.toSet())), null));

    public static final RegistryObject<VillagerType> ABYDOS_TYPE = TYPE_REGISTER.register("abydonian", () -> new VillagerType("abydonian"));


    public static void init() {
        // this stuff here is to initialize the fucking TRADES map BEFORE our custom villager types are registered into the minecraft registry...
        // it's because Mojang thought that if there is type that is not in their fucking default, it's good idea to throw an exception while init of the TRADES map...
        // to actually fix the trades for the FISHERMAN (that is causing the exception), we need than to manually override its trades in the VillagerTradesEvent... well done Mojang
        if (VillagerTrades.TRADES.isEmpty())
            JSG.logger.info("Trades of villagers is empty map!");
    }

    @NotNull
    public static Optional<VillagerType> checkAndGetCustomType(Villager villager) {
        var biome = villager.level().getBiome(villager.blockPosition());
        if (biome.is(JSGBiomeTags.IS_ABYDOS)) {
            return Optional.of(ABYDOS_TYPE.get());
        }
        return Optional.empty();
    }


    public static boolean isPriest(VillagerProfession profession) {
        return profession == PRIEST_DESERT.get() || profession == PRIEST_JUNGLE.get() || profession == PRIEST_PLAINS.get()
                || profession == PRIEST_SAVANNA.get() || profession == PRIEST_SNOW.get() || profession == PRIEST_SWAMP.get() || profession == PRIEST_TAIGA.get();
    }

    public static void registerTrades(VillagerTradesEvent event) {
        VillagerTradesBuilder.create(event)
                .setForLevels(5)
                .setForProfessions(VillagerProfession.CARTOGRAPHER)
                .register((trader, random) -> {
                     if (!(trader.level() instanceof ServerLevel serverLevel))
                        return null;
                     if(serverLevel.dimension() == JSGDimensions.ABYDOS) {
                        var map = ItemHelper.getMapForTarget(JSGStructureTags.ABYDOS_MARKED_ON_MAP, Component.translatable("filled_map.abydos_treasure"), MapDecoration.Type.RED_X, serverLevel, trader.blockPosition());
                        if(map == null) return null;
                        return new MerchantOffer(new ItemStack(Items.FLINT_AND_STEEL), new ItemStack(JSGItems.FOOD_CHOCOLATE_BAR.get(), 24), map, 3, 2, 0.1F);
                     }
                     if(serverLevel.dimension() == Level.OVERWORLD) {
                        var map = ItemHelper.getMapForTarget(JSGStructureTags.OVERWORLD_MARKED_ON_MAP, Component.translatable("filled_map.burried_stargate"), MapDecoration.Type.RED_X, serverLevel, trader.blockPosition());
                        if(map == null) return null;
                        return new MerchantOffer(new ItemStack(Items.BOOKSHELF, 16), new ItemStack(Items.GOLDEN_APPLE, 8), map, 3, 2, 0.1F);
                     }
                     return null;
                })

                .setInput1(new ItemStack(CoreItems.TITANIUM_INGOT.get(), 20))
                .setOutput(new ItemStack(Items.EMERALD, 1))
                .setMaxUses(12)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 1))
                .setInput2(new ItemStack(Items.COCOA_BEANS, 3))
                .setOutput(new ItemStack(JSGItems.FOOD_CHOCOLATE_BAR.get(), 6))
                .setMaxUses(12)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(CoreItems.NAQUADAH_ORE_RAW.get(), 24))
                .setOutput(new ItemStack(Items.EMERALD, 1))
                .setMaxUses(12)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(Items.PAPER,4))
                .setOutput(new ItemStack(CoreItems.NOTEBOOK_PAGE_EMPTY.get(), 8))
                .setMaxUses(12)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 2))
                .setOutput(new ItemStack(Items.CALCITE,12))
                .setMaxUses(12)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 1))
                .setOutput(new ItemStack(Items.APPLE,4))
                .setMaxUses(12)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 1))
                .setInput2(new ItemStack(CoreItems.FOOD_LEMON.get(),6))
                .setOutput(new ItemStack(Items.MUSHROOM_STEW,1))
                .setMaxUses(5)
                .setXpPerTrade(15)
                .setPriceMul(0.05f)
                .setForLevels(2)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.CALCITE,4))
                .setInput2(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(CoreItems.BLACK_CHALK.get(),1))
                .setMaxUses(12)
                .setXpPerTrade(10)
                .setPriceMul(0.02f)
                .setForLevels(3)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,6))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_PEGASUS.get(),4))
                .setOutput(new ItemStack(JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD.get(),1))
                .setMaxUses(6)
                .setXpPerTrade(20)
                .setPriceMul(0.02f)
                .setForLevels(3)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(Items.BREAD,6))
                .setMaxUses(12)
                .setXpPerTrade(10)
                .setPriceMul(0.02f)
                .setForLevels(3)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.IRON_INGOT, 8))
                .setInput2(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(Items.CAULDRON,1))
                .setMaxUses(12)
                .setXpPerTrade(15)
                .setPriceMul(0.02f)
                .setForLevels(4)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,4))
                .setInput2(new ItemStack(CoreItems.TRINIUM_INGOT.get(),2))
                .setOutput(new ItemStack(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get(),1))
                .setMaxUses(12)
                .setXpPerTrade(15)
                .setPriceMul(0.02f)
                .setForLevels(4)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 4))
                .setInput2(new ItemStack(CoreItems.NAQUADAH_ALLOY.get(),3))
                .setOutput(new ItemStack(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get(),1))
                .setMaxUses(12)
                .setXpPerTrade(15)
                .setPriceMul(0.02f)
                .setForLevels(4)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,4))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_ALLOY.get(),3))
                .setMaxUses(10)
                .setXpPerTrade(10)
                .setPriceMul(0.02f)
                .setForLevels(4)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 4))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_RED.get(),1))
                .setMaxUses(10)
                .setXpPerTrade(30)
                .setPriceMul(0.02f)
                .setForLevels(5)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,2))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_RED.get(),1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_RED_SMALL.get(),2))
                .setMaxUses(12)
                .setXpPerTrade(30)
                .setPriceMul(0.02f)
                .setForLevels(5)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 3))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_PEGASUS.get(),2))
                .setMaxUses(4)
                .setXpPerTrade(30)
                .setPriceMul(0.02f)
                .setForLevels(5)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,2))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_PEGASUS.get(),1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_PEGASUS_SMALL.get(),1))
                .setMaxUses(8)
                .setXpPerTrade(30)
                .setPriceMul(0.02f)
                .setForLevels(5)
                .setPriestOnly()
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_ENDER.get(),2))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_PLAINS.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(CoreItems.TRINIUM_NUGGET.get(),8))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_PLAINS.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,3))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_ENDER.get(),1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_ENDER_SMALL.get(),2))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.2f)
                .setForLevels(2)
                .setForProfessions(PRIEST_PLAINS.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_YELLOW.get(),3))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_DESERT.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_YELLOW.get(),1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_YELLOW_SMALL.get(),2))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_DESERT.get())
                .register()

                .setInput1(new ItemStack(Items.IRON_INGOT, 6))
                .setInput2(new ItemStack(CoreItems.TITANIUM_NUGGET.get(),1))
                .setOutput(new ItemStack(CoreItems.JSG_WRENCH.get(),1))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_DESERT.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 2))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_WHITE.get(),3))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SAVANNA.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,2))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_WHITE.get(),1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_WHITE_SMALL.get(),3))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SAVANNA.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD, 1))
                .setInput2(new ItemStack(Items.WHEAT,48))
                .setOutput(new ItemStack(CoreItems.FOOD_LEMON.get(),1))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SAVANNA.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_BLUE.get(),4))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_TAIGA.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_BLUE.get(),1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_BLUE_SMALL.get(),2))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_TAIGA.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(Items.COPPER_INGOT,16))
                .setOutput(new ItemStack(CoreItems.COPPER_INGOT_WEATHERED.get(),8))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_TAIGA.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,2))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_BLUE.get(),3))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SNOW.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_BLUE.get(),1))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_BLUE_SMALL.get(),2))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SNOW.get())
                .register()

                .setInput1(new ItemStack(Items.COPPER_INGOT,16))
                .setInput2(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(CoreItems.COPPER_INGOT_OXIDIZED.get(),12))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SNOW.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,2))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_ENDER.get(),4))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_JUNGLE.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(CoreItems.CRYSTAL_ENDER.get(),2))
                .setOutput(new ItemStack(CoreItems.CRYSTAL_ENDER_SMALL.get(),4))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_JUNGLE.get())
                .register()

                .setInput1(new ItemStack(Items.IRON_INGOT,32))
                .setOutput(new ItemStack(CoreItems.MORTAR_AND_PESTLE.get(),1))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_JUNGLE.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(Items.WHEAT,8))
                .setOutput(new ItemStack(Items.COCOA_BEANS,2))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_JUNGLE.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setInput2(new ItemStack(Items.COPPER_INGOT,4))
                .setOutput(new ItemStack(CoreItems.COPPER_INGOT_OXIDIZED.get(),8))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SWAMP.get())
                .register()

                .setInput1(new ItemStack(Items.EMERALD,1))
                .setOutput(new ItemStack(CoreItems.JSG_HAMMER.get(),1))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SWAMP.get())
                .register()

                .setInput1(new ItemStack(CoreItems.CRYSTAL_RED.get(),4))
                .setInput2(new ItemStack(Items.EMERALD,3))
                .setOutput(new ItemStack(JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD.get(),1))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(PRIEST_SWAMP.get())
                .register()

                //Slave Miner
                .setInput1(new ItemStack(Items.CARROT,4))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_RAW_NUGGET.get(), 1))
                .setMaxUses(8)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.SWEET_BERRIES, 6))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_RAW_DUST.get(),2))
                .setMaxUses(6)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.APPLE, 8))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_ORE_RAW.get(), 3))
                .setMaxUses(10)
                .setXpPerTrade(1)
                .setPriceMul(0.02f)
                .setForLevels(1)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.COOKIE, 3))
                .setOutput(new ItemStack(Items.STONE_SHOVEL,1))
                .setMaxUses(5)
                .setXpPerTrade(15)
                .setPriceMul(0.05f)
                .setForLevels(2)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.BEETROOT,20))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_RAW_DUST.get(),2))
                .setMaxUses(5)
                .setXpPerTrade(12)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.POTATO,16))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_RAW_NUGGET.get(),2))
                .setMaxUses(6)
                .setXpPerTrade(8)
                .setPriceMul(0.02f)
                .setForLevels(2)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.BREAD,3))
                .setOutput(new ItemStack(Items.STONE_PICKAXE,1))
                .setMaxUses(12)
                .setXpPerTrade(10)
                .setPriceMul(0.02f)
                .setForLevels(3)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.COOKED_CHICKEN,4))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_ORE_RAW.get(),2))
                .setMaxUses(6)
                .setXpPerTrade(20)
                .setPriceMul(0.02f)
                .setForLevels(3)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.COOKED_RABBIT,6))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_RAW_NUGGET.get(),27))
                .setMaxUses(12)
                .setXpPerTrade(10)
                .setPriceMul(0.02f)
                .setForLevels(3)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.COOKED_MUTTON,12))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_RAW_DUST.get(),4))
                .setMaxUses(12)
                .setXpPerTrade(15)
                .setPriceMul(0.02f)
                .setForLevels(4)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.COOKED_BEEF, 6))
                .setOutput(new ItemStack(Items.IRON_SHOVEL,1))
                .setMaxUses(8)
                .setXpPerTrade(15)
                .setPriceMul(0.02f)
                .setForLevels(4)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.COOKED_PORKCHOP,8))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_RAW_DUST.get(),3))
                .setMaxUses(10)
                .setXpPerTrade(10)
                .setPriceMul(0.02f)
                .setForLevels(4)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.CAKE,1))
                .setOutput(new ItemStack(CoreBlocks.NAQUADAH_RAW_BLOCK.get(),1))
                .setMaxUses(10)
                .setXpPerTrade(20)
                .setPriceMul(0.02f)
                .setForLevels(5)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(Items.PUMPKIN_PIE, 16))
                .setOutput(new ItemStack(Items.IRON_PICKAXE,1))
                .setMaxUses(4)
                .setXpPerTrade(32)
                .setPriceMul(0.02f)
                .setForLevels(5)
                .setForProfessions(SLAVE_MINER.get())
                .register()

                .setInput1(new ItemStack(JSGItems.FOOD_CHOCOLATE_BAR.get(),4))
                .setOutput(new ItemStack(CoreItems.NAQUADAH_ALLOY_RAW.get(),2))
                .setMaxUses(8)
                .setXpPerTrade(30)
                .setPriceMul(0.02f)
                .setForLevels(5)
                .setForProfessions(SLAVE_MINER.get())
                .register();
    }
}
