package dev.tauri.jsg.stargate.orlin;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.blockentity.stargate.StargateOrlinBaseBE;
import dev.tauri.jsg.particle.ParticleScene;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public class StargateOrlinOpeningParticleScenes {
    public static final Function<ChevronEnum, ParticleScene<StargateOrlinBaseBE>> CHEVRON = (chevron) -> new ParticleScene<>(10, (be, coef, tick) -> {
        var pos = switch (chevron) {
            case C1 -> new Vec3(1.5, 2.5, 0.6);
            case C2 -> new Vec3(1.75, 1.3, 0.6);
            case C3 -> new Vec3(1.2, 0.5, 0.6);
            case C4 -> new Vec3(-0.2, 0.5, 0.6);
            case C5 -> new Vec3(-0.75, 1.3, 0.6);
            case C6 -> new Vec3(-0.5, 2.5, 0.6);
            default -> new Vec3(0.5, 3, 0.6);
        };
        pos = be.relative(pos, new Vec3(0.5, 0.5, 0.5));
        var velocity = new Vec3(0, 0, 0.08);
        velocity = be.rotated(velocity);
        var level = be.getLevel();
        if (level == null) return;
        for (var i = 0; i < 3; i++)
            level.addParticle(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    });

    public static final Supplier<ParticleScene<StargateOrlinBaseBE>> SMOKE = () -> new ParticleScene<>(0, (be, coef, tick) -> {
        if (tick % 5 != 0) return;
        for (var chevron : ChevronEnum.values()) {
            if (chevron == ChevronEnum.C8 || chevron == ChevronEnum.C7) continue;
            var pos = switch (chevron) {
                case C1 -> new Vec3(1.5, 2.5, 0.7);
                case C2 -> new Vec3(1.75, 1.3, 0.7);
                case C3 -> new Vec3(1.2, 0.5, 0.7);
                case C4 -> new Vec3(-0.2, 0.5, 0.7);
                case C5 -> new Vec3(-0.75, 1.3, 0.7);
                case C6 -> new Vec3(-0.5, 2.5, 0.7);
                default -> new Vec3(0.5, 3, 0.7);
            };
            pos = be.relative(pos, new Vec3(0.5, 0.5, 0.5));
            var velocity = new Vec3(0, -0.0, 0.06);
            velocity = be.rotated(velocity);
            var level = be.getLevel();
            if (level == null) return;
            if (level.random.nextFloat() < 0.2) return;
            level.addParticle(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        }
    });
}
