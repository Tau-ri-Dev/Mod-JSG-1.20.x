package dev.tauri.jsg.datagen.lang;

import java.util.stream.Stream;

import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGEntities;
import dev.tauri.jsg.common.registry.JSGItems;
import net.minecraft.data.PackOutput;

public class en_us extends InheritableLang {

    public en_us(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        // ------------------- MAIN MENU --------------------

        add("menu.about", "About JSG Mod");
        add("menu.updater.download", "Download");
        add("menu.updater.close", "Close");
        add("menu.ram.help", "Help");
        add("menu.music.volume", "Music volume:");

        // >>> World creation <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("createWorld.stargates_generating", "Generating stargates in dimensions... %s%%");
        add("createWorld.stargates_generating.trying", "Trying generation in %s");
        add("createWorld.stargates_generating.searching", "Searching for structure in %s");
        add("createWorld.stargates_generating.generating", "Generating stargate in %s");
        add("createWorld.stargates_generating.searching_stargate", "Searching for stargate in %s (%s) attempt %s");
        add("createWorld.stargates_generating.running_sg_regen", "Running stargate regeneration...");
        add("createWorld.stargate_disassemble.milkyway.title", "Milky Way Stargate");
        add("createWorld.stargate_disassemble.milkyway.chevron_frame", "Chevron Frame");
        add("createWorld.stargate_disassemble.milkyway.chevron_light", "Chevron Light");
        add("createWorld.stargate_disassemble.milkyway.chevron_lock", "Chevron Lock");
        add("createWorld.stargate_disassemble.milkyway.glyph_ring", "Glyph Ring");
        add("createWorld.stargate_disassemble.milkyway.gate_frame", "Stargate frame");
        add("createWorld.dhd_disassemble.milkyway.title", "Milky Way Dial Home Device");
        add("createWorld.dhd_disassemble.milkyway.dhd_table", "DHD Table");
        add("createWorld.dhd_disassemble.milkyway.crystals_holder", "Crystals Holder");
        add("createWorld.dhd_disassemble.milkyway.crystals", "DHD Crystals");
        add("createWorld.dhd_disassemble.milkyway.control_crystal", "The Control Crystal");
        add("createWorld.dhd_disassemble.milkyway.buttons_plate", "Buttons Plate");
        add("createWorld.dhd_disassemble.milkyway.buttons", "DHD Buttons");

        add("createWorld.stargates_generating.dim_status.ok", "Generated");
        add("createWorld.stargates_generating.dim_status.already_there", "Already there");
        add("createWorld.stargates_generating.dim_status.no_structure", "No structure available");
        add("createWorld.stargates_generating.dim_status.skipped", "Skipped by config");
        add("createWorld.stargates_generating.dim_status.error", "Error");

        add("joinWorld.jsg.abydos_update.title", "New JSG version detected!");
        add("joinWorld.jsg.abydos_update.desc", "In this version, the Abydos dimension got a massive update, and unfortunately, can not load the old Abydos dimension.\nBy continuing to load the world, the old Abydos dimension will be PERMANENTLY DELETED, and replaced with newly generated one!");
        add("joinWorld.jsg.abydos_update.btn.proceed", "Delete old Abydos dimension");
        add("joinWorld.jsg.abydos_update.btn.cancel", "Cancel");
        add("joinWorld.jsg.abydos_update.deleting_world", "Deleting old Abydos dimension...");

        // >>> Tips <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("menu.tip.kawoosh", "TIP: Try to avoid standing in front of a Stargate as it activates.");
        add("menu.tip.unstable_eh", "TIP: Travelling through an unstable wormhole can result in death.");
        add("menu.tip.abydos_cartouche", "TIP: Explore Abydos to find cartouches with stargate addresses.");

        // ------------------- CONFIG GUI --------------------

        add("gui.jsg.config", "Just Stargate Mod Configuration");
        add("gui.jsg.config_child.debug", "Debug Settings");
        add("gui.jsg.config_child.general", "General Configuration");
        add("gui.jsg.config_child.dialhomedevice", "DHD Configuration");
        add("gui.jsg.config_child.stargate", "Stargates Configuration");
        add("gui.jsg.config_child.computersintegration", "Computers Integration Configuration");

        add("gui.jsg.ingame_config.default_value", "Default: %s");
        add("gui.jsg.ingame_config.time_limit_mode.title", "Stargate Time Limit Mode");
        add("gui.jsg.ingame_config.time_limit_mode.comment", "Specifies what should the stargate do when the gate opened for more than Stargate Time Limit Time");
        add("gui.jsg.ingame_config.time_limit_time.title", "Stargate Time Limit Time");
        add("gui.jsg.ingame_config.time_limit_time.comment", "How many seconds can be opened before closing/drawing more power (set up by Stargate Time Limit Mode)");
        add("gui.jsg.ingame_config.time_limit_power.title", "Stargate Time Limit Power Draw Multiplier");
        add("gui.jsg.ingame_config.time_limit_power.comment", "Specifies multiplier of stargate's power draw when the gate opened for more than Stargate Time Limit Time and Stargate Time Limit Mode is set to DRAW_MORE_POWER");
        add("gui.jsg.ingame_config.spin_speed.title", "Stargate's Ring Rotation Speed");
        add("gui.jsg.ingame_config.spin_speed.comment", "Ring rotation speed in %");
        add("gui.jsg.ingame_config.force_unstable.title", "Force Unstable Event Horizon");
        add("gui.jsg.ingame_config.force_unstable.comment", "If set to Enabled, stargate's event horizon will be unstable permanently (this also effects connected gate on the other side)");
        add("gui.jsg.ingame_config.enable_bury_state.title", "Enable Bury State");
        add("gui.jsg.ingame_config.enable_bury_state.comment", "When the inner space of the stargate is filled with blocks, should the gate become undialable?");
        add("gui.jsg.ingame_config.allow_rig.title", "Random Incoming Generator");
        add("gui.jsg.ingame_config.allow_rig.comment", "Allow spawning random incoming wormholes with mobs waves");
        add("gui.jsg.ingame_config.max_energy_crystals.title", "Enabled Energy Crystals Slots");
        add("gui.jsg.ingame_config.max_energy_crystals.comment", "Number of enabled energy crystals slots");
        add("gui.jsg.ingame_config.dhd_oc_press_sound.title", "DHD Button Sound when dialing");
        add("gui.jsg.ingame_config.dhd_oc_press_sound.comment", "Play DHD button press sound when dialing via other method than DHD itself");
        add("gui.jsg.ingame_config.allow_incoming_animation.title", "Incoming Animations");
        add("gui.jsg.ingame_config.allow_incoming_animation.comment", "Allow incoming animations to play on this gate");
        add("gui.jsg.ingame_config.dhd_last_lock.title", "DHD Last Chevron Lock");
        add("gui.jsg.ingame_config.dhd_last_lock.comment", "Play chevron locking animation on the final chevron when dialing by DHD");
        add("gui.jsg.ingame_config.spin_ring_incoming.title", "Spin Ring by Incoming Animation");
        add("gui.jsg.ingame_config.spin_ring_incoming.comment", "Start spinning the ring when incoming wormhole occurs. This require the Incoming Animations option to be set to Enabled.");
        add("gui.jsg.ingame_config.point_of_origin.title", "Point of Origin Variant");
        add("gui.jsg.ingame_config.point_of_origin.comment", "Variant of the PoO to be used on this gate. This will not change any gate's behaviour - it's only a visual");
        add("gui.jsg.ingame_config.fast_dialing.title", "Fast Dialing");
        add("gui.jsg.ingame_config.fast_dialing.comment", "Switch to fast dialing on this gate when dialing by a Remote");
        add("gui.jsg.ingame_config.orange_shield.title", "Orange Shield");
        add("gui.jsg.ingame_config.orange_shield.comment", "Switch shield to orange color on this gate");

        // ----------------- CREATIVE TABS ------------------

        add("itemGroup.jsg.machines", "JSG: Machines");

        // --------------------- ITEMS ----------------------

        // >>> Records - Music Discs / Siren Discs <<<<<<<<<<

        JSGItems.RECORDS.values().forEach(rec -> addItem(rec, "§3"+(rec.get().getDescriptionId().contains("siren")?"Siren":"Music")+" Disc"));
        // add("item.jsg.music_disc_sgc_theme", "§3Music Disc");
        // add("item.jsg.music_disc_continuum_opening", "§3Music Disc");
        // add("item.jsg.music_disc_origins_theme", "§3Music Disc");
        // add("item.jsg.music_disc_atlantis_theme", "§3Music Disc");
        // add("item.jsg.music_disc_atlantis_dialing", "§3Music Disc");
        // add("item.jsg.music_disc_destiny_dialing", "§3Music Disc");
        // add("item.jsg.music_disc_destiny_gauntlet", "§3Music Disc");
        // add("item.jsg.music_disc_destiny_opening", "§3Music Disc");

        // add("item.jsg.music_disc_siren_sgc_offworld", "§3Siren Disc");
        // add("item.jsg.music_disc_siren_sgc_dialing", "§3Siren Disc");

        // >>> Iris Upgrades and Iris Crafting Elements <<<<<

        addItem(JSGItems.IRIS_BLADE, "Titanium Iris Blade");
        addItem(JSGItems.QUAD_IRIS_BLADE, "Titanium Quad Iris Blade");
        addItem(JSGItems.UPGRADE_IRIS, "Titanium Iris");

        addItem(JSGItems.IRIS_BLADE_TRINIUM, "Trinium Iris Blade");
        addItem(JSGItems.QUAD_IRIS_BLADE_TRINIUM, "Trinium Quad Iris Blade");
        addItem(JSGItems.UPGRADE_IRIS_TRINIUM, "Trinium Iris");

        addItem(JSGItems.SHIELD_EMITTER, "Shield Emitter");
        addItem(JSGItems.UPGRADE_SHIELD, "Shield Upgrade");

        addItem(JSGItems.UPGRADE_IRIS_CREATIVE, "Creative Iris");

        // >>> Upgrade Crystals <<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addItem(JSGItems.CRYSTAL_GLYPH_DHD, "DHD Glyph Crystal");
        addItem(JSGItems.CRYSTAL_GLYPH_STARGATE, "Stargate Glyph Crystal");
        addItem(JSGItems.CRYSTAL_GLYPH_MILKYWAY, "Milky Way Glyphs Crystal");
        addItem(JSGItems.CRYSTAL_GLYPH_PEGASUS, "Pegasus Glyphs Crystal");
        addItem(JSGItems.CRYSTAL_GLYPH_UNIVERSE, "Universe Glyphs Crystal");

        // >>> DHD Components <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.dhd.assembly_helper.needed", "Missing %s");

        addItem(JSGItems.MILKYWAY_DHD_BUTTONS_CONSOLE, "Milky Way DHD Buttons Console");
        addItem(JSGItems.MILKYWAY_DHD_CONTROL_CRYSTALS, "Milky Way DHD Control Crystals");
        addItem(JSGItems.MILKYWAY_DHD_MAIN_CRYSTAL, "Milky Way DHD Control Crystal");
        addItem(JSGItems.MILKYWAY_DHD_ACTIVATION_BUTTON, "Bright Red Button (BRB)");
        addItem(JSGItems.MILKYWAY_DHD_UPGRADES_COVER, "Milky Way Upgrades Cover Plate");

        addItem(JSGItems.PEGASUS_DHD_BUTTONS_CONSOLE, "Pegasus DHD Buttons Console");
        addItem(JSGItems.PEGASUS_DHD_CONTROL_CRYSTALS, "Pegasus DHD Control Crystals");
        addItem(JSGItems.PEGASUS_DHD_MAIN_CRYSTAL, "Pegasus DHD Control Crystal");
        addItem(JSGItems.PEGASUS_DHD_ACTIVATION_BUTTON, "Bright Blue Button (BBB)");
        addItem(JSGItems.PEGASUS_DHD_UPGRADES_COVER, "Pegasus Upgrades Cover Plate");

        addItem(JSGItems.HOLDER_CRYSTAL, "Milky Way Crystal Holder");
        addItem(JSGItems.HOLDER_CRYSTAL_PEGASUS, "Pegasus Crystal Holder");

        // >>> Handheld devices <<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addItem(JSGItems.ADMIN_CONTROLLER, "Admin Controller");
        addItem(JSGItems.GDO, "Garage Door Opener (GDO)");
        addItem(JSGItems.UNIVERSE_DIALER, "Universe Dialer");

        // >>> Food <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addItem(JSGItems.FOOD_CHOCOLATE_BAR, "5th Avenue Chocolate Bar");
        addItem(JSGItems.FOOD_ENHANCER, "Jibbaran Enhancer");

        // >>> Spawn Eggs <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addItem(JSGItems.EGG_MASTADGE, "Mastadge Spawn Egg");

        // >>> Schematics >>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<

        addItem(JSGItems.SCHEMATIC_MILKYWAY, "Milky Way Schematic");
        addItem(JSGItems.SCHEMATIC_PEGASUS, "Pegasus Schematic");
        addItem(JSGItems.SCHEMATIC_UNIVERSE, "Universe Schematic");
        addItem(JSGItems.SCHEMATIC_TOLLAN, "Tollan Schematic");

        // >>> Cartridges - Inks <<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addItem(JSGItems.CARTRIDGE_BLACK, "Black Cartridge");
        addItem(JSGItems.CARTRIDGE_CYAN, "Cyan Cartridge");
        addItem(JSGItems.CARTRIDGE_MAGENTA, "Magenta Cartridge");
        addItem(JSGItems.CARTRIDGE_YELLOW, "Yellow Cartridge");

        add("item.jsg.black_cartridge.empty", "Empty Black Cartridge");
        add("item.jsg.cyan_cartridge.empty", "Empty Cyan Cartridge");
        add("item.jsg.magenta_cartridge.empty", "Empty Magenta Cartridge");
        add("item.jsg.yellow_cartridge.empty", "Empty Yellow Cartridge");

        // >>> Crafting Elements <<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addItem(JSGItems.FRAGMENT_MILKYWAY, "Milky Way Ring Fragment");
        addItem(JSGItems.FRAGMENT_PEGASUS, "Pegasus Ring Fragment");
        addItem(JSGItems.FRAGMENT_UNIVERSE, "Universe Ring Fragment");
        addItem(JSGItems.FRAGMENT_TOLLAN, "Tollan Ring Fragment");

        // >>> TREASURE MAPS<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        add("filled_map.burried_stargate", "Chappa'ai Location Map");
        add("filled_map.abydos_treasure", "Old Abydos Map");

        // >>> Weapons <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // >>> Advancements Icons <<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.icon_jsg", "Main JSG Icon");

        // >>> Integrations Items <<<<<<<<<<<<<<<<<<<<<<<<<<<

        // ===>>> Tinkers Construct <<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.iris_blade_golden_cast", "Iris Blade Gold Cast");
        add("item.jsg.iris_blade_sand_cast", "Iris Blade Sand Cast");
        add("item.jsg.iris_blade_red_sand_cast", "Iris Blade Red Sand Cast");
        add("item.jsg.iris_blade_stone", "Stone Iris Blade");

        add("pattern.jsg.iris_blade", "Iris Blade");

        // ----------- BLOCKS / BLOCK ENTITIES --------------

        // >>> Special Blocks <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addBlock(JSGBlocks.IRIS_BLOCK, "Iris / Shield block");

        // >>> Natural Blocks<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addBlock(JSGBlocks.ABYDOS_SAND, "Sand");

        // >>> DHDs <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addBlock(JSGBlocks.DHD_MILKYWAY, "Milky Way Dial Home Device (DHD)");
        addBlock(JSGBlocks.DHD_PEGASUS, "Pegasus Dial Home Device (DHD)");

        // >>> Stargates <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addBlock(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK, "Orlin's Stargate Base");
        addBlock(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK, "Orlin's Stargate Ring");

        addBlock(JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK, "Milky Way Stargate Base");
        addBlock(JSGBlocks.STARGATE_MILKYWAY_CHEVRON_BLOCK, "Milky Way Stargate Chevron");
        addBlock(JSGBlocks.STARGATE_MILKYWAY_RING_BLOCK, "Milky Way Stargate Ring");

        addBlock(JSGBlocks.STARGATE_MOVIE_BASE_BLOCK, "Movie Stargate Base");
        addBlock(JSGBlocks.STARGATE_MOVIE_CHEVRON_BLOCK, "Movie Stargate Chevron");
        addBlock(JSGBlocks.STARGATE_MOVIE_RING_BLOCK, "Movie Stargate Ring");

        addBlock(JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK, "Tollan Stargate Base");
        addBlock(JSGBlocks.STARGATE_TOLLAN_CHEVRON_BLOCK, "Tollan Stargate Chevron");
        addBlock(JSGBlocks.STARGATE_TOLLAN_RING_BLOCK, "Tollan Stargate Ring");

        addBlock(JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK, "Pegasus Stargate Base");
        addBlock(JSGBlocks.STARGATE_PEGASUS_CHEVRON_BLOCK, "Pegasus Stargate Chevron");
        addBlock(JSGBlocks.STARGATE_PEGASUS_RING_BLOCK, "Pegasus Stargate Ring");

        addBlock(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK, "Universe Stargate Base");
        addBlock(JSGBlocks.STARGATE_UNIVERSE_CHEVRON_BLOCK, "Universe Stargate Chevron");
        addBlock(JSGBlocks.STARGATE_UNIVERSE_RING_BLOCK, "Universe Stargate Ring");

        // >>> Redstone IO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addBlock(JSGBlocks.SG_REDSTONE_DIALER_I_BLOCK, "Stargate Redstone Dialer");
        addBlock(JSGBlocks.SG_REDSTONE_STATE_O_BLOCK, "Stargate Redstone State Output");

        // >>> Machines <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        addBlock(JSGBlocks.TOASTER, "Toaster");
        addBlock(JSGBlocks.PRINTER, "Printer");

        // -------------------- ENTITIES --------------------

        add("entity.minecraft.villager.jsg.priest_desert", "Priest");
        add("entity.minecraft.villager.jsg.priest_jungle", "Priest");
        add("entity.minecraft.villager.jsg.priest_plains", "Priest");
        add("entity.minecraft.villager.jsg.priest_savanna", "Priest");
        add("entity.minecraft.villager.jsg.priest_snow", "Priest");
        add("entity.minecraft.villager.jsg.priest_swamp", "Priest");
        add("entity.minecraft.villager.jsg.priest_taiga", "Priest");
        add("entity.minecraft.villager.jsg.slave_miner", "Slave Miner");

        addEntityType(JSGEntities.MASTADGE, "Mastadge");

        // ------------------- Tool Tips --------------------

        // >>> Items <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // ===>>> Records - Music Discs / Siren Discs <<<<<<<

        add("item.jsg.music_disc_sgc_theme.desc", "Joel Goldsmith - Entering the Stargate");
        add("item.jsg.music_disc_continuum_opening.desc", "Stargate Continuum - Intro");
        add("item.jsg.music_disc_origins_theme.desc", "Filip Olejka - Origins");
        add("item.jsg.music_disc_atlantis_theme.desc", "Stargate Atlantis - Theme");
        add("item.jsg.music_disc_atlantis_dialing.desc", "Stargate Atlantis - Dialing from Earth");
        add("item.jsg.music_disc_destiny_dialing.desc", "Joel Goldsmith - Dialing Earth");
        add("item.jsg.music_disc_destiny_gauntlet.desc", "Joel Goldsmith - Stargate Universe Gauntlet");
        add("item.jsg.music_disc_destiny_opening.desc", "Joel Goldsmith - Stargate Universe Intro");

        add("item.jsg.music_disc_siren_sgc_offworld.desc", "SGC Offworld Alarm");
        add("item.jsg.music_disc_siren_sgc_dialing.desc", "SGC Gate Active Alarm");

        // ===>>> Iris <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.upgrade_iris.tooltip", "Iris upgrade tier I");
        add("item.jsg.upgrade_iris.tooltip.extended", "Iris works as in lore.%nl%Just install this into any gate.");

        add("item.jsg.upgrade_iris_trinium.tooltip", "Iris upgrade tier II");
        add("item.jsg.upgrade_iris_trinium.tooltip.extended", "Iris works as in lore.%nl%Just install this into any gate.");

        add("item.jsg.upgrade_shield.tooltip", "Iris upgrade tier III");
        add("item.jsg.upgrade_shield.tooltip.extended", "Shield blocks incoming travellers.%nl%Just install this into any gate.%nl%Instead of durability%nl%it consumes energy from the gate buffer.");

        add("item.jsg.upgrade_iris_creative.tooltip", "Iris with infinite durability");

        add("item.jsg.iris.tooltip.integrity", "Integrity:");
        add("item.jsg.iris.tooltip.durability", "Durability:");

        // ===>>> Upgrade Crystals <<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.crystal_glyph_dhd.tooltip", "Upgrade for DHDs");
        add("item.jsg.crystal_glyph_dhd.tooltip.extended", "This crystal allows you to §lDIAL%nl%8 and 9 symbol addresses when%nl%placed into a DHD.%nl%%nl%§oThis upgrade can be combined%nl%§owith other DHD upgrades!");

        add("item.jsg.crystal_glyph_stargate.tooltip", "Upgrade for Stargates");
        add("item.jsg.crystal_glyph_stargate.tooltip.extended", "This crystal allows you to §lONLY DISPLAY%nl%8th and 9th symbols of the gate's address when%nl%placed into a stargate.%nl%%nl%§oThis upgrade can be combined%nl%§owith other stargate upgrades!");

        add("item.jsg.crystal_glyph_milkyway.tooltip", "Upgrade for Stargates");
        add("item.jsg.crystal_glyph_milkyway.tooltip.extended", "This crystal allows you to §lONLY DISPLAY%nl%gate's Milky Way address when%nl%placed into a stargate.%nl%%nl%§oThis upgrade can be combined%nl%§owith other stargate upgrades!");

        add("item.jsg.crystal_glyph_pegasus.tooltip", "Upgrade for Stargates");
        add("item.jsg.crystal_glyph_pegasus.tooltip.extended", "This crystal allows you to §lONLY DISPLAY%nl%gate's Pegasus address when%nl%placed into a stargate.%nl%%nl%§oThis upgrade can be combined%nl%§owith other stargate upgrades!");

        add("item.jsg.crystal_glyph_universe.tooltip", "Upgrade for Stargates");
        add("item.jsg.crystal_glyph_universe.tooltip.extended", "This crystal allows you to §lONLY DISPLAY%nl%gate's Universe address when%nl%placed into a stargate.%nl%%nl%§oThis upgrade can be combined%nl%§owith other stargate upgrades!");

        // ===>>> DHD Components <<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.crystal_control_dhd.tooltip", "Main control crystal of DHD");
        add("item.jsg.crystal_control_dhd.tooltip.extended", "Insert this crystal into DHD (Dial Home Device)%nl%to make the DHD functional and be able%nl%to link with the nearest gate.");

        add("item.jsg.crystal_control_pegasus_dhd.tooltip", "Main control crystal for a DHD");
        add("item.jsg.crystal_control_pegasus_dhd.tooltip.extended", "Insert this crystal into DHD (Dial Home Device)%nl%to make the DHD functional and be able%nl%to link with the nearest gate.");

        // ===>>> Handheld Devices <<<<<<<<<<<<<<<<<<<<<<<<<<

        add("admin_controller.tooltip", "Stargate controller and debugger");
        add("admin_controller.tooltip.extended", "This item shows you whole SG network when linked to a gate.%nl%It's capable of dialing the linked gate%nl%by spinning the ring, locking symbols like DHD%nl%and dialing all symbols at once like Nox did in series.");

        add("item.jsg.gdo.tooltip", "GDO is capable of sending an IDC code through an open wormhole to open the iris.");
        add("item.jsg.universe_dialer.tooltip", "A controller that works with Universe gates, along with much more!");

        // ===>>> Tinkers Construct Compat <<<<<<<<<<<<<<<<<<

        add("item.jsg.iris_blade_stone.tooltip", "Use this blade to craft cast");

        // ===>>> Food <<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.chocolate_bar.tooltip", "Bahni? Bahni-wae!");

        // >>> Blocks / Block Entities <<<<<<<<<<<<<<<<<<<<<<

        // ===>>> DHDs <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("block.jsg.dhd_milkyway.tooltip", "Control panel for Milky Way, Tollan and Movie gates");
        add("block.jsg.dhd_pegasus.tooltip", "Control panel for Pegasus gates");

        // ===>>> Stargates <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("block.jsg.stargate_orlin_base_block.open_count", "Open count: %s / %s");
        add("block.jsg.stargate_orlin_base_block.tooltip", "The main block of Orlin's gate");
        add("block.jsg.stargate_orlin_base_block.tooltip.extended", "Place this block first when building a stargate.%nl%When placed, it will show you a diagram%nl%of how the stargate should be built.");
        add("block.jsg.stargate_orlin_member_block.tooltip", "The construction block of Orlin's gate");
        add("block.jsg.stargate_orlin_member_block.tooltip.extended", "You need this block to complete building a new stargate.");

        add("block.jsg.stargate_milkyway_base_block.tooltip", "The main block of Milky Way gate");
        add("block.jsg.stargate_milkyway_base_block.tooltip.extended", "Place this block first when building a stargate.%nl%When placed, it will show you a diagram%nl%of how the stargate should be built.");
        add("block.jsg.stargate_milkyway_chevron_block.tooltip", "The locking mechanism of Milky Way gate");
        add("block.jsg.stargate_milkyway_chevron_block.tooltip.extended", "You need this block to complete building a new stargate.");
        add("block.jsg.stargate_milkyway_ring_block.tooltip", "The construction block of Milky Way gate");
        add("block.jsg.stargate_milkyway_ring_block.tooltip.extended", "You need this block to complete building a new stargate.");

        add("block.jsg.stargate_movie_base_block.tooltip", "The main block of Movie gate");
        add("block.jsg.stargate_movie_base_block.tooltip.extended", "Place this block first when building a stargate.%nl%When placed, it will show you a diagram%nl%of how the stargate should be built.");
        add("block.jsg.stargate_movie_chevron_block.tooltip", "The locking mechanism of Movie gate");
        add("block.jsg.stargate_movie_chevron_block.tooltip.extended", "You need this block to complete building a new stargate.");
        add("block.jsg.stargate_movie_ring_block.tooltip", "The construction block of Movie gate");
        add("block.jsg.stargate_movie_ring_block.tooltip.extended", "You need this block to complete building a new stargate.");

        add("block.jsg.stargate_tollan_base_block.tooltip", "The main block of Tollan gate");
        add("block.jsg.stargate_tollan_base_block.tooltip.extended", "Place this block first when building a stargate.%nl%When placed, it will show you a diagram%nl%of how the stargate should be built.");
        add("block.jsg.stargate_tollan_chevron_block.tooltip", "The locking mechanism of Tollan gate");
        add("block.jsg.stargate_tollan_chevron_block.tooltip.extended", "You need this block to complete building a new stargate.");
        add("block.jsg.stargate_tollan_ring_block.tooltip", "The construction block of Tollan gate");
        add("block.jsg.stargate_tollan_ring_block.tooltip.extended", "You need this block to complete building a new stargate.");

        add("block.jsg.stargate_pegasus_base_block.tooltip", "The main block of Pegasus gate");
        add("block.jsg.stargate_pegasus_base_block.tooltip.extended", "Place this block first when building a stargate.%nl%When placed, it will show you diagram%nl%how the stargate should be build.");
        add("block.jsg.stargate_pegasus_chevron_block.tooltip", "The locking mechanism of Pegasus gate");
        add("block.jsg.stargate_pegasus_chevron_block.tooltip.extended", "You need this block to complete building a new stargate.");
        add("block.jsg.stargate_pegasus_ring_block.tooltip", "The construction block of Pegasus gate");
        add("block.jsg.stargate_pegasus_ring_block.tooltip.extended", "You need this block to complete building a new stargate.");

        add("block.jsg.stargate_universe_base_block.tooltip", "The main block of Universe gate");
        add("block.jsg.stargate_universe_base_block.tooltip.extended", "Place this block first when building a stargate.%nl%When placed, it will show you a diagram%nl%of how the stargate should be built.");
        add("block.jsg.stargate_universe_chevron_block.tooltip", "The locking mechanism of Universe gate");
        add("block.jsg.stargate_universe_chevron_block.tooltip.extended", "You need this block to complete building a new stargate.");
        add("block.jsg.stargate_universe_ring_block.tooltip", "The construction block of Universe gate");
        add("block.jsg.stargate_universe_ring_block.tooltip.extended", "You need this block to complete building a new stargate.");

        // ===>>> Redstone IO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("block.jsg.sg_redstone_dialer_input_block.tooltip", "Dial a stargate using redstone signal");
        add("block.jsg.sg_redstone_dialer_input_block.tooltip.extended", "To dial a symbol you need to set symbol ID%nl%using listed inputs then engage it.%nl%Selected symbol id = ((Right * 16) + Front)%nl%%nl%§lInputs:%nl%Top: Engage gate%nl%Bottom: Enable DHD Dialing animation%nl%Left: Engage symbol%nl%Right: 2nd bit of symbol ID%nl%Front: 1st bit of symbol ID");

        add("block.jsg.sg_redstone_state_output_block.tooltip", "Get redstone output by current stargate state");
        add("block.jsg.sg_redstone_state_output_block.tooltip.extended", "§lOutput:%nl%0: Idle%nl%1: Dialing DHD%nl%2: Dialing Computer%nl%3: Engaged Incoming%nl%4: Engaged Outgoing%nl%5: Unstable%nl%6: Failing%nl%7: Incoming Wormhole");

        // ===>>> Machines <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("block.jsg.toaster.tooltip", "Orlin still owes Carter a new one.");
        add("block.jsg.printer.tooltip", "Useful to print notebook pages");


        // ---------------------- GUI -----------------------

        // >>> Options -> Key Binds <<<<<<<<<<<<<<<<<<<<<<<<<

        add("key.categories.jsg", "Just Stargate Mod");
        add("config.jsg.address_down", "Universe dialer address down");
        add("config.jsg.address_edit", "Open address edit menu");
        add("config.jsg.address_scroll", "Universe dialer address scroll");
        add("config.jsg.address_up", "Universe dialer address up");
        add("config.jsg.mode_down", "Universe dialer mode down");
        add("config.jsg.mode_scroll", "Universe dialer mode scroll");
        add("config.jsg.mode_up", "Universe dialer mode up");
        add("config.jsg.rename_entry", "Universe dialer rename entry");

        // >>> Stargate <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // ===>>> Milkyway Glyphs <<<<<<<<<<<<<<<<<<<<<<<<<<<

        Stream.of(SymbolMilkyWayEnum.values()).skip(1).forEach(sym -> add("glyph.jsg.milkyway." + sym.name().toLowerCase(), sym.getEnglishName()));
        add("glyph.jsg.milkyway.bright_red_button", "Bright Red Button");

        // ===>>> Pegasus Glyphs <<<<<<<<<<<<<<<<<<<<<<<<<<<<

        Stream.of(SymbolPegasusEnum.values()).skip(1).forEach(sym -> add("glyph.jsg.pegasus." + sym.name().toLowerCase(), sym.getEnglishName()));
        add("glyph.jsg.pegasus.bright_blue_button", "Bright Blue Button");

        // ===>>> Universe Glyphs <<<<<<<<<<<<<<<<<<<<<<<<<<<

        Stream.of(SymbolUniverseEnum.values()).skip(1).forEach(sym -> add("glyph.jsg.universe." + sym.name().toLowerCase(), sym.getEnglishName()));

        // ===>>> General Stargate GUI <<<<<<<<<<<<<<<<<<<<<<

        add("gui.stargate.energy_crystals", "Energy Crystals");

        // ===>>> Address Tab <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("gui.stargate.jsg.milkyway_address", "Milky Way address");
        add("gui.stargate.jsg.pegasus_address", "Pegasus address");
        add("gui.stargate.jsg.universe_address", "Universe address");

        // ===>>> Iris Tab <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("gui.stargate.iris_code", "Iris Code");
        add("gui.stargate.iris.help_title", "Current mode:");
        add("gui.stargate.iris.opened", "Open");
        add("gui.stargate.iris.closed", "Closed");
        add("gui.stargate.iris.auto", "Automatic");
        add("gui.stargate.iris.oc", "CC / OC");
        add("gui.stargate.iris.dialer", "Dialer");

        add("gui.stargate.iris.opened_help", "Iris is permanently open");
        add("gui.stargate.iris.closed_help", "Iris is permanently closed");
        add("gui.stargate.iris.auto_help", "Iris automatically closes on incoming wormhole,");
        add("gui.stargate.iris.auto1_help", "it can be opened with GDO by sending code set in field");
        add("gui.stargate.iris.oc_help", "Iris can be controlled by CC or OC2 computers");
        add("gui.stargate.iris.dialer_help", "Iris can be controlled by universe dialer");

        // ===>>> Info Tab <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("gui.stargate.info", "Stargate information");
        add("gui.stargate.state.closed", "Stargate closed!");
        add("gui.stargate.state.opened", "Open time:");
        add("gui.stargate.state.gate_temp", "Gate temp:");
        add("gui.stargate.state.iris_temp", "Iris temp:");

        // >>> DHDs <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // ===>>> General DHD GUI <<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("gui.dhd.reactorStatus", "Cold fusion reactor: %s");

        add("gui.dhd.reactor.online", "Running");
        add("gui.dhd.reactor.not_linked", "Not linked");
        add("gui.dhd.reactor.no_fuel", "No fuel");
        add("gui.dhd.reactor.standby", "Stand-by");
        add("gui.dhd.reactor.no_crystal", "Crystal missing");
        add("gui.dhd.reactor.no_fluid_tank", "Fluid tank missing");

        // >>> GDO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.gdo.in_menu.alert", "Type code or press ESC");
        add("item.jsg.gdo.code_rejected", "CODE INVALID");
        add("item.jsg.gdo.code_accepted", "Code accepted! Opening iris...");
        add("item.jsg.gdo.iris_busy", "Iris is busy!");
        add("item.jsg.gdo.iris_opened", "Iris is open!");
        add("item.jsg.gdo.operator", "T-Mobile 5G");
        add("item.jsg.gdo.code", "Code:");

        // >>> Universe Dialer <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.universe_dialer.in_menu.alert", "Edit entry name or press ESC");
        add("item.jsg.universe_dialer.saved_gates", "Saved gates: %s");
        add("item.jsg.universe_dialer.mode_scan", "Nearby");
        add("item.jsg.universe_dialer.mode_saved", "Memory");
        add("item.jsg.universe_dialer.mode_info", "Status");
        add("item.jsg.universe_dialer.manual_dialing", "Dialing");
        add("item.jsg.universe_dialer.manual_dialing.delete", "Delete");

        // >>> Admin Controller <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("gui.admincontroller.name", "Admin Controller");

        add("gui.admincontroller.tab.dialing.name", "Dialling");
        add("gui.admincontroller.tab.diagnostics.name", "Diagnostics");
        add("gui.admincontroller.tab.network.name", "Network");

        add("gui.admincontroller.tab.dialing.gate.not_linked", "OFFLINE");
        add("gui.admincontroller.tab.dialing.abort.name", "Abort dialing");
        add("gui.admincontroller.tab.dialing.close.name", "Close stargate");
        add("gui.admincontroller.tab.dialing.iris.name", "Toggle the gate's iris");

        add("gui.admincontroller.tab.diagnostics.state", "Stargate status:");
        add("gui.admincontroller.tab.diagnostics.subspace", "Subspace status:");
        add("gui.admincontroller.tab.diagnostics.energy", "Energy (FE):");
        add("gui.admincontroller.tab.diagnostics.energy_consumption", "Energy consumption (FE/tick):");
        add("gui.admincontroller.tab.diagnostics.seconds_open", "Open time:");
        add("gui.admincontroller.tab.diagnostics.seconds_to_close", "Seconds until closure:");

        add("gui.admincontroller.tab.network.address_list.name", "Addresses");
        add("gui.admincontroller.tab.network.map.name", "Network map");
        add("gui.admincontroller.tab.network.map.you_are_here", "You are here");
        add("gui.admincontroller.tab.network.map.stargate.name", "Name:");
        add("gui.admincontroller.tab.network.map.stargate.pos", "Position:");
        add("gui.admincontroller.tab.network.map.stargate.type", "Type:");
        add("gui.admincontroller.tab.network.map.stargate.dimension", "Dimension:");
        add("gui.admincontroller.tab.network.dimension_select.all.name", "All dimensions");
        add("gui.admincontroller.tab.network.address_list.slow_dail.name", "Slow dialing");
        add("gui.admincontroller.tab.network.address_list.fast_dial.name", "Fast dialing");
        add("gui.admincontroller.tab.network.address_list.nox_dialing.name", "Nox dialing");
        add("gui.admincontroller.tab.network.address_list.address.name", "Get address");
        add("gui.admincontroller.tab.network.address_list.teleport.name", "Teleport to gate");

        add("gui.admincontroller.response.error", "✘");
        add("gui.admincontroller.response.success", "✔");
        add("gui.admincontroller.response.stargate.not_linked.error", "Controller not linked to a gate");
        add("gui.admincontroller.response.stargate.not_merged.error", "Stargate must be merged");
        add("gui.admincontroller.response.stargate.dial_address.error.busy", "Stargate is busy");
        add("gui.admincontroller.response.stargate.dial_address.error.already_engaged", "One or more symbols have already been engaged");
        add("gui.admincontroller.response.stargate.dial_address.error.blocked_by_event", "Dialling blocked by an error (Check logs)");
        add("gui.admincontroller.response.stargate.dial_address.success", "Begun dialling...");
        add("gui.admincontroller.response.stargate.give_address.success", "Giving address book...");
        add("gui.admincontroller.response.stargate.abort.error", "Stargate is not dialing");
        add("gui.admincontroller.response.stargate.abort.success", "Dialing aborted");
        add("gui.admincontroller.response.stargate.iris.empty", "Stargate doesn't have an iris");
        add("gui.admincontroller.response.stargate.iris.busy", "Stargate's iris is busy");
        add("gui.admincontroller.response.stargate.iris.close", "Closing the iris...");
        add("gui.admincontroller.response.stargate.iris.open", "Opening the iris...");
        add("gui.admincontroller.response.stargate.close.success", "Closing gate...");
        add("gui.admincontroller.response.stargate.close.error.not_open", "Stargate is not open");
        add("gui.admincontroller.response.stargate.close.error.blocked_by_event", "Closing blocked by an error (Check logs)");

        // >>> Notebook pages and Notebooks <<<<<<<<<<<<<<<<<<
        add("item.jsg.notebook.unnamed", "<unnamed>");

        // ----------------- CHAT MESSAGES ------------------

        add("chat.orlins.energyStored", "Energy stored: %s FE (Required: %s FE, %s s)");

        // -------------- ACTION BAR MESSAGES ---------------

        // >>> DHDs <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("block.jsg.dhd_milkyway.no_crystal_warn", "DHD has no control crystal");
        add("block.jsg.dhd_milkyway.not_linked_warn", "DHD is not linked to the gate");
        add("block.jsg.dhd_milkyway.not_enough_power", "Insufficient power");
        add("block.jsg.dhd_milkyway.incoming_wormhole_warn", "Unable to close an incoming wormhole");
        add("block.jsg.dhd_milkyway.computer_dial", "Computer dial is in progress");

        add("block.jsg.dhd_pegasus.unknown_buttons", "This button isn't working");

        // >>> Remote - Universe Dialer <<<<<<<<<<<<<<<<<<<<<

        add("item.jsg.universe_dialer.dial_start", "Dialing the gate...");
        add("item.jsg.universe_dialer.aborting", "Aborting dial");
        add("item.jsg.universe_dialer.gate_busy", "Stargate is busy");
        add("block.jsg.dhd_block.incoming_wormhole_warn", "Unable to close an incoming wormhole");

        // >>> Machines <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // ===>>> Printer <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("block.jsg.printer.empty_input", "Insert a notebook page");
        add("block.jsg.printer.no_ink.general", "Cartridge is missing!");
        add("block.jsg.printer.no_ink.black", "Not enough black color!");
        add("block.jsg.printer.no_ink.cyan", "Not enough cyan color!");
        add("block.jsg.printer.no_ink.magenta", "Not enough magenta color!");
        add("block.jsg.printer.no_ink.yellow", "Not enough yellow color!");
        add("block.jsg.printer.busy", "Printer is busy");
        add("block.jsg.printer.full_output", "Take the printed page from the printer");

        // ----------------- DEATH MESSAGES -----------------

        add("death.attack.kawoosh", "%s was disintegrated by the Unstable Vortex");
        add("death.attack.unstable_eh", "%s didn't make it to the other side");
        add("death.attack.wrong_side", "%s entered the wrong side of the gate");
        add("death.attack.iris", "%s slammed into the Iris");

        // ------------------ ADVANCEMENTS ------------------

        add("advancement.jsg.craft_printer.title", "Not enough Magenta!");
        add("advancement.jsg.craft_printer.description", "Works like regular printer, but it's more advanced");

        add("advancement.jsg.priest_trade.title", "Sacrifices to their gods");
        add("advancement.jsg.priest_trade.description", "Trade with Priest Villager");

        add("advancement.jsg.visit_abydos.title", "Welcome to Ra's realm");
        add("advancement.jsg.visit_abydos.description", "Visit Abydos");

        add("advancement.jsg.use_gdo.title", "Code accepted!");
        add("advancement.jsg.use_gdo.description", "Use a GDO to open the iris");

        add("advancement.jsg.obtain_toaster.title", "Carter's Toaster");
        add("advancement.jsg.obtain_toaster.description", "You wouldn't believe the things that you can make from common items.");

        add("advancement.jsg.activate_orlin_gate.title", "So you built one?");
        add("advancement.jsg.activate_orlin_gate.description", "Ohh... and you're going to need a new toaster!");

        add("advancement.jsg.obtain_milkyway_glyph_crystal.title", "We Need Six Points...");
        add("advancement.jsg.obtain_milkyway_glyph_crystal.description", "Obtain Milky Way Glyph Crystal");

        add("advancement.jsg.obtain_pegasus_glyph_crystal.title", "We Determined 7th Symbol");
        add("advancement.jsg.obtain_pegasus_glyph_crystal.description", "Obtain Pegasus Glyph Crystal");

        add("advancement.jsg.obtain_universe_glyph_crystal.title", "Not Incorrect, Incomplete");
        add("advancement.jsg.obtain_universe_glyph_crystal.description", "Obtain Universe Glyph Crystal");

        add("advancement.jsg.obtain_stargate_glyph_crystal.title", "Extra Symbols");
        add("advancement.jsg.obtain_stargate_glyph_crystal.description", "Obtain Stargate Glyph Crystal");

        add("advancement.jsg.activate_movie_gate.title", "Giza, 1928");
        add("advancement.jsg.activate_movie_gate.description", "Activate Movie Stargate");

        add("advancement.jsg.activate_mw_gate.title", "Pyramid with Sun above it");
        add("advancement.jsg.activate_mw_gate.description", "Build Milkyway Stargate");

        add("advancement.jsg.activate_tollan_gate.title", "Ours is bigger");
        add("advancement.jsg.activate_tollan_gate.description", "Build a Tollan stargate");

        add("advancement.jsg.activate_pg_gate.title", "Bon Voyage!");
        add("advancement.jsg.activate_pg_gate.description", "Build Pegasus Stargate");

        add("advancement.jsg.activate_uni_gate.title", "Its Not Address, Its a Code");
        add("advancement.jsg.activate_uni_gate.description", "Build Universe Stargate");

        add("advancement.jsg.craft_shield.title", "Using power, using power...");
        add("advancement.jsg.craft_shield.description", "Obtain stargate shield");

        add("advancement.jsg.kawoosh_death.title", "Escaping Hadante");
        add("advancement.jsg.kawoosh_death.description", "Get cremated by the Unstable Vortex");

        add("advancement.jsg.iris_impact.title", "Like bugs on a windshield");
        add("advancement.jsg.iris_impact.description", "Witness an object hit the iris");

        add("advancement.jsg.wormhole_go.title", "What's a wormhole? And why a worm?");
        add("advancement.jsg.wormhole_go.description", "Enter the Event Horizon of a Stargate");

        add("advancement.jsg.unstable_wormhole.title", "Unstable Wormhole?!");
        add("advancement.jsg.unstable_wormhole.description", "For a moment i thought we were in trouble");

        add("advancement.jsg.obtain_mw_dhd.title", "This is how they control it");
        add("advancement.jsg.obtain_mw_dhd.description", "Obtain Milkyway DHD");

        add("advancement.jsg.obtain_pg_dhd.title", "Look how small it is!");
        add("advancement.jsg.obtain_pg_dhd.description", "Obtain Pegasus DHD");

        add("advancement.jsg.obtain_dhd_glyph_crystal.title", "Additional Control Crystal");
        add("advancement.jsg.obtain_dhd_glyph_crystal.description", "Obtain DHD Glyph Crystal");

        // -------------------- COMMANDS --------------------

        add("commands.prepare.not_preparable", "Targeted block is not prepare-able");
        add("commands.prepare.success", "Blocks successfully prepared for saving into NBT");
        add("commands.prepare.error", "Error while preparing block to saving into NBT");
        add("commands.rig.not_stargate", "No Stargate nearby that could initiate RIG");
        add("commands.rig.already.active", "Stargate already has an active RIG");
        add("commands.sgsetaddress.notstargate", "Target block isn't stargate base block");
        add("commands.sgsetaddress.success", "Target stargate address is successfully changed");

        // ---------------- SOUND SUBTITLES -----------------

        // >>> Stargates <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // ===>>> Orlin Stargate <<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.gate_orlin_dial", "Orlin gate dials");
        add("jsg.subtitle.gate_orlin_dial_fail", "Stargate fails to connect");
        add("jsg.subtitle.gate_orlin_broke", "Stargate has broken");

        // ===>>> Milkyway Stargate <<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.gate_milkyway_ring_roll", "Stargate ring rolls");
        add("jsg.subtitle.gate_milkyway_chevron_open", "Stargate chevron opens");
        add("jsg.subtitle.gate_milkyway_chevron_shut", "Stargate chevron shuts");
        add("jsg.subtitle.gate_milkyway_incoming", "Stargate chevrons lock");
        add("jsg.subtitle.gate_milkyway_open", "Stargate wormhole opens");
        add("jsg.subtitle.gate_milkyway_close", "Stargate wormhole closes");
        add("jsg.subtitle.gate_milkyway_dial_fail", "Stargate fails to connect");
        add("jsg.subtitle.gate_milkyway_dial_fail_computer", "Stargate computer dialing fails");

        // ===>>> Pegasus Stargate <<<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.gate_pegasus_ring_roll", "Stargate ring rolls");
        add("jsg.subtitle.gate_pegasus_chevron_open", "Stargate chevron opens");
        add("jsg.subtitle.gate_pegasus_chevron_shut", "Stargate chevron shuts");
        add("jsg.subtitle.gate_pegasus_incoming", "Stargate chevrons lock");
        add("jsg.subtitle.gate_pegasus_open", "Stargate wormhole opens");
        add("jsg.subtitle.gate_pegasus_close", "Stargate wormhole closes");
        add("jsg.subtitle.gate_pegasus_dial_fail", "Stargate fails to connect");
        add("jsg.subtitle.gate_pegasus_dial_fail_computer", "Stargate computer dialing fails");

        // ===>>> Universe Stargate <<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.gate_universe_dial_start", "Universe gate starts to dial");
        add("jsg.subtitle.gate_universe_roll", "Universe gate rolls");
        add("jsg.subtitle.gate_universe_chevron_lock", "Universe gate chevron locks");
        add("jsg.subtitle.gate_universe_chevron_top_lock", "Universe gate top chevron locks");
        add("jsg.subtitle.gate_universe_open", "Universe gate wormhole opens");
        add("jsg.subtitle.gate_universe_close", "Universe gate wormhole closes");
        add("jsg.subtitle.gate_universe_fail", "Universe gate fails to connect");

        // ===>>> Wormhole / Event Horizon <<<<<<<<<<<<<<<<<

        add("jsg.subtitle.wormhole_loop", "Stargate wormhole ripples");
        add("jsg.subtitle.wormhole_go", "Stargate wormhole receives traveler");
        add("jsg.subtitle.wormhole_flicker", "Stargate wormhole flickers");

        // ===>>> Iris / Shield <<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.iris_opening", "Iris opening");
        add("jsg.subtitle.iris_closing", "Iris closing");
        add("jsg.subtitle.iris_hit", "Thud against the iris");

        add("jsg.subtitle.shield_opening", "Shield opening");
        add("jsg.subtitle.shield_closing", "Shield closing");
        add("jsg.subtitle.shield_hit", "Thud against the shield");
        add("jsg.subtitle.shield_humming", "Shield hums");

        // >>> DHDs <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // ===>>> Milkyway DHD <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.dhd_milkyway_press", "DHD symbol pressed");
        add("jsg.subtitle.dhd_milkyway_press_brb", "DHD bright red button pressed");

        // ===>>> Pegasus DHD <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.dhd_pegasus_press", "DHD symbol pressed");
        add("jsg.subtitle.dhd_pegasus_press_brb", "DHD bright blue button pressed");

        // >>> Universe Dialer <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.universe_dialer_connect", "Dialer connected");
        add("jsg.subtitle.universe_dialer_change_mode", "Changed Dialer mode");
        add("jsg.subtitle.universe_dialer_beep", "Dialer started dialing");
        add("jsg.subtitle.universe_dialer_error", "Dialer Error");

        // >>> GDO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        add("jsg.subtitle.gdo_button_beep", "Pressed GDO button");
    }

}
