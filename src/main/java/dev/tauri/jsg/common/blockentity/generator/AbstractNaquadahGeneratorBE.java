package dev.tauri.jsg.common.blockentity.generator;

import dev.tauri.jsg.core.common.packet.TargetPoint;
import dev.tauri.jsg.core.common.blockentity.BEStateProvider;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockentity.ScheduledTaskExecutorInterface;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AbstractNaquadahGeneratorBE extends BlockEntity implements BEStateProvider, ITickable, ICapabilityProvider, ScheduledTaskExecutorInterface {
    public AbstractNaquadahGeneratorBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Nonnull
    public Level getLevelNotNull() {
        return Objects.requireNonNull(getLevel());
    }

    public long getTime() {
        return getLevelNotNull().getGameTime();
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, @NotNull CompoundTag customData) {

    }

    @Override
    public State getState(StateType stateType) {
        return null;
    }

    @Override
    public State createState(StateType stateType) {
        return null;
    }

    @Override
    public void setState(StateType stateType, State state) {

    }

    @Override
    public void sendState(StateType type, State state) {

    }

    @Override
    public TargetPoint getTargetPoint() {
        return null;
    }

    @Override
    public void tick(@NotNull Level level) {
        // Scheduled tasks
        ScheduledTask.iterate(scheduledTasks, getTime());
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    // ------------------------------------------------------------------------
    // Scheduled tasks

    protected List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(getTime());

        scheduledTasks.add(scheduledTask);
        setChanged();
    }

    public void removeTask(ScheduledTask scheduledTask) {
        scheduledTasks.remove(scheduledTask);
        setChanged();
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void saveAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
    }
}
