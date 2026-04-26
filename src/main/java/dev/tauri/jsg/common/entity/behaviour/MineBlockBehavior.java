package dev.tauri.jsg.common.entity.behaviour;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

public class MineBlockBehavior extends Behavior<Villager> {
    private static final int MINE_DURATION = 200;
    public static final float SPEED_MODIFIER = 0.5F;
    @Nullable
    private BlockPos orePos;
    private long nextOkStartTime;
    private int timeWorkedSoFar;
    private final List<BlockPos> validOreAround = Lists.newArrayList();

    protected final Predicate<BlockState> isValid;

    public MineBlockBehavior(Predicate<BlockState> isValid) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT));
        this.isValid = isValid;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(pLevel, pOwner)) {
            return false;
        }
        BlockPos.MutableBlockPos pos = pOwner.blockPosition().mutable();
        this.validOreAround.clear();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    pos.set(pOwner.getX() + (double) i, pOwner.getY() + (double) j, pOwner.getZ() + (double) k);
                    if (this.validPos(pos, pLevel)) {
                        this.validOreAround.add(new BlockPos(pos));
                    }
                }
            }
        }

        this.orePos = this.getValidBlock(pLevel);
        return this.orePos != null;

    }

    @Nullable
    private BlockPos getValidBlock(ServerLevel pServerLevel) {
        return this.validOreAround.isEmpty() ? null : this.validOreAround.get(pServerLevel.getRandom().nextInt(this.validOreAround.size()));
    }

    @ParametersAreNonnullByDefault
    private boolean validPos(BlockPos pPos, ServerLevel pServerLevel) {
        return isValid.test(pServerLevel.getBlockState(pPos));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        if (pGameTime > this.nextOkStartTime && this.orePos != null) {
            pEntity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.orePos));
            pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.orePos), 0.5F, 1));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        pEntity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.timeWorkedSoFar = 0;
        this.nextOkStartTime = pGameTime + 40L;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        if (this.orePos == null || this.orePos.closerToCenterThan(pOwner.position(), 1.0D)) {
            if (this.orePos != null && pGameTime > this.nextOkStartTime) {
                BlockState blockstate = pLevel.getBlockState(this.orePos);
                if (isValid.test(blockstate)) {
                    pLevel.destroyBlock(this.orePos, true, pOwner);
                } else {
                    this.validOreAround.remove(this.orePos);
                    this.orePos = this.getValidBlock(pLevel);
                    if (this.orePos != null) {
                        this.nextOkStartTime = pGameTime + 20L;
                        pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.orePos), 0.5F, 1));
                        pOwner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.orePos));
                    }
                }
            }

            ++this.timeWorkedSoFar;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        return this.timeWorkedSoFar < 200;
    }
}
