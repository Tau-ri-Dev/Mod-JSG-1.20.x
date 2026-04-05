package dev.tauri.jsg.item.admincontroller;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.common.helper.ItemRenderingHelper;
import dev.tauri.jsg.loader.ElementEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class AdminControllerBEWLR extends BlockEntityWithoutLevelRenderer {

    public AdminControllerBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemDisplayContext context, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();
        boolean isGui = (context == ItemDisplayContext.GUI);

        // 1. FIXED
        if (context == ItemDisplayContext.FIXED) {
            stack.translate(0.5, 0.5, 0.5);
            stack.scale(0.6f, 0.6f, 0.6f);
        }
        // 2. FIRST PERSON
        else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            boolean isRightHand = (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
            HumanoidArm handSide = isRightHand ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
            var partialTicks = Minecraft.getInstance().getPartialTick();

            /* Hand rendering is now turned off until core separation, because debug mod not working.

            stack.translate(isRightHand ? 0.8 : -0.8, -0.6, 0.2);
            stack.mulPose(Axis.YP.rotationDegrees(isRightHand ? 25 : -25));

            // WARNING no standard axis rotations! x -> left, right; y -> up, down; z ->  palm rotation
            stack.mulPose(Axis.XP.rotationDegrees(-100));  //-90 ??
            stack.mulPose(Axis.YP.rotationDegrees(isRightHand ? -50 : 40));
            stack.mulPose(Axis.ZP.rotationDegrees(90));

            HandHeldDeviceRenderer.renderArms(stack, buffer, light, handSide, partialTicks); */

            ItemRenderingHelper.applyBobbing(stack, partialTicks);
            stack.scale(0.6f, 0.6f, 0.6f);
            stack.translate(0, 0.75, 0);                   //WIP position for hand -> -1.5, -0.5, -1.5
            stack.mulPose(Axis.YP.rotationDegrees(90));
        }
        // 3. THIRD PERSON
        else if (context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            stack.scale(0.4f, 0.4f, 0.4f);
            stack.translate(1.25, 0.85, 1.12);
            stack.mulPose(Axis.XP.rotationDegrees(0));
        }
        // 4. GUI
        else if (isGui) {
            RenderSystem.enableDepthTest();
            AbstractOBJModel.setGUIRender();
            stack.translate(0.5, 0.49, 0);
            stack.mulPose(Axis.XP.rotationDegrees(30));
            stack.mulPose(Axis.YP.rotationDegrees(20));
            stack.scale(0.75f, 0.75f, 0.75f);
        }
        // 5. GROUND
        else if (context == ItemDisplayContext.GROUND) {
            stack.translate(0.5, 0.2, 0.5);
            stack.scale(0.8f, 0.8f, 0.8f);
            stack.mulPose(Axis.XP.rotationDegrees(270));
        }

        ElementEnum.ADMIN_CONTROLLER.bindTexture().render(stack, buffer, light, overlay);

        if (isGui) {
            AbstractOBJModel.resetRenderType();
        }
        stack.popPose();
    }
   }
