package dev.tauri.jsg.common.particle;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ParticleAnimationHandler<T extends BlockEntity> {
    private final ArrayList<ParticleScene<T>> scenes = new ArrayList<>();

    @SuppressWarnings("all")
    public void tick(@NotNull T source) {
        if (source.getLevel() == null) return;
        var tick = source.getLevel().getGameTime();
        var copy = new ArrayList<>(scenes);
        var toDelete = new ArrayList<Integer>();
        for (int i = 0; i < copy.size(); i++) {
            var scene = copy.get(i);
            if (scene.timeStarted < 0) scene.timeStarted = tick;
            var coef = 0f;
            if (scene.length > 0) {
                coef = (float) (((double) (tick - scene.timeStarted)) / ((double) scene.length));
                if (coef > 1f) {
                    toDelete.add(i);
                    continue;
                }
            }
            scene.animator.accept(source, coef, tick);
        }
        toDelete.forEach(i -> scenes.remove(i));
    }

    public void clear() {
        scenes.clear();
    }

    public void play(@NotNull ParticleScene<T> scene) {
        scenes.add(scene);
    }
}
