package dev.tauri.jsg.common.stargate.animation.spinning;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.api.stargate.animation.ISpinHelper;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.math.MathFunctionLinear;
import dev.tauri.jsg.core.common.util.math.MathFunctionQuadratic;
import dev.tauri.jsg.core.common.util.math.MathRange;
import dev.tauri.jsg.core.common.util.math.MathRangedFunction;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Helper that handlers ring movement across server and client
 */
public class ClassicSpinHelper extends State implements ISpinHelper {
    public static float A_ANGLE_PER_TICK = 1.8f;
    public static final float U_SPEEDUP_TIME = 35;
    public static final float S_STOP_TIME = 25;

    public static final float MIN_ANGLE_TO_DIAL = 60;

    protected final StargateAbstractBaseBE<?, ?> stargate;
    protected final Consumer<CompoundTag> onSpinStop;

    protected boolean isSpinning;
    protected double lastStableRingAngle;
    protected double lastRingVelocity;
    protected double ringRotationStart;
    protected double targetRingAngle;
    protected Boolean rotateFreely = null;
    protected float speedFactor = 1f;
    protected EnumSpinDirection direction = EnumSpinDirection.CLOCKWISE;

    // server only!
    protected CompoundTag stopSpinData;

    public ClassicSpinHelper(StargateAbstractBaseBE<?, ?> stargate, Consumer<CompoundTag> onSpinStop) {
        this.stargate = stargate;
        this.onSpinStop = onSpinStop;
    }

    @Override
    public SymbolInterface getCurrentTopSymbol(float bounds) {
        return stargate.getSymbolType().getSymbolByAngle(getRingAngle(), bounds);
    }

    @Override
    public float getRingAngle() {
        return (float) apply(stargate.getTime(), true);
    }

    @Override
    public double apply(double tick, boolean clampAngle) {
        var coef = (tick - ringRotationStart);
        double a;
        if (coef <= 0) {
            a = lastStableRingAngle;
        } else {
            a = calculate(coef);
            a = lastStableRingAngle + (a * direction.mul);
        }
        if (clampAngle) {
            while (a < 0) {
                a += 360f;
            }
            a = (a % 360f);
        }
        return a;
    }


    private float getDurationBeforeStop(double targetAngle) {
        return (float) (targetAngle / (A_ANGLE_PER_TICK * speedFactor) + (U_SPEEDUP_TIME - S_STOP_TIME) / 2);
    }

    protected double getAngleToTarget() {
        var angleDiff = (targetRingAngle - lastStableRingAngle) * direction.mul;
        while (angleDiff < MIN_ANGLE_TO_DIAL) {
            angleDiff = angleDiff + 360f;
        }
        return angleDiff;
    }

    @Override
    public float getTickToStop() {
        var speed = Math.abs(this.lastRingVelocity);
        var stopTimeCoefficient = (float) (speed / (A_ANGLE_PER_TICK * speedFactor));
        return stopTimeCoefficient * S_STOP_TIME;
    }

    @Override
    public float getDuration(double angleDiff) {
        return getDurationBeforeStop(angleDiff) + S_STOP_TIME;
    }

