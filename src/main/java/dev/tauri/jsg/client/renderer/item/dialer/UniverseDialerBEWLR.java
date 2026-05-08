package dev.tauri.jsg.client.renderer.item.dialer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import dev.tauri.jsg.core.client.renderer.HandPosition;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class UniverseDialerBEWLR extends AbstractItemBEWLR {
    @Override
    public void renderItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick) {
        //if(itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) return;
        stack.pushPose();
        RenderSystem.enableDepthTest();

        float f = (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ? 1 : -1);

        if(itemDisplayContext == ItemDisplayContext.GUI){
            stack.scale(0.2f, 0.2f, 0.2f);
            stack.mulPose(Axis.XP.rotationDegrees(-90));
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(1.5, 0, 0);
        }
        else if(itemDisplayContext == ItemDisplayContext.GROUND){
            stack.scale(0.2f, 0.2f, 0.2f);
        }
        else if(itemDisplayContext == ItemDisplayContext.FIXED){
            stack.translate(0.5, 0.5, 0.43);
            stack.mulPose(Axis.XP.rotationDegrees(90));
            stack.scale(0.2f, 0.2f, 0.2f);
        }
        else {
            if(itemDisplayContext.firstPerson()) {
                stack.translate(0, -0.1, 1);
                stack.scale(0.2f, 0.2f, 0.2f);
                stack.mulPose(Axis.YP.rotationDegrees(190));
                stack.mulPose(Axis.XP.rotationDegrees(50));
                stack.mulPose(Axis.ZN.rotationDegrees(-10 * f));
            }
            else {
                stack.translate(0, -0.7, 1);
                stack.scale(0.2f, 0.2f, 0.2f);
                stack.mulPose(Axis.YP.rotationDegrees(190));
                stack.mulPose(Axis.XP.rotationDegrees(70));
                stack.mulPose(Axis.ZN.rotationDegrees(-10));
            }
        }

        var compound = itemStack.getTag();
        if (compound != null) {
            var biomeOverlay = CoreBiomeOverlays.NORMAL.get();
            if (compound.contains(UniverseDialerItem.C_BIOME_OVERLAY)) {
                biomeOverlay = BiomeOverlayInstance.byId(JSGMapping.rl(compound.getString(UniverseDialerItem.C_BIOME_OVERLAY)));
            }

            ElementEnum.UNIVERSE_DIALER.bindTexture(biomeOverlay).render(stack, bufferSource, light);
            RenderSystem.enableDepthTest();

            stack.pushPose();
            var mode = UniverseDialerMode.valueOf(JSGMapping.rl(compound.getString(UniverseDialerItem.C_MODE))).orElse(UniverseDialerMode.getDefault());
            var modeTag = mode.getTag(compound);

            boolean notLinked = (mode.matchBlocks != null && !modeTag.contains(UniverseDialerMode.C_LINKED_POS));

            stack.mulPose(Axis.XP.rotationDegrees(90));
            stack.mulPose(Axis.YP.rotationDegrees(180));
            stack.translate(0.65, 0, 1.1865);
            stack.scale(1, -1, 1);
            stack.translate(-0.2, 0.8, -1);

            stack.pushPose();
            RenderSystem.enableBlend();

            if (notLinked)
                UDModesRenderUtils.drawWaringGlyph(stack, bufferSource, light, new Color(0xffffffff, true));

            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, 0, 0, mode.localize(), true, false);
            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, -0.7f, 0, mode.next().localize(), false, false);

            RenderSystem.enableDepthTest();
            mode.getScreen().render(itemStack, modeTag, itemDisplayContext, stack, bufferSource, light, overlay);
            RenderSystem.disableBlend();

            stack.popPose();
            stack.popPose();
        }
        stack.popPose();
        RenderSystem.disableDepthTest();
    }

    @Override
    public void renderHands(HumanoidArm handSide, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick) {
        if (handSide == HumanoidArm.RIGHT) {
            stack.mulPose(Axis.YP.rotationDegrees(120));
            stack.translate(0.5, 0.2, -0.4);
        }
        else {
            stack.mulPose(Axis.YP.rotationDegrees(-120));
            stack.translate(-0.5, 0.2, -0.4);
        }
        super.renderHands(handSide, itemStack, itemDisplayContext, stack, bufferSource, light, overlay, partialTick);
    }

    @Override
    public boolean renderHands(ItemDisplayContext itemDisplayContext) {
        return true;
    }

    @Override
    public HandPosition getHandPosition(ItemDisplayContext itemDisplayContext) {
        return HandPosition.NORMAL;
    }
}
