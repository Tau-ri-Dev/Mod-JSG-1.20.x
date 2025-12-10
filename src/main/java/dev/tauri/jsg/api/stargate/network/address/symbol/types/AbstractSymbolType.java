package dev.tauri.jsg.api.stargate.network.address.symbol.types;

import dev.tauri.jsg.api.client.LoadersHolder;
import dev.tauri.jsg.api.client.model.IModelLoader;
import dev.tauri.jsg.api.client.screen.ITab;
import dev.tauri.jsg.api.client.screen.ITabAddress;
import dev.tauri.jsg.api.client.texture.ITextureLoader;
import dev.tauri.jsg.api.stargate.network.address.IAddress;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolTypeRegistry;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolUsage;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public abstract class AbstractSymbolType<T extends SymbolInterface> {

    public static AbstractSymbolType<?> byId(int id) {
        return SymbolTypeRegistry.byId(id);
    }

    public static AbstractSymbolType<?> byId(String id) {
        return SymbolTypeRegistry.byId(id);
    }

    public static int getId(AbstractSymbolType<?> type) {
        return SymbolTypeRegistry.getId(type);
    }

    public static AbstractSymbolType<?>[] values(@Nullable SymbolUsage usage) {
        return SymbolTypeRegistry.values(usage);
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AbstractSymbolType<?> s)) return false;
        return Objects.equals(s.getId(), getId());
    }

    public boolean hasOrigin() {
        return getOrigin() != null;
    }

    public abstract int[] getAncientTitlePos();

    @OnlyIn(Dist.CLIENT)
    public abstract ITab.ITabBuilder finalizeAddressTab(ITab.ITabBuilder builder);

    @OnlyIn(Dist.CLIENT)
    public abstract ITabAddress.SymbolCoords getSymbolCoords(int symbol);

    public abstract T[] getValues();

    public abstract Block getBaseBlock();

    public abstract Item getGlyphUpgrade();

    public abstract Block getDHDBlock();

    public abstract String getId();

    public abstract T getBRB();

    public abstract int getIconWidth();

    public abstract int getIconHeight();

    public abstract T getRandomSymbol(Random random);

    public abstract T getOrigin();

    public abstract int getMaxSymbolsDisplay(boolean hasUpgrade);

    public abstract int getMinimalSymbolCountTo(AbstractSymbolType<?> symbolType, boolean localDial);

    public abstract boolean validateDialedAddress(IAddress address);

    public abstract float getAnglePerGlyph();

    public float getAngleByAngIndex(int index) {
        return index;
    }

    public float getAngleOfNearest(float angle) {
        return 0;
    }

    public abstract T getSymbolByAngle(float angle, float bounds);

    public T getSymbolByAngle(float angle) {
        return getSymbolByAngle(angle, 360);
    }

    public abstract T getTopSymbol();

    public abstract T valueOf(int id);

    public abstract T fromEnglishName(String englishName);

    public abstract T getFirstValidForAddress();

    public ITextureLoader getTextureLoader() {
        return LoadersHolder.JSG_HOLDER.texture();
    }

    public IModelLoader getModelLoader() {
        return LoadersHolder.JSG_HOLDER.model();
    }
}
