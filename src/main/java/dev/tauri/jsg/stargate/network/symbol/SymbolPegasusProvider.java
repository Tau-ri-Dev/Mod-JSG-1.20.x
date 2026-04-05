package dev.tauri.jsg.stargate.network.symbol;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.client.model.IModelLoader;
import dev.tauri.jsg.core.client.screen.tab.ITab;
import dev.tauri.jsg.core.client.screen.tab.tabs.TabAddress;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.SymbolUsage;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.registry.JSGBlocks;
import dev.tauri.jsg.registry.JSGItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum.*;

public class SymbolPegasusProvider extends SymbolType<SymbolPegasusEnum> {
    @Override
    public SymbolPegasusEnum getFirstValidForAddress() {
        return ROEHI;
    }

    @Override
    public ITextureLoader getTextureLoader() {
        return JSGApi.JSG_LOADERS_HOLDER.texture();
    }

    @Override
    public IModelLoader getModelLoader() {
        return JSGApi.JSG_LOADERS_HOLDER.model();
    }

    @Override
    public SymbolUsage getSymbolUsage() {
        return JSGSymbolUsages.STARGATES.get();
    }

    @Override
    public IPointOfOriginType getPointOfOriginType() {
        return StargateTypes.PEGASUS.get();
    }

    @Override
    public SymbolPegasusEnum getBRB() {
        return BBB;
    }

    @Override
    public int[] getAncientTitlePos() {
        return new int[]{330, 18};
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITab.ITabBuilder finalizeAddressTab(ITab.ITabBuilder builder) {
        return builder.setTexture(JSGMapping.rl(JSG.MOD_ID, "textures/gui/container_stargate.png"), 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public TabAddress.SymbolCoords getSymbolCoords(int symbol) {
        return new TabAddress.SymbolCoords(29 + 34 * (symbol % 3), 20 + 28 * (symbol / 3));
    }

    @Override
    public SymbolPegasusEnum[] getValues() {
        return SymbolPegasusEnum.values();
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.get();
    }

    @Override
    public Item getGlyphUpgrade() {
        return JSGItems.CRYSTAL_GLYPH_PEGASUS.get();
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_PEGASUS.get();
    }

    @Override
    public int getIconWidth() {
        return 27;
    }

    @Override
    public int getIconHeight() {
        return 27;
    }

    public SymbolPegasusEnum getRandomSymbol(Random random) {
        int id;
        do {
            id = random.nextInt(38);
        } while (valueOf(id) == null || !valueOf(id).isValidForAddress() || id == SUBIDO.id);

        return valueOf(id);
    }

    @Override
    public boolean validateDialedAddress(IAddress stargateAddress) {
        if (stargateAddress.getSize() < 7) return false;

        return stargateAddress.get(stargateAddress.getSize() - 1).origin();
    }

    public int getMinimalSymbolCountTo(SymbolType<?> symbolType, boolean localDial) {

        boolean eightChevrons = JSGConfig.Stargate.pegAndMilkUseEightChevrons.get();

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.MILKYWAY.getId()))
            return (localDial && !eightChevrons) ? 7 : 8;

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.PEGASUS.getId()))
            return localDial ? 7 : 8;

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.UNIVERSE.getId()))
            return 9;
        return symbolType.getMinimalSymbolCountTo(JSGSymbolTypes.PEGASUS.get(), localDial);
    }

    public SymbolPegasusEnum getOrigin() {
        return SUBIDO;
    }

    public int getMaxSymbolsDisplay(boolean hasUpgrade) {
        return hasUpgrade ? 8 : 6;
    }

    public float getAnglePerGlyph() {
        return 1;
    }

    @Override
    public SymbolPegasusEnum getSymbolByAngle(float angle, float bounds) {
        return getOrigin();
    }

    public SymbolPegasusEnum getTopSymbol() {
        return SUBIDO;
    }

    private static final Map<Integer, SymbolPegasusEnum> ID_MAP = new HashMap<>();
    private static final Map<String, SymbolPegasusEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
            ID_MAP.put(symbol.id, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
        ENGLISH_NAME_MAP.put("point of origin", SUBIDO);
    }

    public SymbolPegasusEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    public SymbolPegasusEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase());
    }
}
