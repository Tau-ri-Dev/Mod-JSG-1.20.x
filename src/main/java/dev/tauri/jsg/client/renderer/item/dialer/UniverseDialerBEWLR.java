package dev.tauri.jsg.client.renderer.item.dialer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.helper.ItemRenderingHelper;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
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
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class UniverseDialerBEWLR extends BlockEntityWithoutLevelRenderer {
    public UniverseDialerBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        if (itemStack.hasTag()) {
            stack.pushPose();
            RenderSystem.enableDepthTest();
            // Item frame
            if (itemDisplayContext == ItemDisplayContext.FIXED) {
                stack.pushPose();
                stack.translate(0.53, 0.50, 0.6);
                stack.mulPose(Axis.XP.rotationDegrees(90));
                stack.mulPose(Axis.ZP.rotationDegrees(180));

                stack.scale(0.2f, 0.2f, 0.2f);
            } else {
                boolean mainHand = (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
                boolean thirdPerson = (itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
                HumanoidArm handSide = mainHand ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
                var partialTicks = Minecraft.getInstance().getPartialTick();

                if (thirdPerson) {
                    stack.pushPose();

                    stack.scale(0.3f, 0.3f, 0.3f);

                    stack.translate(2, 2.1, 3);
                    stack.mulPose(Axis.XP.rotationDegrees(-45));
                    stack.mulPose(Axis.YP.rotationDegrees(180));
                    stack.mulPose(Axis.ZP.rotationDegrees(0));

                    stack.mulPose(Axis.XP.rotationDegrees(50));
                    stack.translate(0, 1.5, 0.3);
                    stack.scale(0.7f, 0.7f, 0.7f);
                } else {
                    HandHeldDeviceRenderer.renderArms(stack, bufferSource, light, handSide, partialTicks);

                    stack.pushPose();
                    ItemRenderingHelper.applyBobbing(stack, partialTicks);

                    stack.scale(0.3f, 0.3f, 0.3f);

                    stack.translate(2, 2.1, 3);
                    stack.mulPose(Axis.XP.rotationDegrees(-45));
                    stack.mulPose(Axis.YP.rotationDegrees(180));
                    stack.mulPose(Axis.ZP.rotationDegrees(0));
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
            stack.popPose();
            RenderSystem.disableDepthTest();
        }
    }
}
