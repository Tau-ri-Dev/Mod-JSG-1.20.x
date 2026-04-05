package dev.tauri.jsg.screen.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.client.screen.EnumMainMenuGateType;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.util.math.NumberUtils;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.loader.ElementEnum;
import dev.tauri.jsg.renderer.stargate.ChevronTextureList;
import dev.tauri.jsg.renderer.stargate.StargateMilkyWayRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL14C;

import static dev.tauri.jsg.screen.gui.mainmenu.GuiCustomMainMenu.graphics;
import static dev.tauri.jsg.screen.gui.mainmenu.GuiCustomMainMenu.poseStack;

public class MainMenuGateRenderer {
    public static ResourceLocation getIconsTexture(EnumMainMenuGateType gateType) {
        return switch (gateType) {
            case PEGASUS -> JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/icons_pg.png");
            case UNIVERSE -> JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/icons_uni.png");
            default -> JSGMapping.rl(JSG.MOD_ID, "textures/gui/mainmenu/icons_mw.png");
        };
    }

    public static double coefficient = 1.0;

    public static float translationZ = 0;
    public static float disassemblyCoef = 0;

    @SuppressWarnings("all")
    public static void renderGate(EnumMainMenuGateType gateType, int x, int y, float size, double tick) {
        renderGate(gateType, x, y, 0f, size, 7, true, tick);
    }

