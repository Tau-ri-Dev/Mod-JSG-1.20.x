package dev.tauri.jsg.raycaster.instances;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.core.common.raycaster.util.RayCastedButton;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RaycasterPegasusDHD extends RaycasterDHD {
    public static final List<RayCastedButton> BUTTONS = List.of(
            //Outer ring
            //Danami
            new RayCastedButton(0, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.331697f, 0.355341f, 0.790171f),
                    new Vector3f(0.227268f, 0.244546f, 0.870042f),
                    new Vector3f(0.124619f, 0.292153f, 0.841415f),
                    new Vector3f(0.18145f, 0.425024f, 0.748269f)
            )),

            //Arami
            new RayCastedButton(1, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.454873f, 0.242572f, 0.857983f),
                    new Vector3f(0.311907f, 0.167156f, 0.916579f),
                    new Vector3f(0.232858f, 0.240746f, 0.872328f),
                    new Vector3f(0.339168f, 0.350287f, 0.79321f)
            )),

            //Setas
            new RayCastedButton(2, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.528648f, 0.101636f, 0.942732f),
                    new Vector3f(0.362638f, 0.070406f, 0.974758f),
                    new Vector3f(0.315754f, 0.162006f, 0.919676f),
                    new Vector3f(0.460023f, 0.235712f, 0.862108f)
            )),

            //Aldeni
            new RayCastedButton(3, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.545028f, -0.052191f, 1.03523f),
                    new Vector3f(0.373963f, -0.035218f, 1.03827f),
                    new Vector3f(0.364325f, 0.064465f, 0.978331f),
                    new Vector3f(0.53092f, 0.093715f, 0.947495f)
            )),

            //Aaxel
            new RayCastedButton(4, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.502237f, -0.202242f, 1.12546f),
                    new Vector3f(0.344656f, -0.13827f, 1.10024f),
                    new Vector3f(0.373308f, -0.041307f, 1.04193f),
                    new Vector3f(0.544176f, -0.060315f, 1.04012f)
            )),

            //Bydo
            new RayCastedButton(5, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.404913f, -0.332256f, 1.20364f),
                    new Vector3f(0.277891f, -0.227584f, 1.15395f),
                    new Vector3f(0.341729f, -0.143847f, 1.1036f),
                    new Vector3f(0.498353f, -0.209689f, 1.12994f)
            )),

            //Avoniv
            new RayCastedButton(6, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.263602f, -0.428143f, 1.2613f),
                    new Vector3f(0.180905f, -0.29348f, 1.19357f),
                    new Vector3f(0.27301f, -0.232044f, 1.15663f),
                    new Vector3f(0.398418f, -0.338219f, 1.20723f)
            )),

            //Ecrumig
            new RayCastedButton(7, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.093618f, -0.479514f, 1.29219f),
                    new Vector3f(0.064206f, -0.328818f, 1.21482f),
                    new Vector3f(0.174598f, -0.29634f, 1.19529f),
                    new Vector3f(0.2552f, -0.431976f, 1.26361f)
            )),

            //Laylox
            new RayCastedButton(8, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.08662f, -0.4808f, 1.29297f),
                    new Vector3f(-0.059558f, -0.329769f, 1.21539f),
                    new Vector3f(0.057158f, -0.329769f, 1.21539f),
                    new Vector3f(0.084219f, -0.480801f, 1.29297f)
            )),

            //Ca po
            new RayCastedButton(9, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.257579f, -0.431863f, 1.26354f),
                    new Vector3f(-0.176976f, -0.296228f, 1.19523f),
                    new Vector3f(-0.066585f, -0.328706f, 1.21476f),
                    new Vector3f(-0.095997f, -0.479402f, 1.29213f)
            )),

            //Alura
            new RayCastedButton(10, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.400733f, -0.338006f, 1.2071f),
                    new Vector3f(-0.275325f, -0.231832f, 1.1565f),
                    new Vector3f(-0.18322f, -0.293268f, 1.19344f),
                    new Vector3f(-0.265918f, -0.427931f, 1.26117f)
            )),

            //Lenchan
            new RayCastedButton(11, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.50057f, -0.209399f, 1.12977f),
                    new Vector3f(-0.343946f, -0.143557f, 1.10342f),
                    new Vector3f(-0.280108f, -0.227294f, 1.15377f),
                    new Vector3f(-0.407131f, -0.331967f, 1.20347f)
            )),

            //Acjesis
            new RayCastedButton(12, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.546271f, -0.05998f, 1.03991f),
                    new Vector3f(-0.375403f, -0.040971f, 1.04173f),
                    new Vector3f(-0.346751f, -0.137935f, 1.10004f),
                    new Vector3f(-0.504333f, -0.201907f, 1.12526f)
            )),

            //Dawnre
            new RayCastedButton(13, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.532883f, 0.09406f, 0.947285f),
                    new Vector3f(-0.366287f, 0.06481f, 0.978122f),
                    new Vector3f(-0.375926f, -0.034873f, 1.03806f),
                    new Vector3f(-0.546991f, -0.051847f, 1.03502f)
            )),

            //Subido
            new RayCastedButton(14, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.461856f, 0.236029f, 0.861916f),
                    new Vector3f(-0.317587f, 0.162322f, 0.919485f),
                    new Vector3f(-0.364472f, 0.070723f, 0.974566f),
                    new Vector3f(-0.530482f, 0.101953f, 0.942539f)
            )),

            //Zamilloz
            new RayCastedButton(15, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.340889f, 0.350541f, 0.793056f),
                    new Vector3f(-0.23458f, 0.241f, 0.872173f),
                    new Vector3f(-0.313629f, 0.16741f, 0.916425f),
                    new Vector3f(-0.456595f, 0.242826f, 0.857828f)
            )),

            //Recktic
            new RayCastedButton(16, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.183089f, 0.425188f, 0.748169f),
                    new Vector3f(-0.12626f, 0.292317f, 0.841315f),
                    new Vector3f(-0.228909f, 0.244711f, 0.869942f),
                    new Vector3f(-0.333338f, 0.355506f, 0.79007f)
            )),

            //Robandus
            new RayCastedButton(17, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.005557f, 0.45188f, 0.732118f),
                    new Vector3f(-0.004366f, 0.310712f, 0.830254f),
                    new Vector3f(-0.11949f, 0.294249f, 0.840154f),
                    new Vector3f(-0.174066f, 0.427783f, 0.746609f)
            )),

            //Unknown 1
            new RayCastedButton(18, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.172469f, 0.427726f, 0.746644f),
                    new Vector3f(0.117893f, 0.294192f, 0.840188f),
                    new Vector3f(0.002769f, 0.310656f, 0.830288f),
                    new Vector3f(0.00396f, 0.451824f, 0.732153f)
            )),

            //Inner Ring
            //Zeo
            new RayCastedButton(19, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.056602f, -0.29104f, 1.22707f),
                    new Vector3f(-0.030412f, -0.137358f, 1.16711f),
                    new Vector3f(0.027995f, -0.137357f, 1.16711f),
                    new Vector3f(0.054185f, -0.29104f, 1.22707f)

            )),

            //Tahnan
            new RayCastedButton(20, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.165358f, -0.259584f, 1.20816f),
                    new Vector3f(-0.087789f, -0.12074f, 1.15711f),
                    new Vector3f(-0.032546f, -0.136992f, 1.16689f),
                    new Vector3f(-0.060574f, -0.290412f, 1.2267f)
            )),

            //Elenami
            new RayCastedButton(21, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.256303f, -0.19957f, 1.17207f),
                    new Vector3f(-0.135761f, -0.089056f, 1.13806f),
                    new Vector3f(-0.089669f, -0.1198f, 1.15655f),
                    new Vector3f(-0.168877f, -0.257884f, 1.20714f)
            )),

            //Hamlinto
            new RayCastedButton(22, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.319583f, -0.1175f, 1.12272f),
                    new Vector3f(-0.16913f, -0.04574f, 1.11202f),
                    new Vector3f(-0.137183f, -0.087644f, 1.13722f),
                    new Vector3f(-0.258988f, -0.196982f, 1.17051f)
            )),

            //Salma
            new RayCastedButton(23, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.348338f, -0.022268f, 1.06545f),
                    new Vector3f(-0.184278f, 0.004514f, 1.0818f),
                    new Vector3f(-0.16994f, -0.044009f, 1.11098f),
                    new Vector3f(-0.321142f, -0.114305f, 1.1208f)
            )),

            //Abrin
            new RayCastedButton(24, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.339454f, 0.075805f, 1.00648f),
                    new Vector3f(-0.179565f, 0.05626f, 1.05068f),
                    new Vector3f(-0.184389f, 0.006377f, 1.08068f),
                    new Vector3f(-0.348603f, -0.018813f, 1.06338f)
            )),

            //Poco Re
            new RayCastedButton(25, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.293893f, 0.166092f, 0.952186f),
                    new Vector3f(-0.155502f, 0.103892f, 1.02204f),
                    new Vector3f(-0.178964f, 0.058053f, 1.0496f),
                    new Vector3f(-0.338395f, 0.079146f, 1.00447f)
            )),

            //Hacemill
            new RayCastedButton(26, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.216592f, 0.238809f, 0.90846f),
                    new Vector3f(-0.114696f, 0.142246f, 0.998974f),
                    new Vector3f(-0.154254f, 0.10542f, 1.02112f),
                    new Vector3f(-0.291626f, 0.168958f, 0.950464f)
            )),

            //Olavii
            new RayCastedButton(27, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.115929f, 0.286076f, 0.880037f),
                    new Vector3f(-0.061569f, 0.167168f, 0.983988f),
                    new Vector3f(-0.112937f, 0.143345f, 0.998314f),
                    new Vector3f(-0.213362f, 0.240889f, 0.90721f)
            )),

            //Ramnon
            new RayCastedButton(28, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(-0.00281f, 0.302771f, 0.869998f),
                    new Vector3f(-0.001878f, 0.175956f, 0.978704f),
                    new Vector3f(-0.059489f, 0.167717f, 0.983658f),
                    new Vector3f(-0.112086f, 0.287145f, 0.879395f)
            )),

            //Unknown 2
            new RayCastedButton(29, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.110504f, 0.287085f, 0.87943f),
                    new Vector3f(0.057908f, 0.167658f, 0.983694f),
                    new Vector3f(0.000297f, 0.175897f, 0.97874f),
                    new Vector3f(0.001229f, 0.302713f, 0.870034f)
            )),

            //Gilltin
            new RayCastedButton(30, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.211736f, 0.240717f, 0.907313f),
                    new Vector3f(0.111311f, 0.143173f, 0.998417f),
                    new Vector3f(0.059943f, 0.166996f, 0.984092f),
                    new Vector3f(0.114302f, 0.285905f, 0.880141f)
            )),

            //Sibbron
            new RayCastedButton(31, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.289915f, 0.168693f, 0.950624f),
                    new Vector3f(0.152543f, 0.105154f, 1.02128f),
                    new Vector3f(0.112985f, 0.141981f, 0.999134f),
                    new Vector3f(0.214881f, 0.238545f, 0.90862f)
            )),

            //Amiwill
            new RayCastedButton(32, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.336568f, 0.078816f, 1.00467f),
                    new Vector3f(0.177137f, 0.057723f, 1.0498f),
                    new Vector3f(0.153675f, 0.103562f, 1.02224f),
                    new Vector3f(0.292066f, 0.165763f, 0.952386f)
            )),

            //Illume
            new RayCastedButton(33, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.346641f, -0.019172f, 1.06359f),
                    new Vector3f(0.182427f, 0.006017f, 1.08089f),
                    new Vector3f(0.177604f, 0.055901f, 1.0509f),
                    new Vector3f(0.337493f, 0.075446f, 1.0067f)
            )),

            //Sandovi
            new RayCastedButton(34, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.319042f, -0.114655f, 1.12101f),
                    new Vector3f(0.16784f, -0.044359f, 1.11119f),
                    new Vector3f(0.182179f, 0.004165f, 1.08201f),
                    new Vector3f(0.346239f, -0.022617f, 1.06567f)
            )),

            //Baselai
            new RayCastedButton(35, JSGSymbolTypes.PEGASUS, List.of( //pokracovat tady (continue here)
                    new Vector3f(0.256762f, -0.197284f, 1.1707f),
                    new Vector3f(0.134957f, -0.087946f, 1.1374f),
                    new Vector3f(0.166903f, -0.046042f, 1.1122f),
                    new Vector3f(0.317357f, -0.117801f, 1.1229f)
            )),

            //Once El
            new RayCastedButton(36, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.166549f, -0.258106f, 1.20727f),
                    new Vector3f(0.087341f, -0.120022f, 1.15668f),
                    new Vector3f(0.133433f, -0.089277f, 1.1382f),
                    new Vector3f(0.253976f, -0.19979f, 1.1722f)
            )),

            //Roehi
            new RayCastedButton(37, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0.05818f, -0.290529f, 1.22677f),
                    new Vector3f(0.030152f, -0.137109f, 1.16696f),
                    new Vector3f(0.085395f, -0.120856f, 1.15719f),
                    new Vector3f(0.162964f, -0.259701f, 1.20823f)
            )),

            //Big Blue Button
            new RayCastedButton(38, JSGSymbolTypes.PEGASUS, List.of(
                    new Vector3f(0f, -0.134298f, 1.15577f),
                    new Vector3f(-0.027319f, -0.132555f, 1.15473f),
                    new Vector3f(-0.056276f, -0.126536f, 1.1511f),
                    new Vector3f(-0.081455f, -0.117285f, 1.14554f),
                    new Vector3f(-0.106562f, -0.103534f, 1.13727f),
                    new Vector3f(-0.126872f, -0.087779f, 1.1278f),
                    new Vector3f(-0.145409f, -0.067786f, 1.11577f),
                    new Vector3f(-0.158649f, -0.047233f, 1.10342f),
                    new Vector3f(-0.168606f, -0.023166f, 1.08894f),
                    new Vector3f(-0.173341f, -0.000042f, 1.07504f),
                    new Vector3f(-0.173641f, 0.025492f, 1.05968f),
                    new Vector3f(-0.169358f, 0.048681f, 1.04574f),
                    new Vector3f(-0.159967f, 0.072915f, 1.03117f),
                    new Vector3f(-0.14713f, 0.093655f, 1.01869f),
                    new Vector3f(-0.129066f, 0.113963f, 1.00648f),
                    new Vector3f(-0.109067f, 0.130008f, 0.996835f),
                    new Vector3f(-0.084287f, 0.144188f, 0.988308f),
                    new Vector3f(-0.059293f, 0.153799f, 0.982529f),
                    new Vector3f(-0.030482f, 0.160315f, 0.97861f),
                    new Vector3f(-0.003201f, 0.16245f, 0.977327f),
                    new Vector3f(0.026517f, 0.160597f, 0.978441f),
                    new Vector3f(0.053129f, 0.155025f, 0.981792f),
                    new Vector3f(0.080535f, 0.145002f, 0.987818f),
                    new Vector3f(0.103594f, 0.132327f, 0.995441f),
                    new Vector3f(0.125718f, 0.115222f, 1.00573f),
                    new Vector3f(0.142724f, 0.096817f, 1.01679f),
                    new Vector3f(0.157168f, 0.074482f, 1.03023f),
                    new Vector3f(0.16628f, 0.052341f, 1.04354f),
                    new Vector3f(0.171479f, 0.027198f, 1.05866f),
                    new Vector3f(0.171709f, 0.003721f, 1.07278f),
                    new Vector3f(0.1671f, -0.021507f, 1.08795f),
                    new Vector3f(0.158422f, -0.043775f, 1.10134f),
                    new Vector3f(0.144504f, -0.066354f, 1.11491f),
                    new Vector3f(0.12786f, -0.085001f, 1.12613f),
                    new Vector3f(0.106141f, -0.102483f, 1.13664f),
                    new Vector3f(0.083334f, -0.115488f, 1.14446f),
                    new Vector3f(0.056168f, -0.12598f, 1.15077f),
                    new Vector3f(0.029669f, -0.131934f, 1.15435f)
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
        return blockState.is(JSGBlocks.DHD_PEGASUS.get());
    }

    @Override
    protected List<RayCastedButton> getButtons() {
        return BUTTONS;
    }
}
