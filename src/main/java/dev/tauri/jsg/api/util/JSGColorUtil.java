package dev.tauri.jsg.api.util;

import java.awt.*;

@SuppressWarnings("unused")
public class JSGColorUtil {
    public static int blendColors(int colorA, int colorB, float colorBRatio) {
        colorBRatio = Math.min(1f, colorBRatio);
        return blendColors(colorA, colorB, 1.0f - colorBRatio, colorBRatio);
    }

    public static int blendColors(int a, int b, float colorARation, float colorBRatio) {

        int aA = (a >> 24) & 0xff;
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24) & 0xff;
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int alpha = ((int) (aA * colorARation) + (int) (bA * colorBRatio));
        int red = ((int) (aR * colorARation) + (int) (bR * colorBRatio));
        int green = ((int) (aG * colorARation) + (int) (bG * colorBRatio));
        int blue = ((int) (aB * colorARation) + (int) (bB * colorBRatio));

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static Color toColor(int hex) {
        return new Color(hex);
    }

    public static int fromColor(Color color) {
        return color.getRGB();
    }
}
