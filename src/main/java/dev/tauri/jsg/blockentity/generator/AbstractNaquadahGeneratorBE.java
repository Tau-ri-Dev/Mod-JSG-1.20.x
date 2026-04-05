package dev.tauri.jsg.blockentity.generator;

import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockentity.ScheduledTaskExecutorInterface;
import dev.tauri.jsg.core.common.blockentity.StateProviderInterface;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AbstractNaquadahGeneratorBE extends BlockEntity implements StateProviderInterface, ITickable, ICapabilityProvider, ScheduledTaskExecutorInterface {
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
    public PacketDistributor.TargetPoint getTargetPoint() {
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return super.getCapability(cap, side);
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
    public void load(CompoundTag compound) {
        super.load(compound);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
    }
}