    protected double calculate(double tick) {
        if (!isSpinning) return 0;
        if (rotateFreely != null) {
            if (!rotateFreely) {
                var speed = (float) Math.abs(lastRingVelocity);
                var ticksToStop = getTickToStop();
                if (ticksToStop <= 0) {
                    this.lastStableRingAngle = targetRingAngle;
                    JSG.logger.warn("Speed of ring is 0 while trying to stop it!");
                    onStopped();
                    return 0;
                }
                var stopFunc = getStopFunction(speed, 0, ticksToStop, 0);
                var stopFuncRange = new MathRange(0, ticksToStop);

                if (stopFuncRange.test((float) tick))
                    return stopFunc.apply((float) tick);
                this.lastStableRingAngle = (lastStableRingAngle + (stopFunc.apply(ticksToStop) / direction.mul));
                onStopped();
                return 0;
            }
            var speedUp = getSpeedupRangedFunction(A_ANGLE_PER_TICK * speedFactor, U_SPEEDUP_TIME);
            var spinFunc = getLinearSpinFunction(A_ANGLE_PER_TICK * speedFactor);
            if (tick >= speedUp.range.end)
                return spinFunc.apply((float) tick);
            return speedUp.function.apply((float) tick);
        }

        var angleDiff = getAngleToTarget();
        float durationBeforeStop = getDurationBeforeStop(angleDiff);
        if (durationBeforeStop < U_SPEEDUP_TIME) {
            durationBeforeStop = (durationBeforeStop + S_STOP_TIME) / 2;
            float a = (float) (angleDiff / durationBeforeStop);
            var speedUp = getSpeedupRangedFunction(a, durationBeforeStop);
            var stop = getStopFunction(a, durationBeforeStop, durationBeforeStop, durationBeforeStop);
            if (new MathRange(durationBeforeStop, 2 * durationBeforeStop).test((float) tick))
                return stop.apply((float) tick);
            if (speedUp.range.test((float) tick))
                return speedUp.function.apply((float) tick);
            this.lastStableRingAngle = targetRingAngle;
            onStopped();
            return 0;
        }
        var speedUp = getSpeedupRangedFunction(A_ANGLE_PER_TICK * speedFactor, U_SPEEDUP_TIME);
        var spinFunc = getLinearSpinFunction(A_ANGLE_PER_TICK * speedFactor);
        var stopFunc = getStopFunction(A_ANGLE_PER_TICK * speedFactor, U_SPEEDUP_TIME, S_STOP_TIME, durationBeforeStop);

        var spinFuncRange = new MathRange(U_SPEEDUP_TIME, durationBeforeStop);
        var stopFuncRange = new MathRange(durationBeforeStop, durationBeforeStop + S_STOP_TIME);

        if (stopFuncRange.test((float) tick))
            return stopFunc.apply((float) tick);
        if (spinFuncRange.test((float) tick))
            return spinFunc.apply((float) tick);
        if (speedUp.range.test((float) tick))
            return speedUp.function.apply((float) tick);

        this.lastStableRingAngle = targetRingAngle;
        onStopped();
        return 0;
    }

    protected void onStopped() {
        this.isSpinning = false;
        this.rotateFreely = null;
        if (stargate.getLevel() != null && !stargate.getLevel().isClientSide) {
            stargate.getSoundManager().updateRingRollSound(false);
            stargate.playSoundEvent(StargateSoundEventEnum.RING_STOP);
            var data = stopSpinData;
            stopSpinData = null;
            this.onSpinStop.accept(Optional.ofNullable(data).orElseGet(CompoundTag::new));
            stargate.setChanged();
            stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
            sendSpinStop();
        }
    }

    @Override
    public boolean isSpinning() {
        return isSpinning;
    }

    @Override
    public float getRingVelocity() {
        return (float) (apply(stargate.getTime() + 1, false) - apply(stargate.getTime(), false));
    }

    @Override
    public boolean stopSpinning(boolean clearStopData) {
        if (clearStopData) {
            this.stopSpinData = null;
            stargate.setChanged();
        }
        if (!isSpinning() || (rotateFreely != null && !rotateFreely)) return false;
        this.lastRingVelocity = getRingVelocity();
        this.lastStableRingAngle = apply(stargate.getTime(), true);
        this.ringRotationStart = stargate.getTime();
        this.rotateFreely = false;
        stargate.setChanged();
        stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
        return true;
    }

