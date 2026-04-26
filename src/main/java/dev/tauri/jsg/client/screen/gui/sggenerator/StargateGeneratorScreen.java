package dev.tauri.jsg.client.screen.gui.sggenerator;

import dev.tauri.jsg.common.worldgen.generator.StargateGeneratorStepStatus;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static net.minecraft.client.GameNarrator.NO_TITLE;

@OnlyIn(Dist.CLIENT)
public class StargateGeneratorScreen extends AbstractStargateGeneratorScreen {
    public StargateGeneratorScreen(Supplier<Integer> total, Supplier<ConcurrentHashMap<String, StargateGeneratorStepStatus>> stats, Supplier<Component> message) {
        super(NO_TITLE, () -> "createWorld.stargates_generating", total, stats, message);
    }
}
