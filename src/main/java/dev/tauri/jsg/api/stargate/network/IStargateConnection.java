package dev.tauri.jsg.api.stargate.network;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.manager.IStargateDialingManager;
import dev.tauri.jsg.api.stargate.result.StargateConnectionStatus;
import dev.tauri.jsg.api.util.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * This object contains all information needed to maintain secure wormhole connection between two gates.
 * Its main use is in {@link IStargateDialingManager}
 *
 * @author MineDragonCZ_ (Tau'ri Dev)
 */
public interface IStargateConnection extends INBTSerializable<CompoundTag>, ITickable {
    StargateConnectionStatus getStatus();

    Optional<StargatePos> getTarget();

    boolean isInitiating();

    boolean isNox();

    boolean withoutEnergy();

    long getSince();

    long getSecondsOpen();


    void runOnConnected(BiConsumer<IStargateConnection, Stargate<?>> function);

    void runIfConnected(TriConsumer<IStargateConnection, Stargate<?>, Boolean> function);

    void runIfConnectedElse(TriConsumer<IStargateConnection, Stargate<?>, Boolean> function, Runnable fallback);

    <R> R callConnected(BiFunction<IStargateConnection, Stargate<?>, R> function, Supplier<R> fallback);

    void runOnBothConnected(BiConsumer<IStargateConnection, Stargate<?>> function);

    void runIfInitializing(BiConsumer<IStargateConnection, Stargate<?>> function);

    <R> R callIfInitiating(BiFunction<IStargateConnection, Stargate<?>, R> function, Supplier<R> fallback);

    <R> R callOnInitiating(BiFunction<IStargateConnection, Stargate<?>, R> function, Supplier<R> fallback);
}
