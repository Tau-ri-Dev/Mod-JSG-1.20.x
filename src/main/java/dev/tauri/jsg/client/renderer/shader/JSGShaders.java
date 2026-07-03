package dev.tauri.jsg.client.renderer.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = JSGCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JSGShaders {
    private static EventHorizonShaderInstance eventHorizonShader;
    private static KawooshShaderInstance kawooshShader;

    private static long eventHorizonStartNanos = 0L;

    public static EventHorizonShaderInstance getEventHorizonShaderInstance() {
        return eventHorizonShader;
    }

    public static KawooshShaderInstance getKawooshShader() {
        return kawooshShader;
    }

    @SubscribeEvent
    public static void shaderRegister(RegisterShadersEvent event) {
        try {
            JSG.logger.info("Loading custom shaders.");
            event.registerShader(new EventHorizonShaderInstance(event.getResourceProvider(), JSGMapping.rl(JSG.MOD_ID, "event_horizon"), DefaultVertexFormat.POSITION_TEX_COLOR), shaderInstance -> eventHorizonShader = (EventHorizonShaderInstance) shaderInstance);
            event.registerShader(new KawooshShaderInstance(event.getResourceProvider(), JSGMapping.rl(JSG.MOD_ID, "kawoosh"), DefaultVertexFormat.POSITION_COLOR), shaderInstance -> kawooshShader = (KawooshShaderInstance) shaderInstance);
            JSG.logger.info("Shaders loaded.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Event-horizon shader with its {@code Time} uniform refreshed from a monotonic wall clock, so the
     * puddle animates smoothly regardless of tick rate. Pass as the custom shader to EmissiveRenderer.
     */
    public static Supplier<ShaderInstance> getEventHorizonShader() {
        return () -> {
            EventHorizonShaderInstance shader = eventHorizonShader;
            if (eventHorizonStartNanos == 0L) eventHorizonStartNanos = System.nanoTime();
            shader.setTime((System.nanoTime() - eventHorizonStartNanos) / 1.0e9f);
            return shader;
        };
    }
}
