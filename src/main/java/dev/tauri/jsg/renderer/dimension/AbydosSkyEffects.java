package dev.tauri.jsg.renderer.dimension;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.client.renderer.dimension.AbstractSkyEffects;
import dev.tauri.jsg.core.client.renderer.dimension.util.CustomSkyObjectRenderer;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Function;

public class AbydosSkyEffects extends AbstractSkyEffects {
    public static Function<Integer, Pair<Float, Float>> getUV(int cols, int rows, int col, int row) {
        var pieceW = (1f / (float) cols);
        var pieceH = (1f / (float) rows);
        return (corner) -> switch (corner) {
            case 1 -> Pair.of(pieceW * (col + 1), pieceH * row);
            case 2 -> Pair.of(pieceW * (col + 1), pieceH * (row + 1));
            case 3 -> Pair.of(pieceW * col, pieceH * (row + 1));
            default -> Pair.of(pieceW * col, pieceH * row);
        };
    }

    public static final Function<CustomSkyObjectRenderer, CustomSkyObjectRenderer.UVGetter> ADVANCED_MOON_PHASER = (sun) -> (moon1, corner, level, ticks, partialTicks, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog) -> {
        var t = level.getTimeOfDay(partialTicks);
        var sunCelestialAngle = sun.getObjectCelestialAngle(t, false);
        var sunHorizontalAngle = sun.getObjectHorizontalAngle(t, false);
        var moonCelestialAngle = moon1.getObjectCelestialAngle(t, false);
        var moonHorizontalAngle = moon1.getObjectHorizontalAngle(t, false);

        var verticalDif = MathHelper.wrapDegrees((int) (moonCelestialAngle - sunCelestialAngle));
        var horizontalDif = MathHelper.wrapDegrees((int) (moonHorizontalAngle - sunHorizontalAngle));
        var sunAbove = (verticalDif > 0);
        var sunOnLeft = (horizontalDif > 0);
        verticalDif = Math.abs(verticalDif);
        horizontalDif = Math.abs(horizontalDif);

        var cols = 16;
        var rows = 16;

        if (verticalDif < 10) {
            if (horizontalDif < 10) {
                return getUV(cols, rows, 4, 0).apply(corner);
            }
            if (horizontalDif < 30) {
                return getUV(cols, rows, (sunOnLeft ? 3 : 5), 0).apply(corner);
            }
            if (horizontalDif < 50) {
                return getUV(cols, rows, (sunOnLeft ? 2 : 6), 0).apply(corner);
            }
            if (horizontalDif < 90) {
                return getUV(cols, rows, (sunOnLeft ? 1 : 7), 0).apply(corner);
            }
            return getUV(cols, rows, 0, 0).apply(corner);
        }
        if (verticalDif < 30) {
            if (horizontalDif < 10) {
                return getUV(cols, rows, (sunAbove ? 3 : 5), 2).apply(corner);
            }
            if (horizontalDif < 50) {
                return getUV(cols, rows, (sunAbove ? 2 : 6), ((sunOnLeft == sunAbove) ? 4 : 5)).apply(corner);
            }
            if (horizontalDif < 90) {
                return getUV(cols, rows, (sunAbove ? 1 : 7), ((sunOnLeft == sunAbove) ? 4 : 5)).apply(corner);
            }
            return getUV(cols, rows, 0, 0).apply(corner);
        }

        if (verticalDif < 120) {
            if (verticalDif < 50) {
                if (horizontalDif < 10) {
                    return getUV(cols, rows, (sunAbove ? 2 : 6), 2).apply(corner);
                }
            }
            if (horizontalDif < 50) {
                return getUV(cols, rows, (sunAbove ? 1 : 7), 2).apply(corner);
            }
        }

        // full
        return getUV(cols, rows, 0, 0).apply(corner);
    };


    public static final CustomSkyObjectRenderer SUN = new CustomSkyObjectRenderer(false, true, JSGMapping.rl(JSG.MOD_ID, "textures/environment/abydos/sun.png"), 1.0f, 15.0f, 0, 0);

    public static final CustomSkyObjectRenderer MOON1 = new CustomSkyObjectRenderer(true, false, JSGMapping.rl(JSG.MOD_ID, "textures/environment/abydos/moon_1.png"), 1.0f, 3.0f, -15, -55 - 45, () -> {
    }, ADVANCED_MOON_PHASER.apply(SUN));
    public static final CustomSkyObjectRenderer MOON2 = new CustomSkyObjectRenderer(true, false, JSGMapping.rl(JSG.MOD_ID, "textures/environment/abydos/moon_2.png"), 2.0f, 2.0f, 8, -60 - 45, () -> {
    }, ADVANCED_MOON_PHASER.apply(SUN));
    public static final CustomSkyObjectRenderer MOON3 = new CustomSkyObjectRenderer(true, false, JSGMapping.rl(JSG.MOD_ID, "textures/environment/abydos/moon_3.png"), 3.0f, 5.0f, 15, -90 - 45, () -> {
    }, ADVANCED_MOON_PHASER.apply(SUN));

    public AbydosSkyEffects() {
        super();
        addCustomSkyObjects(SUN, MOON1, MOON2, MOON3);
    }

    @Override
    public int getStarsCount() {
        return 5000;
    }

    @Override
    public long getStarsGenerationSeed() {
        return 42;
    }

    @Override
    public boolean drawDefaultSun() {
        return false;
    }

    @Override
    public boolean drawDefaultMoon() {
        return false;
    }

    @Override
    public Color getStarColor(RandomSource random) {
        var i = random.nextInt(50);
        return switch (i) {
            case 0 -> new Color(0xFFF8BA97, true);
            case 1 -> new Color(0xFFAEAFF8, true);
            case 2 -> new Color(0xfffffa86, true);
            case 3 -> new Color(0xff7BA0FF, true);
            case 4 -> new Color(0xffD6E7FB, true);
            case 5 -> new Color(0xFFF85C66, true);
            default -> new Color(0xFFFADAA1, true);
        };
    }

    @Override
    public float getRainLevel(ClientLevel level, float pDelta) {
        return 0f;
    }

    @Override
    public @Nullable ResourceLocation getCustomCloudsTexture() {
        return JSGMapping.rl(JSG.MOD_ID, "textures/environment/abydos/clouds.png");
    }

    @Override
    public float getCloudsSpeed() {
        return 5f;
    }
}
