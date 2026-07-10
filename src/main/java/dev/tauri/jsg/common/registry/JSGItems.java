package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.Constants;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.dialhomedevice.StargateDHD;
import dev.tauri.jsg.api.stargate.StargateUpgrade;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.common.item.CartridgeItem;
import dev.tauri.jsg.common.item.admincontroller.AdminControllerItem;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.common.item.linkable.gdo.GDOItem;
import dev.tauri.jsg.common.item.stargate.IrisItem;
import dev.tauri.jsg.common.item.stargate.dialhomedevice.part.DHDAbstractPartItem;
import dev.tauri.jsg.common.item.stargate.dialhomedevice.part.DHDFluidTankItem;
import dev.tauri.jsg.common.item.stargate.dialhomedevice.part.DHDMilkyWayButtonsConsoleItem;
import dev.tauri.jsg.common.item.stargate.dialhomedevice.part.DHDPegasusButtonsConsoleItem;
import dev.tauri.jsg.core.common.integration.Integrations;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.item.JSGMusicDiscItem;
import dev.tauri.jsg.core.common.item.JSGSpawnEggItem;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.core.common.registry.helper.CoreRegistryHelpers;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import dev.tauri.jsg.core.common.registry.JSGDeferredRegister;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class JSGItems {
    private static final JSGDeferredRegister<Item> REGISTER = JSGApi.REGISTRY_HELPER.item();

    /**
     * Icons used in advancements
     */

    public static final RegistryObject<JSGItem> ICON_KAWOOSH_DEATH = Constants.JSG_ITEM_HELPER.builder("icon_kawoosh_death").clearTooltip().buildGeneric();
    public static final RegistryObject<JSGItem> ICON_WORMHOLE = Constants.JSG_ITEM_HELPER.builder("icon_wormhole").clearTooltip().buildGeneric();

    /**
     * DHD Parts
     */
    public static RegistryObject<DHDAbstractPartItem> MILKYWAY_DHD_CONTROL_CRYSTALS;
    public static RegistryObject<DHDAbstractPartItem> MILKYWAY_DHD_BUTTONS_CONSOLE = null;
    public static RegistryObject<DHDAbstractPartItem> MILKYWAY_DHD_MAIN_CRYSTAL;
    public static RegistryObject<DHDAbstractPartItem> MILKYWAY_DHD_ACTIVATION_BUTTON = null;
    public static RegistryObject<DHDAbstractPartItem> MILKYWAY_DHD_UPGRADES_COVER;

    public static RegistryObject<DHDAbstractPartItem> PEGASUS_DHD_CONTROL_CRYSTALS;
    public static RegistryObject<DHDAbstractPartItem> PEGASUS_DHD_BUTTONS_CONSOLE = null;
    public static RegistryObject<DHDAbstractPartItem> PEGASUS_DHD_MAIN_CRYSTAL;
    public static RegistryObject<DHDAbstractPartItem> PEGASUS_DHD_ACTIVATION_BUTTON = null;
    public static RegistryObject<DHDAbstractPartItem> PEGASUS_DHD_UPGRADES_COVER;

    public static RegistryObject<DHDFluidTankItem> DHD_NAQUADAH_TANK;

    static {
        MILKYWAY_DHD_CONTROL_CRYSTALS = REGISTER.register("milkyway_dhd_control_crystals", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), true, 100).withPartsNeededBeforeRemoval(List.of(MILKYWAY_DHD_BUTTONS_CONSOLE)).withPartsNeededRemovedBeforeAssembly(List.of(MILKYWAY_DHD_BUTTONS_CONSOLE)));
        MILKYWAY_DHD_BUTTONS_CONSOLE = REGISTER.register("milkyway_dhd_buttons_console", () -> new DHDMilkyWayButtonsConsoleItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), true, 100).withPartsNeededBeforeRemoval(List.of(MILKYWAY_DHD_ACTIVATION_BUTTON)));
        MILKYWAY_DHD_MAIN_CRYSTAL = REGISTER.register("milkyway_dhd_main_control_crystal", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.UNCOMMON), List.of(CoreTabs.TAB_RESOURCES, CoreTabs.TAB_UPGRADES, CoreTabs.TAB_TRANSPORTATION.get()), true, 101).withPartsNeededBeforeRemoval(List.of(MILKYWAY_DHD_ACTIVATION_BUTTON)).withPartsNeededRemovedBeforeAssembly(List.of(MILKYWAY_DHD_ACTIVATION_BUTTON)));
        MILKYWAY_DHD_ACTIVATION_BUTTON = REGISTER.register("milkyway_dhd_activation_button", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), true, SymbolMilkyWayEnum.BRB.id).withPartsNeededBeforeAssembly(List.of(MILKYWAY_DHD_BUTTONS_CONSOLE)));
        MILKYWAY_DHD_UPGRADES_COVER = REGISTER.register("milkyway_dhd_upgrades_cover", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), false, 108));

        PEGASUS_DHD_CONTROL_CRYSTALS = REGISTER.register("pegasus_dhd_control_crystals", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), true, 100).withPartsNeededBeforeRemoval(List.of(PEGASUS_DHD_BUTTONS_CONSOLE)).withPartsNeededRemovedBeforeAssembly(List.of(PEGASUS_DHD_BUTTONS_CONSOLE)));
        PEGASUS_DHD_BUTTONS_CONSOLE = REGISTER.register("pegasus_dhd_buttons_console", () -> new DHDPegasusButtonsConsoleItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), true, 100).withPartsNeededBeforeRemoval(List.of(PEGASUS_DHD_ACTIVATION_BUTTON)));
        PEGASUS_DHD_MAIN_CRYSTAL = REGISTER.register("pegasus_dhd_main_control_crystal", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.UNCOMMON), List.of(CoreTabs.TAB_RESOURCES, CoreTabs.TAB_UPGRADES, CoreTabs.TAB_TRANSPORTATION.get()), true, 101).withPartsNeededBeforeRemoval(List.of(PEGASUS_DHD_ACTIVATION_BUTTON)).withPartsNeededRemovedBeforeAssembly(List.of(PEGASUS_DHD_ACTIVATION_BUTTON)));
        PEGASUS_DHD_ACTIVATION_BUTTON = REGISTER.register("pegasus_dhd_activation_button", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), true, SymbolPegasusEnum.BBB.id).withPartsNeededBeforeAssembly(List.of(PEGASUS_DHD_BUTTONS_CONSOLE)));
        PEGASUS_DHD_UPGRADES_COVER = REGISTER.register("pegasus_dhd_upgrades_cover", () -> new DHDAbstractPartItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), false, 108));


        DHD_NAQUADAH_TANK = REGISTER.register("dhd_naquadah_tank", () -> (DHDFluidTankItem) (new DHDFluidTankItem(new Item.Properties().rarity(Rarity.COMMON), List.of(CoreTabs.TAB_RESOURCES), false, 107).withPartsNeededBeforeRemoval(List.of(MILKYWAY_DHD_UPGRADES_COVER, PEGASUS_DHD_UPGRADES_COVER))));
    }

    /**
     * These allow for dialing 8th glyph(cross dimension travel) and show different address spaces
     */
    public static final RegistryObject<JSGItem> CRYSTAL_GLYPH_DHD = Constants.JSG_ITEM_HELPER.builder("crystal_glyph_dhd").setInTabs(List.of(CoreTabs.TAB_UPGRADES)).buildUpgrade(() -> StargateDHD.DHDUpgradeEnum.CHEVRON_UPGRADE);
    public static final RegistryObject<JSGItem> CRYSTAL_GLYPH_STARGATE = Constants.JSG_ITEM_HELPER.builder("crystal_glyph_stargate").setInTabs(List.of(CoreTabs.TAB_UPGRADES)).buildUpgrade(() -> StargateUpgrade.CHEVRON_UPGRADE);
    public static final RegistryObject<JSGItem> CRYSTAL_GLYPH_MILKYWAY = Constants.JSG_ITEM_HELPER.builder("crystal_glyph_milkyway").setInTabs(List.of(CoreTabs.TAB_UPGRADES)).buildUpgrade(() -> StargateUpgrade.MILKYWAY_GLYPHS);
    public static final RegistryObject<JSGItem> CRYSTAL_GLYPH_PEGASUS = Constants.JSG_ITEM_HELPER.builder("crystal_glyph_pegasus").setInTabs(List.of(CoreTabs.TAB_UPGRADES)).buildUpgrade(() -> StargateUpgrade.PEGASUS_GLYPHS);
    public static final RegistryObject<JSGItem> CRYSTAL_GLYPH_UNIVERSE = Constants.JSG_ITEM_HELPER.builder("crystal_glyph_universe").setInTabs(List.of(CoreTabs.TAB_UPGRADES)).buildUpgrade(() -> StargateUpgrade.UNIVERSE_GLYPHS);

    /**
     * Crafting items
     */
    public static final RegistryObject<JSGItem> HOLDER_CRYSTAL = Constants.JSG_ITEM_HELPER.builder("holder_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> HOLDER_CRYSTAL_PEGASUS = Constants.JSG_ITEM_HELPER.builder("holder_crystal_pegasus").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    /**
     * TOOLS
     */
    public static final RegistryObject<JSGItem> ADMIN_CONTROLLER = REGISTER.register("admin_controller", AdminControllerItem::new);

    public static final RegistryObject<JSGItem> UNIVERSE_DIALER = REGISTER.register("universe_dialer", UniverseDialerItem::new);
    public static final RegistryObject<JSGItem> GDO = REGISTER.register("gdo", GDOItem::new);

    /**
     * FRAGMENTS
     */
    public static final RegistryObject<JSGItem> FRAGMENT_MILKYWAY = Constants.JSG_ITEM_HELPER.builder("fragment_stargate_milkyway").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> FRAGMENT_PEGASUS = Constants.JSG_ITEM_HELPER.builder("fragment_stargate_pegasus").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> FRAGMENT_UNIVERSE = Constants.JSG_ITEM_HELPER.builder("fragment_stargate_universe").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> FRAGMENT_TOLLAN = Constants.JSG_ITEM_HELPER.builder("fragment_stargate_tollan").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    /**
     * SCHEMATICS
     */
    public static final RegistryObject<JSGItem> SCHEMATIC_MILKYWAY = Constants.JSG_ITEM_HELPER.builder("schematic_milkyway").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> SCHEMATIC_PEGASUS = Constants.JSG_ITEM_HELPER.builder("schematic_pegasus").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> SCHEMATIC_UNIVERSE = Constants.JSG_ITEM_HELPER.builder("schematic_universe").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> SCHEMATIC_TOLLAN = Constants.JSG_ITEM_HELPER.builder("schematic_tollan").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();


    /**
     * Iris/Shield upgrade
     */
    public static final RegistryObject<JSGItem> IRIS_BLADE = Constants.JSG_ITEM_HELPER.builder("iris_blade").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> QUAD_IRIS_BLADE = Constants.JSG_ITEM_HELPER.builder("quad_iris_blade").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> UPGRADE_IRIS = REGISTER.register("upgrade_iris", () ->
            IrisItem.createDurability(500, new ItemStack(CoreItems.TITANIUM_INGOT.get()), EnumIrisType.IRIS_TITANIUM)
    );

    public static final RegistryObject<JSGItem> IRIS_BLADE_TRINIUM = Constants.JSG_ITEM_HELPER.builder("iris_blade_trinium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> QUAD_IRIS_BLADE_TRINIUM = Constants.JSG_ITEM_HELPER.builder("quad_iris_blade_trinium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> UPGRADE_IRIS_TRINIUM = REGISTER.register("upgrade_iris_trinium", () ->
            IrisItem.createDurability(1000, new ItemStack(CoreItems.TRINIUM_INGOT.get()), EnumIrisType.IRIS_TRINIUM)
    );

    public static final RegistryObject<JSGItem> UPGRADE_SHIELD = REGISTER.register("upgrade_shield", IrisItem::createShield);
    public static final RegistryObject<JSGItem> SHIELD_EMITTER = Constants.JSG_ITEM_HELPER.builder("shield_emitter").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> UPGRADE_IRIS_CREATIVE = REGISTER.register("upgrade_iris_creative", IrisItem::createCreative);


    public static final RegistryObject<CartridgeItem> CARTRIDGE_BLACK = REGISTER.register("black_cartridge", () -> new CartridgeItem(Color.BLACK, 0.08f));
    public static final RegistryObject<CartridgeItem> CARTRIDGE_CYAN = REGISTER.register("cyan_cartridge", () -> new CartridgeItem(Color.CYAN, 0.03f));
    public static final RegistryObject<CartridgeItem> CARTRIDGE_MAGENTA = REGISTER.register("magenta_cartridge", () -> new CartridgeItem(Color.MAGENTA, 0.02f));
    public static final RegistryObject<CartridgeItem> CARTRIDGE_YELLOW = REGISTER.register("yellow_cartridge", () -> new CartridgeItem(Color.YELLOW, 0.01f));

    /**
     * FOOD
     */

    public static final RegistryObject<JSGItem> FOOD_CHOCOLATE_BAR = Constants.JSG_ITEM_HELPER.builder("chocolate_bar").setProperties(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).fast().saturationMod(0.2f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, false, false), 1).alwaysEat().build())).buildGeneric();
    public static final RegistryObject<JSGItem> FOOD_ENHANCER = Constants.JSG_ITEM_HELPER.builder("jibbaran_enhancer").clearTooltip().setProperties(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, true), 0.5f).alwaysEat().build())).buildGeneric();

    /**
     * SPAWN EGGS
     */

    public static final RegistryObject<JSGSpawnEggItem> EGG_MASTADGE = Constants.JSG_ITEM_HELPER.builder("mastadge_spawn_egg").buildSpawnEgg(JSGEntities.MASTADGE, 0xC19A6B, 0x814141);

    /**
     * RECORDS
     */
    public static final Map<SoundEvent, RegistryObject<JSGMusicDiscItem>> RECORDS = new HashMap<>();

    static {
        var values = new ArrayList<>(SoundEvent.values());
        for (var sound : values) {
            if (sound == null) continue;
            if (!sound.resourceLocation.getPath().startsWith("record.")) continue;
            RECORDS.put(sound,
                    REGISTER.register(
                            "music_disc_" + sound.resourceLocation.getPath().toLowerCase().replaceAll("record\\.", "").replaceAll("\\.", "_"),
                            () -> new JSGMusicDiscItem(sound.event, sound.length)
                    )
            );
        }
    }

    public static void init() {
        Integrations.TCONSTRUCT.addOnLoad(() -> {
            CoreRegistryHelpers.ITEM_HELPER.builder("iris_blade_golden_cast").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
            CoreRegistryHelpers.ITEM_HELPER.builder("iris_blade_sand_cast").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
            CoreRegistryHelpers.ITEM_HELPER.builder("iris_blade_red_sand_cast").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
            CoreRegistryHelpers.ITEM_HELPER.builder("iris_blade_stone").setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
        });
        Integrations.CREATE.addOnLoad(() -> {
            CoreRegistryHelpers.ITEM_HELPER.builder("incomplete_chocolate_bar").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
        });
    }
}
