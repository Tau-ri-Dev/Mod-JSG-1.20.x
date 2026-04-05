package dev.tauri.jsg.particle;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.util.TriConsumer;

public class ParticleScene<T extends BlockEntity> {
    public long timeStarted = -1;
    public final TriConsumer<T, Float, Long> animator;
    public final int length; // ticks

    public ParticleScene(int length, TriConsumer<T, Float, Long> animator) {
        this.length = length;
        this.animator = animator;
    }
}
