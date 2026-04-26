package dev.tauri.jsg.common.stargate.animation.spinning;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.stargate.animation.chevron.StargatePegasusChevronsState;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Consumer;

public class PegasusSpinHelper extends ClassicSpinHelper {
    protected SymbolInterface targetSymbol = SymbolPegasusEnum.SUBIDO;
    protected int slotsToMove;
    protected int currentSlot;

    public PegasusSpinHelper(StargateAbstractBaseBE<?, ?> stargate, Consumer<CompoundTag> onSpinStop) {
        super(stargate, onSpinStop);
    }

    public SymbolInterface getTargetSymbol() {
        return targetSymbol;
    }

    @Override
    public SymbolInterface getCurrentTopSymbol(float bounds) {
        return JSGSymbolTypes.PEGASUS.get().getOrigin();
    }

    @Override
    public float getRingAngle() {
        return -1;
    }

    @Override
    public double apply(double tick, boolean clampAngle) {
        var a = calculate(tick - ringRotationStart);
        if (!clampAngle) return a;
        while (a < 0) {
            a += 36;
        }
        return (a % 36f);
    }

    @Override
    protected double calculate(double tick) {
        if (tick < 0) {
            return this.currentSlot;
        }
        if (!isSpinning()) return this.currentSlot;

        var slotsMoved = (tick * speedFactor);
        if (slotsMoved >= this.slotsToMove) {
            slotsMoved = this.slotsToMove;
            if (stargate.getLevel() != null && !stargate.getLevel().isClientSide()) {
                var currentSlot = (int) (this.currentSlot + (slotsMoved * direction.mul));
                while (currentSlot < 0) {
                    currentSlot += 36;
                }
                this.currentSlot = currentSlot;
                onStopped();
                stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
                return 0;
            }
            var currentSlot = ((slotsMoved * direction.mul) + this.currentSlot);
            while (currentSlot < 0) {
                currentSlot += 36;
            }
            return currentSlot % 36f;
        }
        return ((slotsMoved * direction.mul) + this.currentSlot);
    }

    @Override
    public boolean stopSpinning(boolean clearStopData) {
        if (!isSpinning()) return false;
        if (clearStopData)
            stopSpinData = null;
        onStopped();
        return true;
    }

    @Override
    protected void onStopped() {
        this.isSpinning = false;
        if (stargate.getLevel() != null && !stargate.getLevel().isClientSide) {
            if (stopSpinData == null || !stopSpinData.getBoolean("keepRingRoll"))
                stargate.getSoundManager().updateRingRollSound(false);
            stargate.playSoundEvent(StargateSoundEventEnum.RING_STOP);
            var data = stopSpinData;
            stopSpinData = null;
            onSpinStop.accept(Optional.ofNullable(data).orElseGet(CompoundTag::new));
            stargate.setChanged();
            stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
            sendSpinStop();
        }
    }

    @ParametersAreNonnullByDefault
    public Pair<Boolean, Float> moveTo(SymbolInterface targetSymbol, ChevronEnum chevronToMoveTo, EnumSpinDirection direction, ChevronEnum startingPoint, boolean playRingRollStart) {
        if (isSpinning()) return Pair.of(false, -1f);
        this.isSpinning = true;
        updateRingSpeedFromConfig();
        this.direction = direction;
        this.targetSymbol = targetSymbol;
        this.ringRotationStart = stargate.getTime();
        this.currentSlot = StargatePegasusChevronsState.slotFromChevron(startingPoint);
        var targetSlot = StargatePegasusChevronsState.slotFromChevron(chevronToMoveTo);
        if (direction == EnumSpinDirection.CLOCKWISE) {
            if (targetSlot > this.currentSlot) {
                this.slotsToMove = (targetSlot - this.currentSlot);
            } else {
                this.slotsToMove = 36 - (this.currentSlot - targetSlot);
            }
        } else {
            if (targetSlot > this.currentSlot) {
                this.slotsToMove = 36 - (targetSlot - this.currentSlot);
            } else {
                this.slotsToMove = (this.currentSlot - targetSlot);
            }
        }
        if (this.slotsToMove <= 16)
            this.slotsToMove += 36;
        stargate.setChanged();
        stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
        if (stargate.getLevel() != null && !stargate.getLevel().isClientSide && playRingRollStart) {
            stargate.getSoundManager().updateRingRollSound(true);
            sendSpinStart();
        }
        return Pair.of(true, speedFactor * this.slotsToMove);
    }

