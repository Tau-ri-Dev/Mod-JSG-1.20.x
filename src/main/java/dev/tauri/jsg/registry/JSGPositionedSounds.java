package dev.tauri.jsg.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import dev.tauri.jsg.core.mapping.JSGMapping;

public class JSGPositionedSounds {
    public static final PositionedSound WORMHOLE_TRAVEL = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.wormhole.travel"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    // gate ring sounds
    public static final PositionedSound MILKYWAY_RING_ROLL = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.ring_roll.loop"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound MILKYWAY_RING_ROLL_START = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.ring_roll.start"), false, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound UNIVERSE_RING_ROLL = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.ring_roll.loop"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound UNIVERSE_RING_ROLL_START = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.ring_roll.start"), false, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound PEGASUS_RING_ROLL = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.pegasus.ring_roll.loop"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound PEGASUS_RING_ROLL_START = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.pegasus.ring_roll.start"), false, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound MOVIE_RING_ROLL = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.movie.ring_roll.loop"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound MOVIE_RING_ROLL_START = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.movie.ring_roll.start"), false, 1).register(JSGApi.REGISTRY_HELPER.sound());
    // loops
    public static final PositionedSound WORMHOLE_LOOP = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.wormhole.loop"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound SHIELD_HUMMING = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.shield.humming"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    // main menu
    public static final PositionedSound MAINMENU_INTRO = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "music.menu.intro"), false, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound KINO_FLYBY = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "misc.kino.flyby"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound MAINMENU_ACT1 = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "music.menu.act1"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound MAINMENU_ACT2 = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "music.menu.act2"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound MAINMENU_ACT3 = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "music.menu.act3"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound MAINMENU_ACT6 = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "music.menu.act6"), true, 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final PositionedSound PRINTER_PRINTING = new PositionedSound(JSGMapping.rl(JSGApi.MOD_ID, "misc.printer.printing"), false, 1).register(JSGApi.REGISTRY_HELPER.sound());

    public static void init() {

    }
}
