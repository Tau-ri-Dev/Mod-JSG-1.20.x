package dev.tauri.jsg.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdminControllerBEWLR extends AbstractItemBEWLR {
    @Override
    public void renderItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource buffer, int light, int overlay, float partialTick) {
        if(itemDisplayContext == ItemDisplayContext.GUI){
            stack.scale(0.7f, 0.7f, 0.7f);
            stack.mulPose(Axis.ZP.rotationDegrees(-45));
            stack.mulPose(Axis.YP.rotationDegrees(180));
            stack.translate(0.35, 0.35, 0);
        }
        else if(itemDisplayContext == ItemDisplayContext.GROUND){
            stack.scale(0.7f, 0.7f, 0.7f);
        }
        else if(itemDisplayContext == ItemDisplayContext.FIXED){
            stack.translate(0.5, 0.5, 0.48);
            stack.scale(0.7f, 0.7f, 0.7f);
        }
        else {
            if(itemDisplayContext.firstPerson()){
                stack.mulPose(Axis.YP.rotationDegrees(180));
                stack.scale(0.4f, 0.4f, 0.4f);
                stack.translate(0, -0.2, -3.5 / 2f);
                stack.mulPose(Axis.ZP.rotationDegrees(5));
            }
            else {
                stack.mulPose(Axis.YP.rotationDegrees(180));
                stack.translate(0, -0.5, -0.8);
                stack.scale(0.7f, 0.7f, 0.7f);
            }
        }

        ElementEnum.ADMIN_CONTROLLER.bindTexture().render(stack, buffer, light, overlay);
    }

    @Override
    public boolean renderHands(ItemDisplayContext itemDisplayContext) {
        return true;
    }
}
