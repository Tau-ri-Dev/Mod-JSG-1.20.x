package dev.tauri.jsg.client.renderer.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.helper.ItemRenderingHelper;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

import static dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer.drawModalRectWithCustomSizedTexture;

@OnlyIn(Dist.CLIENT)
public class GarageDoorOpenerBEWLR extends BlockEntityWithoutLevelRenderer {
    public GarageDoorOpenerBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        stack.pushPose();
        RenderSystem.enableDepthTest();
        // Item frame
        if (itemDisplayContext == ItemDisplayContext.FIXED) {
            stack.pushPose();

            stack.translate(0.5, 0.5, 0.5);
            stack.scale(1, 1, 1);
            stack.mulPose(Axis.XP.rotationDegrees(90));

            stack.scale(0.2f, 0.2f, 0.2f);
            stack.pushPose();
            stack.pushPose();
        } else {
            boolean mainHand = (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
            boolean thirdPerson = (itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
            if (mainHand)
                translateForRightHand(stack, bufferSource, light, !thirdPerson);
            else
                translateForLeftHand(stack, bufferSource, light, !thirdPerson);

            if (thirdPerson) {
                if (mainHand) {
                    stack.mulPose(Axis.XP.rotationDegrees(-100));
                    stack.mulPose(Axis.YP.rotationDegrees(194));
                    stack.mulPose(Axis.ZP.rotationDegrees(-36));
                    stack.translate(1, -3, -1.2);
                    stack.scale(0.7f, 0.7f, 0.7f);
                } else {
                    stack.mulPose(Axis.XP.rotationDegrees(-100));
                    stack.mulPose(Axis.YP.rotationDegrees(163));
                    stack.mulPose(Axis.ZP.rotationDegrees(33));
                    stack.translate(-2.5, -3, -1.2);
                    stack.scale(0.75f, 0.75f, 0.75f);
                }
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
        HandHeldDeviceRenderer.drawStringWithShadow(stack, bufferSource, 0, -0.4f, I18n.format("item.jsg.gdo.code") + " " + code, 0xffffff, true);

        stack.popPose();
        stack.popPose();

        stack.popPose();
        stack.popPose();

        stack.popPose();
        stack.popPose();
        RenderSystem.disableDepthTest();
    }

    private void translateForRightHand(PoseStack stack, MultiBufferSource source, int light, boolean renderHand) {
        stack.mulPose(Axis.YP.rotationDegrees(-25));
        stack.mulPose(Axis.ZP.rotationDegrees(-30));
        stack.translate(-0.5, 0.25, -0.8);

        var partialTicks = Minecraft.getInstance().getPartialTick();

        stack.pushPose();
        stack.translate(0, 0.2, 0.5);
        stack.mulPose(Axis.ZP.rotationDegrees(10));

        if (renderHand)
            HandHeldDeviceRenderer.renderArms(stack, source, light, HumanoidArm.RIGHT, partialTicks);

        stack.pushPose();
        if (renderHand)
            ItemRenderingHelper.applyBobbing(stack, partialTicks);

        stack.mulPose(Axis.ZP.rotationDegrees(-60));
        stack.translate(0, 0.75, 1);
        stack.scale(0.15f, 0.15f, 0.15f);
        stack.pushPose();

        stack.mulPose(Axis.YP.rotationDegrees(57));
        stack.mulPose(Axis.ZP.rotationDegrees(-11));
        stack.translate(2, 0, -0.2);
    }

    private void translateForLeftHand(PoseStack stack, MultiBufferSource source, int light, boolean renderHand) {
        stack.mulPose(Axis.XP.rotationDegrees(-50));
        stack.mulPose(Axis.YP.rotationDegrees(50));
        stack.mulPose(Axis.ZP.rotationDegrees(130));
        stack.translate(-0.65, -0.4, -0.3);

        var partialTicks = Minecraft.getInstance().getPartialTick();

        stack.pushPose();
        stack.translate(0, 0.2, 0.5);
        stack.mulPose(Axis.ZP.rotationDegrees(10));

        if (renderHand)
            HandHeldDeviceRenderer.renderArms(stack, source, light, HumanoidArm.LEFT, partialTicks);

        stack.pushPose();
        if (renderHand)
            ItemRenderingHelper.applyBobbing(stack, partialTicks);

        stack.mulPose(Axis.ZP.rotationDegrees(-60));
        stack.translate(0, 0.75, 1);
        stack.scale(0.15f, 0.15f, 0.15f);
        stack.pushPose();

        stack.mulPose(Axis.YP.rotationDegrees(57));
        stack.mulPose(Axis.ZP.rotationDegrees(-11));
        stack.translate(2, 0, -0.2);

        stack.mulPose(Axis.XP.rotationDegrees(-35));
        stack.mulPose(Axis.YP.rotationDegrees(195));
        stack.mulPose(Axis.ZP.rotationDegrees(6));
        stack.translate(0, -0.5, -1.2);
        stack.scale(0.9f, 0.9f, 0.9f);
    }
}
