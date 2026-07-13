package dev.tauri.jsg.client.renderer.blockentity.dialhomedevice;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.client.renderer.blockentity.dialhomedevice.DHDAbstractRenderer.PartRenderable;
import dev.tauri.jsg.common.dialhomedevice.animation.DHDButtonsState;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.raycaster.RaycasterMilkyWayDHD;
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
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

import java.util.List;

public class DHDMilkyWayRenderer extends DHDAbstractRenderer<DHDMilkyWayRendererState> {

    public DHDMilkyWayRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    public List<RayCastedButton> getRaycasterButtons() {
        if (rendererState == null) return List.of();
        if (!rendererState.isAssembled(JSGItems.MILKYWAY_DHD_BUTTONS_CONSOLE.get())) {
            return RaycasterMilkyWayDHD.BUTTONS.stream()
                    .filter(btn -> btn.buttonId >= 100)
                    .toList();
        }
        if (!rendererState.isAssembled(JSGItems.MILKYWAY_DHD_ACTIVATION_BUTTON.get())) {
            return RaycasterMilkyWayDHD.BUTTONS.stream()
                    .filter(btn -> btn.buttonId != tileEntity.getSymbolType().getBRB().getId())
                    .toList();
        }
        return RaycasterMilkyWayDHD.BUTTONS;
    }

    @Override
    public Raycaster getRaycaster() {
        return JSGRaycasters.MILKYWAY_DHD_RAYCASTER.get();
    }

