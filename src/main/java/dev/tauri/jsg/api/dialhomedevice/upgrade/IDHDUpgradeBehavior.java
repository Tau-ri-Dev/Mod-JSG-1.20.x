package dev.tauri.jsg.api.dialhomedevice.upgrade;

import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Holds behavior of a DHD upgrade (usually crystal)
 *
 * @author VojtechSin
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDHDUpgradeBehavior extends ITickable, INBTSerializable<CompoundTag> {
    /**
     * Called when upgrade that holds this behavior is attached to the DHD
     *
     * @param level the level the dhd is on
     */
    void onAttach(Level level);

    /**
     * Called when upgrade that holds this behavior is detached from the DHD
     *
     * @param level the level the dhd is on
     */
    void onDetach(Level level);

    /**
     * Modifies energy that DHD generates per one mB of refined naquadah
     *
     * @param baseEnergy current energy that is going to be generated
     * @return modified energy - return baseEnergy to make no modifications
     */
    double modifyEnergyPerNaquadah(double baseEnergy);

    /**
     * Hooks to symbol activation event of the dhd.
     * Event is called then symbol is activated on client side and press sound is played.
     *
     * @param symbol the symbol that is being activated
     * @return false to cancel the event
     */
    boolean onSymbolActivated(SymbolInterface symbol);

    /**
     * Hooks to button press event of the dhd. Can cancel this event.
     *
     * @param symbol the symbol that is being pressed
     * @param player if played triggered this event, then it's the executor, otherwise null if event has been triggered by something else
     * @param force  used on pegasus gate to abort dialing fast while still dialing symbols in buffer
     * @return false to cancel the event
     */
    boolean onSymbolButtonPushed(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force);

    /**
     * Modifies maximum symbols that can be dialed using this dhd.
     *
     * @param symbolsCount current max symbols count
     * @param dialedAddress the dialed address of the linked stargate or null
     * @return modified symbols count - return symbolsCount to make no modifications
     */
    int modifyMaxSymbolsInAddress(int symbolsCount, @Nullable StargateAddressDynamic dialedAddress);

    class Builder {
        public IDHDUpgradeBehavior build() {
            return new DHDUpgradeBehaviorImpl();
        }
    }
}
