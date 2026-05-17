package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.Constants;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.StargateUpgrade;
import dev.tauri.jsg.api.stargate.dialhomedevice.StargateDHD;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.common.item.CartridgeItem;
import dev.tauri.jsg.common.item.admincontroller.AdminControllerItem;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.common.item.linkable.gdo.GDOItem;
import dev.tauri.jsg.common.item.stargate.IrisItem;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class JSGItems {
    private static final DeferredRegister<Item> REGISTER = JSGApi.REGISTRY_HELPER.item();

    /**
     * Icons used in advancements
     */

    public static final RegistryObject<JSGItem> ICON_KAWOOSH_DEATH = Constants.JSG_ITEM_HELPER.builder("icon_kawoosh_death").clearTooltip().buildGeneric();
    public static final RegistryObject<JSGItem> ICON_WORMHOLE = Constants.JSG_ITEM_HELPER.builder("icon_wormhole").clearTooltip().buildGeneric();

    /**
     * DHD power/control crystal
     */
    public static final RegistryObject<JSGItem> CRYSTAL_CONTROL_MILKYWAY_DHD = Constants.JSG_ITEM_HELPER.builder("crystal_control_dhd").setInTabs(List.of(CoreTabs.TAB_UPGRADES, CoreTabs.TAB_TRANSPORTATION)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_CONTROL_PEGASUS_DHD = Constants.JSG_ITEM_HELPER.builder("crystal_control_pegasus_dhd").setInTabs(List.of(CoreTabs.TAB_UPGRADES, CoreTabs.TAB_TRANSPORTATION)).buildGeneric();

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

    public static final RegistryObject<JSGItem> DHD_BRB = Constants.JSG_ITEM_HELPER.builder("dhd_brb").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> DHD_BBB = Constants.JSG_ITEM_HELPER.builder("dhd_bbb").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
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
