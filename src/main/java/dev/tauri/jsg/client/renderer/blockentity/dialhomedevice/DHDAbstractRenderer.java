package dev.tauri.jsg.client.renderer.blockentity.dialhomedevice;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import dev.tauri.jsg.core.client.renderer.IRaycasterButtonsRenderer;
import dev.tauri.jsg.core.client.renderer.LinkableRenderer;
import dev.tauri.jsg.core.common.config.values.JSGConfigValue;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class DHDAbstractRenderer<S extends DHDAbstractRendererState> implements LinkableRenderer, BlockEntityRenderer<DHDAbstractBE>, IRaycasterButtonsRenderer {

    public DHDAbstractRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    public DHDAbstractBE tileEntity;
    public Level level;
    public float partialTicks;
    public S rendererState;


    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("unchecked")
    public void render(DHDAbstractBE te, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        tileEntity = te;
        rendererState = (S) te.getRendererStateClient();
        this.partialTicks = partialTicks;

        if (rendererState != null && te.getLevel() != null) {
            this.level = te.getLevel();
            level.updateSkyBrightness();
            @SuppressWarnings("null")
            BlockState state = te.getLevel().getBlockState(te.getBlockPos());
            if (state.getBlock() != getDHDBlock()) return;
            renderLink(te.getBlockPos(), te, poseStack, bufferSource);
            renderRaycasterButtons(te, poseStack, bufferSource);

            poseStack.pushPose();

            if (state.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.SNOWY)) {
                BlockRenderer.renderBlock(level, te.getBlockPos(), Blocks.SNOW.defaultBlockState(), new BlockPos(0, 0, 0), poseStack, bufferSource, combinedLight, combinedOverlay);
            }

            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(Objects.requireNonNull(level).getBlockState(tileEntity.getBlockPos()).getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ROTATION_PROPERTY) * -22.5f));

            renderDHD(poseStack, bufferSource, combinedLight, combinedOverlay);
            renderSymbols(poseStack, bufferSource, combinedLight, combinedOverlay);

            poseStack.popPose();

            rendererState.iterate(level, partialTicks);
        }
    }

    public abstract void renderSymbols(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay);

    public abstract void renderDHD(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay);

    public abstract Block getDHDBlock();

    protected Vector3f getColorByAddress(DHDAbstractRendererState rendererState, CompoundTag compound, SymbolType<?> symbolType, SymbolInterface symbol) {
        if (rendererState == null) return new Vector3f(1f, 1f, 1f);
        if (compound != null && JSGConfig.DialHomeDevice.enablePageHint.get()) {

            // if item is notebook item
            if (compound.contains("pages"))
                compound = NotebookItem.getSelectedPageFromCompound(compound);
            if (compound == null)
                return new Vector3f(1f, 1f, 1f);
            var type = NotebookPageType.pageDataFromCompound(compound);
            if (type == null || type.data() == null)
                return new Vector3f(1f, 1f, 1f);
            if (!(type.data() instanceof StargateAddressData stargateAddressData))
                return new Vector3f(1f, 1f, 1f);
            var stargateAddress = stargateAddressData.getAddress();
            var st = stargateAddress.getSymbolType();

            int[] symbolsToDisplay = stargateAddressData.symbolsToDisplay;
            List<Integer> symbolsToDisplayList = Arrays.stream(symbolsToDisplay).boxed().toList();

            // check address type && button is not activated
            if (st == symbolType && !rendererState.isButtonActive(symbol) && !rendererState.isButtonActive(st.getOrigin())) {

                int activatedButtons = rendererState.getActivatedButtons();
                SymbolInterface displayedSymbol = st.getOrigin();
                if (symbolsToDisplayList.contains(activatedButtons + 1) && activatedButtons <= 7 && !rendererState.stargateIsConnected)
                    displayedSymbol = stargateAddress.get(activatedButtons);
                else if (!symbolsToDisplayList.contains(activatedButtons + 1)) {
                    for (int i = activatedButtons + 2; i <= 7; i++) {
                        if (symbolsToDisplayList.contains(i))
                            return new Vector3f(1f, 1f, 1f);
                    }
                }

                // set color
                if ((stargateAddress.contains(symbol) || symbol.origin()) && displayedSymbol == symbol) {
                    JSGConfigValue.RGBAValue color;
                    if (symbol.origin()) color = JSGConfig.DialHomeDevice.pageHintColorOrigin;
                    else if (activatedButtons < 6) color = JSGConfig.DialHomeDevice.pageHintColorNormal;
                    else color = JSGConfig.DialHomeDevice.pageHintColorExtra;
                    return new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
                }
            }
        }
        return new Vector3f(1f, 1f, 1f);
    }

    public CompoundTag getNoteBookPage() {
        Player p = Minecraft.getInstance().player;
        if (p == null) return null;
        CompoundTag compound = null;
        ItemStack item = p.getItemInHand(InteractionHand.MAIN_HAND);
        if (item.hasTag())
            compound = item.getTag();
        else {
            item = p.getItemInHand(InteractionHand.OFF_HAND);
            if (item.hasTag())
                compound = item.getTag();
        }
        return compound;
    }
}