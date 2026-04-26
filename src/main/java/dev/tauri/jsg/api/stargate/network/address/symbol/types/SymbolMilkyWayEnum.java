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

public enum SymbolMilkyWayEnum implements SymbolInterface {
    SCULPTOR(0, 19, "Sculptor", "0.obj"),
    SCORPIUS(1, 8, "Scorpius", "1.obj"),
    CENTAURUS(2, 4, "Centaurus", "2.obj"),
    MONOCEROS(3, 31, "Monoceros", "3.obj"),
    ORIGIN(4, 0, "Point of Origin", "4.obj"),
    PEGASUS(5, 18, "Pegasus", "5.obj"),
    ANDROMEDA(6, 21, "Andromeda", "6.obj"),
    SERPENSCAPUT(7, 6, "Serpens Caput", "7.obj"),
    ARIES(8, 23, "Aries", "8.obj"),
    LIBRA(9, 5, "Libra", "9.obj"),
    ERIDANUS(10, 28, "Eridanus", "10.obj"),
    LEOMINOR(11, 37, "Leo Minor", "11.obj"),
    HYDRA(12, 33, "Hydra", "12.obj"),
    SAGITTARIUS(13, 11, "Sagittarius", "13.obj"),
    SEXTANS(14, 36, "Sextans", "14.obj"),
    SCUTUM(15, 10, "Scutum", "15.obj"),
    PISCES(16, 20, "Pisces", "16.obj"),
    VIRGO(17, 2, "Virgo", "17.obj"),
    BOOTES(18, 3, "Bootes", "18.obj"),
    AURIGA(19, 27, "Auriga", "19.obj"),
    CORONAAUSTRALIS(20, 9, "Corona Australis", "20.obj"),
    GEMINI(21, 32, "Gemini", "21.obj"),
    LEO(22, 38, "Leo", "22.obj"),
    CETUS(23, 25, "Cetus", "23.obj"),
    TRIANGULUM(24, 22, "Triangulum", "24.obj"),
    AQUARIUS(25, 17, "Aquarius", "25.obj"),
    MICROSCOPIUM(26, 13, "Microscopium", "26.obj"),
    EQUULEUS(27, 16, "Equuleus", "27.obj"),
    CRATER(28, 1, "Crater", "28.obj"),
    PERSEUS(29, 24, "Perseus", "29.obj"),
    CANCER(30, 35, "Cancer", "30.obj"),
    NORMA(31, 7, "Norma", "31.obj"),
    TAURUS(32, 26, "Taurus", "32.obj"),
    CANISMINOR(33, 30, "Canis Minor", "33.obj"),
    CAPRICORNUS(34, 14, "Capricornus", "34.obj"),
    LYNX(35, 34, "Lynx", "35.obj"),
    ORION(36, 29, "Orion", "36.obj"),
    PISCISAUSTRINUS(37, 15, "Piscis Austrinus", "37.obj"),
    BRB(38, -1, "Bright Red Button", "brb.obj");

    public static final float ANGLE_PER_GLYPH = 9.2307692f;

    public final int id;
    public final int angleIndex;
    public final float angle;

    public final String englishName;
    public final String translationKey;
    private final ResourceLocation iconResource;
    private final ResourceLocation modelResource;
    private final ResourceLocation modelResourceLight;

    SymbolMilkyWayEnum(int id, int angleIndex, String englishName, String model) {
        this.id = id;

        this.angleIndex = angleIndex;
        this.angle = 360 - (angleIndex * ANGLE_PER_GLYPH);

        this.englishName = englishName;
        this.translationKey = "glyph.jsg.milkyway." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = JSGMapping.rl(JSGApi.MOD_ID, "textures/gui/symbol/milkyway/" + englishName.toLowerCase().replaceAll(" ", "_") + ".png");

        this.modelResource = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/" + model.split("\\.")[0] + "_base.obj");
        this.modelResourceLight = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/" + model);
    }

    public boolean brb() {
        return this == BRB;
    }

    @Override
    public boolean origin() {
        return this == ORIGIN;
    }

    @Override
    public int getId() {
        return id;
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
    public String getEnglishName() {
        return englishName;
    }

    @Override
    public String toString() {
        return getEnglishName();
    }

    @Override
    public ResourceLocation getIconResource(@Nullable PointOfOrigin origin) {
        if (origin()) {
            return Objects.requireNonNullElseGet(origin, () -> Objects.requireNonNull(StargateTypes.MILKYWAY.get().getDefaultPoO())).getPath(StargatePointOfOriginsDefaults.VARIANT_ICON, false);
        }
        return iconResource;
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
    public SymbolType<SymbolMilkyWayEnum> getSymbolType() {
        return JSGSymbolTypes.MILKYWAY.get();
    }

    @Override
    public boolean isValidForAddress() {
        return !brb() && !origin();
    }

    @Override
    public SymbolInterface getNext(boolean previous) {
        var id = this.getId();
        while (true) {
            id += (previous ? -1 : 1);
            if (id < 0) id = 37;
            id = id % 38;
            var symbol = JSGSymbolTypes.MILKYWAY.get().valueOf(id);
            if (symbol != null && symbol.isValidForAddress()) return symbol;
        }
    }

    @SuppressWarnings("all")
    @NotNull
    public static SymbolType<SymbolMilkyWayEnum> getProvider() {
        try {
            return (SymbolType<SymbolMilkyWayEnum>) Class.forName("dev.tauri.jsg.common.stargate.network.symbol.SymbolMilkyWayProvider").getConstructor().newInstance();
        } catch (Exception e) {
            JSGApi.logger.error("Error while getting symbol provider: ", e);
        }
        return null;
    }
}
