package dev.tauri.jsg.stargate.animation.chevron;

import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.core.client.renderer.Activation;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.NBTUtils;
import dev.tauri.jsg.renderer.activation.UniverseActivation;
import dev.tauri.jsg.stargate.manager.state.StargateAbstractStateManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class StargateUniverseChevronsState extends StargateChevronsState {
    protected final Map<SymbolInterface, SymbolState> symbolsStates = new HashMap<>();

    public StargateUniverseChevronsState(StargateAbstractStateManager<?, ?> stateManager) {
        super(stateManager);
        for (var s : stateManager.stargate.getSymbolType().getValues()) {
            symbolsStates.put(s, new SymbolState(stateManager, s));
        }
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        var stargate = stateManager.stargate;
        var dialingManager = stargate.getDialingManager();
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_SYMBOL_LOCK.get()) {
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);

            if (customData.getBoolean("litAll")) {
                lockAllSymbols();
            } else if (customData.getBoolean("litAddress")) {
                for (var symbol : stateManager.stargate.getDialingManager().getDialedAddress().subList(0, stateManager.stargate.getDialingManager().getDialedAddressSize()))
                    getSymbol(symbol).lock();
            } else {
                var symbol = stargate.getSymbolType().valueOf(customData.getInt("symbol"));
                getSymbol(symbol).lock();
            }

        } else
            super.executeTask(scheduledTask, customData);
    }

    public SymbolState getSymbol(SymbolInterface symbol) {
        var state = symbolsStates.get(symbol);
        if (state == null) {
            state = new SymbolState(stateManager, symbol);
            symbolsStates.put(symbol, state);
        }
        return state;
    }

    public void lockAllSymbols() {
        for (var s : symbolsStates.values())
            s.lock();
    }

    public void dimAllSymbols() {
        for (var s : symbolsStates.values())
            s.dim();
    }

    @Override
    public void dimAll() {
        super.dimAll();
        dimAllSymbols();
    }

    @Override
    public void update(float partialTicks) {
        super.update(partialTicks);
        for (var s : symbolsStates.values())
            s.update(partialTicks);
    }

    @Override
    public void tick(Level level) {
        super.tick(level);
        if (level.isClientSide) return;
        for (var s : symbolsStates.values())
            s.tick(level);
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = super.serializeNBT();
        compound.put("symbolsStates", NBTUtils.serializeMap(symbolsStates, (c, k) -> c.putInt("symbol", k.getId()), (c, v) -> c.put("state", v.serializeNBT())));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        NBTUtils.deSerializeMap(symbolsStates, compound.getCompound("symbolsStates"), (c) -> stateManager.stargate.getSymbolType().valueOf(c.getInt("symbol")), (c) -> new SymbolState(stateManager, c.getCompound("state")));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        NBTUtils.mapToBytes(symbolsStates, buf, (b, k) -> b.writeInt(k.getId()), (b, v) -> v.toBytes(b));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        NBTUtils.mapFromBytes(symbolsStates, buf, (b) -> stateManager.stargate.getSymbolType().valueOf(b.readInt()), (b) -> new SymbolState(stateManager, b));
    }

    public static class SymbolState extends State implements INBTSerializable<CompoundTag>, ITickable {
        protected final StargateAbstractStateManager<?, ?> stateManager;
        protected SymbolInterface symbol;
        protected float lightStage;
        @Nullable
        protected Activation<SymbolInterface> activation;

        public SymbolState(StargateAbstractStateManager<?, ?> stateManager, SymbolInterface symbol) {
            this.stateManager = stateManager;
            this.symbol = symbol;
        }

        public SymbolState(StargateAbstractStateManager<?, ?> stateManager, CompoundTag compound) {
            this.stateManager = stateManager;
            this.symbol = stateManager.stargate.getSymbolType().getOrigin();
            deserializeNBT(compound);
        }

        public SymbolState(StargateAbstractStateManager<?, ?> stateManager, ByteBuf buf) {
            this.stateManager = stateManager;
            this.symbol = stateManager.stargate.getSymbolType().getOrigin();
            fromBytes(buf);
        }

        public boolean isLocked() {
            return lightStage > 0;
        }

        public float getLightStage() {
            return lightStage;
        }

        public void lock() {
            if (isLocked()) return;
            activation = new UniverseActivation(getSymbol(), stateManager.stargate.getTime(), false);
            stateManager.stargate.setChanged();
            send();
        }

        public void dim() {
            if (!isLocked()) return;
            activation = new UniverseActivation(getSymbol(), stateManager.stargate.getTime(), true);
            stateManager.stargate.setChanged();
            send();
        }

        public void send() {
            if (stateManager.stargate.getLevel() == null || stateManager.stargate.getLevel().isClientSide)
                return; // only send state by server
            stateManager.getAndSendState(JSGStateTypes.CHEVRONS_STATE.get());
        }

        public SymbolInterface getSymbol() {
            return symbol;
        }

        protected Activation<SymbolInterface> getActivation(ByteBuf buf) {
            return new UniverseActivation(getSymbol(), buf);
        }

        protected long getTickCompensation() {
            return 3;
        }

        public void update(float partialTicks) {
            if (activation != null) {
                var result = activation.activate(stateManager.stargate.getTime(), partialTicks);
                lightStage = result.stage;
                if (result.remove) {
                    lightStage = activation.getFinalState();
                    activation = null;
                }
            }
        }

        @Override
        public void tick(Level level) {
            // update symbol light on server too
            update(0);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeFloat(lightStage);
            buf.writeInt(symbol.getId());
            if (activation != null) {
                buf.writeBoolean(true);
                activation.toBytes(buf);
            } else buf.writeBoolean(false);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            lightStage = buf.readFloat();
            symbol = stateManager.stargate.getSymbolType().valueOf(buf.readInt());
            if (buf.readBoolean()) {
                activation = getActivation(buf);
                if (activation.stateChange >= (stateManager.stargate.getTime() - getTickCompensation()))
                    activation.stateChange = (long) Math.ceil(stateManager.stargate.getTime() + (double) Minecraft.getInstance().getPartialTick());
            }
        }
    }
}
