package dev.tauri.jsg.api.stargate.animation;

import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.util.ITickable;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public interface ISpinHelper extends INBTSerializable<CompoundTag>, ITickable {
    /**
     * Gets nearest symbol from the top that is in maximum 5 degrees away from the top
     *
     * @return symbol under the top chevron or null if there is none in maximum 5 degrees away from the top
     */
    default SymbolInterface getCurrentTopSymbol() {
        return getCurrentTopSymbol(5);
    }

    /**
     * Gets nearest symbol from the top
     *
     * @param bounds range to search in - in degrees
     * @return symbol in the bounds under the top chevron or null if there is none in this bounds
     */
    SymbolInterface getCurrentTopSymbol(float bounds);

    /**
     * Calculates the current ring angle
     *
     * @param tick level tick + partial ticks if on client
     * @return angle if the ring
     */
    float apply(double tick);

    /**
     * @return stop animation length in ticks relative to current ring speed
     */
    float getTickToStop();

    float getDuration(double angleDiff);

    /**
     * @return true if the ring is spinning, otherwise false
     */
    boolean isSpinning();

    /**
     * @return ring spin velocity (can be negative)
     */
    float getRingVelocity();

    /**
     * Stop spinning of the ring and play stop animation
     *
     * @return true if success, otherwise false
     */
    boolean stopSpinning(boolean clearStopData);

    /**
     * Start the spinning animation of the gate's ring and spin until told to stop
     *
     * @param direction direction in which the ring should spin
     * @return true if success, otherwise false
     */
    boolean rotateFreely(@NotNull EnumSpinDirection direction);

    /**
     * Start spinning the ring to get it under specified angle. Stops automatically if reaches the angle or when told to stop.
     *
     * @param angle     target angle
     * @param direction direction in which the ring should spin
     * @return true is success, otherwise false
     */
    Pair<Boolean, Float> moveTo(double angle, @NotNull EnumSpinDirection direction);

    /**
     * Start spinning the ring to get target symbol under the top chevron. Stops automatically if reaches the symbol or when told to stop.
     *
     * @param symbol    target symbol
     * @param direction direction in which the ring should spin
     * @return true is success, otherwise false
     */
    Pair<Boolean, Float> moveTo(@NotNull SymbolInterface symbol, @NotNull EnumSpinDirection direction);

    /**
     * Start spinning the ring to get target symbol under the top chevron. Stops automatically if reaches the symbol or when told to stop.<br>
     * Direction of the spin is the opposite of the previous spin.
     *
     * @param symbol target symbol
     * @return true is success, otherwise false
     */
    Pair<Boolean, Float> moveTo(@NotNull SymbolInterface symbol);

    Pair<Boolean, Float> moveToAndEngage(@NotNull SymbolInterface symbolInterface, boolean isFinal, boolean noEnergy, boolean ignoreMaxChevrons);
}
