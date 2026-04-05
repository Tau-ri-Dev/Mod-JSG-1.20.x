package dev.tauri.jsg.worldgen.generator;

import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public enum StargateGeneratorDimStatus implements StargateGeneratorStepStatus {
    GENERATED(0xFF7CBC80, () -> Component.translatable("createWorld.stargates_generating.dim_status.ok")),
    ALREADY_GENERATED(0xFF7B7FBA, () -> Component.translatable("createWorld.stargates_generating.dim_status.already_there")),
    NO_STRUCTURE(0xFFCF97EB, () -> Component.translatable("createWorld.stargates_generating.dim_status.no_structure")),
    SKIPPED(0xFFFFE0A0, () -> Component.translatable("createWorld.stargates_generating.dim_status.skipped")),
    ERROR(0xFFF08484, () -> Component.translatable("createWorld.stargates_generating.dim_status.error"));

    public final int color;
    public final Supplier<Component> title;

    StargateGeneratorDimStatus(int color, Supplier<Component> title) {
        this.color = color;
        this.title = title;
    }

    @Override
    public Component getMessage() {
        return title.get();
    }

    @Override
    public int getColor() {
        return color;
    }
}