    /**
     * Start the spinning animation of the gate's ring and spin until told to stop
     *
     * @param direction direction in which the ring should spin
     * @return true if success, otherwise false
     */
    @Override
    public boolean rotateFreely(@NotNull EnumSpinDirection direction) {
        if (isSpinning()) return false;
        this.isSpinning = true;
        updateRingSpeedFromConfig();
        this.direction = direction;
        this.ringRotationStart = stargate.getTime();
        //this.lastStableRingAngle = apply(stargate.getTime());
        this.rotateFreely = true;
        stargate.setChanged();
        stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
        if (stargate.getLevel() != null && !stargate.getLevel().isClientSide) {
            stargate.getSoundManager().updateRingRollSound(true);
            sendSpinStart();
        }
        return true;
    }

    /**
     * Start spinning the ring to get it under specified angle. Stops automatically if reaches the angle or when told to stop.
     *
     * @param angle     target angle
     * @param direction direction in which the ring should spin
     * @return true is success, otherwise false
     */
    @Override
    public Pair<Boolean, Float> moveTo(double angle, @NotNull EnumSpinDirection direction) {
        if (isSpinning()) return Pair.of(false, -1f);
        this.isSpinning = true;
        this.rotateFreely = null;
        updateRingSpeedFromConfig();
        this.direction = direction;
        this.targetRingAngle = angle;
        this.ringRotationStart = stargate.getTime();
        //this.lastStableRingAngle = apply(stargate.getTime());
        stargate.setChanged();
        stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
        if (stargate.getLevel() != null && !stargate.getLevel().isClientSide) {
            stargate.getSoundManager().updateRingRollSound(true);
            sendSpinStart();
        }
        return Pair.of(true, getDuration(getAngleToTarget()));
    }

    /**
     * Start spinning the ring to get target symbol under the top chevron. Stops automatically if reaches the symbol or when told to stop.
     *
     * @param symbol    target symbol
     * @param direction direction in which the ring should spin
     * @return true is success, otherwise false
     */
    @Override
    public Pair<Boolean, Float> moveTo(@NotNull SymbolInterface symbol, @NotNull EnumSpinDirection direction) {
        return moveTo(symbol.getAngle(), direction);
    }

    /**
     * Start spinning the ring to get target symbol under the top chevron. Stops automatically if reaches the symbol or when told to stop.<br>
     * Direction of the spin is the opposite of the previous spin.
     *
     * @param symbol target symbol
     * @return true is success, otherwise false
     */
    @Override
    public Pair<Boolean, Float> moveTo(@NotNull SymbolInterface symbol) {
        var direction = (this.direction != null ? this.direction.opposite() : EnumSpinDirection.CLOCKWISE);
        return moveTo(symbol.getAngle(), direction);
    }

    public void setSpinStopData(@Nullable CompoundTag compound) {
        this.stopSpinData = compound;
        stargate.setChanged();
    }

    @Override
    public Pair<Boolean, Float> moveToAndEngage(@NotNull SymbolInterface symbolInterface, boolean isFinal, boolean noEnergy, boolean ignoreMaxChevrons) {
        var data = new CompoundTag();
        data.putInt("symbol", symbolInterface.getId());
        data.putBoolean("isFinal", isFinal);
        data.putBoolean("noEnergy", noEnergy);
        data.putBoolean("ignoreMaxChevrons", ignoreMaxChevrons);
        var r = moveTo(symbolInterface);
        if (r.first())
            setSpinStopData(data);
        return r;
    }

    protected void updateRingSpeedFromConfig() {
        if (stargate instanceof StargateClassicBaseBE<?> configBe) {
            speedFactor = configBe.getSpeedFactor();
        }
    }

    @Override
    public void tick(@NotNull Level level) {
        if (!level.isClientSide && isSpinning())
            // update the spin state on server too
            apply(level.getGameTime(), false);
    }

    public void onLoad(@NotNull Level level) {
        if (level.isClientSide)
            stargate.getStateManager().requestState(JSGStateTypes.SPIN_STATE.get());
        else
            stargate.getStateManager().sendState(JSGStateTypes.SPIN_STATE.get(), this);
    }

