package dev.tauri.jsg.api.config;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.client.screen.EnumMainMenuGateType;
import dev.tauri.jsg.api.config.util.StargateTimeLimitModeEnum;
import dev.tauri.jsg.core.common.config.JSGConfigChild;
import dev.tauri.jsg.core.common.config.JSGCoreConfig;
import dev.tauri.jsg.core.common.config.values.JSGConfigValue;
import dev.tauri.jsg.core.common.helper.TemperatureHelper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

public class JSGConfig {
    public static final JSGConfigChild C_GENERAL = new JSGConfigChild(() -> General.BUILDER, "General", JSGApi.MOD_ID);
    public static final JSGConfigChild C_DEBUG = new JSGConfigChild(() -> Debug.BUILDER, "Debug", JSGApi.MOD_ID);
    public static final JSGConfigChild C_DHD = new JSGConfigChild(() -> DialHomeDevice.BUILDER, "DialHomeDevice", JSGApi.MOD_ID);
    public static final JSGConfigChild C_SG = new JSGConfigChild(() -> Stargate.BUILDER, "Stargate", JSGApi.MOD_ID);

    public static class General {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final JSGConfigValue.BooleanValue enableAutoUpdater = C_GENERAL.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Check for updates", true,
                "Should JSG check for update on startup?",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.DoubleValue volume = C_GENERAL.add(new JSGConfigValue.DoubleValue(BUILDER,
                "JSG sounds volume", 1.0, 0, 3, true,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.EnumValue<TemperatureHelper.EnumTemperatureUnit> temperatureUnit = C_GENERAL.add(new JSGConfigValue.EnumValue<>(BUILDER,
                "Temperature unit", TemperatureHelper.EnumTemperatureUnit.CELSIUS,
                "Specifies what unit will be used to display temperatures",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.BooleanValue disableJSGMainMenu = C_GENERAL.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Disable JSG main menu", false,
                "Disables showing custom main menu",
                "WARNING! - Requires reloading!",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.EnumValue<EnumMainMenuGateType> mainMenuGateType = C_GENERAL.add(new JSGConfigValue.EnumValue<>(BUILDER,
                "Main menu gate type", EnumMainMenuGateType.BY_ACT,
                "Gate type to be displayed in the main menu",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.BooleanValue enableLogo = C_GENERAL.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Enable Tau'ri logo on startup", true,
                "WARNING! - Requires reloading!",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.BooleanValue mainMenuDebugMode = C_GENERAL.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Enable debug mode in main menu", false,
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.BooleanValue playMusic = C_GENERAL.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Play music in main menu", true,
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.DoubleValue mainMenuMusicVolume = C_GENERAL.add(new JSGConfigValue.DoubleValue(BUILDER,
                "Main menu music volume", 1.0, 0.0, 1.0, true,
                "SIDE: CLIENT"
        ));
    }

    public static class Debug {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        public static final JSGConfigValue.BooleanValue renderInvisibleBlocks = C_DEBUG.add(new JSGConfigValue.BooleanValue(BUILDER, "Render invisible blocks", false));
        public static final JSGConfigValue.BooleanValue renderBoundingBoxes = C_DEBUG.add(new JSGConfigValue.BooleanValue(BUILDER, "Render bounding boxes", false));
    }

    public static class DialHomeDevice {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        public static final JSGConfigValue.IntValue rangeFlat = C_DHD.add(new JSGConfigValue.IntValue(BUILDER,
                "DHD range's radius horizontal", 25, 1, 64,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue rangeVertical = C_DHD.add(new JSGConfigValue.IntValue(BUILDER,
                "DHD range's radius vertical", 15, 1, 64,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue universeDialerReach = C_DHD.add(new JSGConfigValue.IntValue(BUILDER,
                "Universe dialer max horizontal reach radius", 10, 1, 64,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue fluidCapacity = C_DHD.add(new JSGConfigValue.IntValue(BUILDER,
                "DHD's max fluid capacity", 16000, 1, 128000,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.DoubleValue capacityUpgradeMultiplier = C_DHD.add(new JSGConfigValue.DoubleValue(BUILDER,
                "Capacity upgrade multiplier", 2D, 1D, 5D,
                "When capacity upgrade is placed in the DHD,",
                "then multiply internal capacity by this number",
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.IntValue energyPerNaquadah = C_DHD.add(new JSGConfigValue.IntValue(BUILDER,
                "Energy per 1mB Naquadah", 10240, 1, 50000,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue efficiencyUpgradeMultiplier = C_DHD.add(new JSGConfigValue.DoubleValue(BUILDER,
                "Efficiency upgrade multiplier", 1.4D, 1D, 5D,
                "Energy per 1mB is multiplied by this",
                "when efficiency upgrade is placed in the DHD",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue activationLevel = C_DHD.add(new JSGConfigValue.DoubleValue(BUILDER,
                "Cold fusion reactor activation energy level", 0.9D, 0D, 1D, true,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue deactivationLevel = C_DHD.add(new JSGConfigValue.DoubleValue(BUILDER,
                "Cold fusion reactor deactivation energy level", 0.98D, 0D, 1D, true,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue computerDialSound = C_DHD.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Enable press sound when dialing with computer", false,
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue dhdLastOpen = C_DHD.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Enable opening last chevron while dialing with dhd", true,
                "Enable opening last chevron while dialing milkyway gate with dhd",
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue enablePageHint = C_DHD.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Enable hint when dialing on DHDs with notebook page", true,
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.RGBAValue pageHintColorNormal = C_DHD.add(new JSGConfigValue.RGBAValue(BUILDER,
                "Dialing helper colors.Normal", 127, 255, 255,
                "Set colors of each dial helper button",
                "You should use HEX values",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.RGBAValue pageHintColorExtra = C_DHD.add(new JSGConfigValue.RGBAValue(BUILDER,
                "Dialing helper colors.ExtraSymbols", 229, 107, 238,
                "Set colors of each dial helper button",
                "You should use HEX values",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.RGBAValue pageHintColorOrigin = C_DHD.add(new JSGConfigValue.RGBAValue(BUILDER,
                "Dialing helper colors.Origin", 127, 255, 127,
                "Set colors of each dial helper button",
                "You should use HEX values",
                "SIDE: CLIENT"
        ));
    }

    public static class Stargate {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        // --------------------------------------
        //      MECHANICS
        // --------------------------------------

        public static final JSGConfigValue.BooleanValue enableBurriedState = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Mechanics.Enable burried state for gates", true,
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue stargateOrlinMaxOpenCount = C_SG.add(new JSGConfigValue.IntValue(BUILDER,
                "Mechanics.Orlin's gate max open count", 1, 0, 15000,
                "Orlin's gate max open count",
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.IntValue universeGateNearbyReach = C_SG.add(new JSGConfigValue.IntValue(BUILDER,
                "Mechanics.Universe dialer nearby radius", 1024, 5, Integer.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.BooleanValue enableGateOverHeatExplosion = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Mechanics.Enable gate overheat with explosion", true,
                "Should gate explode when its overheated?",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue gateMaxHeat = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER,
                "Mechanics.Max stargate heat", 4340D, 0, Double.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.BooleanValue allowConnectToDialing = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Mechanics.Connect to dialing gate", true,
                "If target gate is dialing and this option is set to true,",
                "the target gate will stop dialing and open incoming wormhole.",
                "If this is set to false and the dialed gate dialing address,",
                "the connection will not established.",
                "If it cause issues, set it to false.",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue pegAndMilkUseEightChevrons = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Mechanics.Use 8 chevrons between MW and PG gates", true,
                "Change this to true, if you want to use 8 chevrons between pegasus and milkyway gates",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue useStrictSevenSymbolsUniGate = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER,
                "Mechanics.Need only 7 symbols between Uni gates", false,
                "If you want to dial UNI-UNI only with seven symbols (interdimensional for example), set this to true",
                "SIDE: SERVER"
        ));

        // --------------------------------------
        //      IRIS
        // --------------------------------------

        public static final JSGConfigValue.BooleanValue killAtDestination = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "Iris.Iris kills at destination", true,
                "If set to 'false' player get killed by iris on entering event horizon",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue allowCreative = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "Iris.Allow creative bypass", true,
                "Set it to true, if u want to bypass",
                "shield/iris damage by creative gamemode",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue irisDestroysBlocks = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "Iris.Can iris destroy blocks", false,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue enableIrisOverHeatCollapse = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "Iris.Enable iris overheat collapse", true,
                "Should iris break when its overheated?",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue irisShieldPowerDraw = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Iris.Shield power draw", 500, 0, 500000,
                "Energy/tick used for make shield closed",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue irisCodeLength = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Iris.Maximum iris code length", 9, 1, 32,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.IntValue irisUnbreakingChance = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Iris.Unbreaking chance per level", 10, 0, 100,
                "0 - disables unbreaking on iris",
                "100 - unbreaking makes iris unbreakable",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue irisTitaniumMaxHeat = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "Iris.Max titanium iris heat", 1668, 0, Double.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.DoubleValue irisTriniumMaxHeat = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "Iris.Max trinium iris heat", 2336, 0, Double.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        // --------------------------------------
        //      POWER
        // --------------------------------------

        public static final JSGConfigValue.IntValue stargateEnergyStorage = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Power.Stargate's internal buffer size", 71280000, 4608, Integer.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.IntValue stargateMaxEnergyTransfer = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Power.Stargate's max power throughput", 26360, 1, 500000,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue openingBlockToEnergyRatio = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Power.Stargate wormhole open power draw", 4608, 0, 500000,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue keepAliveBlockToEnergyRatioPerTick = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Power.Stargate wormhole sustain power draw", 2, 0, 50,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue instabilitySeconds = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Power.Stargate instability threshold", 20, 1, 120,
                "Seconds of energy left before gate becomes unstable",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue stargateOrlinEnergyMul = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "Power.Orlin's gate energy multiplier", 2.0D, 0, 100,

                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.DoubleValue stargateUniverseEnergyMul = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "Power.Universe gate energy multiplier", 2.0D, 0, 100,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.IntValue universeCapacitors = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "Power.Capacitors supported by Universe gates", 1, 0, 3,
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.DoubleValue eightSymbolAddressMul = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "Power.Stargate eight symbols address power mul", 1.3f, 0, 100,
                "Specifies the multiplier of power needed to keep the gate alive",
                "when 8-symbols address is dialed",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue nineSymbolAddressMul = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "Power.Stargate nine symbols address power mul", 1.7f, 0, 100,
                "Specifies the multiplier of power needed to keep the gate alive",
                "when 9-symbols address is dialed",
                "SIDE: SERVER"
        ));

        // --------------------------------------
        //      VISUAL
        // --------------------------------------

        public static final JSGConfigValue.BooleanValue allowIncomingAnimations = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "Visual.Allow incoming animations", true,
                "If the incoming animations of gates generate issues, set it to false",
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue enableTravelAnimation = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "Enable travel animation", true,
                "SIDE: SERVER!!!"
        ));

        // --------------------------------------
        //      EVENT HORIZON
        // --------------------------------------

        public static final JSGConfigValue.BooleanValue wrongSideKilling = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "EventHorizon.Enable wrong side killing", true,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue ehDeathChance = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "EventHorizon.Unstable Event Horizon chance of death", 0.07f, 0, 1,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue renderEHifTheyNot = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "EventHorizon.Render EHs even if they are not rendering", true,
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.IntValue eventHorizonRenderSections = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "EventHorizon.Rendering.Number of EH sections", 36 * 2, 12, 36 * 8,
                "Defines how many sections should be used to render EH kawoosh",
                "DO NOT CHANGE THIS UNLESS YOU KNOW WHAT YOU ARE DOING!",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.IntValue eventHorizonRenderQuads = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "EventHorizon.Rendering.Number of EH quads", 16, 8, 64,
                "Defines how many quads should be used to render EH circle",
                "DO NOT CHANGE THIS UNLESS YOU KNOW WHAT YOU ARE DOING!",
                "SIDE: CLIENT"
        ));

        public static final JSGConfigValue.BooleanValue blackHoleCanDestroyBlocks = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "EventHorizon.Blackhole vortex can destroy blocks", true,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.BooleanValue blackHoleCanSuckBlocks = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "EventHorizon.Blackhole field can suck blocks", true,
                "SIDE: SERVER"
        ));

        // --------------------------------------
        //      AUTOCLOSE
        // --------------------------------------

        public static final JSGConfigValue.BooleanValue autocloseEnabled = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "AutoClose.Autoclose enabled", true,
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue secondsToAutoclose = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "AutoClose.Seconds to autoclose with no players nearby", 5, 1, 300,
                "SIDE: SERVER"
        ));

        // --------------------------------------
        //      OPEN TIME LIMIT
        // --------------------------------------

        public static final JSGConfigValue.IntValue maxOpenedSeconds = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "OpenTimeLimit.Maximum seconds of gate should be open", 240, 5, 3000,
                "In seconds (2280 = 38 minutes)",
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.EnumValue<StargateTimeLimitModeEnum> maxOpenedWhat = C_SG.add(new JSGConfigValue.EnumValue<>(BUILDER, "OpenTimeLimit.Gate open time limit mode", StargateTimeLimitModeEnum.DRAW_MORE_POWER,
                "What happens after gate's open time reaches limit?",
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.IntValue maxOpenedPowerDrawAfterLimit = C_SG.add(new JSGConfigValue.IntValue(BUILDER, "OpenTimeLimit.Power draw after opened time limit", 10000, 0, 50000,
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        // --------------------------------------
        //      RIG
        // --------------------------------------

        public static final JSGConfigValue.BooleanValue enableRandomIncoming = C_SG.add(new JSGConfigValue.BooleanValue(BUILDER, "RandomIncomingGenerator.Enable random incoming wormholes", true,
                "Enable random incoming wormholes generator",
                "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                "SIDE: SERVER"
        ));

        public static final JSGConfigValue.DoubleValue rigChance = C_SG.add(new JSGConfigValue.DoubleValue(BUILDER, "RandomIncomingGenerator.Chance to generate RIG on gate", 1.0, 0.0, 50,
                "SIDE: SERVER"
        ));


        // --------------------------------------
    }


    // ----------------------------------------------------
    // REGISTRATION

    public static final String CONFIG_GENERAL_VERSION = "2.0";
    private static final String CONFIG_FILE_NAME = "jsg/jsgConfig_" + CONFIG_GENERAL_VERSION + "/";

    public static final ArrayList<JSGConfigChild> LIST = new ArrayList<>();

    public static void register() {
        LIST.clear();
        LIST.add(C_GENERAL);
        LIST.add(C_DEBUG);
        LIST.add(C_DHD);
        LIST.add(C_SG);

        JSGCoreConfig.register(JSG.MOD_ID, CONFIG_FILE_NAME, LIST);
    }

    public static void load() {
    }
}
