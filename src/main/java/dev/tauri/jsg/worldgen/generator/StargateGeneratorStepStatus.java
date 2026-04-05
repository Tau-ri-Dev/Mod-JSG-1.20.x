package dev.tauri.jsg.worldgen.generator;

import net.minecraft.network.chat.Component;

public interface StargateGeneratorStepStatus {
    Component getMessage();

    int getColor();
}
