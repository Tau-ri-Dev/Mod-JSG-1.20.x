package dev.tauri.jsg.client.renderer.blockentity.stargate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class StargateRendererStatic {

    public static PoseStack currentStack;
    public static int packedLight;

    public static final float EVENT_HORIZON_RADIUS = 3.790975f;

    private static final int QUADS = JSGConfig.Stargate.eventHorizonRenderQuads.get();
    private static final int SECTIONS = JSGConfig.Stargate.eventHorizonRenderSections.get();
    private static final float SECTION_ANGLE = (float) (2 * Math.PI / SECTIONS);

    public static final float INNER_CIRCLE_RADIUS = 0.25f;
    private static final float QUAD_STEP = (EVENT_HORIZON_RADIUS - INNER_CIRCLE_RADIUS) / QUADS;

    private static final List<Float> OFFSET_LIST = new ArrayList<>();
    private static final List<Float> SIN = new ArrayList<>();
    private static final List<Float> COS = new ArrayList<>();

    private static final List<Float> QUAD_RADIUS = new ArrayList<>();

    static InnerCircle innerCircle;
    static List<QuadStrip> quadStrips = new ArrayList<>();

    private static final Random RANDOM = new Random();

    private static float getRandomFloat() {
        return RANDOM.nextFloat() * 2 - 1;
    }

    public static float getOffset(int index, double tick, float mul, int quadStripIndex) {
        return (float) (Math.sin(tick / 4f + OFFSET_LIST.get(index)) * mul * (quadStripIndex / 4f) * (quadStripIndex - quadStrips.size()) / 400f);
    }

    private static float toUV(float coord) {
        return (coord + 1) / 2f;
    }

    static {
        initEventHorizon();
        initKawoosh();
    }

    private static void initEventHorizon() {
        for (int i = 0; i < SECTIONS * (QUADS + 1); i++) {
            OFFSET_LIST.add(getRandomFloat() * 3);
        }

        for (int i = 0; i <= SECTIONS; i++) {
            SIN.add((float) Math.sin(SECTION_ANGLE * i));
            COS.add((float) Math.cos(SECTION_ANGLE * i));
        }

        innerCircle = new InnerCircle();

        for (int i = 0; i <= QUADS; i++) {
            QUAD_RADIUS.add(INNER_CIRCLE_RADIUS + QUAD_STEP * i);
        }

        for (int i = 0; i < QUADS; i++) {
            quadStrips.add(new QuadStrip(i));
        }
    }

    public static void computeFaceNormalTri(@NotNull Vector3f saveTo, float x0, float y0, float z0,
                                            float x1, float y1, float z1,
                                            float x2, float y2, float z2) {
        // note: subtraction order is significant here because of how the cross product works.
        // If we're wrong our calculated normal will be pointing in the opposite direction of how it should.
        // This current order is similar enough to the order in the quad variant.
        final float dx0 = x2 - x0;
        final float dy0 = y2 - y0;
        final float dz0 = z2 - z0;
        final float dx1 = x0 - x1;
        final float dy1 = y0 - y1;
        final float dz1 = z0 - z1;

        float normX = dy0 * dz1 - dz0 * dy1;
        float normY = dz0 * dx1 - dx0 * dz1;
        float normZ = dx0 * dy1 - dy0 * dx1;

        float l = (float) Math.sqrt(normX * normX + normY * normY + normZ * normZ);

        if (l != 0) {
            normX /= l;
            normY /= l;
            normZ /= l;
        }

        saveTo.set(normX, normY, normZ);
    }

    public static class InnerCircle {
        private final List<Float> x = new ArrayList<>();
        private final List<Float> y = new ArrayList<>();

        private final List<Float> tx = new ArrayList<>();
        private final List<Float> ty = new ArrayList<>();

        public InnerCircle() {
            float texMul = (INNER_CIRCLE_RADIUS / EVENT_HORIZON_RADIUS);

            for (int i = 0; i < SECTIONS; i++) {
                x.add(SIN.get(i) * INNER_CIRCLE_RADIUS);
                y.add(COS.get(i) * INNER_CIRCLE_RADIUS);

                tx.add(toUV(SIN.get(i) * texMul));
                ty.add(toUV(COS.get(i) * texMul));
            }
        }

        private final Vector3f cache = new Vector3f();

        public void render(float tick, boolean white, Float alpha, float mul, byte animationOverride, int color, int innerColor) {
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            boolean animated = animationOverride != -1;
            var c = new Color(color, true);
            var innerC = new Color(innerColor, true);

            if (white) {
                if (alpha > 0.5f)
                    alpha = 1.0f - alpha;
            }
            for (int k = 0; k < 2; k++) {
                final boolean coloredBackground = (k == 0);
                final float a = (alpha != null ? alpha : 1f) * (coloredBackground ? (c.getAlpha() / 255f) : 1f);
                EmissiveRenderer.renderWithLightOverlay(currentStack, packedLight, !coloredBackground, () -> {
                }, () -> {
                    Tesselator t = Tesselator.getInstance();
                    BufferBuilder b = t.getBuilder();
                    Matrix4f matrix = currentStack.last().pose();

                    b.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.NEW_ENTITY);

                    int texIndex = (int) (tick * 4 % 185);
                    float xTexOffset = texIndex % 14 / 14f;
                    float yTexOffset = texIndex / 14 / 14f;

                    float xTex = 0.5f;
                    float yTex = 0.5f;

                    if (animated) {
                        xTex /= 14.0f;
                        xTex += xTexOffset;
                        yTex /= 14.0f;
                        yTex += yTexOffset;
                    } else yTex *= -1;

                    if (coloredBackground)
                        b.vertex(matrix, 0, 0, 0).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                    else
                        b.vertex(matrix, 0, 0, 0).color((innerC.getRed() / 255f), (innerC.getGreen() / 255f), (innerC.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                    int index;
                    for (int i = SECTIONS; i >= 0; i--) {
                        if (i == SECTIONS)
                            index = 0;
                        else
                            index = i;

                        if (i % 3 == 0) {
                            computeFaceNormalTri(cache, x.get(index), y.get(index), getOffset(index, tick * mul, mul, 0),
                                    x.get(index + 1), y.get(index + 1), getOffset(index + 1, tick * mul, mul, 0),
                                    x.get(index + 2), y.get(index + 2), getOffset(index + 2, tick * mul, mul, 0));
                        }

                        xTex = tx.get(index);
                        yTex = ty.get(index);

                        if (animated) {
                            xTex /= 14.0f;
                            xTex += xTexOffset;
                            yTex /= 14.0f;
                            yTex += yTexOffset;
                        } else yTex *= -1;

                        if (coloredBackground)
                            b.vertex(matrix, x.get(index), y.get(index), getOffset(index, tick * mul, mul, 0)).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                        else
                            b.vertex(matrix, x.get(index), y.get(index), getOffset(index, tick * mul, mul, 0)).color((innerC.getRed() / 255f), (innerC.getGreen() / 255f), (innerC.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(currentStack.last().normal(), cache.x, cache.y, cache.z).endVertex();
                    }

                    t.end();
                });
            }
        }

        public void renderShield(Float alpha, int color) {
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            var c = new Color(color, true);
            final float a = (alpha != null ? alpha : 1f) * (c.getAlpha() / 255f);
            EmissiveRenderer.renderWithLightOverlay(currentStack, packedLight, true, () -> {
            }, () -> {
                Tesselator t = Tesselator.getInstance();
                BufferBuilder b = t.getBuilder();
                Matrix4f matrix = currentStack.last().pose();
                b.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.NEW_ENTITY);
                float xTex = 0.5f;
                float yTex = -0.5f;

                b.vertex(matrix, 0, 0, 0).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();

                int index;
                for (int i = SECTIONS; i >= 0; i--) {
                    if (i == SECTIONS)
                        index = 0;
                    else
                        index = i;

                    xTex = tx.get(index);
                    yTex = -ty.get(index);

                    b.vertex(matrix, x.get(index), y.get(index), 0).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                }

                t.end();
            });
        }
    }

    public static class QuadStrip {
        private final List<Float> x = new ArrayList<>();
        private final List<Float> y = new ArrayList<>();

        private final List<Float> tx = new ArrayList<>();
        private final List<Float> ty = new ArrayList<>();

        private final int quadStripIndex;

        private final float radMul;

        public final float innerRadius;

        public QuadStrip(int quadStripIndex) {
            this(quadStripIndex, QUAD_RADIUS.get(quadStripIndex), QUAD_RADIUS.get(quadStripIndex + 1), null);
        }

        public QuadStrip(int quadStripIndex, float innerRadius, float outerRadius, Float tick) {
            this(quadStripIndex, innerRadius, outerRadius, tick, 1);
        }

        public QuadStrip(int quadStripIndex, float innerRadius, float outerRadius, Float tick, float radMul) {
            this.quadStripIndex = quadStripIndex;
            this.radMul = radMul;
            this.innerRadius = innerRadius;
            recalculate(innerRadius, outerRadius, tick);
        }

        Vector3f cache = new Vector3f();

        public void recalculate(float innerRadius, float outerRadius, Float tick) {

            List<Float> radius = new ArrayList<>();
            List<Float> texMul = new ArrayList<>();


            radius.add(innerRadius);
            radius.add(outerRadius);

            for (int i = 0; i < 2; i++)
                texMul.add(radius.get(i) / EVENT_HORIZON_RADIUS);

            for (int k = 0; k < 2; k++) {
                for (int i = 0; i < SECTIONS; i++) {
                    float rad = radius.get(k);

                    if (tick != null) {
                        if (quadStripIndex == 9)
                            rad += getOffset(i, tick, 5 * radMul, quadStripIndex) * 0.75f;
                        else
                            rad += getOffset(i, tick, 1 * radMul, quadStripIndex) * 2;
                    }

                    x.add(rad * SIN.get(i));
                    y.add(rad * COS.get(i));

                    tx.add(toUV(SIN.get(i) * texMul.get(k)));
                    ty.add(toUV(COS.get(i) * texMul.get(k)));
                }
            }
        }

        public void render(double tick, boolean white, Float alpha, float mul, byte animationOverride, int color, int innerColor) {
            render(tick, null, null, white, alpha, mul, false, animationOverride, color, false, innerColor);
        }

        public void render(double tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul, int color, int innerColor) {
            render(tick, outerZ, innerZ, white, alpha, mul, false, (byte) 0, color, false, innerColor);
        }

        public void render(double tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul, boolean reversed, int color, int innerColor) {
            render(tick, outerZ, innerZ, white, alpha, mul, reversed, (byte) 0, color, false, innerColor);
        }

        public void render(double tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul, boolean reversed, byte animationOverride, int color, boolean offsetZ, int innerColor) {
            boolean animate = animationOverride != -1;

            var c = new Color(color, true);
            var innerC = new Color(innerColor, true);

            if (white) {
                if (alpha > 0.5f)
                    alpha = 1.0f - alpha;
            }

            int texIndex = (int) (tick * 4 % 185);
            float xTexOffset = texIndex % 14 / 14f;
            float yTexOffset = texIndex / 14 / 14f;
            for (int k = 0; k < 2; k++) {
                final boolean coloredBackground = (k == 0);
                final float a = (alpha != null ? alpha : 1f) * (coloredBackground ? (c.getAlpha() / 255f) : 1f);
                EmissiveRenderer.renderWithLightOverlay(currentStack, LightTexture.FULL_BRIGHT, !coloredBackground, () -> {
                }, () -> {
                    Tesselator t = Tesselator.getInstance();
                    BufferBuilder b = t.getBuilder();
                    Matrix4f matrix = currentStack.last().pose();

                    b.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.NEW_ENTITY);

                    int index;
                    for (int i = reversed ? 0 : SECTIONS; (reversed && i <= SECTIONS) || (!reversed && i >= 0); i += (reversed ? 1 : -1)) {

                        if (i == SECTIONS)
                            index = 0;
                        else
                            index = i;

                        if (i % 3 == 0) {
                            computeFaceNormalTri(cache, x.get(index), y.get(index), getOffset(index + SECTIONS * quadStripIndex, tick * mul, mul, quadStripIndex),
                                    x.get(index + 1), y.get(index + 1), getOffset(index + 1 + SECTIONS * quadStripIndex, tick * mul, mul, quadStripIndex),
                                    x.get(index + 2), y.get(index + 2), getOffset(index + 2 + SECTIONS * quadStripIndex, tick * mul, mul, quadStripIndex));
                        }

                        float z;

                        if (outerZ != null) z = outerZ;
                        else z = getOffset(index + SECTIONS * quadStripIndex, tick * mul, mul, quadStripIndex);

                        if (offsetZ)
                            z += getOffset(index, tick * mul, mul, quadStripIndex);

                        float xTex = tx.get(index);
                        float yTex = ty.get(index);

                        if (animate) {
                            xTex /= 14.0f;
                            xTex += xTexOffset;
                            yTex /= 14.0f;
                            yTex += yTexOffset;
                        } else yTex *= -1;

                        if (coloredBackground)
                            b.vertex(matrix, x.get(index), y.get(index), z).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                        else
                            b.vertex(matrix, x.get(index), y.get(index), z).color((innerC.getRed() / 255f), (innerC.getGreen() / 255f), (innerC.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(currentStack.last().normal(), cache.x, cache.y, cache.z).endVertex();


                        index = index + SECTIONS;

                        xTex = tx.get(index);
                        yTex = ty.get(index);

                        if (animate) {
                            xTex /= 14.0f;
                            xTex += xTexOffset;
                            yTex /= 14.0f;
                            yTex += yTexOffset;
                        } else yTex *= -1;

                        if (innerZ != null) z = innerZ;
                        else z = getOffset(index + SECTIONS * quadStripIndex, tick * mul, mul, quadStripIndex + 1);

                        if (coloredBackground)
                            b.vertex(matrix, x.get(index), y.get(index), z).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                        else
                            b.vertex(matrix, x.get(index), y.get(index), z).color((innerC.getRed() / 255f), (innerC.getGreen() / 255f), (innerC.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(currentStack.last().normal(), cache.x, cache.y, cache.z).endVertex();
                    }

                    t.end();

                });
            }
        }

        public void renderCurved(double tick, boolean white, Float alpha, float z1, float z2, float z3, float z4, float z5, int color, int innerColor) {
            var c = new Color(color, true);
            var innerC = new Color(innerColor, true);

            if (white) {
                if (alpha > 0.5f)
                    alpha = 1.0f - alpha;
            }

            int texIndex = (int) (tick * 4 % 185);
            float xTexOffset = texIndex % 14 / 14f;
            float yTexOffset = texIndex / 14 / 14f;
            for (int k = 0; k < 2; k++) {
                final boolean coloredBackground = (k == 0);
                final float a = (alpha != null ? alpha : 1f) * (coloredBackground ? (c.getAlpha() / 255f) : 1f);
                EmissiveRenderer.renderWithLightOverlay(currentStack, LightTexture.FULL_BRIGHT, !coloredBackground, () -> {
                }, () -> {
                    Tesselator t = Tesselator.getInstance();
                    BufferBuilder b = t.getBuilder();
                    Matrix4f matrix = currentStack.last().pose();

                    b.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.NEW_ENTITY);

                    int index;
                    for (int i = SECTIONS; i >= 0; i--) {
                        if (i == SECTIONS)
                            index = 0;
                        else
                            index = i;

                        if (i % 3 == 0) {
                            computeFaceNormalTri(cache, x.get(index), y.get(index), z1,
                                    x.get(index + 1), y.get(index + 1), z2,
                                    x.get(index + 2), y.get(index + 2), z3);
                        }
                        float z = z4;

                        float xTex = tx.get(index);
                        float yTex = ty.get(index);

                        xTex /= 14.0f;
                        xTex += xTexOffset;
                        yTex /= 14.0f;
                        yTex += yTexOffset;

                        if (coloredBackground)
                            b.vertex(matrix, x.get(index), y.get(index), z).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                        else
                            b.vertex(matrix, x.get(index), y.get(index), z).color((innerC.getRed() / 255f), (innerC.getGreen() / 255f), (innerC.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(currentStack.last().normal(), cache.x, cache.y, cache.z).endVertex();


                        index = index + SECTIONS;

                        xTex = tx.get(index);
                        yTex = ty.get(index);

                        xTex /= 14.0f;
                        xTex += xTexOffset;
                        yTex /= 14.0f;
                        yTex += yTexOffset;

                        z = z5;

                        if (coloredBackground)
                            b.vertex(matrix, x.get(index), y.get(index), z).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                        else
                            b.vertex(matrix, x.get(index), y.get(index), z).color((innerC.getRed() / 255f), (innerC.getGreen() / 255f), (innerC.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(currentStack.last().normal(), cache.x, cache.y, cache.z).endVertex();
                    }

                    t.end();
                });
            }
        }

        public void renderShield(Float alpha, int color) {
            var c = new Color(color, true);

            final float a = (alpha != null ? alpha : 1f) * (c.getAlpha() / 255f);
            EmissiveRenderer.renderWithLightOverlay(currentStack, LightTexture.FULL_BRIGHT, true, () -> {
            }, () -> {
                Tesselator t = Tesselator.getInstance();
                BufferBuilder b = t.getBuilder();
                Matrix4f matrix = currentStack.last().pose();

                b.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.NEW_ENTITY);

                int index;
                for (int i = SECTIONS; i >= 0; i--) {
                    if (i == SECTIONS)
                        index = 0;
                    else
                        index = i;

                    float xTex = tx.get(index);
                    float yTex = -ty.get(index);

                    b.vertex(matrix, x.get(index), y.get(index), 0).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();

                    index = index + SECTIONS;

                    xTex = tx.get(index);
                    yTex = -ty.get(index);

                    b.vertex(matrix, x.get(index), y.get(index), 0).color((c.getRed() / 255f), (c.getGreen() / 255f), (c.getBlue() / 255f), a).uv(xTex, yTex).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 1, 0).endVertex();
                }
                t.end();
            });
        }
    }

    static final float kawooshRadius = 2.5f;
    private static final float kawooshSize = 9f;
    private static final int kawooshSections = 36;

    static Map<Float, Float> Z_RadiusMap;

    private static void initKawoosh() {
        Z_RadiusMap = new LinkedHashMap<>();

        float wortexLengthRange = 0.5090f;

        float step = wortexLengthRange / kawooshSections;

        float scaleZ = kawooshSize / wortexLengthRange;
        float scaleY = kawooshRadius / 0.1333f;

        // back wortex
        boolean soundStart = false;
        for (int i = kawooshSections; i >= 0; i--) {
            float zPrev = step * (i + 1);
            float z = step * i;
            float y = (0.195f / (z * 5 + 0.8f)) - 0.05f;

            if (i == kawooshSections) y = 0;
            if (y < 0) y = 0;
            if (y == 0) continue;
            if (!soundStart) {
                soundStart = true;
                Z_RadiusMap.put(-zPrev * scaleZ, 0f);
            }
            Z_RadiusMap.put(-z * scaleZ, y * scaleY);
        }

        // front kawoosh
        for (int i = 0; i <= kawooshSections; i++) {
            float z = step * i;
            float y = 0;
            if (z <= 0.1f) {
                y = ((float) -Math.sqrt(0.01 - Math.pow(z - 0.1, 2))) + 0.190f;
            } else if (z <= 0.4f) {
                y = (float) (Math.sin(-z * 13f) / 92 + 0.10f);
            } else if (z <= wortexLengthRange) {
                y = ((float) Math.sqrt(0.012 - Math.pow(z - 0.4, 2)));
            }

            if (i == kawooshSections) y = 0;
            if (y < 0) y = 0;
            Z_RadiusMap.put(z * scaleZ, y * scaleY);
        }
    }


    public static void renderShield(float alpha, int color, boolean backOnly) {
        currentStack.pushPose();
        RenderSystem.enableBlend();
        for (int k = (backOnly ? 1 : 0); k < 2; k++) {
            if (k == 1)
                currentStack.mulPose(Axis.YN.rotationDegrees(180));

            innerCircle.renderShield(alpha, color);
            for (StargateRendererStatic.QuadStrip strip : quadStrips)
                strip.renderShield(alpha, color);
        }
        RenderSystem.disableBlend();
        currentStack.popPose();
    }
}
