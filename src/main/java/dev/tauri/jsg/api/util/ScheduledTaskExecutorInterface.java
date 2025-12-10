package dev.tauri.jsg.api.util;

import dev.tauri.jsg.api.registry.ScheduledTaskType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Used with {@link ScheduledTaskType} to execute scheduled tasks.
 * 
 * @author MrJake
 */
public interface ScheduledTaskExecutorInterface {
	
	/**
	 * Adds given {@link ScheduledTask} to the list.
	 * 
	 * @param scheduledTask The task to be added.
	 */
	void addTask(ScheduledTask scheduledTask);
	
	/**
	 * Executes given task.
	 * 
	 * @param scheduledTask The task.
	 * @param customData Custom data passed by the user.
	 */
	void executeTask(ScheduledTaskType scheduledTask, @NotNull CompoundTag customData);
}
