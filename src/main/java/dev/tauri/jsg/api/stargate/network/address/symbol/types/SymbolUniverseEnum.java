package dev.tauri.jsg.api.stargate.network.address.symbol.types;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.client.LoadersHolder;
import dev.tauri.jsg.api.registry.BiomeOverlayRegistry;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.util.I18n;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static dev.tauri.jsg.api.stargate.network.address.symbol.SymbolTypeRegistry.UNIVERSE;

public enum SymbolUniverseEnum implements SymbolInterface {
    TOP_CHEVRON(0, null),
    G1(1, "01.obj"),
    G2(2, "02.obj"),
    G3(3, "03.obj"),
    G4(4, "04.obj"),
    G5(5, "05.obj"),
    G6(6, "06.obj"),
    G7(7, "07.obj"),
    G8(8, "08.obj"),
    G9(9, "09.obj"),
    G10(10, "10.obj"),
    G11(11, "11.obj"),
    G12(12, "12.obj"),
    G13(13, "13.obj"),
    G14(14, "14.obj"),
    G15(15, "15.obj"),
    G16(16, "16.obj"),
    G17(17, "17.obj"),
    G18(18, "18.obj"),
    G19(19, "19.obj"),
    G20(20, "20.obj"),
    G21(21, "21.obj"),
    G22(22, "22.obj"),
    G23(23, "23.obj"),
    G24(24, "24.obj"),
    G25(25, "25.obj"),
    G26(26, "26.obj"),
    G27(27, "27.obj"),
    G28(28, "28.obj"),
    G29(29, "29.obj"),
    G30(30, "30.obj"),
    G31(31, "31.obj"),
    G32(32, "32.obj"),
    G33(33, "33.obj"),
    G34(34, "34.obj"),
    G35(35, "35.obj"),
    G36(36, "36.obj");


    public static final int ANGLE_PER_SECTION = 8;

    public final int id;
    public ResourceLocation modelResource;
    public final int angle;
    public final int angleIndex;
    public final String englishName;
    public final String translationKey;
    public final ResourceLocation iconResource;

    SymbolUniverseEnum(int id, String model) {
        this.id = id;

        if (model != null)
            this.modelResource = LoadersHolder.JSG_HOLDER.model().getModelResource("universe/" + model);

        int id0 = id - 1;
        this.angleIndex = id0 + id0 / 4 + 1; // skip one each 4
        this.angle = 360 - (angleIndex * ANGLE_PER_SECTION);
        this.englishName = "Glyph " + id;
        this.translationKey = "glyph.jsg.universe.g" + id;
        this.iconResource = JSGApi.rl("textures/gui/symbol/universe/g" + id + ".png");
    }

    @Override
    public boolean origin() {
        return this == G17;
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public int getAngleIndex() {
        return angleIndex;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    @Override
    public String toString() {
        return getEnglishName();
    }

    @Override
    public ResourceLocation getIconResource(BiomeOverlayRegistry.BiomeOverlayInstance overlay, ResourceKey<Level> dimensionId, int configOrigin) {
        return iconResource;
    }

    @Override
    public String localize() {
        return I18n.format(translationKey);
    }

    @Override
    public AbstractSymbolType<SymbolUniverseEnum> getSymbolType() {
        return UNIVERSE;
    }

    @Override
    public boolean isValidForAddress() {
        return this != TOP_CHEVRON && !origin();
    }

    @Override
    public SymbolInterface getNext(boolean previous) {
        var id = this.getId();
        while (true) {
            id += (previous ? -1 : 1);
            if (id < 0) id = 36;
            id = id % 37;
            var symbol = UNIVERSE.valueOf(id);
            if (symbol != null && symbol.isValidForAddress()) return symbol;
        }
    }

    @SuppressWarnings("all")
    @NotNull
    public static AbstractSymbolType<SymbolUniverseEnum> getProvider() {
        try {
            return (AbstractSymbolType<SymbolUniverseEnum>) Class.forName("dev.tauri.jsg.stargate.network.symbol.SymbolUniverseProvider").getConstructor().newInstance();
        } catch (Exception e) {
            JSGApi.logger.error("Error while getting symbol provider: ", e);
        }
        return null;
    }
}
