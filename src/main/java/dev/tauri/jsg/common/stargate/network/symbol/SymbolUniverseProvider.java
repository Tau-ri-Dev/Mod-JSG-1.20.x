package dev.tauri.jsg.common.stargate.network.symbol;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Objects;
import java.util.Random;

import static dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum.*;

public class SymbolUniverseProvider extends SymbolType<SymbolUniverseEnum> {
    @Override
    public SymbolUniverseEnum getFirstValidForAddress() {
        return G1;
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
        return StargateTypes.UNIVERSE.get();
    }

    @Override
    public SymbolUniverseEnum getBRB() {
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public TabAddress.SymbolCoords getSymbolCoords(int symbol) {
        return new TabAddress.SymbolCoords(24 + 16 * (symbol % 6), 20 + 45 * (symbol / 6));
    }

    @Override
    public float getAnglePerGlyph() {
        return ANGLE_PER_SECTION;
    }

    @Override
    public SymbolUniverseEnum getSymbolByAngle(float angle, float bounds) {
        SymbolUniverseEnum closest = null;
        var closestAngle = Float.MAX_VALUE;
        while (angle < 0) {
            angle += 360f;
        }
        angle = (angle % 360f);
        for (var symbol : SymbolUniverseEnum.values()) {
            var diffAngle = Math.abs(symbol.angle - angle);
            if (closestAngle > diffAngle) {
                closestAngle = diffAngle;
                closest = symbol;
            }
            diffAngle = Math.abs((symbol.angle + 360) - angle);
            if (closestAngle > diffAngle) {
                closestAngle = diffAngle;
                closest = symbol;
            }
            diffAngle = Math.abs(symbol.angle - (angle + 360));
            if (closestAngle > diffAngle) {
                closestAngle = diffAngle;
                closest = symbol;
            }
        }
        return closest;
    }

    @Override
    public float getAngleOfNearest(float angle) {
        int end = 38;
        int current = 0;

        int loops = 0;
        int temp = end;
        while (current < end) {
            temp = end - current;

            if ((angle < getAngleByAngIndex(temp) && angle < getAngleByAngIndex(temp - 1)) || angle == getAngleByAngIndex(temp))
                return getAngleByAngIndex(temp);
            current++;

            loops++;
            if (loops > 250)
                break;
        }
        return getAngleByAngIndex(temp);
    }

    @Override
    public float getAngleByAngIndex(int index) {
        if (index < 0) index = 0;
        if (index > 36) index = 36;
        for (SymbolUniverseEnum symbol : SymbolUniverseEnum.values()) {
            if (symbol.angleIndex == index) {
                return symbol.angle;
            }
        }
        return 0;
    }

    @Override
    public SymbolUniverseEnum[] getValues() {
        return SymbolUniverseEnum.values();
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get();
    }

    @Override
    public Item getGlyphUpgrade() {
        return JSGItems.CRYSTAL_GLYPH_UNIVERSE.get();
    }

    @Override
    public Block getDHDBlock() {
        return null;
    }

    @Override
    public int getIconWidth() {
        return 20;
    }

    @Override
    public int getIconHeight() {
        return 42;
    }

    @Override
    public SymbolUniverseEnum getRandomSymbol(Random random) {
        int id;
        do {
            id = random.nextInt(36) + 1;
        } while (valueOf(id) == null || !valueOf(id).isValidForAddress() || id == getOrigin().getId());

        return valueOf(id);
    }

    @Override
    public boolean validateDialedAddress(IAddress stargateAddress) {
        if (stargateAddress.getSize() < 7)
            return false;

        return stargateAddress.get(stargateAddress.getSize() - 1).origin();
    }

    @Override
    public int getMinimalSymbolCountTo(SymbolType<?> symbolType, boolean localDial) {
        if (JSGConfig.Stargate.useStrictSevenSymbolsUniGate.get())
            localDial = true;

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.PEGASUS.getId()))
            return 9;

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.MILKYWAY.getId()))
            return 9;

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.UNIVERSE.getId()))
            return localDial ? 7 : 8;
        return symbolType.getMinimalSymbolCountTo(JSGSymbolTypes.UNIVERSE.get(), localDial);
    }

    @Override
    public SymbolUniverseEnum getOrigin() {
        return G17;
    }

    @Override
    public int getMaxSymbolsDisplay(boolean hasUpgrade) {
        return hasUpgrade ? 8 : 6;
    }

    @Override
    public SymbolUniverseEnum getTopSymbol() {
        return TOP_CHEVRON;
    }

    @Override
    public SymbolUniverseEnum fromEnglishName(String englishName) {
        var symbol = super.fromEnglishName(englishName);
        if (symbol != null) return symbol;
        try {
            int index = Integer.parseInt(englishName.replace("G", ""));
            if (index < 1 || index >= getValues().length) return null;

            return SymbolUniverseEnum.values()[index];
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
