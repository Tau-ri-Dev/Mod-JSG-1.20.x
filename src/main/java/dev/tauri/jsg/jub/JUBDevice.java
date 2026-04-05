package dev.tauri.jsg.jub;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.capability.JSGCapabilities;
import dev.tauri.jsg.core.common.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class JUBDevice {
    public abstract void onChanged();

    protected JUB bus;
    @NotNull
    public final BlockEntity be;

    public JUBDevice(@NotNull BlockEntity be) {
        this.be = be;
    }

    public JUB getBus() {
        return this.bus;
    }

    public BlockPos getBlockPos() {
        return this.be.getBlockPos();
    }

    public void disconnectFromBus() {
        disconnectFromBus(false);
    }

    public void disconnectFromBus(boolean silent) {
        if (this.bus != null)
            this.bus.deviceDisconnected(this, silent);
        this.bus = null;
        JSG.logger.info("Disconnected from bus!");
    }

    public void connectToBus(@NotNull JUB bus) {
        connectToBus(bus, false);
    }

    public void connectToBus(@NotNull JUB bus, boolean silent) {
        if (this.bus != null)
            this.bus.deviceDisconnected(this, silent);
        this.bus = bus;
        bus.deviceConnected(this, silent);
        JSG.logger.info("Connected to bus!");
    }

    /**
     * @param name   name of the packet
     * @param data   data of the packet
     * @param target can be null -> broadcast
     */
    protected void sendPacket(String name, Object data, @Nullable JUBDevice target) {
        var bus = getBus();
        if (bus == null) return;
        bus.sendPacket(name, data, target, this);
    }

    /**
     * @param name   name of the packet
     * @param data   data of the packet
     * @param sender source of the packet
     */
    protected abstract void packetReceived(String name, Object data, JUBDevice sender);

    @Nullable
    public IEnergyStorage getEnergyStorage() {
        return null;
    }


    public void updateBusDevices(Level level, BlockPos posChanged) {
        var changedBE = level.getBlockEntity(posChanged);
        var wasRemoved = (changedBE == null || changedBE.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS).resolve().isEmpty());
        if (wasRemoved) {
            var deviceBus = getBus();
            if (deviceBus == null) return;


            for (var d : deviceBus.getConnectedDevices()) {
                if (d.getBlockPos().equals(posChanged)) {
                    deviceBus.invalidate();

                    var jubNew = new JUB();
                    connectToBus(jubNew, true);
                    updateBusConnection(this, new HashMap<>(), jubNew);
                    break;
                }
            }
        } else {
            JSG.logger.info(BlockHelper.blockPosToBetterString(posChanged));
            var changedBECap = changedBE.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS).resolve().get();
            if (getBus() == null) {
                if (changedBECap.getBus() == null) {
                    changedBECap.connectToBus(new JUB());
                    connectToBus(changedBECap.getBus());
                } else {
                    connectToBus(changedBECap.getBus());
                }
            } else {
                if (changedBECap.getBus() == null) {
                    changedBECap.connectToBus(getBus());
                } else if (!getBus().equals(changedBECap.getBus())) {
                    connectToBus(changedBECap.getBus());
                    //getBus().merge(changedBECap.getBus());
                }
            }
        }
    }

    @NotNull
    private HashMap<BlockPos, JUBDevice> updateBusConnection(JUBDevice source, HashMap<BlockPos, JUBDevice> map, JUB newBus) {
        var level = be.getLevel();
        if (level == null) return map;
        for (var dir : Direction.values()) {
            var newPos = source.getBlockPos().offset(dir.getNormal());
            if (map.containsKey(newPos)) continue;
            var newBE = level.getBlockEntity(newPos);
            if (newBE == null) continue;
            var capOpt = newBE.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS).resolve();
            if (capOpt.isEmpty()) continue;

            if (capOpt.get().getBus() != newBus)
                capOpt.get().connectToBus(newBus, true);

            map.put(newPos, capOpt.get());
            updateBusConnection(capOpt.get(), map, newBus);
        }
        return map;
    }
}
