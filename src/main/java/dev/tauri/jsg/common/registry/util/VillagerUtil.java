package dev.tauri.jsg.common.registry.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.tauri.jsg.common.entity.behaviour.MineBlockBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class VillagerUtil {
    private static Pair<Integer, BehaviorControl<LivingEntity>> getMinimalLookBehavior() {
        return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(SetEntityLookTarget.create(EntityType.VILLAGER, 8.0F), 2), Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 2), Pair.of(new DoNothing(30, 60), 8))));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getWorkPackageForSlave(Predicate<BlockState> isValidOre, float pSpeedModifier) {
        WorkAtPoi workatpoi = new WorkAtPoi();
        return ImmutableList.of(getMinimalLookBehavior(),
                Pair.of(5, new RunOne<>(ImmutableList.of(
                        Pair.of(workatpoi, 7),
                        Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
                        Pair.of(StrollToPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
                        Pair.of(StrollToPoiList.create(MemoryModuleType.SECONDARY_JOB_SITE, pSpeedModifier, 1, 6, MemoryModuleType.JOB_SITE), 5),
                        Pair.of(new MineBlockBehavior(isValidOre), 2)
                ))),
                Pair.of(10, new ShowTradesToPlayer(400, 1600)),
                Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)),
                Pair.of(2, SetWalkTargetFromBlockMemory.create(MemoryModuleType.JOB_SITE, pSpeedModifier, 9, 100, 1200)),
                Pair.of(3, new GiveGiftToHero(100)),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }
}
