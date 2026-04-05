package dev.tauri.jsg.screen.provider;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.screen.gui.AbydosWarningGui;
import dev.tauri.jsg.screen.gui.sggenerator.StargateGeneratorScreen;
import dev.tauri.jsg.worldgen.generator.StargateGeneratorStepStatus;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SGGeneratorGuiProvider {
    public static void showProgress(Supplier<Integer> total, Supplier<ConcurrentHashMap<String, StargateGeneratorStepStatus>> stats, Supplier<Component> message) {
        RenderSystem.recordRenderCall(() -> Minecraft.getInstance().forceSetScreen(new StargateGeneratorScreen(total, stats, message)));
    }

    public static void showAbydosWarning(BooleanConsumer callback) {
        RenderSystem.recordRenderCall(() -> Minecraft.getInstance().forceSetScreen(new AbydosWarningGui(callback)));
    }
}
