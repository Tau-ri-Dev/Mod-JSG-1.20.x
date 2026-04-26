package dev.tauri.jsg.common.stargate.rig;

import net.minecraft.util.RandomSource;

public class MobsCount {
    public int min;
    public int max;

    public MobsCount(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int get(RandomSource random) {
        if (min > max) {
            var a = min;
            min = max;
            max = a;
        }
        if (min == max) return max;
        return min + random.nextInt((max - min) + 1);
    }
}
