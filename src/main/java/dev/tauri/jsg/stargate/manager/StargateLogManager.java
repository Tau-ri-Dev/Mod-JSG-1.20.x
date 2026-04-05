package dev.tauri.jsg.stargate.manager;

import dev.tauri.jsg.core.common.blockentity.IBELogManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.slf4j.event.Level;

import java.util.LinkedList;

public class StargateLogManager implements IBELogManager {
    public static final int MAX_SIZE = 100;
    protected final LinkedList<ILogLine> logs = new LinkedList<>();

    @Override
    public void log(Level level, Component component) {
        synchronized (logs) {
            logs.addLast(new LogLine(level, component));
            while (logs.size() > MAX_SIZE) {
                logs.removeFirst();
            }
        }
    }

    @Override
    public LinkedList<ILogLine> getLogs() {
        synchronized (logs) {
            return new LinkedList<>(logs);
        }
    }

    @Override
    public void clearLogs() {
        synchronized (logs) {
            logs.clear();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        synchronized (logs) {
            var compound = new CompoundTag();
            compound.putInt("size", logs.size());
            for (int i = 0; i < logs.size(); i++) {
                compound.put("line" + i, logs.get(i).serializeNBT());
            }
            return compound;
        }
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        synchronized (logs) {
            logs.clear();
            var size = compound.getInt("size");
            for (int i = 0; i < size; i++) {
                logs.addLast(new LogLine(compound.getCompound("line" + i)));
            }
            while (logs.size() > MAX_SIZE) {
                logs.removeFirst();
            }
        }
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        synchronized (logs) {
            buf.writeInt(logs.size());
            for (ILogLine logLine : logs) {
                logLine.toBytes(buf);
            }
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        synchronized (logs) {
            logs.clear();
            var size = buf.readInt();
            for (int i = 0; i < size; i++) {
                logs.addLast(new LogLine(buf));
            }
        }
    }

    public static class LogLine implements ILogLine {
        protected long time;
        protected Level level;
        protected Component component;

        public LogLine(Level level, Component component) {
            this.time = System.currentTimeMillis();
            this.level = level;
            this.component = component;
        }

        public LogLine(CompoundTag compound) {
            deserializeNBT(compound);
        }

        public LogLine(FriendlyByteBuf buf) {
            fromBytes(buf);
        }

        @Override
        public Component component() {
            return component.copy();
        }

        @Override
        public Level level() {
            return level;
        }

        @Override
        public long time() {
            return time;
        }

        @Override
        public CompoundTag serializeNBT() {
            var compound = new CompoundTag();
            compound.putLong("time", time);
            compound.putString("level", level.name());
            compound.putString("component", Component.Serializer.toJson(component));
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
            time = compound.getLong("time");
            level = Level.valueOf(compound.getString("level"));
            component = Component.Serializer.fromJson(compound.getString("component"));
        }

        @Override
        public void toBytes(FriendlyByteBuf buf) {
            buf.writeLong(time);
            buf.writeInt(level.ordinal());
            buf.writeComponent(component);
        }

        @Override
        public void fromBytes(FriendlyByteBuf buf) {
            time = buf.readLong();
            level = Level.values()[buf.readInt()];
            component = buf.readComponent();
        }
    }
}