    private static MathRangedFunction getSpeedupRangedFunction(float a, float u) {
        return new MathRangedFunction(new MathRange(0, u), new MathFunctionQuadratic(a / (2 * u), 0, 0));
    }

    private static MathFunctionQuadratic getStopFunction(float a, float u, float s, float x0) {
        return new MathFunctionQuadratic(-a / (2 * s), a + (a * x0 / s), -(a * u / 2 + a * x0 * x0 / (2 * s)));
    }

    private static MathFunctionLinear getLinearSpinFunction(float a) {
        return new MathFunctionLinear(a, -a * U_SPEEDUP_TIME / 2);
    }

    protected void sendSpinStart() {
        if (stargate.getLevel() == null || stargate.getLevel().isClientSide()) return;
        StargateComputerEvents.SPIN_START.apply(direction, speedFactor).sendVia(stargate);
        stargate.getListenerHandler().gateRingSpin();
    }

    protected void sendSpinStop() {
        if (stargate.getLevel() == null || stargate.getLevel().isClientSide()) return;
        StargateComputerEvents.SPIN_STOP.apply(getCurrentTopSymbol()).sendVia(stargate);
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putBoolean("isSpinning", isSpinning);
        compound.putDouble("lastRingVelocity", lastRingVelocity);
        compound.putDouble("lastStableRingAngle", lastStableRingAngle);
        compound.putDouble("ringRotationStart", ringRotationStart);
        compound.putDouble("targetRingAngle", targetRingAngle);
        if (rotateFreely != null)
            compound.putBoolean("rotateFreely", rotateFreely);
        compound.putFloat("speedFactor", speedFactor);
        compound.putInt("direction", direction.ordinal());
        if (stopSpinData != null)
            compound.put("stopSpinData", stopSpinData);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        isSpinning = compound.getBoolean("isSpinning");
        lastRingVelocity = compound.getDouble("lastRingVelocity");
        lastStableRingAngle = compound.getDouble("lastStableRingAngle");
        ringRotationStart = compound.getDouble("ringRotationStart");
        targetRingAngle = compound.getDouble("targetRingAngle");
        if (compound.contains("rotateFreely"))
            rotateFreely = compound.getBoolean("rotateFreely");
        else
            rotateFreely = null;
        speedFactor = compound.getFloat("speedFactor");
        direction = EnumSpinDirection.values()[compound.getInt("direction")];
        if (compound.contains("stopSpinData"))
            stopSpinData = compound.getCompound("stopSpinData");
        else
            stopSpinData = null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isSpinning);
        buf.writeDouble(lastRingVelocity);
        buf.writeDouble(lastStableRingAngle);
        buf.writeDouble(ringRotationStart);
        buf.writeDouble(targetRingAngle);
        if (rotateFreely != null) {
            buf.writeBoolean(true);
            buf.writeBoolean(rotateFreely);
        } else
            buf.writeBoolean(false);
        buf.writeFloat(speedFactor);
        buf.writeInt(direction.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isSpinning = buf.readBoolean();
        lastRingVelocity = buf.readDouble();
        lastStableRingAngle = buf.readDouble();
        ringRotationStart = buf.readDouble();
        targetRingAngle = buf.readDouble();
        if (buf.readBoolean())
            rotateFreely = buf.readBoolean();
        else
            rotateFreely = null;
        speedFactor = buf.readFloat();
        direction = EnumSpinDirection.values()[buf.readInt()];
    }

    public void from(ClassicSpinHelper state) {
        isSpinning = state.isSpinning;
        lastRingVelocity = state.lastRingVelocity;
        lastStableRingAngle = state.lastStableRingAngle;
        ringRotationStart = state.ringRotationStart;
        targetRingAngle = state.targetRingAngle;
        rotateFreely = state.rotateFreely;
        speedFactor = state.speedFactor;
        direction = state.direction;
    }
}
