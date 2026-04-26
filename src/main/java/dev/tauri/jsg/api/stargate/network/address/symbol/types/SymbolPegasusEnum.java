package dev.tauri.jsg.api.stargate.network.address.symbol.types;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum SymbolPegasusEnum implements SymbolInterface {
    ROEHI(37, "Roehi", "37.obj", 11),
    ONCEEL(36, "Once El", "36.obj", 35),
    BASELAI(35, "Baselai", "35.obj", 33),
    SANDOVI(34, "Sandovi", "34.obj", 10),
    ILLUME(33, "Illume", "33.obj", 25),
    AMIWILL(32, "Amiwill", "32.obj", 30),
    SIBBRON(31, "Sibbron", "31.obj", 0), //36
    GILLTIN(30, "Gilltin", "30.obj", 9),
    // no texture exist, but it is showing ROEHI because while rendering pegasus gate idle cycle is going through 0 - 35
    UNKNOW2(29, "Unknow2", "29.obj", 11),
    RAMNON(28, "Ramnon", "28.obj", 24),
    OLAVII(27, "Olavii", "27.obj", 14),
    HACEMILL(26, "Hacemill", "26.obj", 16),
    POCORE(25, "Poco Re", "25.obj", 13),
    ABRIN(24, "Abrin", "24.obj", 12),
    SALMA(23, "Salma", "23.obj", 17),
    HAMLINTO(22, "Hamlinto", "22.obj", 15),
    ELENAMI(21, "Elenami", "21.obj", 7),
    TAHNAN(20, "Tahnan", "20.obj", 32),
    ZEO(19, "Zeo", "19.obj", 4),
    // same as UNKNOW2
    UNKNOW1(18, "Unknow1", "18.obj", 35),
    ROBANDUS(17, "Robandus", "17.obj", 1),
    RECKTIC(16, "Recktic", "16.obj", 6),
    ZAMILLOZ(15, "Zamilloz", "15.obj", 19),
    SUBIDO(14, "Subido", "14.obj", 3), // origin
    DAWNRE(13, "Dawnre", "13.obj", 8),
    ACJESIS(12, "Acjesis", "12.obj", 29),
    LENCHAN(11, "Lenchan", "11.obj", 22),
    ALURA(10, "Alura", "10.obj", 21),
    CAPO(9, "Ca Po", "9.obj", 28),
    LAYLOX(8, "Laylox", "8.obj", 34),
    ECRUMIG(7, "Ecrumig", "7.obj", 20),
    AVONIV(6, "Avoniv", "6.obj", 23),
    BYDO(5, "Bydo", "5.obj", 2),
    AAXEL(4, "Aaxel", "4.obj", 26),
    ALDENI(3, "Aldeni", "3.obj", 5),
    SETAS(2, "Setas", "2.obj", 31),
    ARAMI(1, "Arami", "1.obj", 27),
    DANAMI(0, "Danami", "0.obj", 18),

    BBB(38, "Bright Blue Button", "brb.obj", -39);

    public final int id;
    public final int textureSlot;
    public final String englishName;
    public final String translationKey;
    private final ResourceLocation iconResource;
    private final ResourceLocation gateSymbolResource;
    private final ResourceLocation gateSymbolOffResource;
    private final ResourceLocation modelResource;
    private final ResourceLocation modelResourceLight;

    SymbolPegasusEnum(int id, String englishName, String model, int textureSlot) {
        this.id = id;
        this.textureSlot = textureSlot;
        this.englishName = englishName;
        this.translationKey = "glyph.jsg.pegasus." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = JSGMapping.rl(JSGApi.MOD_ID, "textures/gui/symbol/pegasus/" + englishName.toLowerCase().replace(" ", "_") + ".png");

        this.gateSymbolResource = JSGApi.JSG_LOADERS_HOLDER.texture().getTextureResource("pegasus/glyphs.png");
        this.gateSymbolOffResource = JSGApi.JSG_LOADERS_HOLDER.texture().getTextureResource("pegasus/glyphs_off.png");

        this.modelResource = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("pegasus/dhd/buttons/" + model.split("\\.")[0] + "_base.obj");
        this.modelResourceLight = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("pegasus/dhd/buttons/" + model);
    }

    @Override
    public boolean origin() {
        return this == SUBIDO;
    }

    public boolean brb() {
        return this == BBB;
    }

    @Override
    public float getAngle() {
        return id;
    }

    @Override
    public int getAngleIndex() {
        return id;
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
    public ResourceLocation getIconResource(@Nullable PointOfOrigin origin) {
        return getIconResource(origin, StargatePointOfOriginsDefaults.VARIANT_ICON);
    }

    public ResourceLocation getIconResource(@Nullable PointOfOrigin origin, @NotNull String variant) {
        if (origin()) {
            return Objects.requireNonNullElseGet(origin, () -> Objects.requireNonNull(StargateTypes.PEGASUS.get().getDefaultPoO())).getPath(variant, false);
        }
        return (variant.equalsIgnoreCase(StargatePointOfOriginsDefaults.VARIANT_GATE_OFF_PNG) ? gateSymbolOffResource : (variant.equalsIgnoreCase(StargatePointOfOriginsDefaults.VARIANT_GATE_PNG) ? gateSymbolResource : iconResource));
    }

    public void bindIconTexture(@Nullable PointOfOrigin origin, @NotNull String variant) {
        if (variant.equalsIgnoreCase(StargatePointOfOriginsDefaults.VARIANT_ICON)) {
            bindIconTexture(origin);
            return;
        }
        var location = getIconResource(origin, variant);
        var loader = getSymbolType().getTextureLoader();
        if (origin() && JSGApi.JSG_LOADERS_HOLDER.texture().isTextureLoaded(location)) // origins are loaded and saved inside JSG texture loader by Origins Loader
            loader = JSGApi.JSG_LOADERS_HOLDER.texture();
        loader.getTexture(location).bindTexture();
    }

    @Override
    public ResourceLocation getModelResource(IPointOfOriginType type, @Nullable PointOfOrigin origin, String variant) {
        if (origin()) {
            return Objects.requireNonNullElseGet(origin, () -> Objects.requireNonNull(type.getDefaultPoO())).getPath(variant, true);
        }

        return variant.equalsIgnoreCase(StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT) ? this.modelResourceLight : this.modelResource;
    }

    @Override
    public String localize() {
        return I18n.format(translationKey);
    }

    @Override
    public SymbolType<SymbolPegasusEnum> getSymbolType() {
        return JSGSymbolTypes.PEGASUS.get();
    }

    @Override
    public boolean isValidForAddress() {
        return !brb() && !origin() && this != UNKNOW1 && this != UNKNOW2;
    }


    @Override
    public SymbolInterface getNext(boolean previous) {
        var id = this.getId();
        while (true) {
            id += (previous ? -1 : 1);
            if (id < 0) id = 37;
            id = id % 38;
            var symbol = JSGSymbolTypes.PEGASUS.get().valueOf(id);
            if (symbol != null && symbol.isValidForAddress()) return symbol;
        }
    }

    @Override
    public boolean canBePressed() {
        return !(this == UNKNOW1 || this == UNKNOW2);
    }

    @SuppressWarnings("all")
    @NotNull
    public static SymbolType<SymbolPegasusEnum> getProvider() {
        try {
            return (SymbolType<SymbolPegasusEnum>) Class.forName("dev.tauri.jsg.common.stargate.network.symbol.SymbolPegasusProvider").getConstructor().newInstance();
        } catch (Exception e) {
            JSGApi.logger.error("Error while getting symbol provider: ", e);
        }
        return null;
    }
}
