package dev.tauri.jsg.client.renderer.blockentity.dialhomedevice;

import dev.tauri.jsg.core.common.util.ItemNBT;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.dialhomedevice.animation.DHDButtonsState;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import dev.tauri.jsg.core.client.renderer.IRaycasterButtonsRenderer;
import dev.tauri.jsg.core.client.renderer.LinkableRenderer;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.config.values.JSGConfigValue;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        rendererState = (S) te.getStateManager().getRendererStateClient();
        this.partialTicks = partialTicks;
        this.level = te.getLevel();
        if (rendererState == null || level == null) return;
        level.updateSkyBrightness();

        BlockState state = level.getBlockState(te.getBlockPos());
        if (state.getBlock() != getDHDBlock()) return;
        renderLink(te.getBlockPos(), te, poseStack, bufferSource);
        renderRaycasterButtons(te, poseStack, bufferSource);

        var dhdRotationAngle = level.getBlockState(tileEntity.getBlockPos()).getValue(JSGProperties.ROTATION_PROPERTY) * -22.5f;

        poseStack.pushPose();

        if (state.getValue(JSGProperties.SNOWY)) {
            BlockRenderer.renderBlock(level, te.getBlockPos(), Blocks.SNOW.defaultBlockState(), new BlockPos(0, 0, 0), poseStack, bufferSource, combinedLight, combinedOverlay);
        }

        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(dhdRotationAngle));

        renderDHD(poseStack, bufferSource, combinedLight, combinedOverlay);

        var assemblyRenderTitleRunnable = renderAssembly(poseStack, bufferSource, combinedLight, combinedOverlay, showAssemblyHelper());

        for (var symbol : tileEntity.getSymbolType().getValues()) {
            Optional.ofNullable(tileEntity.getStateManager().getButtonsState().get(symbol)).ifPresent(s -> s.update(partialTicks));
        }

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.2, 0.5);
        assemblyRenderTitleRunnable.ifPresent(Runnable::run);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(dhdRotationAngle));
        renderSymbols(poseStack, bufferSource, combinedLight, combinedOverlay, tileEntity.getStateManager().getButtonsState());
        poseStack.popPose();

        renderNaquadahTankTooltip(poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    protected abstract IDHDPartItem getUpgradesCover();

    public void renderNaquadahTankTooltip(PoseStack stack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!Optional.ofNullable(Minecraft.getInstance().player).map(LocalPlayer::isShiftKeyDown).orElse(false))
            return;
        if (tileEntity.isAssembled(getUpgradesCover()))
            return;

        var btn = getRaycaster().getRaycastedButton(level, tileEntity.getBlockPos(), Minecraft.getInstance().player, InteractionHand.MAIN_HAND);
        if (btn == null) return;
        if (btn.buttonId != tileEntity.getFluidTankItemPart().getRaycasterButtonID()) return;

        stack.pushPose();
        stack.translate(0.5, 0, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(Objects.requireNonNull(level).getBlockState(tileEntity.getBlockPos()).getValue(JSGProperties.ROTATION_PROPERTY) * -22.5f));
        stack.translate(0, 0.4, -0.4);
        //stack.mulPose(Axis.YN.rotationDegrees(Objects.requireNonNull(level).getBlockState(tileEntity.getBlockPos()).getValue(JSGProperties.ROTATION_PROPERTY) * -22.5f));

        var scale = 0.1f;
        stack.scale(-0.025f * scale, -0.025f * scale, 0.025f * scale);
        Matrix4f matrix4f = stack.last().pose();
        float backgroundOpacityConfig = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int backgroundOpacity = (int) (backgroundOpacityConfig * 255.0f) << 24;
        Font font = Minecraft.getInstance().font;
        AtomicInteger i = new AtomicInteger(0);
        Util.make(new ArrayList<Component>(), list -> {
            list.add(Component.translatable("gui.dhd.reactorStatus", I18n.format("gui.dhd.reactor." + rendererState.reactorState.name().toLowerCase())));
            if (!tileEntity.isAssembled(tileEntity.getFluidTankItemPart()))
                return;
            list.add(Component.literal(rendererState.naquadahAmount + "mB/" + rendererState.naquadahMaxAmount + "mB"));
        }).forEach(component -> {
            var xOffset = font.width(component) / 2;
            font.drawInBatch(component, -xOffset, i.get() * 10, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, backgroundOpacity, LightTexture.FULL_BRIGHT);
            //font.drawInBatch(component, 0, i.get() * 10, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            i.getAndIncrement();
        });
        stack.popPose();
    }

    public abstract void renderSymbols(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, DHDButtonsState buttonsState);

    public abstract void renderDHD(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay);

    public abstract Block getDHDBlock();

    // TODO: Optimize this probably??
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
            var buttonsState = tileEntity.getStateManager().getButtonsState();
            if (st == symbolType && !buttonsState.get(symbol).isActive() && !buttonsState.get(st.getOrigin()).isActive()) {

                var activatedButtons = buttonsState.getActivatedButtons();
                SymbolInterface displayedSymbol = st.getOrigin();
                if (symbolsToDisplayList.contains(activatedButtons.size() + 1) && activatedButtons.size() <= 7 && !activatedButtons.contains(symbolType.getBRB()))
                    displayedSymbol = stargateAddress.get(activatedButtons.size());
                else if (!symbolsToDisplayList.contains(activatedButtons.size() + 1)) {
                    for (int i = activatedButtons.size() + 2; i <= 7; i++) {
                        if (symbolsToDisplayList.contains(i))
                            return new Vector3f(1f, 1f, 1f);
                    }
                }

                // set color
                if ((stargateAddress.contains(symbol) || symbol.origin()) && displayedSymbol == symbol) {
                    JSGConfigValue.RGBAValue color;
                    if (symbol.origin()) color = JSGConfig.DialHomeDevice.pageHintColorOrigin;
                    else if (activatedButtons.size() < 6) color = JSGConfig.DialHomeDevice.pageHintColorNormal;
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
        if (ItemNBT.hasTag(item))
            compound = ItemNBT.getTag(item);
        else {
            item = p.getItemInHand(InteractionHand.OFF_HAND);
            if (ItemNBT.hasTag(item))
                compound = ItemNBT.getTag(item);
        }
        return compound;
    }

    @OnlyIn(Dist.CLIENT)
    @ParametersAreNonnullByDefault
    public void renderTitle(PoseStack stack, MultiBufferSource bufferSource, String title) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        stack.pushPose();
        stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        stack.translate(0, 0, -0.1);
        var scale = 0.2f;
        stack.scale(-0.025f * scale, -0.025f * scale, 0.025f * scale);
        Matrix4f matrix4f = stack.last().pose();
        float backgroundOpacityConfig = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int backgroundOpacity = (int) (backgroundOpacityConfig * 255.0f) << 24;
        Font font = Minecraft.getInstance().font;
        float x = (float) (-font.width(title) / 2);
        font.drawInBatch(title, x, 0, 553648127, false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundOpacity, LightTexture.FULL_BRIGHT);
        font.drawInBatch(title, x, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }

    @NotNull
    public abstract PartRenderable getPartModelRenderable(IDHDPartItem part);

    public abstract Item getNeededSchematic();

    public boolean hasPartInHand(IDHDPartItem part) {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.getItemInHand(InteractionHand.MAIN_HAND).is(part.self()) || player.getItemInHand(InteractionHand.OFF_HAND).is(part.self());
    }

    public boolean showAssemblyHelper() {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;
        var item = getNeededSchematic();
        return player.getItemInHand(InteractionHand.MAIN_HAND).is(item) || player.getItemInHand(InteractionHand.OFF_HAND).is(item);
    }

    public Optional<Runnable> renderAssembly(PoseStack stack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, boolean hasSchematic) {
        rendererState.assembledParts.forEach(part -> {
            var renderable = getPartModelRenderable(part);
            stack.pushPose();
            renderable.render(stack, bufferSource, combinedLight, combinedOverlay, partialTicks, 1, 1, 1, 1, true);
            stack.popPose();
        });
        if (!hasSchematic) return Optional.empty();
        var nextPartOpt = tileEntity.getNextPartToAssemble(rendererState::isAssembled);
        if (nextPartOpt.isEmpty()) return Optional.empty();
        var nextPart = nextPartOpt.get();
        var renderable = getPartModelRenderable(nextPart);
        var hasPart = hasPartInHand(nextPart);
        var item = nextPart.self();
        var r = hasPart ? 1f : 0.1f;
        var g = hasPart ? 1f : 0.1f;
        var b = hasPart ? 1f : 0.1f;
        var a = (float) (Math.sin((JSGMinecraftHelper.getGUITicks() + partialTicks) / 4f) / 2f + 0.5f) / 1.2f;

        stack.pushPose();
        renderable.render(stack, bufferSource, combinedLight, combinedOverlay, partialTicks, r, g, b, a, false);
        stack.popPose();

        return Optional.of(() -> {
            stack.pushPose();
            stack.translate(0, 1.5, 0);
            renderTitle(stack, bufferSource, I18n.format("item.jsg.dhd.assembly_helper.needed", item.getDescription().getString()));
            //stack.scale(0.7f, 0.7f, 0.7f);
            stack.translate(0, 0.3, 0);
            stack.mulPose(Axis.YP.rotationDegrees(JSGMinecraftHelper.getGUITicks() + partialTicks));
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(item), ItemDisplayContext.GROUND, combinedLight, combinedOverlay, stack, bufferSource, level, 0);
            stack.popPose();
        });
    }

    public interface PartRenderable {
        void render(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTick, float r, float g, float b, float alpha, boolean assembled);
    }
}