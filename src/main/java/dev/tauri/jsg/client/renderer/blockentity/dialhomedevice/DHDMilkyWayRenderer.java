package dev.tauri.jsg.client.renderer.blockentity.dialhomedevice;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.raycaster.RaycasterMilkyWayDHD;
import dev.tauri.jsg.common.registry.JSGBlocks;
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
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class DHDMilkyWayRenderer extends DHDAbstractRenderer<DHDMilkyWayRendererState> {

    public DHDMilkyWayRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    public List<RayCastedButton> getRaycasterButtons() {
        return RaycasterMilkyWayDHD.BUTTONS;
    }

    @Override
    public Raycaster getRaycaster() {
        return JSGRaycasters.MILKYWAY_DHD_RAYCASTER.get();
    }

    @Override
    public void renderSymbols(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        CompoundTag compound = getNoteBookPage();

        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            poseStack.pushPose();
            var btnColor = getColorByAddress(rendererState, compound, JSGSymbolTypes.MILKYWAY.get(), symbol);

            if (symbol.origin()) {
                // render plate for PoO
                var plate = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4_base.obj");
                var plateLight = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4.obj");
                ElementEnum.MILKYWAY_DHD_BASE.bindTexture(rendererState.getBiomeOverlay());
                JSGApi.JSG_LOADERS_HOLDER.model().getModel(plate).render(poseStack, bufferSource, combinedLight, combinedOverlay, false, btnColor.x, btnColor.y, btnColor.z, 1f, false);

                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(rendererState.getButtonTexture(symbol, rendererState.getBiomeOverlay())).bindTexture();
                JSGApi.JSG_LOADERS_HOLDER.model().getModel(plateLight).render(poseStack, bufferSource, combinedLight, combinedOverlay, rendererState.isButtonActive(symbol), btnColor.x, btnColor.y, btnColor.z, 1f, false);
            }

            // render symbol light emissive
            JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(rendererState.getButtonTexture(symbol, rendererState.getBiomeOverlay())).bindTexture();
            symbol.getModel(tileEntity.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(poseStack, bufferSource, combinedLight, combinedOverlay, rendererState.isButtonActive(symbol), btnColor.x, btnColor.y, btnColor.z, 1f, false);
            if (symbol.brb()) {
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
            // render symbol base
            ElementEnum.MILKYWAY_DHD_BASE.bindTexture(rendererState.getBiomeOverlay());
            symbol.getModel(tileEntity.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD).render(poseStack, bufferSource, combinedLight, combinedOverlay, false, btnColor.x, btnColor.y, btnColor.z, 1f, false);
            poseStack.popPose();
        }
    }

    @Override
    public void renderDHD(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ElementEnum.MILKYWAY_DHD_BASE.bindTexture(rendererState.getBiomeOverlay()).render(poseStack, bufferSource, combinedLight, combinedOverlay);
        ElementEnum.MILKYWAY_DHD_BUTTON_CONSOLE.render(poseStack, bufferSource, combinedLight, combinedOverlay);
        ElementEnum.MILKYWAY_DHD_CRYSTAL_HOLDER.render(poseStack, bufferSource, combinedLight, combinedOverlay);
        ElementEnum.MILKYWAY_DHD_UPGRADE_COVER.render(poseStack, bufferSource, combinedLight, combinedOverlay);
        ElementEnum.MILKYWAY_DHD_UPGRADE_CRYSTAL.bindTexture(rendererState.getBiomeOverlay()).render(poseStack, bufferSource, combinedLight, combinedOverlay);

        ElementEnum.MILKYWAY_DHD_CRYSTALS.bindTexture(rendererState.getBiomeOverlay()).render(poseStack, bufferSource, combinedLight, combinedOverlay);
        if (!tileEntity.getItemStackHandler().getStackInSlot(0).isEmpty())
            ElementEnum.MILKYWAY_DHD_CONTROL_CRYSTAL.render(poseStack, bufferSource, combinedLight, combinedOverlay);

        ElementEnum.MILKYWAY_DHD_FLUID_TANK_BASE.bindTexture(rendererState.getBiomeOverlay()).render(poseStack, bufferSource, combinedLight, combinedOverlay);
        RenderSystem.enableBlend();
        TextureAtlasSprite sprite = BlockRenderer.getFluidTexture(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.still.get(), 1000), BlockRenderer.FluidTextureType.STILL);
        if (sprite != null) {
            ITexture.bindTextureWithMc(sprite.atlasLocation());
            ElementEnum.MILKYWAY_DHD_FLUID_TANK_FLUID.render(poseStack, bufferSource, combinedLight, combinedOverlay, sprite);
        }
        ElementEnum.MILKYWAY_DHD_FLUID_TANK_GLASS.bindTexture(rendererState.getBiomeOverlay()).render(poseStack, bufferSource, combinedLight, combinedOverlay);
        RenderSystem.disableBlend();
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_MILKYWAY.get();
    }
}
