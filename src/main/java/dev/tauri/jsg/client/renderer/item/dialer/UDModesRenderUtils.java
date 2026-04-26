package dev.tauri.jsg.client.renderer.item.dialer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

import static dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer.drawTexturedRect;

public class UDModesRenderUtils {

    public static void drawWaringGlyph(PoseStack stack, MultiBufferSource source, int light, Color color) {
        stack.pushPose();
        stack.translate(-1.2, -0.75, 0);
        stack.scale(1, -1, 1);
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(180));
        RenderSystem.enableDepthTest();

        ITexture.bindTextureWithMc(JSGMapping.rl(JSG.MOD_ID, "textures/gui/universe_warning.png"));
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        drawTexturedRect(stack, source, light, 0, 0, 0, 0.24f, 0.24f);
        RenderSystem.setShaderColor(1f, 1, 1f, 1f);
        stack.popPose();
        stack.popPose();
    }

    @ParametersAreNonnullByDefault
    public static void drawAddress(int yOffset, boolean selected, PoseStack stack, MultiBufferSource source, int light, @Nullable String name, EnumStargateState gateStatus, StargateAddressDynamic address, @Nullable StargateAddressDynamic toDialAddress, @Nullable StargateAddressDynamic dialedAddress) {
        var symbolsToDisplay = new int[address.size()];
        for (var i = 0; i < address.size(); i++) {
            symbolsToDisplay[i] = i + 1;
        }
        drawAddress(yOffset, selected, stack, source, light, name, gateStatus, symbolsToDisplay, address, toDialAddress, dialedAddress);
    }

    public static void drawAddress(int yOffset, boolean selected, PoseStack stack, MultiBufferSource source, int light, @Nullable String name, EnumStargateState gateStatus, int[] symbolsToDisplay, StargateAddress address, @Nullable StargateAddressDynamic toDialAddress, @Nullable StargateAddressDynamic dialedAddress) {
        boolean isIdle = gateStatus.idle();

        int dialed = -1;
        boolean isDialingThisAddr = false;
        if (toDialAddress != null && toDialAddress.equals(address)) {
            dialed = 0;
            isDialingThisAddr = true;
        }

        if (dialedAddress != null && isDialingThisAddr)
            dialed = dialedAddress.getSize();

        if (dialed == 0 && isIdle)
            dialed = -1;

        boolean engage_poo = (isDialingThisAddr && dialedAddress != null && dialedAddress.contains(dialedAddress.getSymbolType().getOrigin()));

        if (name != null) {
            String entryName = name;
            if (dialed > -1)
                entryName += " (" + dialed + ")";
            IUniverseDialerScreen.drawStringWithShadow(stack, source, -0.25f, -0.5f - 0.2f * yOffset, entryName, selected, false, true, dialed >= 0, gateStatus);
        } else {
            var symbolPos = 0;
            for (var i : symbolsToDisplay) {
                boolean engage_s = (i <= dialed);
                var symbol = (i > address.getSize() ? address.getSymbolType().getOrigin() : address.get(i - 1));
                if (symbol.origin()) engage_s = engage_poo;
                renderSymbol(stack, source, light, yOffset, symbolPos, symbol, isDialingThisAddr, selected, engage_s, gateStatus);
                symbolPos++;
                if (symbol.origin()) break;
            }
        }
    }

    public static void renderSymbol(PoseStack stack, MultiBufferSource source, int light, int yOffset, int symbolPos, SymbolInterface symbol, boolean dialing, boolean isActive, boolean engage, EnumStargateState stargateState) {
        stack.pushPose();
        stack.translate(-0.2, -0.75, 0);
        stack.scale(1, -1, 1);
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(180));
        IUniverseDialerScreen.renderSymbol(stack, source, light, yOffset, symbolPos, symbol, dialing, isActive, engage, stargateState);
        stack.popPose();
        stack.popPose();
    }
}
