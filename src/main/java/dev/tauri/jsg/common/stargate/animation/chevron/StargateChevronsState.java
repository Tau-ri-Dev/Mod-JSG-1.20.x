package dev.tauri.jsg.common.stargate.animation.chevron;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.animation.IChevronsState;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.client.renderer.activation.StargateActivation;
import dev.tauri.jsg.common.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.core.client.renderer.Activation;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockentity.ScheduledTaskExecutorInterface;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.math.MathFunction;
import dev.tauri.jsg.core.common.util.math.MathFunctionImpl;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import dev.tauri.jsg.core.common.util.math.MathRange;
import io.netty.buffer.ByteBuf;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class StargateChevronsState extends State implements INBTSerializable<CompoundTag>, ITickable, ScheduledTaskExecutorInterface, IChevronsState {
    protected final Map<ChevronEnum, ChevronState> states = new HashMap<>();
    protected final StargateAbstractStateManager<?, ?> stateManager;

    protected static final MathRange CHEVRON_OPEN_RANGE = new MathRange(0, 1.57f);
    protected static final MathFunction CHEVRON_OPEN_FUNCTION = new MathFunctionImpl(x -> x * x * x * x / 80f);

    protected static final MathRange CHEVRON_CLOSE_RANGE = new MathRange(0, 1.428f);
    protected static final MathFunction CHEVRON_CLOSE_FUNCTION = new MathFunctionImpl(x0 -> MathHelper.cos(x0 * 1.1f) / 12f);

    public StargateChevronsState(StargateAbstractStateManager<?, ?> stateManager) {
        this.stateManager = stateManager;
        for (var ch : ChevronEnum.values()) {
            states.put(ch, getEmptyState(stateManager, ch));
        }
    }

    public MathRange getChevronOpenRange() {
        return CHEVRON_OPEN_RANGE;
    }

    public MathRange getChevronCloseRange() {
        return CHEVRON_CLOSE_RANGE;
    }

    public MathFunction getChevronOpenFunction() {
        return CHEVRON_OPEN_FUNCTION;
    }

    public MathFunction getChevronCloseFunction() {
        return CHEVRON_CLOSE_FUNCTION;
    }

    @Override
    public ChevronState get(ChevronEnum chevron) {
        var state = states.get(chevron);
        if (state == null) {
            state = getEmptyState(stateManager, chevron);
            states.put(chevron, state);
        }
        return state;
    }

    public ChevronState get(int dialedAddressSize, boolean isFinal) {
        if (isFinal) return get(ChevronEnum.getFinal());
        var chevron = ChevronEnum.valueOf(dialedAddressSize);
        if (chevron == null) return get(ChevronEnum.getFinal());
        return get(chevron);
    }

    protected abstract ChevronState getEmptyState(StargateAbstractStateManager<?, ?> stateManager, ChevronEnum ch);

    protected abstract ChevronState chevronStateFromNBT(StargateAbstractStateManager<?, ?> stateManager, CompoundTag tag);

    protected abstract ChevronState chevronStateFromBytes(StargateAbstractStateManager<?, ?> stateManager, ByteBuf buff);

    public void update(float partialTicks) {
        for (var s : states.values())
            s.update(partialTicks);
    }

    public void dimAll() {
        for (var s : states.values())
            s.dim();
    }

    public void lockAll() {
        for (var s : states.values())
            s.lock();
    }

    public void lockMultiple(List<ChevronEnum> chevrons) {
        for (var c : chevrons) {
            get(c).lock();
        }
    }

    public void openMultiple(List<ChevronEnum> chevrons) {
        for (var c : chevrons) {
            get(c).open();
        }
    }

    public void openAll() {
        for (var s : states.values())
            s.open();
    }

    public void closeAll() {
        for (var s : states.values())
            s.close();
    }

    public boolean isOpenedAny() {
        for (var s : states.values()) {
            if (s.isOpen())
                return true;
        }
        return false;
    }

    public void send() {
        if (stateManager.stargate.getLevel() == null || stateManager.stargate.getLevel().isClientSide)
            return; // only send state by server
        stateManager.getAndSendState(JSGStateTypes.CHEVRONS_STATE.get());
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        var states = new ListTag();
        for (var ch : ChevronEnum.values()) {
            states.add(this.states.getOrDefault(ch, getEmptyState(stateManager, ch)).serializeNBT());
        }
        compound.put("states", states);
        compound.put("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        var states = compound.getList("states", Tag.TAG_COMPOUND);
        for (var s : states) {
            var state = chevronStateFromNBT(stateManager, (CompoundTag) s);
            this.states.put(state.getChevron(), state);
        }
        try {
            ScheduledTask.deserializeList(compound.getCompound("scheduledTasks"), scheduledTasks, this);
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            JSG.logger.warn("Exception at reading NBT");
            JSG.logger.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it", e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for (var ch : ChevronEnum.values()) {
            this.states.getOrDefault(ch, getEmptyState(stateManager, ch)).toBytes(buf);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        for (var ch : ChevronEnum.values()) {
            this.states.put(ch, chevronStateFromBytes(stateManager, buf));
        }
    }

    @Override
    public void tick(Level level) {
        if (level.isClientSide) return;
        ScheduledTask.iterate(scheduledTasks, level.getGameTime());
        for (var s : states.values()) {
            var chevronOpen = s.chevronOpen;
            var chevronOpening = s.chevronOpening;
            var chevronClosing = s.chevronClosing;
            var chevronActionStart = s.chevronActionStart;
            var activation = s.activation;
            var lightStage = s.lightStage;

            s.tick(level);

            if (chevronOpen != s.chevronOpen ||
                    chevronOpening != s.chevronOpening ||
                    chevronClosing != s.chevronClosing ||
                    chevronActionStart != s.chevronActionStart ||
                    activation != s.activation ||
                    lightStage != s.lightStage
            ) {
                stateManager.stargate.setChanged();
            }
        }
    }

    public static final int A_CHEVRON_OPEN_AFTER_STOPPED_DELAY = 10;
    public static final int A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY = 7;
    public static final int A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY = 15;

    protected final List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(stateManager.stargate.getTime());

        if (scheduledTask.getWaitTime() <= 0) {
            scheduledTask.execute();
            return;
        }

        scheduledTasks.add(scheduledTask);
        stateManager.stargate.setChanged();
    }

    public void clearTasks() {
        scheduledTasks.clear();
        stateManager.stargate.setChanged();
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        var stargate = stateManager.stargate;
        var dialingManager = stargate.getDialingManager();
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_LIGHT_UP.get()) {
            var result = dialingManager.onChevronActivates(customData);
            if (result != null && result != StargateChevronEngageResult.OK) {
                if (result == StargateChevronEngageResult.FAILED_FAIL_GATE)
                    onChevronFailedToLock(true);
                else
                    onChevronFailedToLock(customData.getBoolean("checkConnection"));
            } else {
                if (customData.getBoolean("setIdle"))
                    dialingManager.setStargateState(EnumStargateState.IDLE);
                stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
                if (customData.getBoolean("litAll")) {
                    lockAll();
                } else {
                    get(customData.getInt("chevron"), customData.getBoolean("isFinal")).lock();
                }
                if (customData.getBoolean("animateTopChevronLock")) {
                    scheduleChevronClose(A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY, ChevronEnum.getFinal(), customData.getBoolean("isFinal"));
                    if (!customData.getBoolean("isFinal")) {
                        get(ChevronEnum.getFinal().getKey(), true).lock();
                        scheduleChevronDim(A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY + 2, ChevronEnum.getFinal(), true);
                    }
                }
            }
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_OPEN.get()) {
            stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);

            get(customData.getInt("chevron"), customData.getBoolean("isFinal"))
                    .open();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_CLOSE.get()) {
            stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
            get(customData.getInt("chevron"), customData.getBoolean("isFinal"))
                    .close();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_FAIL.get()) {
            if (customData.getBoolean("setIdle"))
                dialingManager.dialingFailed(StargateOpenResult.ADDRESS_MALFORMED);
            get(customData.getInt("chevron"), customData.getBoolean("isFinal"))
                    .close();
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_DIM.get()) {
            get(customData.getInt("chevron"), customData.getBoolean("isFinal"))
                    .dim();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CLEAR_CHEVRONS.get()) {
            dimAll();
            closeAll();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        } else if (scheduledTask == JSGScheduledTaskTypes.LIGHT_UP_CHEVRONS.get()) {
            if (customData.getBoolean("playSound"))
                stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
            if (customData.contains("chevrons")) {
                var chevrons = new ArrayList<ChevronEnum>();
                for (var i : customData.getIntArray("chevrons"))
                    chevrons.add(ChevronEnum.valueOf(i));
                lockMultiple(chevrons);
            } else
                lockAll();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        }
    }

    public void scheduleChevronActivate(int waitTicks, ChevronEnum chevron, @org.jetbrains.annotations.Nullable SymbolInterface symbolToLock, boolean setIdle) {
        scheduleChevronActivate(waitTicks, chevron, symbolToLock, setIdle, false, false);
    }

    public void scheduleChevronFinalOpenAndActivate(int waitTicks, @org.jetbrains.annotations.Nullable SymbolInterface symbolToLock, boolean setIdle) {
        scheduleChevronOpen(waitTicks, ChevronEnum.getFinal(), false);
        scheduleChevronActivate(waitTicks + A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY, ChevronEnum.getFinal(), symbolToLock, false);
        scheduleChevronClose(waitTicks + A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY + A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY, ChevronEnum.getFinal(), setIdle);
    }

    public void scheduleChevronActivate(int waitTicks, ChevronEnum chevron, @org.jetbrains.annotations.Nullable SymbolInterface symbolToLock, boolean setIdle, boolean checkConnection, boolean animateTopChevronLock) {
        scheduleChevronActivate(waitTicks, chevron, symbolToLock, setIdle, checkConnection, animateTopChevronLock, false, false);
    }

    public void scheduleChevronActivate(int waitTicks, ChevronEnum chevron, @org.jetbrains.annotations.Nullable SymbolInterface symbolToLock, boolean setIdle, boolean checkConnection, boolean animateTopChevronLock, boolean noEnergy, boolean ignoreMaxChevrons) {
        var data = new CompoundTag();
        data.putInt("chevron", chevron.getKey());
        data.putBoolean("isFinal", chevron.isFinal());
        data.putBoolean("setIdle", setIdle);
        data.putBoolean("checkConnection", checkConnection);
        data.putBoolean("noEnergy", noEnergy);
        data.putBoolean("ignoreMaxChevrons", ignoreMaxChevrons);
        data.putBoolean("animateTopChevronLock", animateTopChevronLock);
        if (symbolToLock != null)
            data.putInt("symbol", symbolToLock.getId());
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_LIGHT_UP, waitTicks, data));
    }

    public void scheduleChevronDim(int waitTicks, ChevronEnum chevron, boolean setIdle) {
        var data = new CompoundTag();
        data.putInt("chevron", chevron.getKey());
        data.putBoolean("isFinal", chevron.isFinal());
        data.putBoolean("setIdle", setIdle);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_DIM, waitTicks, data));
    }

    public void scheduleChevronOpen(int waitTicks, ChevronEnum chevron, boolean setIdle) {
        var data = new CompoundTag();
        data.putInt("chevron", chevron.getKey());
        data.putBoolean("isFinal", chevron.isFinal());
        data.putBoolean("setIdle", setIdle);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_OPEN, waitTicks, data));
    }

    public void scheduleChevronClose(int waitTicks, ChevronEnum chevron, boolean setIdle) {
        var data = new CompoundTag();
        data.putInt("chevron", chevron.getKey());
        data.putBoolean("isFinal", chevron.isFinal());
        data.putBoolean("setIdle", setIdle);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_CLOSE, waitTicks, data));
    }

    public void scheduleChevronFail(int waitTicks, ChevronEnum chevron, boolean setIdle) {
        var data = new CompoundTag();
        data.putInt("chevron", chevron.getKey());
        data.putBoolean("isFinal", chevron.isFinal());
        data.putBoolean("setIdle", setIdle);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_FAIL, waitTicks, data));
    }

    public void scheduleChevronsActivateMultiple(int waitTicks, boolean setIdle, boolean playSound, List<ChevronEnum> chevrons) {
        var data = new CompoundTag();
        data.putBoolean("playSound", playSound);
        data.putBoolean("setIdle", setIdle);
        data.putIntArray("chevrons", chevrons.stream().map(ChevronEnum::getKey).toList());
        addTask(new ScheduledTask(JSGScheduledTaskTypes.LIGHT_UP_CHEVRONS, waitTicks, data));
    }

    public void scheduleChevronsDimAll(int waitTicks, boolean setIdle) {
        var data = new CompoundTag();
        data.putBoolean("setIdle", setIdle);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CLEAR_CHEVRONS, waitTicks, data));
    }

    public void scheduleChevronsLockAll(int waitTicks, boolean setIdle) {
        var data = new CompoundTag();
        data.putBoolean("setIdle", setIdle);
        data.putBoolean("litAll", true);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_LIGHT_UP, waitTicks, data));
    }

    protected void onChevronFailedToLock(boolean checkingForConnection) {
        if (!checkingForConnection && !get(ChevronEnum.getFinal()).isOpen())
            return;
        scheduleChevronFail(60, ChevronEnum.getFinal(), checkingForConnection);
    }

    public static class ChevronState extends State implements IChevronState, INBTSerializable<CompoundTag>, ITickable {
        private final Map<BiomeOverlayInstance, ResourceLocation> CHEVRON_RESOURCE_MAP = new HashMap<>();
        private final Map<BiomeOverlayInstance, ResourceLocation> CHEVRON_LIGHT_RESOURCE_MAP = new HashMap<>();
        private final Map<BiomeOverlayInstance, ResourceLocation> CHEVRON_LIGHT_RESOURCE_MAP_OFF = new HashMap<>();

        protected final StargateAbstractStateManager<?, ?> stateManager;
        protected ChevronEnum chevron = ChevronEnum.getFinal();
        @Nullable
        protected Activation<ChevronEnum> activation;
        protected float lightStage;
        protected boolean chevronOpen;
        protected long chevronActionStart;
        protected boolean chevronOpening;
        protected boolean chevronClosing;

        protected Activation<ChevronEnum> getActivation(CompoundTag tag) {
            return new StargateActivation(getChevron(), tag);
        }

        protected Activation<ChevronEnum> getActivation(ByteBuf buf) {
            return new StargateActivation(getChevron(), buf);
        }

        protected ChevronState(StargateAbstractStateManager<?, ?> stateManager, ITextureLoader textureLoader, String chevronTexBase) {
            this.stateManager = stateManager;
            for (BiomeOverlayInstance biomeOverlay : BiomeOverlayInstance.values()) {
                CHEVRON_LIGHT_RESOURCE_MAP.put(biomeOverlay, textureLoader.getTextureResource(chevronTexBase + "_light" + biomeOverlay.suffix() + ".webp"));
                CHEVRON_LIGHT_RESOURCE_MAP_OFF.put(biomeOverlay, textureLoader.getTextureResource(chevronTexBase + "_light_off" + biomeOverlay.suffix() + ".webp"));
                CHEVRON_RESOURCE_MAP.put(biomeOverlay, textureLoader.getTextureResource(chevronTexBase + biomeOverlay.suffix() + ".webp"));
            }
        }

        public ChevronState(StargateAbstractStateManager<?, ?> stateManager, ITextureLoader textureLoader, String chevronTexBase, ChevronEnum chevron) {
            this(stateManager, textureLoader, chevronTexBase);
            this.chevron = chevron;
        }

        public ChevronState(StargateAbstractStateManager<?, ?> stateManager, ITextureLoader textureLoader, String chevronTexBase, CompoundTag compound) {
            this(stateManager, textureLoader, chevronTexBase);
            deserializeNBT(compound);
        }

        public ChevronState(StargateAbstractStateManager<?, ?> stateManager, ITextureLoader textureLoader, String chevronTexBase, ByteBuf buf) {
            this(stateManager, textureLoader, chevronTexBase);
            fromBytes(buf);
        }

        protected double getTickCompensation() {
            return 2;
        }

        @Override
        public void open() {
            if (isOpen()) return;
            if (chevronClosing || chevronOpening) return;
            chevronActionStart = stateManager.stargate.getTime();
            chevronClosing = false;
            chevronOpen = false;
            chevronOpening = true;
            stateManager.stargate.setChanged();
            send();
            onServer(() -> StargateComputerEvents.CHEVRON_OPEN.apply(chevron).sendVia(stateManager.stargate));
        }

        @Override
        public void close() {
            if (!isOpen()) return;
            if (chevronClosing || chevronOpening) return;
            chevronActionStart = stateManager.stargate.getTime();
            chevronOpening = false;
            chevronOpen = true;
            chevronClosing = true;
            stateManager.stargate.setChanged();
            send();
            onServer(() -> StargateComputerEvents.CHEVRON_CLOSE.apply(chevron).sendVia(stateManager.stargate));
        }

        @Override
        public void lock() {
            if (activation != null && activation.isActive() && activation.dim)
                activation = new StargateActivation(getChevron(), activation.stateChange, false);
            else if (isLocked()) return;
            else
                activation = new StargateActivation(getChevron(), stateManager.stargate.getTime(), false);
            stateManager.stargate.setChanged();
            send();
            onServer(() -> StargateComputerEvents.CHEVRON_LIT.apply(chevron).sendVia(stateManager.stargate));
        }

        @Override
        public void dim() {
            if (activation != null && activation.isActive() && !activation.dim)
                activation = new StargateActivation(getChevron(), activation.stateChange, true);
            else if (!isLocked()) return;
            else
                activation = new StargateActivation(getChevron(), stateManager.stargate.getTime(), true);
            stateManager.stargate.setChanged();
            send();
            onServer(() -> StargateComputerEvents.CHEVRON_DIM.apply(chevron).sendVia(stateManager.stargate));
        }

        @Override
        public boolean isOpen() {
            return chevronOpen;
        }

        @Override
        public boolean isLocked() {
            return lightStage > 0;
        }

        public float getState() {
            return lightStage;
        }

        public ResourceLocation getTexture(BiomeOverlayInstance overlayEnum, boolean onlyLight) {
            if (onlyLight) {
                if (getState() < 1)
                    return CHEVRON_LIGHT_RESOURCE_MAP_OFF.get(overlayEnum);
                return CHEVRON_LIGHT_RESOURCE_MAP.get(overlayEnum);
            }
            return CHEVRON_RESOURCE_MAP.get(overlayEnum);
        }

        public void update(float partialTicks) {
            getOffset(partialTicks, 1);
            if (activation != null) {
                var result = activation.activate(stateManager.stargate.getTime(), partialTicks);
                lightStage = result.stage;
                if (result.remove) {
                    lightStage = activation.getFinalState();
                    activation = null;
                }
            }
        }

        public void send() {
            if (stateManager.stargate.getLevel() == null || stateManager.stargate.getLevel().isClientSide)
                return; // only send state by server
            stateManager.getAndSendState(JSGStateTypes.CHEVRONS_STATE.get());
        }

        public void onServer(Runnable runnable) {
            if (stateManager.stargate.getLevel() == null || stateManager.stargate.getLevel().isClientSide) return;
            runnable.run();
        }

        public ChevronEnum getChevron() {
            return chevron;
        }

        public float getOffset(float partialTicks, float mul) {
            float tick = (stateManager.stargate.getTime() - chevronActionStart + partialTicks);
            float x = tick / 6.0f;
            if (x < 0) x = 0;

            var chStates = stateManager.getChevronsState();

            if (chevronOpening) {
                if (chStates.getChevronOpenRange().test(x)) return chStates.getChevronOpenFunction().apply(x) * mul;
                else {
                    chevronOpen = true;
                    chevronOpening = false;
                }
            } else if (chevronClosing) {
                if (chStates.getChevronCloseRange().test(x)) return chStates.getChevronCloseFunction().apply(x) * mul;
                else {
                    chevronOpen = false;
                    chevronClosing = false;
                }
            }

            return chevronOpen ? (0.08333f * mul) : 0;
        }

        // server only
        @Override
        public void tick(Level level) {
            // update chevron position on server too
            update(0);
        }


        @Override
        public CompoundTag serializeNBT() {
            var compound = new CompoundTag();
            compound.putBoolean("chevronOpen", chevronOpen);
            compound.putLong("chevronActionStart", chevronActionStart);
            compound.putBoolean("chevronOpening", chevronOpening);
            compound.putBoolean("chevronClosing", chevronClosing);

            compound.putFloat("lightStage", lightStage);
            compound.putInt("chevron", chevron.getKey());
            if (activation != null)
                compound.put("activation", activation.serializeNBT());
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
            chevronOpen = compound.getBoolean("chevronOpen");
            chevronActionStart = compound.getLong("chevronActionStart");
            chevronOpening = compound.getBoolean("chevronOpening");
            chevronClosing = compound.getBoolean("chevronClosing");

            lightStage = compound.getFloat("lightStage");
            chevron = ChevronEnum.valueOf(compound.getInt("chevron"));
            if (compound.contains("activation"))
                activation = getActivation(compound.getCompound("activation"));
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBoolean(chevronOpen);
            buf.writeLong(chevronActionStart);
            buf.writeBoolean(chevronOpening);
            buf.writeBoolean(chevronClosing);

            buf.writeFloat(lightStage);
            buf.writeInt(chevron.getKey());
            if (activation != null) {
                buf.writeBoolean(true);
                activation.toBytes(buf);
            } else buf.writeBoolean(false);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            chevronOpen = buf.readBoolean();
            chevronActionStart = buf.readLong();
            chevronOpening = buf.readBoolean();
            chevronClosing = buf.readBoolean();

            lightStage = buf.readFloat();
            chevron = ChevronEnum.valueOf(buf.readInt());
            if (buf.readBoolean()) {
                activation = getActivation(buf);
                activation.stateChange = (long) Math.ceil(activation.stateChange + getTickCompensation());
            }
            chevronActionStart = (long) Math.ceil(chevronActionStart + getTickCompensation());
        }
    }
}
