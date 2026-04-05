package dev.tauri.jsg.item.linkable.dialer.modes.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.item.linkable.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.item.linkable.dialer.modes.UDManualDialMode;
import dev.tauri.jsg.item.linkable.dialer.modes.UDNearbyMode;
import dev.tauri.jsg.item.linkable.dialer.utils.UDModesRenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

public class UDManualDialScreen implements IUniverseDialerScreen {
    @Override
    @ParametersAreNonnullByDefault
    public void render(ItemStack itemStack, CompoundTag compound, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        int symbolOffset = compound.getInt(UDManualDialMode.C_SELECTED_GLYPH);
        //var r = (235f / 255f) * 0.8f;
        //var g = (104f / 255f) * 0.8f;
        //var b = (24f / 255f) * 0.8f;

        // common
        // big circle
        //HandHeldDeviceRenderer.drawSemiCircle(0.65f + 0.39f, -0.8f, 0.0001f, (float) Math.PI / 20f, 3f * (float) Math.PI / 2f, 36, 0.9f, 0.04f, r, g, b, 1f);
        // small circle
        //HandHeldDeviceRenderer.drawSemiCircle(0.65f + 0.5f, -0.8f, 0.0001f, (float) Math.PI / 2f, 3f * (float) Math.PI / 2f, 36, 0.65f, 0.04f, r, g, b, 1f);

        stack.pushPose();
        stack.translate(-0.06, -0.85, 0);
        stack.scale(1, -1, 1);

        //stack.pushPose();
        //HandHeldDeviceRenderer.drawColorRect(0.65f - 0.5f - 0.48f + 0.08f, 0.02f - 0.435f, 0, 0.48f, 0.04f, r, g, b, 1f);
        //HandHeldDeviceRenderer.drawSemiCircle(0.65f, 0.02f, 0.0001f, -(float) (Math.PI / 6.4f), (float) (Math.PI / 7f), 36, -1f, 0.04f, r, g, b, 1f);
        //HandHeldDeviceRenderer.drawColorRect(0.65f - 0.38f - 0.58f + 0.08f, 0.885f - 0.435f, 0, 0.58f, 0.04f, r, g, b, 1f);
        //..HandHeldDeviceRenderer.drawColorRect(0.65f - 1f - 2f + 0.04f, 0.02f - 0.3f, 0, 2f, 0.1f, 0, 0, 1f, 1f);
        //..HandHeldDeviceRenderer.drawColorRect(0.65f - 1f - 2f + 0.04f, 0.02f - 0.2f, 0, 2f, 0.4f, 1f, 0, 0, 1f);
        //stack.popPose();

        stack.pushPose();
        stack.scale(1.5f, 1.5f, 1.5f);
        stack.mulPose(Axis.ZP.rotationDegrees(-90));
        SymbolInterface selectedSymbol = null;
        for (var i = -2; i <= 2; ++i) {
            stack.pushPose();
            stack.translate(0, 0.5, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(i * 12));
            stack.translate(0, -0.5, 0);
            var symbolId = (symbolOffset + i) % SymbolUniverseEnum.values().length;
            while (symbolId < 0) {
                symbolId += SymbolUniverseEnum.values().length;
            }

            if (symbolId == 0) {
                stack.pushPose();
                stack.scale(1, -1, 1);
                stack.scale(0.45f, 0.45f, 0.45f);
                stack.translate(0.35f, 0f, 0);
                stack.mulPose(Axis.ZP.rotationDegrees(-90));
                HandHeldDeviceRenderer.drawStringWithShadow(stack, bufferSource, 0, 0, I18n.format("item.jsg.universe_dialer.manual_dialing.delete"), i == 0 ? new Color(1f, 0, 0.3f, 1f).getRGB() : 0x0060FF, true);
                stack.popPose();
                stack.popPose();
                continue;
            }

            var symbol = JSGSymbolTypes.UNIVERSE.get().valueOf(symbolId);
            IUniverseDialerScreen.renderSymbol(stack, bufferSource, light, 0, 0, symbol, false, i == 0, false, EnumStargateState.IDLE);
            stack.popPose();
            if (i == 0) {
                selectedSymbol = symbol;
            }
        }
        stack.popPose();
        stack.popPose();

        if (selectedSymbol != null) {
            stack.pushPose();
            stack.scale(0.7f, 0.7f, 0.7f);
            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, -0.7f, -0.4f, selectedSymbol.localize(), true, false);
            stack.popPose();
        }

        // render address
        if (compound.contains(UDManualDialMode.C_EDIT_ADDRESS)) {
            stack.pushPose();
            stack.scale(1.5f, 1.5f, 1.5f);
            stack.translate(-0.1, 0.15f, 0);
            var address = new StargateAddressDynamic(JSGSymbolTypes.UNIVERSE.get());
            address.deserializeNBT(compound.getCompound(UDManualDialMode.C_EDIT_ADDRESS));
            var gateStatus = EnumStargateState.valueOf(compound.getInt(UDNearbyMode.C_STATUS));
            var dialedAddress = IUniverseDialerScreen.addrFromBytes(compound, UDNearbyMode.C_DIALED);
            var toDialAddress = IUniverseDialerScreen.addrFromBytes(compound, UDNearbyMode.C_TO_DIAL);
            UDModesRenderUtils.drawAddress(0, true, stack, bufferSource, light, null, gateStatus, address, toDialAddress, dialedAddress);
            stack.popPose();
        }
    }
}
