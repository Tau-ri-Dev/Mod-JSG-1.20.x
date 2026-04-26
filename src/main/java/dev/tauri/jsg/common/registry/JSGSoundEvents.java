package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.core.mapping.JSGMapping;

public class JSGSoundEvents {
    // ----------------------------------------------------------
    // Stargate - General
    public static final SoundEvent WORMHOLE_GO = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.wormhole.go"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent WORMHOLE_FLICKER = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.wormhole.flicker"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent IRIS_HIT = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.iris.hit"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent IRIS_CLOSING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.iris.close"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent IRIS_OPENING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.iris.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent SHIELD_HIT = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.shield.hit"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent SHIELD_CLOSING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.shield.close"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent SHIELD_OPENING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.stargate.shield.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Stargate - Milky Way
    public static final SoundEvent DHD_MILKYWAY_PRESS = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.dhd.milkyway.press"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent DHD_MILKYWAY_PRESS_BRB = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.dhd.milkyway.press.brb"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MILKYWAY_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MILKYWAY_CLOSE = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.close"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MILKYWAY_DIAL_FAILED = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.fail"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MILKYWAY_DIAL_FAILED_COMPUTER = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.fail_computer"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MILKYWAY_INCOMING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.chevron.incoming"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MILKYWAY_CHEVRON_SHUT = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.chevron.shut"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MILKYWAY_CHEVRON_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.milkyway.chevron.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Stargate - Universe
    public static final SoundEvent GATE_UNIVERSE_DIAL_START = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.dial"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_UNIVERSE_CHEVRON_LOCK = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.chevron.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_UNIVERSE_CHEVRON_TOP_LOCK = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.chevron.lock"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_UNIVERSE_DIAL_FAILED = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.fail"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_UNIVERSE_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_UNIVERSE_CLOSE = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.universe.close"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Stargate - Pegasus
    public static final SoundEvent DHD_PEGASUS_PRESS = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.dhd.pegasus.press"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent DHD_PEGASUS_PRESS_BRB = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.dhd.pegasus.press.brb"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_PEGASUS_CHEVRON_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.pegasus.chevron.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_PEGASUS_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.pegasus.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_PEGASUS_INCOMING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.pegasus.chevron.incoming"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_PEGASUS_DIAL_FAILED = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.pegasus.fail"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Stargate - Orlin
    public static final SoundEvent GATE_ORLIN_DIAL = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.orlin.dial"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_ORLIN_FAIL = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.orlin.fail"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_ORLIN_BROKE = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.orlin.broke"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Stargate - Movie
    public static final SoundEvent GATE_MOVIE_CHEVRON_CLOSE = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.movie.chevron.close"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MOVIE_CHEVRON_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.movie.chevron.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MOVIE_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.movie.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MOVIE_CLOSE = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.movie.close"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_MOVIE_RING_ROLL_STOP = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.movie.ring_roll.stop"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Misc
    public static final SoundEvent GDO_BUTTON_CLICK = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.gui.gdo.button"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent UNIVERSE_DIALER_MODE_CHANGE = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "item.dialer.mode.change"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent UNIVERSE_DIALER_CONNECTED = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "item.dialer.connect"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent UNIVERSE_DIALER_START_DIAL = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "item.dialer.dial.start"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent UNIVERSE_DIALER_ERROR = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "item.dialer.error"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Records
    public static final SoundEvent RECORD_DESTINY_OPENING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.destiny.opening"), 1, 76).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent RECORD_DESTINY_DIALING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.destiny.dialing"), 1, 89).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent RECORD_DESTINY_GAUNTLET = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.destiny.gauntlet"), 1, 158).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent RECORD_ATLANTIS_THEME = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.atlantis.theme"), 1, 99).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent RECORD_ATLANTIS_DIALING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.atlantis.dialing"), 1, 131).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent RECORD_ORIGINS_THEME = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.origins.theme"), 1, 75).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent RECORD_CONTINUUM_OPENING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.continuum.opening"), 1, 98).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent RECORD_SGC_THEME = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.sgc.theme"), 1, 61).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent GATE_NOX_OPEN = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "block.stargate.nox.open"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent AMBIENT_ABYDOS = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "ambient.abydos.wind"), 0.5f).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent DEVICE_CONNECTED = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.jub.device.connected"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent DEVICE_DISCONNECTED = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "misc.jub.device.disconnected"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    // ----------------------------------------------------------
    // Siren
    public static final SoundEvent SIREN_SGC_DIALING = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.siren.sgc.dialing"), 1).register(JSGApi.REGISTRY_HELPER.sound());
    public static final SoundEvent SIREN_SGC_OFFWORLD = new SoundEvent(JSGMapping.rl(JSGApi.MOD_ID, "record.siren.sgc.offworld"), 1).register(JSGApi.REGISTRY_HELPER.sound());

    public static void init() {
    }
}
