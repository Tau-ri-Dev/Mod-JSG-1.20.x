package dev.tauri.jsg.client.renderer.blockentity.dialhomedevice;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.client.renderer.activation.DHDActivation;
import dev.tauri.jsg.core.client.renderer.Activation;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DHDPegasusRendererState extends DHDAbstractRendererState {
    public DHDPegasusRendererState() {
    }

    private static final String SYMBOL_TEXTURE_BASE = "textures/tesr/pegasus/dhd/dhd_button_light_";
    private static final String BRB_TEXTURE_BASE = "textures/tesr/pegasus/dhd/dhd_bbb_";
    private static final String SYMBOL_TEXTURE_END = "png";
    private static final String BRB_TEXTURE_END = "jpg";

    private static final Map<BiomeOverlayInstance, TextureContainer> BIOME_TEXTURE_MAP = new HashMap<>();

    private static class TextureContainer {
        public final Map<Integer, ResourceLocation> SYMBOL_RESOURCE_MAP = new HashMap<>();
        public final Map<Integer, ResourceLocation> BRB_RESOURCE_MAP = new HashMap<>();
    }

    static {
        for (BiomeOverlayInstance bo : BiomeOverlayInstance.values()) {
            // todo: do biome overlays
            var biomeOverlay = CoreBiomeOverlays.NORMAL.get();
            TextureContainer container = new TextureContainer();

            for (int i = 0; i <= 5; i++) {
                container.SYMBOL_RESOURCE_MAP.put(i, JSGMapping.rl(JSG.MOD_ID, SYMBOL_TEXTURE_BASE + i + biomeOverlay.suffix() + "." + SYMBOL_TEXTURE_END));
                container.BRB_RESOURCE_MAP.put(i, JSGMapping.rl(JSG.MOD_ID, BRB_TEXTURE_BASE + i + biomeOverlay.suffix() + "." + BRB_TEXTURE_END));
            }

            BIOME_TEXTURE_MAP.put(bo, container);
        }
    }

    public DHDPegasusRendererState(StargateAddressDynamic addressDialed, boolean brbActive, BiomeOverlayInstance biomeOverride, boolean stargateIsConnected, BEConfig gateConfig) {
        super(addressDialed, brbActive, biomeOverride, stargateIsConnected, gateConfig);
    }

    public DHDPegasusRendererState initClient(BlockPos pos, BiomeOverlayInstance biomeOverlay, boolean stargateIsConnected) {
        super.initClient(pos, biomeOverlay, stargateIsConnected);

        for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
            if (symbol.brb())
                BUTTON_STATE_MAP.put(symbol, brbActive ? 5 : 0);
            else
                BUTTON_STATE_MAP.put(symbol, addressDialed.contains(symbol) ? 5 : 0);
        }

        return this;
    }

    // Symbols
    // Not saved
    private final Map<SymbolInterface, Integer> BUTTON_STATE_MAP = new HashMap<>(38);
    public List<Activation<SymbolInterface>> activationList = new ArrayList<>();

    private boolean isSymbolActiveClientSide(SymbolPegasusEnum symbol) {
        return BUTTON_STATE_MAP.get(symbol) != 0;
    }

    public void clearSymbols(long totalWorldTime) {
        for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
            if (isSymbolActiveClientSide(symbol)) {
                activationList.add(new DHDActivation(symbol, totalWorldTime, true));
            }
        }
    }

    public void activateSymbol(long totalWorldTime, SymbolPegasusEnum symbol) {
        activationList.add(new DHDActivation(symbol, totalWorldTime, false));
    }

    @Override
    public void iterate(Level world, double partialTicks) {
        Activation.iterate(activationList, world.getGameTime(), partialTicks, (index, stage) -> {
            BUTTON_STATE_MAP.put(index, Math.round(stage));
        });
    }

    public ResourceLocation getButtonTexture(SymbolPegasusEnum symbol, BiomeOverlayInstance biomeOverlay) {
        TextureContainer container = BIOME_TEXTURE_MAP.get(biomeOverlay);

        if (symbol.brb())
            return container.BRB_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbol));

        return container.SYMBOL_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbol));
    }

    @Override
    public boolean isButtonActive(SymbolInterface symbol) {
        return BUTTON_STATE_MAP.get(symbol) == 5;
    }

    @Override
    public int getActivatedButtons() {
        int count = 0;
        SymbolInterface origin = JSGSymbolTypes.PEGASUS.get().getOrigin();
        for (int state : BUTTON_STATE_MAP.values()) {
            if (state > 0) count++;
        }
        if (BUTTON_STATE_MAP.get(origin) > 0) count--;
        return count;
    }

    @Override
    public SymbolType<?> getSymbolType() {
        return JSGSymbolTypes.PEGASUS.get();
    }
}