package dev.tauri.jsg.client.renderer.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer;
import dev.tauri.jsg.core.client.renderer.HandPosition;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer.drawModalRectWithCustomSizedTexture;

@OnlyIn(Dist.CLIENT)
public class GarageDoorOpenerBEWLR extends AbstractItemBEWLR {

    @Override
    public void renderItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick) {
        stack.pushPose();
        RenderSystem.enableDepthTest();

        if (itemDisplayContext == ItemDisplayContext.GUI) {
            stack.scale(0.2f, 0.2f, 0.2f);
            stack.mulPose(Axis.XP.rotationDegrees(-90));
            stack.mulPose(Axis.YP.rotationDegrees(180 + 45));
            stack.translate(1.4, 0, -1.25);
            stack.mulPose(Axis.YP.rotationDegrees(45));
        } else if (itemDisplayContext == ItemDisplayContext.GROUND) {
            stack.scale(0.15f, 0.15f, 0.15f);

        } else if (itemDisplayContext == ItemDisplayContext.FIXED) {
            stack.scale(0.2f, 0.2f, 0.2f);
            stack.mulPose(Axis.XP.rotationDegrees(90));
            stack.translate(2.5, 2.8, -2.5);
        } else {
            if (itemDisplayContext.firstPerson()) {
                stack.translate(0, -0.3, 0.6);
                stack.scale(0.1f, 0.1f, 0.1f);
                stack.mulPose(Axis.YP.rotationDegrees(180));
                stack.mulPose(Axis.XP.rotationDegrees(50));
            } else {
                float f = (itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ? 1 : -1);
                stack.translate(0, -0.8, 0.3);
                stack.scale(0.1f, 0.1f, 0.1f);
                stack.mulPose(Axis.YP.rotationDegrees(90 * f));
            }
        }

        ElementEnum.GDO.bindTexture().render(stack, bufferSource, light);

        // render text
        stack.pushPose();

        stack.mulPose(Axis.YP.rotationDegrees(180));
        stack.scale(1, -1, 1);
        stack.mulPose(Axis.XP.rotationDegrees(90));

        double signal = 0;
        double battery = 0;
        var compound = itemStack.getTag();
        if (compound != null) {
            if (compound.contains("battery_percentage")) {
                battery = compound.getDouble("battery_percentage");
                if (battery < 0) battery = 0;
                if (battery > 1) battery = 1;
            }
            if (compound.contains("linked_gate_pos")) {
                if (compound.contains("signal_strength")) {
                    signal = compound.getDouble("signal_strength");
                    if (signal < 0) signal = 0;
                    if (signal > 1) signal = 1;
                }
            }
        }

        // draw battery and signal status
        stack.pushPose();
        RenderSystem.enableDepthTest();

        stack.translate(-2.07, 0.25, 0.19);
        stack.scale(0.7f, 0.7f, 0.7f);
        stack.translate(0.88f, 0.24f, 0);

        ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/battery_background.png"));
        drawModalRectWithCustomSizedTexture(stack, bufferSource, light, 0.44f, 0, 0, 0, 0, 0.24f, 0.24f, 0.24f, 0.24f);
        ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/battery_inside.png"));
        float pixels = (float) (12f * battery + 2f);
        drawModalRectWithCustomSizedTexture(stack, bufferSource, light, 0.44f, 0, 0, 0, 0, (pixels / 16f) * 0.24f, 0.24f, 0.24f, 0.24f);
        if (signal <= 0) {
            ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/signal_no_signal.png"));
            drawModalRectWithCustomSizedTexture(stack, bufferSource, light, 0.2f, 0, 0, 0, 0, 0.24f, 0.24f, 0.24f, 0.24f);
        } else {
            ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/signal_background.png"));
            pixels = 4f;
            if (signal > 0.25f)
                pixels = 7f;
            if (signal > 0.5f)
                pixels = 10f;
            if (signal > 0.75f)
                pixels = 16f;
            drawModalRectWithCustomSizedTexture(stack, bufferSource, light, 0.2f, 0, 0, 0, 0, (pixels / 16f) * 0.24f, 0.24f, 0.24f, 0.24f);
        }
        stack.popPose();

        // draw
        stack.pushPose();
        stack.translate(-0.75, 0.65, 0.19);

        var code = "";
        if (compound != null && compound.contains("entered_code"))
            code = compound.getString("entered_code");

        HandHeldDeviceRenderer.drawStringWithShadow(stack, bufferSource, 0, 0, I18n.format("item.jsg.gdo.operator"), 0, true);
        HandHeldDeviceRenderer.drawStringWithShadow(stack, bufferSource, 0, -0.4f, I18n.format("item.jsg.gdo.code") + " " + code, 0, false);

        stack.popPose();
        stack.popPose();

        stack.popPose();
        RenderSystem.disableDepthTest();
    }

    @Override
    public HandPosition getHandPosition(ItemDisplayContext itemDisplayContext) {
        return HandPosition.LOOK_AT_DISPLAY;
    }
}
