package dev.tauri.jsg.stargate.animation.chevron;

import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.NBTUtils;
import dev.tauri.jsg.stargate.manager.state.StargateAbstractStateManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class StargatePegasusChevronsState extends StargateChevronsState {
    protected final Map<Integer, SymbolInterface> activatedSymbols = new HashMap<>();

    public StargatePegasusChevronsState(StargateAbstractStateManager<?, ?> stateManager) {
        super(stateManager);
    }

    public static int getCorrectedSlot(int slot) {
        return (36 - (slot - 8)) % 36;
    }

    public static int slotFromChevron(ChevronEnum chevron) {
        var slot = (chevron.rotationIndex * 4) - 1;
        if (slot < 0) slot += 36;
        return slot;
    }

    public static Optional<ChevronEnum> chevronFromSlot(int slot) {
        if ((slot + 1) % 4 != 0) return Optional.empty();
        var chevron = ChevronEnum.fromRotationIndex(((slot + 1) % 36) / 4);
        return Optional.ofNullable(chevron);
    }

    public void activateSlot(int slot, @Nullable SymbolInterface symbol) {
        if (symbol == null) return;
        activatedSymbols.put(slot, symbol);
        send();
    }

    public void deactivateSlot(int slot) {
        activatedSymbols.remove(slot);
        send();
    }

    public void deactivateAllSlots() {
        activatedSymbols.clear();
        send();
    }

    public boolean isAnySlotActive() {
        return !activatedSymbols.isEmpty();
    }

    public boolean isSlotActive(int slot) {
        return activatedSymbols.containsKey(slot);
    }

    public Optional<SymbolInterface> getSymbolAtSlot(int slot) {
        return Optional.ofNullable(activatedSymbols.get(slot));
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        var stargate = stateManager.stargate;
        var dialingManager = stargate.getDialingManager();
        // chevrons operations
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_LIGHT_UP.get()) {
            var result = dialingManager.onChevronActivates(customData);
            if (result != null && result != StargateChevronEngageResult.OK) {
                if (result == StargateChevronEngageResult.FAILED_FAIL_GATE)
                    onChevronFailedToLock(true);
                else
                    onChevronFailedToLock(customData.getBoolean("checkConnection"));
            }
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
            stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
            var chevronState = get(customData.getInt("chevron"), customData.getBoolean("isFinal"));
            chevronState.lock();
            if (result != null && result.ok()) {
                activateSlot(StargatePegasusChevronsState.slotFromChevron(chevronState.getChevron()), dialingManager.getDialedAddress().getLast());
            }
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_FAIL.get()) {
            if (customData.getBoolean("setIdle"))
                dialingManager.dialingFailed(StargateOpenResult.ADDRESS_MALFORMED);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CLEAR_CHEVRONS.get()) {
            deactivateAllSlots();
            super.executeTask(scheduledTask, customData);
        } else
            super.executeTask(scheduledTask, customData);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        NBTUtils.mapToBytes(activatedSymbols, buf, ByteBuf::writeInt, (b, v) -> {
            if (v == null) v = JSGSymbolTypes.PEGASUS.get().getOrigin();
            b.writeInt(v.getId());
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        NBTUtils.mapFromBytes(activatedSymbols, buf, ByteBuf::readInt, (b) -> JSGSymbolTypes.PEGASUS.get().valueOf(b.readInt()));
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = super.serializeNBT();
        compound.put("activatedSymbols", NBTUtils.serializeMap(activatedSymbols, (c, k) -> c.putInt("slot", k), (c, v) -> c.putInt("symbol", v.getId())));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        NBTUtils.deSerializeMap(activatedSymbols, compound.getCompound("activatedSymbols"), (c) -> c.getInt("slot"), (c) -> JSGSymbolTypes.PEGASUS.get().valueOf(c.getInt("symbol")));
    }
}
