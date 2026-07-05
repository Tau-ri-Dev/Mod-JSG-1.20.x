package dev.tauri.jsg.common.stargate.network.symbol;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.core.client.model.IModelLoader;
import dev.tauri.jsg.core.client.screen.tab.tabs.TabAddress;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.SymbolUsage;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    @Override
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

    @Override
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

    @Override
    public SymbolPegasusEnum getOrigin() {
        return SUBIDO;
    }

    @Override
    public int getMaxSymbolsDisplay(boolean hasUpgrade) {
        return hasUpgrade ? 8 : 6;
    }

    @Override
    public float getAnglePerGlyph() {
        return 1;
    }

    @Override
    public SymbolPegasusEnum getSymbolByAngle(float angle, float bounds) {
        return getOrigin();
    }

    @Override
    public SymbolPegasusEnum getTopSymbol() {
        return SUBIDO;
    }
}
