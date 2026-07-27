package dev.tauri.jsg.client.renderer.blockentity.dialhomedevice;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.common.dialhomedevice.animation.DHDButtonsState;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.raycaster.RaycasterPegasusDHD;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.JSGRaycasters;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.raycaster.util.RayCastedButton;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DHDPegasusRenderer extends DHDAbstractRenderer<DHDPegasusRendererState> {
    public DHDPegasusRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    public List<RayCastedButton> getRaycasterButtons() {
        if (rendererState == null) return List.of();
        if (!rendererState.isAssembled(JSGItems.PEGASUS_DHD_BUTTONS_CONSOLE.get())) {
            return RaycasterPegasusDHD.BUTTONS.stream()
                    .filter(btn -> btn.buttonId >= 100)
                    .toList();
        }
        if (!rendererState.isAssembled(JSGItems.PEGASUS_DHD_ACTIVATION_BUTTON.get())) {
            return RaycasterPegasusDHD.BUTTONS.stream()
                    .filter(btn -> btn.buttonId != tileEntity.getSymbolType().getBRB().getId())
                    .toList();
        }
        return RaycasterPegasusDHD.BUTTONS;
    }

    @Override
    public Raycaster getRaycaster() {
        return JSGRaycasters.PEGASUS_DHD_RAYCASTER.get();
    }

    @Override
    public void renderSymbols(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, DHDButtonsState buttonsState) {
        if (!tileEntity.isAssembled(JSGItems.PEGASUS_DHD_BUTTONS_CONSOLE.get())) return;
        CompoundTag compound = getNoteBookPage();

        for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
            if (symbol.brb() && !rendererState.isAssembled(JSGItems.PEGASUS_DHD_ACTIVATION_BUTTON.get()))
                continue;
            poseStack.pushPose();
            var btnColor = getColorByAddress(rendererState, compound, JSGSymbolTypes.PEGASUS.get(), symbol);
            var btnTex = buttonsState.get(symbol).getTexture(rendererState.getBiomeOverlay());
            JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(btnTex).bindTexture();
            symbol.getModel(symbol.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(poseStack, bufferSource, combinedLight, combinedOverlay, buttonsState.get(symbol).isActive(), btnColor.x, btnColor.y, btnColor.z, 1f, true);
            if (symbol.brb())
                ElementEnum.PEGASUS_DHD_BASE.bindTexture(rendererState.getBiomeOverlay());
            symbol.getModel(symbol.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD).render(poseStack, bufferSource, combinedLight, combinedOverlay, false, btnColor.x, btnColor.y, btnColor.z, 1f, true);
            poseStack.popPose();
        }
    }

    @Override
    public void renderDHD(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ElementEnum.PEGASUS_DHD_BASE.bindTexture(rendererState.getBiomeOverlay()).render(poseStack, bufferSource, combinedLight, combinedOverlay);
        ElementEnum.PEGASUS_DHD_CRYSTAL_HOLDER.render(poseStack, bufferSource, combinedLight, combinedOverlay);

        var slot = new AtomicInteger(0);
        DHDMilkyWayRenderer.UPGRADE_POSES.forEach(pose -> {
            if (!rendererState.hasUpgrade(slot.getAndIncrement())) return;
            poseStack.pushPose();
            poseStack.translate(pose.x(), pose.y(), -0.083);
            ElementEnum.PEGASUS_DHD_UPGRADE_CRYSTAL.bindTexture(rendererState.getBiomeOverlay()).render(poseStack, bufferSource, combinedLight, combinedOverlay);
            poseStack.popPose();
        });
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_PEGASUS.get();
    }

    @Override
    protected IDHDPartItem getUpgradesCover() {
        return JSGItems.PEGASUS_DHD_UPGRADES_COVER.get();
    }

    @Override
    public @NotNull PartRenderable getPartModelRenderable(IDHDPartItem part) {
        if (part == JSGItems.PEGASUS_DHD_CONTROL_CRYSTALS.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.PEGASUS_DHD_CRYSTALS.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        if (part == JSGItems.PEGASUS_DHD_BUTTONS_CONSOLE.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.PEGASUS_DHD_BUTTON_CONSOLE.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        if (part == JSGItems.PEGASUS_DHD_MAIN_CRYSTAL.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.PEGASUS_DHD_CONTROL_CRYSTAL.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        if (part == JSGItems.PEGASUS_DHD_ACTIVATION_BUTTON.get()) {
            return (poseStack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                if (assembled) return;
                var symbol = tileEntity.getSymbolType().getBRB();
                var buttonsState = tileEntity.getStateManager().getButtonsState();
                poseStack.pushPose();
                var btnTex = buttonsState.get(symbol).getTexture(rendererState.getBiomeOverlay());
                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(btnTex).bindTexture();
                symbol.getModel(symbol.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(poseStack, bufferSource, combinedLight, combinedOverlay, buttonsState.get(symbol).isActive(), r, g, b, a, true);
                if (symbol.brb())
                    ElementEnum.PEGASUS_DHD_BASE.bindTexture(rendererState.getBiomeOverlay());
                symbol.getModel(symbol.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD).render(poseStack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, true);
                poseStack.popPose();
            };
        }
        if (part == JSGItems.DHD_NAQUADAH_TANK.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                stack.pushPose();
                stack.translate(0, 0.252, -0.06);
                stack.mulPose(Axis.YP.rotationDegrees(180));
                ElementEnum.PEGASUS_DHD_FLUID_TANK_BASE
                        .bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
                RenderSystem.enableBlend();
                TextureAtlasSprite sprite = BlockRenderer.getFluidTexture(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.still.get(), 1000), BlockRenderer.FluidTextureType.STILL);
                if (sprite != null) {
                    stack.pushPose();
                    float height = assembled ? (rendererState.naquadahAmount / (float) rendererState.naquadahMaxAmount) : 1f;
                    stack.scale(1, height, 1);
                    ITexture.bindTextureWithMc(sprite.atlasLocation());
                    ElementEnum.PEGASUS_DHD_FLUID_TANK_FLUID
                            .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false, sprite);
                    stack.popPose();
                }
                ElementEnum.PEGASUS_DHD_FLUID_TANK_GLASS
                        .bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
                RenderSystem.disableBlend();
                stack.popPose();
            };
        }
        if (part == JSGItems.PEGASUS_DHD_UPGRADES_COVER.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.PEGASUS_DHD_UPGRADE_COVER.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        throw new UnsupportedOperationException("Item " + part.toString() + " not supported as part of the DHD " + this.getClass().getCanonicalName());
    }

    @Override
    public Item getNeededSchematic() {
        return JSGItems.SCHEMATIC_PEGASUS.get();
    }
}