    @Override
    public void renderSymbols(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, DHDButtonsState buttonsState) {
        if (!tileEntity.isAssembled(JSGItems.MILKYWAY_DHD_BUTTONS_CONSOLE.get())) return;
        CompoundTag compound = getNoteBookPage();

        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            if (symbol == SymbolMilkyWayEnum.AQUILA) continue; // skip rendering Aquila as it doesn't have a model
            if (symbol.brb() && !rendererState.isAssembled(JSGItems.MILKYWAY_DHD_ACTIVATION_BUTTON.get()))
                continue;

            poseStack.pushPose();
            var btnColor = getColorByAddress(rendererState, compound, JSGSymbolTypes.MILKYWAY.get(), symbol);

            if (symbol.origin()) {
                // render plate for PoO
                var plate = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4_base.obj");
                var plateLight = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4.obj");
                ElementEnum.MILKYWAY_DHD_BASE.bindTexture(rendererState.getBiomeOverlay());
                JSGApi.JSG_LOADERS_HOLDER.model().getModel(plate).render(poseStack, bufferSource, combinedLight, combinedOverlay, false, btnColor.x, btnColor.y, btnColor.z, 1f, false);

                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(buttonsState.get(symbol).getTexture(rendererState.getBiomeOverlay())).bindTexture();
                JSGApi.JSG_LOADERS_HOLDER.model().getModel(plateLight).render(poseStack, bufferSource, combinedLight, combinedOverlay, buttonsState.get(symbol).isActive(), btnColor.x, btnColor.y, btnColor.z, 1f, false);
            }

            // render symbol light emissive
            JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(buttonsState.get(symbol).getTexture(rendererState.getBiomeOverlay())).bindTexture();
            symbol.getModel(tileEntity.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(poseStack, bufferSource, combinedLight, combinedOverlay, buttonsState.get(symbol).isActive(), btnColor.x, btnColor.y, btnColor.z, 1f, false);
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
    public void renderDHD(PoseStack stack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ElementEnum.MILKYWAY_DHD_BASE.bindTexture(rendererState.getBiomeOverlay()).render(stack, bufferSource, combinedLight, combinedOverlay);
        ElementEnum.MILKYWAY_DHD_CRYSTAL_HOLDER.render(stack, bufferSource, combinedLight, combinedOverlay);

        var upgradePoses = List.of(
                new Vector2d(0, 0.108),
                new Vector2d(0.08, 0.251),
                new Vector2d(-0.076, 0.251),
                new Vector2d(-0.120, 0.3825),
                new Vector2d(0.120, 0.3825)
        );

        upgradePoses.forEach(pose -> {
            stack.pushPose();
            stack.translate(pose.x(), pose.y(), -0.083);
            ElementEnum.MILKYWAY_DHD_UPGRADE_CRYSTAL.bindTexture(rendererState.getBiomeOverlay()).render(stack, bufferSource, combinedLight, combinedOverlay);
            stack.popPose();
        });
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_MILKYWAY.get();
    }

    @Override
    protected IDHDPartItem getUpgradesCover(){
        return JSGItems.MILKYWAY_DHD_UPGRADES_COVER.get();
    }

    @Override
    public @NotNull PartRenderable getPartModelRenderable(IDHDPartItem part) {
        if (part == JSGItems.MILKYWAY_DHD_CONTROL_CRYSTALS.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.MILKYWAY_DHD_CRYSTALS.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        if (part == JSGItems.MILKYWAY_DHD_BUTTONS_CONSOLE.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.MILKYWAY_DHD_BUTTON_CONSOLE.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        if (part == JSGItems.MILKYWAY_DHD_MAIN_CRYSTAL.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.MILKYWAY_DHD_CONTROL_CRYSTAL.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        if (part == JSGItems.MILKYWAY_DHD_ACTIVATION_BUTTON.get()) {
            return (poseStack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                if (assembled) return;
                var symbol = tileEntity.getSymbolType().getBRB();
                var buttonsState = tileEntity.getStateManager().getButtonsState();
                poseStack.pushPose();
                // render symbol light emissive
                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(buttonsState.get(symbol).getTexture(rendererState.getBiomeOverlay())).bindTexture();
                symbol.getModel(tileEntity.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT)
                        .render(poseStack, bufferSource, combinedLight, combinedOverlay, buttonsState.get(symbol).isActive(), r, g, b, a, false);

                // render symbol base
                ElementEnum.MILKYWAY_DHD_BASE.bindTexture(rendererState.getBiomeOverlay());
                symbol.getModel(tileEntity.getSymbolType().getPointOfOriginType(), tileEntity.getPointOfOrigin(), StargatePointOfOriginsDefaults.VARIANT_DHD)
                        .render(poseStack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
                poseStack.popPose();
            };
        }
        if (part == JSGItems.DHD_NAQUADAH_TANK.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                stack.pushPose();
                stack.translate(0, 0.252, -0.06);
                stack.mulPose(Axis.YP.rotationDegrees(180));
                ElementEnum.MILKYWAY_DHD_FLUID_TANK_BASE
                        .bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);

                RenderSystem.enableBlend();
                TextureAtlasSprite sprite = BlockRenderer.getFluidTexture(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.still.get(), 1000), BlockRenderer.FluidTextureType.STILL);
                if (sprite != null) {
                    stack.pushPose();
                    float height = assembled ? (rendererState.naquadahAmount / (float) rendererState.naquadahMaxAmount) : 1f;
                    stack.scale(1, height, 1);
                    ITexture.bindTextureWithMc(sprite.atlasLocation());
                    ElementEnum.MILKYWAY_DHD_FLUID_TANK_FLUID
                            .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false, sprite);
                    stack.popPose();
                }
                ElementEnum.MILKYWAY_DHD_FLUID_TANK_GLASS
                        .bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
                RenderSystem.disableBlend();

                stack.popPose();
            };
        }
        if (part == JSGItems.MILKYWAY_DHD_UPGRADES_COVER.get()) {
            return (stack, bufferSource, combinedLight, combinedOverlay, partialTick, r, g, b, a, assembled) -> {
                ElementEnum.MILKYWAY_DHD_UPGRADE_COVER.bindTexture(rendererState.getBiomeOverlay())
                        .render(stack, bufferSource, combinedLight, combinedOverlay, false, r, g, b, a, false);
            };
        }
        throw new UnsupportedOperationException("Item " + part.toString() + " not supported as part of the DHD " + this.getClass().getCanonicalName());
    }

    @Override
    public Item getNeededSchematic() {
        return JSGItems.SCHEMATIC_MILKYWAY.get();
    }
}
