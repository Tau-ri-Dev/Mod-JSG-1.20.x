package dev.tauri.jsg.api.config.ingame.options;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.JSGConfigEnumEntry;
import dev.tauri.jsg.api.config.util.StargateTimeLimitModeEnum;
import dev.tauri.jsg.api.config.ingame.option.JSGBooleanConfigOption;
import dev.tauri.jsg.api.config.ingame.option.JSGEnumConfigOption;
import dev.tauri.jsg.api.config.ingame.option.JSGIntRangeConfigOption;

import java.util.List;
import java.util.stream.Stream;

public interface StargateConfigOptions extends BlockConfigOptions {
    List<StargateConfigOptions> COMMON = List.of(
            () -> new JSGBooleanConfigOption("allowIncomingAnim", JSGConfig.Stargate.allowIncomingAnimations.get(),
                    "Enable incoming animation",
                    "on this gate"),
            () -> new JSGBooleanConfigOption("allowRig", JSGConfig.Stargate.enableRandomIncoming.get(), "Enable rig",
                    "on this gate"),
            () -> new JSGBooleanConfigOption("dhdPressSound", JSGConfig.DialHomeDevice.computerDialSound.get(),
                    "Enable DHD press sound",
                    "when dialling with a computer"),
            () -> new JSGIntRangeConfigOption("maxCapacitors", 0, 3, 3, "Specifies how many",
                    "capacitors can be installed",
                    "in this gate"),
            () -> new JSGBooleanConfigOption("incomingSpin", true, "Enable ring spin",
                    "animation during incoming animation"),
            () -> new JSGBooleanConfigOption("enableBuryState", JSGConfig.Stargate.enableBurriedState.get(),
                    "Enable Buried capability for the gate"),
            () -> new JSGEnumConfigOption<>("timeLimitMode", StargateTimeLimitModeEnum.values(),
                    JSGConfig.Stargate.maxOpenedWhat.get(), "Gate open time limit mode"),
            () -> new JSGIntRangeConfigOption("timeLimitTime", 0, null, JSGConfig.Stargate.maxOpenedSeconds.get(),
                    "Gate open time limit in seconds"),
            () -> new JSGIntRangeConfigOption("timeLimitPower", 0, null,
                    JSGConfig.Stargate.maxOpenedPowerDrawAfterLimit.get(), "Gate power draw when the open time limit is reached",
                    " - TIME LIMIT MODE MUST BE SET TO \"DRAW_POWER\" - "),
            // TODO Investigate whether this is truly only for MW and UNI gates as the
            // original comment states
            () -> new JSGIntRangeConfigOption("spinSpeed", 1, 300, 100, "Speed factor of the gate"),
            () -> new JSGBooleanConfigOption("forceUnstable", false, "Force this gate to be in the unstable state"));

    List<StargateConfigOptions> TOLLAN = Stream.concat(COMMON.stream(), Stream.of(
            () -> new JSGBooleanConfigOption("dhdLockPoO", JSGConfig.DialHomeDevice.dhdLastOpen.get(),
                    "Enable opening last chevron",
                    "while dialing milkyway gate with dhd"))).toList();

    List<StargateConfigOptions> MILKYWAY = Stream.concat(TOLLAN.stream(), Stream.of(
            () -> new JSGEnumConfigOption<>("MWoriginModel", Stream.concat(Stream.of(
                                    new JSGConfigEnumEntry("[by overlay]", -1),
                                    new JSGConfigEnumEntry("Default", 0),
                                    new JSGConfigEnumEntry("P7J-989", 1),
                                    new JSGConfigEnumEntry("Nether", 2),
                                    new JSGConfigEnumEntry("Antarctica", 3),
                                    new JSGConfigEnumEntry("Abydos", 4),
                                    new JSGConfigEnumEntry("Tauri", 5)),
                            JSGConfig.Stargate.additionalOrigins.get().stream()
                                    .map(PoO -> new JSGConfigEnumEntry(PoO.split(":")[1], Integer.parseInt(PoO.split(":")[0]))))
                    .toList(), "Override point of origin model"))).toList();

    List<StargateConfigOptions> PEGASUS = Stream.concat(COMMON.stream(), Stream.of(
                    () -> new JSGEnumConfigOption<JSGConfigEnumEntry>("pegDialAnim", List.of(new JSGConfigEnumEntry("Slow", -1),
                            new JSGConfigEnumEntry("Normal", 0), new JSGConfigEnumEntry("Fast", 1)), 1,
                            "Speed of pegasus dialling with DHD")))
            .toList();

    List<StargateConfigOptions> UNIVERSE = Stream
            .concat(COMMON.stream().<StargateConfigOptions>map(old -> switch (old.get().getLabel()) {
                case "maxCapacitors" -> () -> new JSGIntRangeConfigOption("maxCapacitors", 0, 3,
                        JSGConfig.Stargate.universeCapacitors.get(), "Specifies how many",
                        "capacitors can be installed",
                        "in this gate");
                default -> old;
            }), Stream.of(
                    () -> new JSGBooleanConfigOption("enableFastDial", JSGConfig.Stargate.enableFastDialing.get(),
                            "Enable fast dialing"),
                    () -> new JSGBooleanConfigOption("orangeShield", true, "Should a shield appear orange on this gate?")))
            .toList();
}