    @SuppressWarnings("all")
    public static void renderGate(EnumMainMenuGateType gateType, int x, int y, float zFactor, float size, int chevronsActive, boolean finalActive, double tick) {
        coefficient = 1 * 0.75; //JSGConfig.General.mainMenuRingRotationCoefficient;
        poseStack.pushPose();

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        translationZ = ((ForgeHooksClient.getGuiFarPlane() - 1200) / size);
        poseStack.translate(x, y, 0);
        poseStack.scale(size, -size, size);
        poseStack.translate(0, 0, -translationZ);
        poseStack.translate(0, 0, translationZ * zFactor);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Math.min(1f, (disassemblyCoef * 2f)) * 90));

        AbstractOBJModel.setGUIRender();

        switch (gateType) {
            case MILKYWAY:
                renderMWGate(tick, chevronsActive, finalActive);
                break;
            case PEGASUS:
                renderPEGGate(tick);
                break;
            case UNIVERSE:
                renderUNIGate(tick);
                break;
            default:
                break;
        }

        AbstractOBJModel.resetRenderType();

        poseStack.popPose();
        poseStack.popPose();
        disassemblyCoef = 0;
    }

    @SuppressWarnings("all")
    public static void renderDHD(EnumMainMenuGateType gateType, int x, int y, float zFactor, float size, double tick) {
        coefficient = 1 * 0.75; //JSGConfig.General.mainMenuRingRotationCoefficient;
        poseStack.pushPose();

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        translationZ = ((ForgeHooksClient.getGuiFarPlane() - 1200) / size);
        poseStack.translate(x, y, 0);
        poseStack.scale(size, -size, size);
        poseStack.translate(0, 0, -translationZ);
        poseStack.translate(0, 0, translationZ * zFactor);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - Math.min(1f, (disassemblyCoef * 2f)) * 90));

        AbstractOBJModel.setGUIRender();

        switch (gateType) {
            case MILKYWAY:
                renderMWDHD(tick);
                break;
            default:
                break;
        }

        AbstractOBJModel.resetRenderType();

        poseStack.popPose();
        poseStack.popPose();
        disassemblyCoef = 0;
    }

    private static void renderMWGate(double tick, int chevronsActive, boolean finalActive) {
        var disCoef = Math.max(0, disassemblyCoef - 0.5f) * 2.5f;
        // Ring
        poseStack.pushPose();
        poseStack.translate(StargateMilkyWayRenderer.RING_LOC.x, StargateMilkyWayRenderer.RING_LOC.z, StargateMilkyWayRenderer.RING_LOC.y);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (-(tick * (1.8f * coefficient / 2)) % 360)));
        poseStack.translate(-StargateMilkyWayRenderer.RING_LOC.x, -StargateMilkyWayRenderer.RING_LOC.z, -StargateMilkyWayRenderer.RING_LOC.y);
        poseStack.translate(0, 0, disCoef);
        ElementEnum.MILKYWAY_RING.bindTexture().render(poseStack, graphics.bufferSource(), 0);
        JSGSymbolTypes.MILKYWAY.get().getOrigin().getModel(StargateTypes.MILKYWAY.get(), StargatePointOfOriginsDefaults.get(StargateTypes.MILKYWAY.get(), StargatePointOfOriginsDefaults.MilkyWay.TAURI), StargatePointOfOriginsDefaults.VARIANT_GATE).render(poseStack, graphics.bufferSource(), 0);

        poseStack.popPose();

        // Chevrons
        poseStack.pushPose();
        ChevronTextureList chevrons = new ChevronTextureList(JSGApi.JSG_LOADERS_HOLDER.texture(), "milkyway/chevron", chevronsActive, finalActive);
        chevrons.initClient();
        for (ChevronEnum chevron : ChevronEnum.values()) {
            for (int i = 0; i < 2; i++) {
                boolean light = (i == 1);
                var chevronCoef = 1f; //(chevron.rotationIndex *f);
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));
                poseStack.translate(0, 0, disCoef * 2f * chevronCoef);
                if (light) {
                    float color = chevrons.getColor(chevron);
                    RenderSystem.setShaderColor(color, color, color, 1);
                } else
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(chevrons.get(CoreBiomeOverlays.NORMAL.get(), chevron, light)).bindTexture();
                ElementEnum.MILKYWAY_CHEVRON_MOVING.render(poseStack, graphics.bufferSource(), 0);
                poseStack.translate(0, 0, disCoef * chevronCoef);
                ElementEnum.MILKYWAY_CHEVRON_LIGHT.render(poseStack, graphics.bufferSource(), 0);
                poseStack.translate(0, 0, -disCoef * 4f * chevronCoef);
                if (!light) {
                    ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTexture().render(poseStack, graphics.bufferSource(), 0);
                    ElementEnum.MILKYWAY_CHEVRON_BACK.render(poseStack, graphics.bufferSource(), 0);
                }
                poseStack.popPose();
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
        }
        poseStack.popPose();

        // Gate
        poseStack.pushPose();
        ElementEnum.MILKYWAY_GATE.bindTexture().render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();
    }

    private static void renderMWDHD(double tick) {
        var disCoef = Math.max(0, disassemblyCoef - 0.5f) * 1.5f;

        poseStack.pushPose();
        ElementEnum.MILKYWAY_DHD_BASE.bindTexture().render(poseStack, graphics.bufferSource(), 0);

        poseStack.pushPose();
        poseStack.translate(0, disCoef * 4, 0);
        ElementEnum.MILKYWAY_DHD_BUTTON_CONSOLE.render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, disCoef, 0);
        ElementEnum.MILKYWAY_DHD_CRYSTAL_HOLDER.render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();

        ElementEnum.MILKYWAY_DHD_UPGRADE_COVER.render(poseStack, graphics.bufferSource(), 0);

        poseStack.pushPose();
        poseStack.translate(0, disCoef * 2, 0);
        ElementEnum.MILKYWAY_DHD_CRYSTALS.bindTexture().render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, disCoef * 3, 0);
        ElementEnum.MILKYWAY_DHD_CONTROL_CRYSTAL.render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();

        var origin = StargatePointOfOriginsDefaults.get(StargateTypes.MILKYWAY.get(), StargatePointOfOriginsDefaults.MilkyWay.TAURI);
        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            poseStack.pushPose();
            poseStack.translate(0, disCoef * 5, 0);
            if (symbol.origin()) {
                // render plate for PoO
                var plate = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4_base.obj");
                var plateLight = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4.obj");
                ElementEnum.MILKYWAY_DHD_BASE.bindTexture(CoreBiomeOverlays.NORMAL);
                JSGApi.JSG_LOADERS_HOLDER.model().getModel(plate).render(poseStack, graphics.bufferSource(), 0);

                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(getMWDHDButtonTexture(symbol)).bindTexture();
                JSGApi.JSG_LOADERS_HOLDER.model().getModel(plateLight).render(poseStack, graphics.bufferSource(), 0);
            }

            // render symbol light emissive
            JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(getMWDHDButtonTexture(symbol)).bindTexture();
            symbol.getModel(StargateTypes.MILKYWAY.get(), origin, StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(poseStack, graphics.bufferSource(), 0);
            if (symbol.brb()) {
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
            // render symbol base
            ElementEnum.MILKYWAY_DHD_BASE.bindTexture(CoreBiomeOverlays.NORMAL);
            symbol.getModel(StargateTypes.MILKYWAY.get(), origin, StargatePointOfOriginsDefaults.VARIANT_DHD).render(poseStack, graphics.bufferSource(), 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static ResourceLocation getMWDHDButtonTexture(SymbolMilkyWayEnum symbol) {
        if (symbol.brb())
            return JSGMapping.rl(JSG.MOD_ID, "textures/tesr/milkyway/dhd/dhd_brb_0.jpg");

        return JSGMapping.rl(JSG.MOD_ID, "textures/tesr/milkyway/dhd/dhd_button_light_0.jpg");
    }

    private static void renderUNIGate(double tick) {
        poseStack.pushPose();
        poseStack.scale(1.14f, 1.14f, 1.14f);
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (-(tick * (1.8f * coefficient / 2)) % 360)));

        // Gate
        poseStack.pushPose();
        ElementEnum.UNIVERSE_GATE.bindTexture().render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();

        // Chevrons
        poseStack.pushPose();
        ChevronTextureList chevrons = new ChevronTextureList(JSGApi.JSG_LOADERS_HOLDER.texture(), "universe/universe_chevron", 9, true);
        chevrons.initClient();
        for (ChevronEnum chevron : ChevronEnum.values()) {
            for (int i = 0; i < 2; i++) {
                boolean light = (i == 1);
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));
                if (light) {
                    float color = chevrons.getColor(chevron);
                    RenderSystem.setShaderColor(color, color, color, 1);
                } else
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(chevrons.get(CoreBiomeOverlays.NORMAL.get(), chevron, light)).bindTexture();
                ElementEnum.UNIVERSE_CHEVRON.render(poseStack, graphics.bufferSource(), 0);
                poseStack.popPose();
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
        }
        poseStack.popPose();

        // Symbols
        ElementEnum.UNIVERSE_SYMBOL.bindTexture(CoreBiomeOverlays.NORMAL);
        for (SymbolUniverseEnum symbol : SymbolUniverseEnum.values()) {
            if (symbol == SymbolUniverseEnum.TOP_CHEVRON) continue;
            float color = 0.25f;
            switch (symbol) {
                case G17:
                case G10:
                case G15:
                case G20:
                case G26:
                case G28:
                case G13:
                case G18:
                case G6:
                    color += 0.6f;
                    break;
                default:
                    break;
            }
            poseStack.pushPose();
            symbol.getModel(symbol.getSymbolType().getPointOfOriginType(), null, StargatePointOfOriginsDefaults.VARIANT_GATE).render(poseStack, graphics.bufferSource(), 0, false, color);
            poseStack.popPose();
        }

        poseStack.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        poseStack.popPose();
    }

    private static void renderPEGGate(double tick) {
        // Ring
        poseStack.pushPose();
        ElementEnum.PEGASUS_RING.bindTexture().render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();

        // Gate
        poseStack.pushPose();
        ElementEnum.PEGASUS_GATE.bindTexture().render(poseStack, graphics.bufferSource(), 0);
        poseStack.popPose();

        int glyphsCount = (int) ((tick * coefficient / 2) % (37 * 3));
        if (glyphsCount > 36) glyphsCount = 36;

        int chevronsCount = glyphsCount / 4;

        poseStack.pushPose();
        for (int i = -8; i < (glyphsCount - 8); i++) {
            int ii = (i % 36);

            if (ii < 0) ii = 36 + ii;

            ii = 36 - ii;

            renderPegasusGlyph(ii, ii);
        }
        poseStack.popPose();

        // Chevrons
        if (chevronsCount == 4) chevronsCount = 3;
        if (chevronsCount == 5) chevronsCount = 3;
        if (chevronsCount > 5) chevronsCount -= 2;
        poseStack.pushPose();
        ChevronTextureList chevrons = new ChevronTextureList(JSGApi.JSG_LOADERS_HOLDER.texture(), "pegasus/chevron", chevronsCount, (chevronsCount == 7));
        chevrons.initClient();

        for (ChevronEnum chevron : ChevronEnum.values()) {
            for (int i = 0; i < 2; i++) {
                boolean light = (i == 1);
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(chevron.rotation));
                if (light) {
                    float color = chevrons.getColor(chevron);
                    GL14C.glBlendColor(color, color, color, 1);
                } else
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(chevrons.get(CoreBiomeOverlays.NORMAL.get(), chevron, light)).bindTexture();
                ElementEnum.PEGASUS_CHEVRON_MOVING.render(poseStack, graphics.bufferSource(), 0);
                ElementEnum.PEGASUS_CHEVRON_LIGHT.render(poseStack, graphics.bufferSource(), 0);
                if (!light) {
                    ElementEnum.PEGASUS_CHEVRON_FRAME.bindTexture().render(poseStack, graphics.bufferSource(), 0);
                    ElementEnum.PEGASUS_CHEVRON_BACK.render(poseStack, graphics.bufferSource(), 0);
                }
                poseStack.popPose();
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
        }
        poseStack.popPose();
    }

    private static double[] getPositionInRingAtIndex(double radius, int index) {
        double deg = ((360.0 / 36) * index);
        double rad = Math.toRadians(deg);
        return new double[]{radius * Math.cos(rad), radius * Math.sin(rad), deg};
    }

    private static void renderPegasusGlyph(int glyphId, int slot) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        EmissiveRenderer.renderWithLightOverlay(poseStack, 0, false, () -> {
            var symbol = JSGSymbolTypes.PEGASUS.get().valueOf(glyphId);
            if (symbol != null) {
                symbol.bindIconTexture(null, StargatePointOfOriginsDefaults.VARIANT_GATE_PNG);
            }
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        }, () -> {
            double[] slotPos = getPositionInRingAtIndex((StargateMilkyWayRenderer.GATE_DIAMETER / 2) - 0.85, slot);

            // Round is necessary here, since Minecraft doesn't handle many decimal places very well in this case,
            // so that the texture just ceases to exist.
            poseStack.translate(NumberUtils.round(slotPos[0], 3), NumberUtils.round(slotPos[1], 3), translationZ + 0.17);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));

            // The glyphs in the assets are arranged in a circle, so we extract those glyphs at certain positions.
            double radius = 0.94;
            // double[] uv = getPositionInRingAtIndex(radius, -glyphId);
            int textureSlot = JSGSymbolTypes.PEGASUS.get().valueOf(glyphId).textureSlot;
            double[] uv = getPositionInRingAtIndex(radius, -(textureSlot));
            float x = (float) ((uv[0] + radius) / 2);
            float y = (float) ((uv[1] + radius) / 2);

            float tileSize = 0.270f;
            float uvSize = 0.06250f;

            poseStack.mulPose(Axis.YP.rotationDegrees((360.0f / 36) * (slot - textureSlot) + 180));
            Matrix4f matrix = poseStack.last().pose();
            buffer.vertex(matrix, -tileSize, 0, -tileSize).uv(x, y).endVertex();
            buffer.vertex(matrix, -tileSize, 0, tileSize).uv(x, y + uvSize).endVertex();
            buffer.vertex(matrix, tileSize, 0, tileSize).uv(x + uvSize, y + uvSize).endVertex();
            buffer.vertex(matrix, tileSize, 0, -tileSize).uv(x + uvSize, y).endVertex();

            tessellator.end();
        }, GameRenderer::getPositionTexShader);
    }
}
