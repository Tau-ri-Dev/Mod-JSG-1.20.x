package dev.tauri.jsg.api.stargate.network.address.symbol.types;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.client.LoadersHolder;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.BiomeOverlayRegistry;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.util.I18n;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static dev.tauri.jsg.api.stargate.network.address.symbol.SymbolTypeRegistry.MILKYWAY;

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
        this.iconResource = new ResourceLocation(JSGApi.MOD_ID, "textures/gui/symbol/milkyway/" + englishName.toLowerCase().replaceAll(" ", "_") + ".png");

        this.modelResource = LoadersHolder.JSG_HOLDER.model().getModelResource("milkyway/dhd/buttons/" + model.split("\\.")[0] + "_base.obj");
        this.modelResourceLight = LoadersHolder.JSG_HOLDER.model().getModelResource("milkyway/dhd/buttons/" + model);
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
    public ResourceLocation getIconResource(int originId) {
        if (this == ORIGIN) {
            if (JSGConfig.Stargate.enableDiffOrigins.get()) {
                // todo: rewrite origins loader and fix this
                //if (originId >= MOD_POINT_OF_ORIGINS_COUNT)
                //    return OriginsLoader.getResource(OriginsLoader.EnumOriginFileType.TEXTURE, originId);
                return new ResourceLocation(JSGApi.MOD_ID, "textures/gui/symbol/milkyway/origin_" + originId + ".png");
            }
            // todo: rewrite origins loader and fix this
            //return new ResourceLocation(JSGApi.MOD_ID, "textures/gui/symbol/milkyway/origin_" + DEFAULT_ORIGIN_ID + ".png");
            return new ResourceLocation(JSGApi.MOD_ID, "textures/gui/symbol/milkyway/origin_5.png");
        }
        return getIconResource(BiomeOverlayRegistry.NORMAL, Level.OVERWORLD, originId);
    }

    @Override
    public boolean renderIconByMinecraft(int originId) {
        return true;
        //todo: fix this when rewriting origins //return (this != ORIGIN || !JSGConfig.Stargate.enableDiffOrigins.get() || originId < MOD_POINT_OF_ORIGINS_COUNT);
    }

    @Override
    public ResourceLocation getIconResource(BiomeOverlayRegistry.BiomeOverlayInstance overlay, ResourceKey<Level> dimensionId, int configOrigin) {
        if (this == ORIGIN)
            return getIconResource(Stargate.getOriginId(overlay, dimensionId, configOrigin));
        return iconResource;
    }

    public ResourceLocation getModelResource(BiomeOverlayRegistry.BiomeOverlayInstance overlay, ResourceKey<Level> dimensionId, boolean forDHD, boolean lightModel, int configOrigin) {
        return getModelResource(overlay, dimensionId, forDHD, lightModel, false, configOrigin);
    }

    private ResourceLocation getModelResource(BiomeOverlayRegistry.BiomeOverlayInstance overlay, ResourceKey<Level> dimensionId, boolean forDHD, boolean lightModel, boolean notFound, int configOrigin) {
        ResourceLocation modelResource;
        if (this == ORIGIN) {
            if (!notFound)
                modelResource = LoadersHolder.JSG_HOLDER.model().getModelResource("milkyway/" + (!forDHD ? "ring/" : "dhd/buttons/") + "origin_" + Stargate.getOriginId(overlay, dimensionId, configOrigin) + (lightModel ? "_light" : "") + ".obj");
            else
                // todo: rewrite origins loader and fix this 5->default
                modelResource = LoadersHolder.JSG_HOLDER.model().getModelResource("milkyway/" + (!forDHD ? "ring/" : "dhd/buttons/") + "origin_" + 5 + (lightModel ? "_light" : "") + ".obj");

            if (LoadersHolder.JSG_HOLDER.model().getModel(modelResource).isEmpty && !notFound) {
                JSGApi.logger.error("Origin model not loaded!");
                JSGApi.logger.error(modelResource.toString());
                return getModelResource(overlay, dimensionId, forDHD, lightModel, true, configOrigin);
            }

            return modelResource;
        }

        return (forDHD && lightModel) ? this.modelResourceLight : this.modelResource;
    }

    @Override
    public String localize() {
        return I18n.format(translationKey);
    }

    @Override
    public AbstractSymbolType<SymbolMilkyWayEnum> getSymbolType() {
        return MILKYWAY;
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
            var symbol = MILKYWAY.valueOf(id);
            if (symbol != null && symbol.isValidForAddress()) return symbol;
        }
    }

    @SuppressWarnings("all")
    @NotNull
    public static AbstractSymbolType<SymbolMilkyWayEnum> getProvider() {
        try {
            return (AbstractSymbolType<SymbolMilkyWayEnum>) Class.forName("dev.tauri.jsg.stargate.network.symbol.SymbolMilkyWayProvider").getConstructor().newInstance();
        } catch (Exception e) {
            JSGApi.logger.error("Error while getting symbol provider: ", e);
        }
        return null;
    }
}
