package dev.tauri.jsg.client.renderer.blockentity.stargate;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.core.client.renderer.Activation;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChevronTextureList {

    //	private String chevronTextureBase;

    // Saved
    private final List<ChevronEnum> activeChevrons = new ArrayList<>(9);
    //	private int activeChevrons;
    //	private boolean isFinalActive;

    // Not saved
    public Map<ChevronEnum, Integer> CHEVRON_STATE_MAP = new HashMap<>(9);
    private final List<Activation<ChevronEnum>> activationList = new ArrayList<>();

    private final Map<BiomeOverlayInstance, ResourceLocation> CHEVRON_RESOURCE_MAP = new HashMap<>();
    private final Map<BiomeOverlayInstance, ResourceLocation> CHEVRON_LIGHT_RESOURCE_MAP = new HashMap<>();
    private final Map<BiomeOverlayInstance, ResourceLocation> CHEVRON_LIGHT_RESOURCE_MAP_OFF = new HashMap<>();

    public ChevronTextureList(ITextureLoader textureLoader, String chevronTextureBase) {
        for (BiomeOverlayInstance biomeOverlay : BiomeOverlayInstance.values()) {
            CHEVRON_LIGHT_RESOURCE_MAP.put(biomeOverlay, textureLoader.getTextureResource(chevronTextureBase + "_light" + biomeOverlay.suffix() + ".webp"));
            CHEVRON_LIGHT_RESOURCE_MAP_OFF.put(biomeOverlay, textureLoader.getTextureResource(chevronTextureBase + "_light_off" + biomeOverlay.suffix() + ".webp"));
            CHEVRON_RESOURCE_MAP.put(biomeOverlay, textureLoader.getTextureResource(chevronTextureBase + biomeOverlay.suffix() + ".webp"));
        }
    }

    public ChevronTextureList(ITextureLoader textureLoader, String chevronTextureBase, int activeChevrons, boolean isFinalActive) {
        this(textureLoader, chevronTextureBase);

        if (isFinalActive) activeChevrons--;

        for (int i = 0; i < activeChevrons; i++)
            this.activeChevrons.add(ChevronEnum.valueOf(i));

        if (isFinalActive) this.activeChevrons.add(ChevronEnum.getFinal());
    }

    public void initClient() {
        for (ChevronEnum chevron : ChevronEnum.values()) {
            CHEVRON_STATE_MAP.put(chevron, activeChevrons.contains(chevron) ? 10 : 0);
        }
    }

    public void iterate(Level world, double partialTicks) {
        Activation.iterate(activationList, world.getGameTime(), partialTicks, (index, stage) -> CHEVRON_STATE_MAP.put(index, Math.round(stage)));
    }

    public ResourceLocation get(BiomeOverlayInstance overlayEnum, ChevronEnum chevron, boolean onlyLight) {
        if (onlyLight) {
            if (getState(chevron) < 1)
                return CHEVRON_LIGHT_RESOURCE_MAP_OFF.get(overlayEnum);
            return CHEVRON_LIGHT_RESOURCE_MAP.get(overlayEnum);
        }
        return CHEVRON_RESOURCE_MAP.get(overlayEnum);
    }

    public int getState(ChevronEnum chevron) {
        return CHEVRON_STATE_MAP.get(chevron);
    }

    public float getColor(ChevronEnum chevron) {
        return (this.getState(chevron) / 10f);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(activeChevrons.size());

        for (ChevronEnum chevron : activeChevrons) {
            buf.writeInt(chevron.index);
        }
    }

    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        activeChevrons.clear();

        for (int i = 0; i < size; i++) {
            activeChevrons.add(ChevronEnum.valueOf(buf.readInt()));
        }
    }
}
