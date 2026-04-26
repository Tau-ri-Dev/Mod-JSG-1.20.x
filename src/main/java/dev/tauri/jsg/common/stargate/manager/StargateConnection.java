package dev.tauri.jsg.common.stargate.manager;


import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.network.IStargateConnection;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.result.StargateConnectionStatus;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * This object contains all informations needed to maintain secure wormhole connection between two gates.
 * Its main use is in {@link StargateAbstractDialingManager}
 *
 * @author MineDragonCZ_ (Tau'ri Dev)
 */
@SuppressWarnings("all")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class StargateConnection implements IStargateConnection {
    public static StargateConnection createEmpty(Stargate sourceGate) {
        return new StargateConnection(sourceGate);
    }

    private StargateConnectionStatus status = StargateConnectionStatus.NONE;
    private Optional<StargatePos> targetGate = Optional.empty();
    private boolean isRIGConnection = false;
    private boolean initiating = false;
    private boolean isNox = false;
    private boolean noEnergy = false;
    private boolean wasFully = false;
    private long since = 0;

    private final Stargate sourceGate;

    public StargateConnection(Stargate sourceGate) {
        this.sourceGate = sourceGate;
    }

    public StargateConnectionStatus getStatus() {
        return status;
    }

    public Optional<StargatePos> getTarget() {
        return targetGate;
    }

    public boolean isInitiating() {
        return initiating;
    }

    public boolean isNox() {
        return isNox;
    }

    public boolean withoutEnergy() {
        return noEnergy;
    }

    public boolean isRIG() {
        return isRIGConnection;
    }

    public boolean wasFully() {
        return wasFully;
    }

    public long getSecondsOpen() {
        if (!status.full()) return 0;
        return (long) ((sourceGate.getTime() - since) / 20.0);
    }

    public long getSince() {
        return since;
    }

    private void reset() {
        this.targetGate = Optional.empty();
        this.status = StargateConnectionStatus.NONE;
        this.initiating = false;
        this.since = sourceGate.getTime();
        this.isNox = false;
        this.noEnergy = false;
        this.isRIGConnection = false;
        this.wasFully = false;
        this.sourceGate.setStargateChanged();
    }

    public boolean establishRIG(boolean isNox, boolean noEnergy) {
        if (this.status != StargateConnectionStatus.NONE) return false;
        this.targetGate = Optional.empty();
        this.status = StargateConnectionStatus.PREPARED;
        this.initiating = false;
        this.isRIGConnection = true;
        this.since = sourceGate.getTime();
        this.isNox = isNox;
        this.noEnergy = noEnergy;
        this.sourceGate.setStargateChanged();
        return true;
    }

    public boolean establish(StargatePos targetGatePos, boolean isNox, boolean noEnergy) {
        if (this.status != StargateConnectionStatus.NONE) return false;
        this.targetGate = Optional.of(targetGatePos);
        this.status = StargateConnectionStatus.PREPARED;
        this.initiating = true;
        this.isRIGConnection = false;
        this.since = sourceGate.getTime();
        this.isNox = isNox;
        this.noEnergy = noEnergy;
        this.sourceGate.setStargateChanged();
        var targetResult = callConnected((c, sg) -> {
            StargateConnection conn = ((StargateConnection) c);
            if (conn.status != StargateConnectionStatus.NONE) {
                this.reset();
                return false;
            }
            conn.targetGate = Optional.of(sourceGate.getStargatePos());
            conn.status = StargateConnectionStatus.PREPARED;
            conn.initiating = false;
            conn.isRIGConnection = false;
            conn.since = this.since;
            conn.isNox = isNox;
            conn.noEnergy = noEnergy;
            conn.sourceGate.setStargateChanged();
            return true;
        }, () -> {
            reset();
            return false;
        });
        return targetResult;
    }

    public void updateStatus(final StargateConnectionStatus status) {
        JSG.logger.info("Updating wormhole status on gate {} to {}", sourceGate.getStargatePos().toString(), status.name());
        var time = sourceGate.getTime();
        if (status == StargateConnectionStatus.NONE) {
            runOnConnected((c, sg) -> {
                StargateConnection conn = ((StargateConnection) c);
                conn.reset();
            });
            reset();
            return;
        }
        this.status = status;
        this.since = time;
        if (status == StargateConnectionStatus.FULLY)
            this.wasFully = true;
        runOnConnected((c, sg) -> {
            StargateConnection conn = ((StargateConnection) c);
            conn.wasFully = this.wasFully;
            conn.status = this.status;
            conn.since = this.since;
            JSG.logger.info("Updating wormhole status on gate {} to {}", sg.getStargatePos().toString(), this.status.name());
        });
    }

    // client only
    public void setStatusSince(long time) {
        if (sourceGate.getStargateLevel() != null && !sourceGate.getStargateLevel().isClientSide) return;
        this.since = time;
    }

    // client only
    public void setStatusUnsafe(StargateConnectionStatus status) {
        if (sourceGate.getStargateLevel() != null && !sourceGate.getStargateLevel().isClientSide) return;
        this.status = status;
    }

    public void runOnConnected(BiConsumer<IStargateConnection, Stargate<?>> function) {
        callConnected((c, s) -> {
            function.accept(c, s);
            return null;
        }, () -> null);
    }

    public void runIfConnected(TriConsumer<IStargateConnection, Stargate<?>, Boolean> function) {
        if (!status.none() && targetGate.isPresent()) {
            var targetGate = this.targetGate.get().getStargate();
            if (targetGate != null) {
                function.accept(this, targetGate, isInitiating());
            }
        }
    }

    public void runIfConnectedElse(TriConsumer<IStargateConnection, Stargate<?>, Boolean> function, Runnable fallback) {
        if (!status.none() && targetGate.isPresent()) {
            var targetGate = this.targetGate.get().getStargate();
            if (targetGate != null) {
                function.accept(this, targetGate, isInitiating());
                return;
            }
        }
        fallback.run();
    }

    public <R> R callConnected(BiFunction<IStargateConnection, Stargate<?>, R> function, Supplier<R> fallback) {
        if (!status.none() && targetGate.isPresent()) {
            var targetGate = this.targetGate.get().getStargate();
            if (targetGate != null) {
                return function.apply(targetGate.getDialingManager().getConnection(), targetGate);
            }
        }
        return fallback.get();
    }

    public void runOnBothConnected(BiConsumer<IStargateConnection, Stargate<?>> function) {
        function.accept(this, sourceGate);
        if (!status.none() && targetGate.isPresent() && !targetGate.get().equals(sourceGate.getStargatePos())) {
            var targetGate = this.targetGate.get().getStargate();
            if (targetGate != null) {
                function.accept(targetGate.getDialingManager().getConnection(), targetGate);
            }
        }
    }

    public void runIfInitializing(BiConsumer<IStargateConnection, Stargate<?>> function) {
        if (status.none()) return;
        if (!initiating) return;
        function.accept(this, sourceGate);
    }

    public <R> R callIfInitiating(BiFunction<IStargateConnection, Stargate<?>, R> function, Supplier<R> fallback) {
        if (status.none()) return fallback.get();
        if (initiating)
            return function.apply(this, sourceGate);
        return fallback.get();
    }

    public <R> R callOnInitiating(BiFunction<IStargateConnection, Stargate<?>, R> function, Supplier<R> fallback) {
        if (status.none()) return fallback.get();
        if (initiating) {
            return function.apply(this, sourceGate);
        }
        if (targetGate.isPresent() && !targetGate.get().equals(sourceGate.getStargatePos())) {
            var targetGate = this.targetGate.get().getStargate();
            if (targetGate != null) {
                return function.apply(targetGate.getDialingManager().getConnection(), targetGate);
            }
        }
        return fallback.get();
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putInt("status", status.ordinal());
        compound.putLong("since", since);
        compound.putBoolean("initiating", initiating);
        compound.putBoolean("isNox", isNox);
        compound.putBoolean("noEnergy", noEnergy);
        compound.putBoolean("isRIGConnection", isRIGConnection);
        compound.putBoolean("wasFully", wasFully);
        targetGate.ifPresent(stargatePos -> compound.put("target", stargatePos.serializeNBT()));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        targetGate = Optional.empty();
        status = StargateConnectionStatus.values()[compound.getInt("status")];
        since = compound.getLong("since");
        initiating = compound.getBoolean("initiating");
        isNox = compound.getBoolean("isNox");
        noEnergy = compound.getBoolean("noEnergy");
        isRIGConnection = compound.getBoolean("isRIGConnection");
        wasFully = compound.getBoolean("wasFully");
        if (compound.contains("target"))
            targetGate = Optional.of(new StargatePos(compound.getCompound("target")));

        if (!isRIGConnection && targetGate.isEmpty() && status != StargateConnectionStatus.NONE && status != StargateConnectionStatus.CLOSING) {
            JSG.logger.error("Target gate is null while connection is not NONE, this is a bug! Correcting...");
            status = StargateConnectionStatus.NONE;
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeLong(since);
        buf.writeInt(status.ordinal());
        buf.writeBoolean(initiating);
        buf.writeBoolean(isNox);
        buf.writeBoolean(noEnergy);
        buf.writeBoolean(isRIGConnection);
        buf.writeBoolean(wasFully);
        if (targetGate.isPresent()) {
            buf.writeBoolean(true);
            targetGate.get().toBytes(buf);
        } else
            buf.writeBoolean(false);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        since = buf.readLong();
        status = StargateConnectionStatus.values()[buf.readInt()];
        initiating = buf.readBoolean();
        isNox = buf.readBoolean();
        noEnergy = buf.readBoolean();
        isRIGConnection = buf.readBoolean();
        wasFully = buf.readBoolean();
        if (buf.readBoolean()) {
            targetGate = Optional.of(new StargatePos(buf));
        }
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide) return;
        runWatchDog((ServerLevel) level);
    }

    protected void runWatchDog(@NotNull ServerLevel level) {
        var dm = sourceGate.getDialingManager();
        var state = dm.getStargateState();
        if (status.full() != state.engaged()) {
            JSG.logger.warn("Closing stargate {} because wormhole status != mechanical status! (engaged: {}, wormhole open: {})", sourceGate.getStargatePos().toString(), (state.engaged() ? "true" : "false"), (status.full() ? "true" : "false"));
            dm.disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
            return;
        }
        if (!isRIGConnection && sourceGate.getRIGManager().isGateActive() || (isRIGConnection && !sourceGate.getRIGManager().isGateActive() && !status.closing())) {
            JSG.logger.warn("Closing stargate {} because rig status mismatch (conn: {}, rigManager: {})", sourceGate.getStargatePos().toString(), (isRIGConnection ? "true" : "false"), (sourceGate.getRIGManager().isGateActive() ? "true" : "false"));
            if (sourceGate.getRIGManager().isActive())
                sourceGate.getRIGManager().end();
            else
                dm.disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
            return;
        }
        if (status.full()) return;
        if (status.none()) {
            if (state.idle()) return;
            if (state.dialing()) return;
            if (state.failing()) return;
            if (state.resetting()) return;
            if (state.incoming()) {
                JSG.logger.warn("Closing stargate {} because it's incoming while should be idle", sourceGate.getStargatePos().toString());
                updateStatus(StargateConnectionStatus.PREPARED);
            } else if (state == EnumStargateState.UNSTABLE_CLOSING) {
                JSG.logger.warn("Closing stargate {} because it's closing while should be idle", sourceGate.getStargatePos().toString());
                updateStatus(StargateConnectionStatus.CLOSING);
            } else {
                JSG.logger.warn("Closing stargate {} because it's not idle while should be", sourceGate.getStargatePos().toString());
                updateStatus(StargateConnectionStatus.WAITING_FOR_WORMHOLE);
            }
            dm.disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
            return;
        }
        var watchDogTime = ((((status.waiting() || status.prepared()) && initiating) || state.incoming()) ? 5 * 60 * 20 : 30 * 20);
        if ((level.getGameTime() - since) >= watchDogTime) {
            JSG.logger.warn("Closing stargate at {} because it's status ({}/{}) exceeded time limit of {} ticks (was {} ticks)", sourceGate.blockPosition().toString(), status.name(), state.name(), watchDogTime, (level.getGameTime() - since));
            dm.disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
            return;
        }
    }
}
