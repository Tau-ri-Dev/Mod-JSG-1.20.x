package dev.tauri.jsg.api.util;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.registry.ScheduledTaskType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Optional;

public class ScheduledTask implements INBTSerializable<CompoundTag> {
    private ScheduledTaskExecutorInterface executor;
    private long taskCreated;
    private ScheduledTaskType scheduledTask;

    private boolean active;
    private boolean customWaitTime;
    private int waitTime;
    private CompoundTag customData = null;

    public ScheduledTask(ScheduledTaskType scheduledTask) {
        this.scheduledTask = scheduledTask;
        this.active = true;

        this.customWaitTime = false;
    }

    public ScheduledTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        this(scheduledTask);
        this.customData = customData;
    }

    public ScheduledTask(ScheduledTaskType scheduledTask, int waitTime) {
        this(scheduledTask);

        this.customWaitTime = true;
        this.waitTime = waitTime;
    }

    public ScheduledTask(ScheduledTaskType scheduledTask, int waitTime, CompoundTag customData) {
        this(scheduledTask, waitTime);
        this.customData = customData;
    }

    public ScheduledTask(CompoundTag compound) {
        deserializeNBT(compound);
    }

    public ScheduledTask setExecutor(ScheduledTaskExecutorInterface executor) {
        this.executor = executor;

        return this;
    }

    public void setTaskCreated(long taskCreated) {
        this.taskCreated = taskCreated;
    }

    public ScheduledTask inactive() {
        this.active = false;

        return this;
    }

    public ScheduledTask active() {
        this.active = true;

        return this;
    }

    public boolean isActive() {
        return active;
    }

    public boolean update(long worldTicks) {
        if (scheduledTask == null) {
            active = false;
            return false;
        }
        int waitTime = customWaitTime ? this.waitTime : scheduledTask.waitTicks;
        long effTick = worldTicks - taskCreated;
        boolean call = effTick == waitTime;

        if (scheduledTask.overtime)
            call = effTick >= waitTime;

        if (call) {
            execute();
            return true;
        }

        return false;
    }

    public void execute() {
        try {
            executor.executeTask(scheduledTask, Optional.ofNullable(customData).orElseGet(CompoundTag::new));
        } catch (UnsupportedOperationException e) {
            JSGApi.logger.error("UnsupportedOperationException", e);
        }
    }

    public int getWaitTime() {
        if (this.customWaitTime)
            return this.waitTime;
        return scheduledTask.waitTicks;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putLong("taskCreated", taskCreated);
        compound.putString("scheduledTask", scheduledTask.id.toString());
        compound.putBoolean("active", active);

        compound.putBoolean("customWaitTime", customWaitTime);
        compound.putInt("waitTime", waitTime);

        if (customData != null)
            compound.put("customData", customData);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        taskCreated = compound.getLong("taskCreated");
        scheduledTask = ScheduledTaskType.valueOf(new ResourceLocation(compound.getString("scheduledTask")));
        active = compound.getBoolean("active");
        if (scheduledTask == null) active = false;

        customWaitTime = compound.getBoolean("customWaitTime");
        waitTime = compound.getInt("waitTime");

        if (compound.contains("customData"))
            customData = compound.getCompound("customData");
    }

    @Override
    public String toString() {
        return scheduledTask.toString() + (customWaitTime ? ", custom time=" + waitTime : "");
    }

    // Eclipse generated methods
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((scheduledTask == null) ? 0 : scheduledTask.hashCode());
        result = prime * result + Long.hashCode(taskCreated);
        result = prime * result + ((customData == null) ? 0 : customData.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScheduledTask other = (ScheduledTask) obj;
        if (scheduledTask != other.scheduledTask)
            return false;
        if (taskCreated != other.taskCreated)
            return false;
        return customData == other.customData;
    }

    public static void iterate(List<ScheduledTask> scheduledTasks, long worldTicks) {
        for (int i = 0; i < scheduledTasks.size(); ) {
            ScheduledTask scheduledTask = scheduledTasks.get(i);

            if (scheduledTask.isActive()) {
                if (scheduledTask.update(worldTicks))
                    scheduledTasks.remove(scheduledTask);

                else i++;
            } else i++;
        }
    }

    public static CompoundTag serializeList(List<ScheduledTask> scheduledTasks) {
        CompoundTag compound = new CompoundTag();

        compound.putInt("size", scheduledTasks.size());
        for (int i = 0; i < scheduledTasks.size(); i++)
            compound.put("scheduledTask" + i, scheduledTasks.get(i).serializeNBT());

        return compound;
    }

    public static void deserializeList(CompoundTag compound, List<ScheduledTask> scheduledTasks, ScheduledTaskExecutorInterface executor) {
        int size = compound.getInt("size");
        for (int i = 0; i < size; i++)
            scheduledTasks.add(new ScheduledTask(compound.getCompound("scheduledTask" + i)).setExecutor(executor));
    }
}
