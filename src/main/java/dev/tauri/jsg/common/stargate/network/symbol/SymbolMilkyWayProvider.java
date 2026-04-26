package dev.tauri.jsg.common.stargate.network.symbol;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.core.client.model.IModelLoader;
import dev.tauri.jsg.core.client.screen.tab.ITab;
import dev.tauri.jsg.core.client.screen.tab.ITabAddress;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.SymbolUsage;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum.*;

public class SymbolMilkyWayProvider extends SymbolType<SymbolMilkyWayEnum> {
    @Override
    public SymbolMilkyWayEnum getFirstValidForAddress() {
        return SCULPTOR;
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
        return StargateTypes.MILKYWAY.get();
    }

    @Override
    public SymbolMilkyWayEnum getBRB() {
        return BRB;
    }

    @Override
    public int[] getAncientTitlePos() {
        return new int[]{330, 0};
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITab.ITabBuilder finalizeAddressTab(ITab.ITabBuilder builder) {
        return builder.setTexture(JSGMapping.rl(JSG.MOD_ID, "textures/gui/container_stargate.png"), 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITabAddress.SymbolCoords getSymbolCoords(int symbol) {
        return new ITabAddress.SymbolCoords(29 + 31 * (symbol % 3), 20 + 28 * (symbol / 3));
    }

    @Override
    public SymbolMilkyWayEnum[] getValues() {
        return SymbolMilkyWayEnum.values();
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK.get();
    }

    @Override
    public Item getGlyphUpgrade() {
        return JSGItems.CRYSTAL_GLYPH_MILKYWAY.get();
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_MILKYWAY.get();
    }

    @Override
    public int getIconWidth() {
        return 32;
    }

    @Override
    public int getIconHeight() {
        return 32;
    }

    @Override
    public SymbolMilkyWayEnum getSymbolByAngle(float angle, float bounds) {
        SymbolMilkyWayEnum closest = null;
        var closestAngle = Float.MAX_VALUE;
        while (angle < 0) {
            angle += 360;
        }
        angle = (angle % 360);
        for (var symbol : SymbolMilkyWayEnum.values()) {
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
        if (closestAngle >= bounds) return null;
        if (closest != null) return closest;
        return getOrigin();
    }

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

    public float getAngleByAngIndex(int index) {
        if (index < 0) index = 0;
        if (index > 38) index = 38;
        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            if (symbol.angleIndex == index) {
                return symbol.angle;
            }
        }
        return 0;
    }

    @Override
    public SymbolMilkyWayEnum getRandomSymbol(Random random) {
        int id;
        do {
            id = random.nextInt(38);
        } while (valueOf(id) == null || !valueOf(id).isValidForAddress() || id == ORIGIN.id);

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
        boolean eightChevrons = JSGConfig.Stargate.pegAndMilkUseEightChevrons.get();

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.PEGASUS.getId()))
            return (localDial && !eightChevrons) ? 7 : 8;

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.MILKYWAY.getId()))
            return localDial ? 7 : 8;

        if (Objects.equals(symbolType.getId(), JSGSymbolTypes.UNIVERSE.getId()))
            return 9;
        return symbolType.getMinimalSymbolCountTo(JSGSymbolTypes.MILKYWAY.get(), localDial);

    }

    @Override
    public SymbolMilkyWayEnum getOrigin() {
        return ORIGIN;
    }

    @Override
    public int getMaxSymbolsDisplay(boolean hasUpgrade) {
        return hasUpgrade ? 8 : 6;
    }

    @Override
    public float getAnglePerGlyph() {
        return ANGLE_PER_GLYPH;
    }

    @Override
    public SymbolMilkyWayEnum getTopSymbol() {
        return ORIGIN;
    }

    private static final Map<Integer, SymbolMilkyWayEnum> ID_MAP = new HashMap<>();
    private static final Map<String, SymbolMilkyWayEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            ID_MAP.put(symbol.id, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
    }

    @Override
    public SymbolMilkyWayEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    @Override
    public SymbolMilkyWayEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }
}