    public Pair<Boolean, Float> moveToAndEngage(SymbolInterface targetSymbol, ChevronEnum chevronToMoveTo, ChevronEnum startingPoint, boolean noEnergy, boolean ignoreMaxChevrons, boolean playRingRollStart, boolean stopRingRollAfterStop) {
        var direction = (startingPoint == ChevronEnum.getFinal() ? EnumSpinDirection.CLOCKWISE : (this.direction != null ? this.direction.opposite() : EnumSpinDirection.CLOCKWISE));
        return moveToAndEngage(targetSymbol, chevronToMoveTo, direction, startingPoint, noEnergy, ignoreMaxChevrons, playRingRollStart, stopRingRollAfterStop);
    }

    public Pair<Boolean, Float> moveToAndEngage(SymbolInterface targetSymbol, ChevronEnum chevronToMoveTo, EnumSpinDirection direction, ChevronEnum startingPoint, boolean noEnergy, boolean ignoreMaxChevrons, boolean playRingRollStart, boolean stopRingRollAfterStop) {
        var data = new CompoundTag();
        data.putInt("symbol", targetSymbol.getId());
        data.putBoolean("isFinal", chevronToMoveTo.isFinal());
        data.putBoolean("noEnergy", noEnergy);
        data.putBoolean("ignoreMaxChevrons", ignoreMaxChevrons);
        data.putBoolean("keepRingRoll", !stopRingRollAfterStop);
        var r = moveTo(targetSymbol, chevronToMoveTo, direction, startingPoint, playRingRollStart);
        if (r.first())
            setSpinStopData(data);
        return r;
    }

    @Override
    public Pair<Boolean, Float> moveTo(@NotNull SymbolInterface symbol, @NotNull EnumSpinDirection direction) {
        JSG.logger.error("Move to without start offset parameter is unsupported in pegasus spin helper!");
        stargate.getLogManager().error(Component.literal("Move to without start offset parameter is unsupported in pegasus spin helper!"));
        return Pair.of(false, -1f);
    }

    @Override
    public Pair<Boolean, Float> moveTo(double angle, @NotNull EnumSpinDirection direction) {
        JSG.logger.error("Move to with angle parameter is unsupported in pegasus spin helper!");
        stargate.getLogManager().error(Component.literal("Move to with angle parameter is unsupported in pegasus spin helper!"));
        return Pair.of(false, -1f);
    }

    @Override
    public boolean rotateFreely(@NotNull EnumSpinDirection direction) {
        JSG.logger.error("Rotate freely is unsupported in pegasus spin helper!");
        stargate.getLogManager().error(Component.literal("Rotate freely is unsupported in pegasus spin helper!"));
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = super.serializeNBT();
        if (targetSymbol != null)
            compound.putInt("targetSymbol", targetSymbol.getId());
        compound.putInt("slotsToMove", slotsToMove);
        compound.putInt("currentSlot", currentSlot);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        if (compound.contains("targetSymbol"))
            targetSymbol = JSGSymbolTypes.PEGASUS.get().valueOf(compound.getInt("targetSymbol"));
        slotsToMove = compound.getInt("slotsToMove");
        currentSlot = compound.getInt("currentSlot");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        if (targetSymbol != null) {
            buf.writeBoolean(true);
            buf.writeInt(targetSymbol.getId());
        } else
            buf.writeBoolean(false);
        buf.writeInt(slotsToMove);
        buf.writeInt(currentSlot);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        if (buf.readBoolean()) {
            targetSymbol = JSGSymbolTypes.PEGASUS.get().valueOf(buf.readInt());
        }
        slotsToMove = buf.readInt();
        currentSlot = buf.readInt();
    }

    @Override
    public void from(ClassicSpinHelper state) {
        super.from(state);
        this.targetSymbol = ((PegasusSpinHelper) state).targetSymbol;
        this.slotsToMove = ((PegasusSpinHelper) state).slotsToMove;
        this.currentSlot = ((PegasusSpinHelper) state).currentSlot;
    }
}
