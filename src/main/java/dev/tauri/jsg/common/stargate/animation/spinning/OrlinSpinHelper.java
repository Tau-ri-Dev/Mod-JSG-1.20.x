package dev.tauri.jsg.common.stargate.animation.spinning;

import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.common.blockentity.stargate.StargateOrlinBaseBE;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class OrlinSpinHelper extends ClassicSpinHelper {
    public OrlinSpinHelper(StargateOrlinBaseBE stargate) {
        super(stargate, (data) -> {
        });
    }

    @Override
    public SymbolInterface getCurrentTopSymbol(float bounds) {
        return null;
    }

    @Override
    public float getRingAngle() {
        return -1;
    }

    @Override
    public double apply(double tick, boolean clampAngle) {
        return 0;
    }

    @Override
    public float getTickToStop() {
        return 0;
    }

    @Override
    public float getDuration(double angleDiff) {
        return 0;
    }

    @Override
    public boolean isSpinning() {
        return false;
    }

    @Override
    public float getRingVelocity() {
        return 0;
    }

    @Override
    public boolean stopSpinning(boolean clearStopData) {
        return false;
    }

    @Override
    public boolean rotateFreely(@NotNull EnumSpinDirection direction) {
        return false;
    }

    @Override
    public Pair<Boolean, Float> moveTo(double angle, @NotNull EnumSpinDirection direction) {
        return Pair.of(false, -1f);
    }

    @Override
    public Pair<Boolean, Float> moveTo(@NotNull SymbolInterface symbol, @NotNull EnumSpinDirection direction) {
        return Pair.of(false, -1f);
    }

    @Override
    public Pair<Boolean, Float> moveTo(@NotNull SymbolInterface symbol) {
        return Pair.of(false, -1f);
    }

    @Override
    public Pair<Boolean, Float> moveToAndEngage(@NotNull SymbolInterface symbolInterface, boolean isFinal, boolean noEnergy, boolean ignoreMaxChevrons) {
        return Pair.of(false, -1f);
    }

    @Override
    public void tick(@NotNull Level level) {
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
    }
}
