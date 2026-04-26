package dev.tauri.jsg.client.renderer.item.dialer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

public interface IUniverseDialerScreen {
    @ParametersAreNonnullByDefault
    void render(ItemStack itemStack, CompoundTag compound, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay);


    static StargateAddressDynamic addrFromBytes(CompoundTag compound, String baseName) {
        if (compound == null || baseName == null) return null;
        SymbolType<?> symbolType = SymbolType.byId(JSGMapping.rl(compound.getString(baseName + "_symbolType")));
        if (symbolType == null) return null;
        StargateAddressDynamic newAddress = new StargateAddressDynamic(symbolType);
        int addressLength = compound.getByte(baseName + "_addressLength");
        for (int i = 0; i < addressLength; i++) {
            int symbolId = compound.getByte(baseName + "_" + i);
            newAddress.addSymbol(symbolType.valueOf(symbolId));
        }
        return newAddress;
    }

    static void drawStringWithShadow(PoseStack stack, MultiBufferSource source, float x, float y, String text, boolean active, boolean invertActiveColor) {
        drawStringWithShadow(stack, source, x, y, text, active, invertActiveColor, false, false, EnumStargateState.IDLE);
    }

    static void drawStringWithShadow(PoseStack stack, MultiBufferSource source, float x, float y, String text, boolean isActive, boolean invertActiveColor, boolean isAddress, boolean dialing, EnumStargateState stargateState) {
        boolean isEngaged = stargateState.engaged() || stargateState.initiating();
        boolean isEngagedInitiating = stargateState.initiating();
        boolean isIncoming = stargateState.incoming();
        boolean isFailing = stargateState.failing();

        int color;

        float red = 1f;
        float green = 1f;
        float blue = 1f;
        float alpha = 1f;

        if (!isActive || isIncoming || (!isEngagedInitiating && isEngaged))
            alpha = 0.3f;

        if (dialing) {
            red = 0.5f;
            green = 0.7f;
            blue = 1f;
        }
        if (isEngaged && dialing) {
            red = 0.0f;
            green = 1f;
            blue = 0.5f;
        }
        if (isFailing && dialing) {
            red = 1f;
            green = 0.0f;
            blue = 0.3f;
        }
        if (isIncoming || (!isEngagedInitiating && isEngaged)) {
            red = 1f;
            green = 0.7f;
            blue = 0.0f;
        }


        if (!isAddress)
            color = isActive ? (invertActiveColor ? 0 : 0xFFFFFF) : 0x0060FF;
        else
            color = new Color(red, green, blue, alpha).getRGB();
        HandHeldDeviceRenderer.drawStringWithShadow(stack, source, x, y, text, color, isActive);
    }

    static void renderSymbol(PoseStack stack, MultiBufferSource source, int light, int row, int col, SymbolInterface symbol, boolean dialing, boolean isActive, boolean engage, EnumStargateState stargateState) {
        RenderSystem.enableDepthTest();
        boolean isEngaged = stargateState.engaged() || stargateState.initiating();
        boolean isEngagedInitiating = stargateState.initiating();
        boolean isIncoming = stargateState.incoming();
        boolean isFailing = stargateState.failing();

        float x = col * 0.07f - 0.05f;
        float y = -row * 0.2f - 0.16f;
        float scale = 0.5f;
        float w = 0.19f * scale;
        float h = 0.40f * scale;

        symbol.bindIconTexture(null);
        RenderSystem.enableBlend();

        float red = 1f;
        float green = 1f;
        float blue = 1f;
        float alpha = 1f;

        if (!isActive || isIncoming || (!isEngagedInitiating && isEngaged))
            alpha = 0.3f;

        if (dialing && !isEngaged && !engage) {
            red = 0.5f;
            green = 0.7f;
        }
        if (isEngaged && engage) {
            red = 0.0f;
            blue = 0.5f;
        }
        if (isFailing && engage) {
            red = 1f;
            green = 0.0f;
            blue = 0.3f;
        }
        if (isIncoming || (!isEngagedInitiating && isEngaged)) {
            red = 1f;
            green = 0.7f;
            blue = 0.0f;
        }


        RenderSystem.setShaderColor(red, green, blue, alpha);

        HandHeldDeviceRenderer.drawTexturedRect(stack, source, light, x, y, 0, w, h);
        float shadow = 0.008f;

        RenderSystem.setShaderColor(0, 0, 0, 0.15f);
        HandHeldDeviceRenderer.drawTexturedRect(stack, source, light, x + shadow, y - shadow, -0.01f, w, h);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
