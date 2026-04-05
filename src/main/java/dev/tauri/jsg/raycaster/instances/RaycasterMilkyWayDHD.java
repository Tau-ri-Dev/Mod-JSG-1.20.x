package dev.tauri.jsg.raycaster.instances;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.core.common.raycaster.util.RayCastedButton;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RaycasterMilkyWayDHD extends RaycasterDHD {
    public static final List<RayCastedButton> BUTTONS = List.of(
            //Outer ring
            //Sculptor
            new RayCastedButton(0, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.335414f, 0.361746f, 0.787496f),
                    new Vector3f(0.234379f, 0.276212f, 0.897206f),
                    new Vector3f(0.13173f, 0.323818f, 0.868578f),
                    new Vector3f(0.185166f, 0.431428f, 0.745594f)
            )),

            //Scorpius
            new RayCastedButton(1, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.460619f, 0.247623f, 0.856122f),
                    new Vector3f(0.322899f, 0.196232f, 0.9453f),
                    new Vector3f(0.243849f, 0.269823f, 0.901048f),
                    new Vector3f(0.344913f, 0.355338f, 0.791349f)
            )),

            //Centaurus
            new RayCastedButton(2, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.535799f, 0.104844f, 0.941979f),
                    new Vector3f(0.37632f, 0.095954f, 1.0056f),
                    new Vector3f(0.329435f, 0.187554f, 0.950518f),
                    new Vector3f(0.467174f, 0.238919f, 0.861355f)
            )),

            //Monoceros
            new RayCastedButton(3, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.55281f, -0.05112f, 1.03577f),
                    new Vector3f(0.388852f, -0.013755f, 1.07157f),
                    new Vector3f(0.379214f, 0.085927f, 1.01163f),
                    new Vector3f(0.538702f, 0.094787f, 0.948026f)
            )),

            //Point of Origin
            new RayCastedButton(4, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.509807f, -0.203366f, 1.12732f),
                    new Vector3f(0.359138f, -0.121008f, 1.13607f),
                    new Vector3f(0.38779f, -0.024044f, 1.07776f),
                    new Vector3f(0.551745f, -0.061439f, 1.04197f)
            )),

            //Pegasus
            new RayCastedButton(5, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.41145f, -0.335397f, 1.20671f),
                    new Vector3f(0.290398f, -0.214181f, 1.19209f),
                    new Vector3f(0.354235f, -0.130444f, 1.14174f),
                    new Vector3f(0.50489f, -0.21283f, 1.13301f)
            )),

            //Andromeda
            new RayCastedButton(6, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.268398f, -0.432905f, 1.26534f),
                    new Vector3f(0.19008f, -0.283178f, 1.23358f),
                    new Vector3f(0.282186f, -0.221742f, 1.19664f),
                    new Vector3f(0.403214f, -0.34298f, 1.21127f)
            )),

            //Serpens Caput
            new RayCastedButton(7, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.096153f, -0.485323f, 1.29686f),
                    new Vector3f(0.069057f, -0.320522f, 1.25604f),
                    new Vector3f(0.179449f, -0.288044f, 1.23651f),
                    new Vector3f(0.257735f, -0.437785f, 1.26828f)
            )),

            //Aries
            new RayCastedButton(8, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.08662f, -0.486972f, 1.29786f),
                    new Vector3f(-0.059559f, -0.322165f, 1.25703f),
                    new Vector3f(0.057157f, -0.322165f, 1.25703f),
                    new Vector3f(0.084219f, -0.486972f, 1.29786f)
            )),

            //Libra
            new RayCastedButton(9, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.260115f, -0.437672f, 1.26821f),
                    new Vector3f(-0.181828f, -0.287931f, 1.23644f),
                    new Vector3f(-0.071436f, -0.320409f, 1.25597f),
                    new Vector3f(-0.098532f, -0.485211f, 1.2968f)
            )),

            //Eridanus
            new RayCastedButton(10, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.40553f, -0.342766f, 1.21114f),
                    new Vector3f(-0.284501f, -0.221528f, 1.19651f),
                    new Vector3f(-0.192396f, -0.282965f, 1.23346f),
                    new Vector3f(-0.270714f, -0.432692f, 1.26521f)
            )),

            //Leo Minor
            new RayCastedButton(11, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.507108f, -0.212539f, 1.13283f),
                    new Vector3f(-0.356453f, -0.130153f, 1.14157f),
                    new Vector3f(-0.292616f, -0.21389f, 1.19192f),
                    new Vector3f(-0.413668f, -0.335106f, 1.20653f)
            )),

            //Hydra
            new RayCastedButton(12, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.553841f, -0.061102f, 1.04177f),
                    new Vector3f(-0.389886f, -0.023708f, 1.07756f),
                    new Vector3f(-0.361234f, -0.120671f, 1.13586f),
                    new Vector3f(-0.511902f, -0.203029f, 1.12711f)
            )),

            //Sagittarius
            new RayCastedButton(13, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.540665f, 0.095133f, 0.947818f),
                    new Vector3f(-0.381176f, 0.086273f, 1.01142f),
                    new Vector3f(-0.390814f, -0.013409f, 1.07136f),
                    new Vector3f(-0.554772f, -0.050774f, 1.03556f)
            )),

            //Sextans
            new RayCastedButton(14, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.469007f, 0.239237f, 0.861164f),
                    new Vector3f(-0.331268f, 0.187872f, 0.950327f),
                    new Vector3f(-0.378153f, 0.096272f, 1.00541f),
                    new Vector3f(-0.537632f, 0.105162f, 0.941788f)
            )),

            //Scutum
            new RayCastedButton(15, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.346634f, 0.355593f, 0.791195f),
                    new Vector3f(-0.245571f, 0.270078f, 0.900894f),
                    new Vector3f(-0.32462f, 0.196488f, 0.945146f),
                    new Vector3f(-0.46234f, 0.247878f, 0.855967f)
            )),

            //Pisces
            new RayCastedButton(16, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.186806f, 0.431593f, 0.745494f),
                    new Vector3f(-0.13337f, 0.323983f, 0.868479f),
                    new Vector3f(-0.236018f, 0.276377f, 0.897106f),
                    new Vector3f(-0.337054f, 0.361911f, 0.787396f)
            )),

            //Virgo
            new RayCastedButton(17, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.006842f, 0.459f, 0.729013f),
                    new Vector3f(-0.006824f, 0.343746f, 0.856594f),
                    new Vector3f(-0.121949f, 0.327283f, 0.866494f),
                    new Vector3f(-0.175351f, 0.434902f, 0.743504f)
            )),

            //Bootes
            new RayCastedButton(18, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.173755f, 0.434846f, 0.743539f),
                    new Vector3f(0.120352f, 0.327226f, 0.866529f),
                    new Vector3f(0.005228f, 0.34369f, 0.856629f),
                    new Vector3f(0.005246f, 0.458944f, 0.729048f)
            )),

            //Inner Ring
            //Auriga
            new RayCastedButton(19, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.056602f, -0.282598f, 1.26939f),
                    new Vector3f(-0.030413f, -0.121909f, 1.23206f),
                    new Vector3f(0.027995f, -0.121908f, 1.23206f),
                    new Vector3f(0.054184f, -0.282598f, 1.26939f)

            )),

            //Corona Australis
            new RayCastedButton(20, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.17009f, -0.250465f, 1.25007f),
                    new Vector3f(-0.09436f, -0.104351f, 1.22151f),
                    new Vector3f(-0.039117f, -0.120603f, 1.23128f),
                    new Vector3f(-0.065306f, -0.281293f, 1.2686f)
            )),

            //Gemini
            new RayCastedButton(21, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.265254f, -0.188494f, 1.2128f),
                    new Vector3f(-0.14819f, -0.06995f, 1.20082f),
                    new Vector3f(-0.102098f, -0.100694f, 1.21931f),
                    new Vector3f(-0.177828f, -0.246809f, 1.24787f)
            )),

            //Leo
            new RayCastedButton(22, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.331782f, -0.1034f, 1.16163f),
                    new Vector3f(-0.18607f, -0.022434f, 1.17225f),
                    new Vector3f(-0.154123f, -0.064338f, 1.19745f),
                    new Vector3f(-0.271187f, -0.182882f, 1.20943f)
            )),

            //Cetus
            new RayCastedButton(23, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.362464f, -0.004404f, 1.1021f),
                    new Vector3f(-0.203894f, 0.033048f, 1.13889f),
                    new Vector3f(-0.189555f, -0.015475f, 1.16806f),
                    new Vector3f(-0.335267f, -0.096441f, 1.15745f)
            )),

            //Triangulum
            new RayCastedButton(24, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.353976f, 0.097766f, 1.04066f),
                    new Vector3f(-0.199731f, 0.090483f, 1.10435f),
                    new Vector3f(-0.204554f, 0.040599f, 1.13435f),
                    new Vector3f(-0.363124f, 0.003148f, 1.09756f)
            )),

            //Aquarius
            new RayCastedButton(25, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.306113f, 0.189461f, 0.980871f),
                    new Vector3f(-0.174032f, 0.143648f, 1.07238f),
                    new Vector3f(-0.197494f, 0.097809f, 1.09994f),
                    new Vector3f(-0.350615f, 0.102515f, 1.03315f)
            )),

            //Microscopium
            new RayCastedButton(26, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.226447f, 0.265335f, 0.935246f),
                    new Vector3f(-0.129583f, 0.186781f, 1.04644f),
                    new Vector3f(-0.169141f, 0.149955f, 1.06859f),
                    new Vector3f(-0.301468f, 0.195502f, 0.977238f)
            )),

            //Equuleus
            new RayCastedButton(27, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.122863f, 0.317988f, 0.908238f),
                    new Vector3f(-0.071199f, 0.215209f, 1.02935f),
                    new Vector3f(-0.122567f, 0.191385f, 1.04367f),
                    new Vector3f(-0.220297f, 0.272801f, 0.93541f)
            )),

            //Crater
            new RayCastedButton(28, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(-0.005208f, 0.336018f, 0.897396f),
                    new Vector3f(-0.005208f, 0.22585f, 1.02295f),
                    new Vector3f(-0.062819f, 0.217611f, 1.0279f),
                    new Vector3f(-0.114483f, 0.320391f, 0.906793f)
            )),

            //Perseus
            new RayCastedButton(29, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.112903f, 0.320332f, 0.906829f),
                    new Vector3f(0.061239f, 0.217552f, 1.02794f),
                    new Vector3f(0.003628f, 0.225791f, 1.02298f),
                    new Vector3f(0.003628f, 0.335959f, 0.897432f)
            )),

            //Cancer
            new RayCastedButton(30, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.218672f, 0.272629f, 0.935514f),
                    new Vector3f(0.120942f, 0.191213f, 1.04378f),
                    new Vector3f(0.069574f, 0.215037f, 1.02945f),
                    new Vector3f(0.121238f, 0.317817f, 0.908341f)
            )),

            //Norma
            new RayCastedButton(31, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.300636f, 0.198079f, 0.980343f),
                    new Vector3f(0.167431f, 0.149689f, 1.06875f),
                    new Vector3f(0.127872f, 0.186515f, 1.0466f),
                    new Vector3f(0.225602f, 0.267931f, 0.938339f)
            )),

            //Taurus
            new RayCastedButton(32, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.349913f, 0.104761f, 1.03646f),
                    new Vector3f(0.195668f, 0.097478f, 1.10014f),
                    new Vector3f(0.172206f, 0.143317f, 1.07258f),
                    new Vector3f(0.30541f, 0.191707f, 0.984175f)
            )),

            //Canis Minor
            new RayCastedButton(33, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.361163f, 0.002788f, 1.09778f),
                    new Vector3f(0.202593f, 0.040239f, 1.13456f),
                    new Vector3f(0.19777f, 0.090123f, 1.10457f),
                    new Vector3f(0.352015f, 0.097406f, 1.04088f)
            )),

            //Capricornus
            new RayCastedButton(34, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.333168f, -0.096791f, 1.15766f),
                    new Vector3f(0.187456f, -0.015826f, 1.16827f),
                    new Vector3f(0.201794f, 0.032697f, 1.1391f),
                    new Vector3f(0.360364f, -0.004754f, 1.10231f)
            )),

            //Lynx
            new RayCastedButton(35, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.268961f, -0.183185f, 1.20961f),
                    new Vector3f(0.151897f, -0.064641f, 1.19763f),
                    new Vector3f(0.183843f, -0.022737f, 1.17243f),
                    new Vector3f(0.329555f, -0.103702f, 1.16181f)
            )),

            //Orion
            new RayCastedButton(36, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.175499f, -0.247031f, 1.248f),
                    new Vector3f(0.099769f, -0.100916f, 1.21944f),
                    new Vector3f(0.145861f, -0.070172f, 1.20096f),
                    new Vector3f(0.262226f, -0.189855f, 1.20897f)
            )),

            //Piscis Austrinus
            new RayCastedButton(37, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.062911f, -0.281411f, 1.26867f),
                    new Vector3f(0.036722f, -0.120721f, 1.23135f),
                    new Vector3f(0.091965f, -0.104468f, 1.22158f),
                    new Vector3f(0.167695f, -0.250583f, 1.25014f)
            )),

            //Big Red Button
            new RayCastedButton(38, JSGSymbolTypes.MILKYWAY, List.of(
                    new Vector3f(0.029669f, -0.098437f, 1.21005f),
                    new Vector3f(0f, -0.10080f, 1.21148f),
                    new Vector3f(-0.027319f, -0.099059f, 1.21043f),
                    new Vector3f(-0.056276f, -0.093039f, 1.20681f),
                    new Vector3f(-0.081455f, -0.083789f, 1.20124f),
                    new Vector3f(-0.106562f, -0.070037f, 1.19298f),
                    new Vector3f(-0.126872f, -0.054282f, 1.1835f),
                    new Vector3f(-0.145409f, -0.03429f, 1.17148f),
                    new Vector3f(-0.158649f, -0.013736f, 1.15912f),
                    new Vector3f(-0.168606f, 0.010331f, 1.14465f),
                    new Vector3f(-0.173341f, 0.033455f, 1.13074f),
                    new Vector3f(-0.173641f, 0.058989f, 1.11539f),
                    new Vector3f(-0.169358f, 0.082178f, 1.10144f),
                    new Vector3f(-0.159967f, 0.106412f, 1.08687f),
                    new Vector3f(-0.14713f, 0.127152f, 1.0744f),
                    new Vector3f(-0.129066f, 0.14746f, 1.06219f),
                    new Vector3f(-0.109067f, 0.163504f, 1.05254f),
                    new Vector3f(-0.084287f, 0.177685f, 1.04401f),
                    new Vector3f(-0.059293f, 0.187295f, 1.03823f),
                    new Vector3f(-0.030482f, 0.193812f, 1.03431f),
                    new Vector3f(-0.003201f, 0.195947f, 1.03303f),
                    new Vector3f(0.026517f, 0.194094f, 1.03414f),
                    new Vector3f(0.053129f, 0.188521f, 1.0375f),
                    new Vector3f(0.080535f, 0.178499f, 1.04352f),
                    new Vector3f(0.103594f, 0.165824f, 1.05114f),
                    new Vector3f(0.125718f, 0.148718f, 1.06143f),
                    new Vector3f(0.142724f, 0.130313f, 1.0725f),
                    new Vector3f(0.157168f, 0.107978f, 1.08593f),
                    new Vector3f(0.16628f, 0.085838f, 1.09924f),
                    new Vector3f(0.171479f, 0.060694f, 1.11436f),
                    new Vector3f(0.171709f, 0.037218f, 1.12848f),
                    new Vector3f(0.1671f, 0.01199f, 1.14365f),
                    new Vector3f(0.158422f, -0.010278f, 1.15704f),
                    new Vector3f(0.144504f, -0.032857f, 1.17062f),
                    new Vector3f(0.12786f, -0.051504f, 1.18183f),
                    new Vector3f(0.106141f, -0.068986f, 1.19234f),
                    new Vector3f(0.083334f, -0.081991f, 1.20016f),
                    new Vector3f(0.056168f, -0.092483f, 1.20647f)
            )),

            //DHD Immersive Slots - New Functions!!!
            //Button Console
            new RayCastedButton(100, List.of(
                    new Vector3f(-0.000998f, -0.542341f, 1.25871f),
                    new Vector3f(-0.095786f, -0.53561f, 1.25466f),
                    new Vector3f(-0.187988f, -0.515601f, 1.24263f),
                    new Vector3f(-0.27509f, -0.482858f, 1.22294f),
                    new Vector3f(-0.354715f, -0.438276f, 1.19613f),
                    new Vector3f(-0.424692f, -0.383071f, 1.16293f),
                    new Vector3f(-0.483111f, -0.318747f, 1.12425f),
                    new Vector3f(-0.52838f, -0.247061f, 1.08115f),
                    new Vector3f(-0.559263f, -0.169967f, 1.03479f),
                    new Vector3f(-0.574918f, -0.089568f, 0.98644f),
                    new Vector3f(-0.574918f, -0.008057f, 0.937425f),
                    new Vector3f(-0.559263f, 0.072342f, 0.889079f),
                    new Vector3f(-0.52838f, 0.149436f, 0.84272f),
                    new Vector3f(-0.483111f, 0.221122f, 0.799613f),
                    new Vector3f(-0.424692f, 0.285446f, 0.760933f),
                    new Vector3f(-0.354715f, 0.340651f, 0.727736f),
                    new Vector3f(-0.27509f, 0.385233f, 0.700927f),
                    new Vector3f(-0.187988f, 0.417976f, 0.681238f),
                    new Vector3f(-0.095786f, 0.437986f, 0.669206f),
                    new Vector3f(-0.000998f, 0.444717f, 0.665158f),
                    new Vector3f(0.09379f, 0.437986f, 0.669206f),
                    new Vector3f(0.185992f, 0.417976f, 0.681238f),
                    new Vector3f(0.273094f, 0.385234f, 0.700927f),
                    new Vector3f(0.352719f, 0.340652f, 0.727736f),
                    new Vector3f(0.422696f, 0.285446f, 0.760933f),
                    new Vector3f(0.481115f, 0.221123f, 0.799613f),
                    new Vector3f(0.526384f, 0.149436f, 0.84272f),
                    new Vector3f(0.557267f, 0.072342f, 0.889079f),
                    new Vector3f(0.572922f, -0.008057f, 0.937425f),
                    new Vector3f(0.572922f, -0.089567f, 0.98644f),
                    new Vector3f(0.557267f, -0.169966f, 1.03479f),
                    new Vector3f(0.526384f, -0.24706f, 1.08115f),
                    new Vector3f(0.481116f, -0.318747f, 1.12425f),
                    new Vector3f(0.422696f, -0.38307f, 1.16293f),
                    new Vector3f(0.352719f, -0.438276f, 1.19613f),
                    new Vector3f(0.273094f, -0.482858f, 1.22294f),
                    new Vector3f(0.185993f, -0.515601f, 1.24263f),
                    new Vector3f(0.09379f, -0.53561f, 1.25466f)
            )),

            //Main DHD crystal
            new RayCastedButton(101, List.of(
                    new Vector3f(0.035586f, -0.073145f, 0.820082f),
                    new Vector3f(0.063976f, -0.118672f, 0.847439f),
                    new Vector3f(0.035587f, -0.164197f, 0.874798f),
                    new Vector3f(-0.035394f, -0.164193f, 0.874799f),
                    new Vector3f(-0.063782f, -0.118601f, 0.847402f),
                    new Vector3f(-0.035393f, -0.073144f, 0.820084f)
            )),

            //DHD Glyph crystal - Top left slot
            new RayCastedButton(102, List.of(
                    new Vector3f(0.127088f, 0.04991f, 0.396972f),
                    new Vector3f(0.127088f, 0.058796f, 0.461906f),
                    new Vector3f(0.11727f, 0.058796f, 0.461906f),
                    new Vector3f(0.11727f, 0.04991f, 0.396972f)
            )),

            //Efficiency crystal - Top right slot
            new RayCastedButton(103, List.of(
                    new Vector3f(-0.115337f, 0.058793f, 0.396972f),
                    new Vector3f(-0.115337f, 0.058796f, 0.461906f),
                    new Vector3f(-0.125155f, 0.058796f, 0.461906f),
                    new Vector3f(-0.125155f, 0.058793f, 0.396972f)
            )),

            //Capacity crystal - Middle left slot
            new RayCastedButton(104, List.of(
                    new Vector3f(0.083992f, 0.058793f, 0.266573f),
                    new Vector3f(0.083992f, 0.058796f, 0.331507f),
                    new Vector3f(0.074174f, 0.058796f, 0.331507f),
                    new Vector3f(0.074174f, 0.058793f, 0.266573f)
            )),

            //Avenger Virus crystal - Middle right slot
            new RayCastedButton(105, List.of(
                    new Vector3f(-0.072241f, 0.058793f, 0.266573f),
                    new Vector3f(-0.072241f, 0.058796f, 0.331507f),
                    new Vector3f(-0.082059f, 0.058796f, 0.331507f),
                    new Vector3f(-0.082059f, 0.058793f, 0.266573f)
            )),

            //Redirect crystal slot - Bottom slot
            new RayCastedButton(106, List.of(
                    new Vector3f(0.005876f, 0.058793f, 0.123055f),
                    new Vector3f(0.005876f, 0.058796f, 0.187989f),
                    new Vector3f(-0.003942f, 0.058796f, 0.187989f),
                    new Vector3f(-0.003942f, 0.058793f, 0.123055f)
            )),

            //Naqudah tank
            new RayCastedButton(107, List.of(
                    new Vector3f(0.046942f, 0.058796f, 0.251592f),
                    new Vector3f(0.046942f, 0.058796f, 0.476907f),
                    new Vector3f(-0.046942f, 0.058796f, 0.476907f),
                    new Vector3f(-0.046942f, 0.058796f, 0.251592f)
            )),

            //Upgrade area cover
            new RayCastedButton(108, List.of(
                    new Vector3f(0.055437f, 0.213666f, 0.135635f),
                    new Vector3f(0.076547f, 0.204962f, 0.208602f),
                    new Vector3f(0.100799f, 0.196163f, 0.281762f),
                    new Vector3f(0.126313f, 0.18741f, 0.354366f),
                    new Vector3f(0.149959f, 0.178993f, 0.426928f),
                    new Vector3f(0.170176f, 0.171013f, 0.499105f),
                    new Vector3f(0.143924f, 0.167542f, 0.534167f),
                    new Vector3f(0.111409f, 0.165576f, 0.554037f),
                    new Vector3f(0.075813f, 0.165305f, 0.556769f),
                    new Vector3f(0.040621f, 0.166757f, 0.542097f),
                    new Vector3f(0.009277f, 0.16979f, 0.511456f),
                    new Vector3f(0f, 0.171012f, 0.499105f),
                    new Vector3f(-0.009277f, 0.16979f, 0.511456f),
                    new Vector3f(-0.040621f, 0.166757f, 0.542097f),
                    new Vector3f(-0.075813f, 0.165305f, 0.556769f),
                    new Vector3f(-0.111409f, 0.165575f, 0.554037f),
                    new Vector3f(-0.143925f, 0.167542f, 0.534167f),
                    new Vector3f(-0.170177f, 0.171012f, 0.499105f),
                    new Vector3f(-0.14996f, 0.178993f, 0.426928f),
                    new Vector3f(-0.126314f, 0.18741f, 0.354366f),
                    new Vector3f(-0.100799f, 0.196163f, 0.281762f),
                    new Vector3f(-0.076548f, 0.204962f, 0.208602f),
                    new Vector3f(-0.055437f, 0.213666f, 0.135635f),
                    new Vector3f(-0.035593f, 0.215664f, 0.118621f),
                    new Vector3f(-0.012265f, 0.216715f, 0.109676f),
                    new Vector3f(0.012264f, 0.216715f, 0.109676f),
                    new Vector3f(0.035592f, 0.215664f, 0.118621f)
            ))
    );

    @Override
    public boolean testBlockState(BlockState blockState) {
        return blockState.is(JSGBlocks.DHD_MILKYWAY.get());
    }

    @Override
    protected List<RayCastedButton> getButtons() {
        return BUTTONS;
    }
}
