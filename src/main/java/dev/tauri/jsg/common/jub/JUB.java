package dev.tauri.jsg.common.jub;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JUB {
    private final List<JUBDevice> devices;
    public final UUID uuid;

    public JUB() {
        devices = new ArrayList<>();
        uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JUB otherJub)) return false;
        return this.uuid.equals(otherJub.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }


    public List<JUBDevice> getConnectedDevices() {
        return new ArrayList<>(devices);
    }

    public void invalidate() {
        getConnectedDevices().forEach(d -> d.disconnectFromBus(true));
    }

    public void deviceConnected(JUBDevice device, boolean silent) {
        if (devices.contains(device)) return;
        devices.add(device);
        getConnectedDevices().forEach(JUBDevice::onChanged);
        sendPacket("connected", device, null, device);
    }

    public void deviceDisconnected(JUBDevice device, boolean silent) {
        if (!devices.contains(device)) return;
        devices.remove(device);
        device.onChanged();
        getConnectedDevices().forEach(JUBDevice::onChanged);
        sendPacket("disconnected", device, null, device);
    }

    @NotNull
    public JUB merge(@NotNull JUB bus) {
        if (bus == this) return this;
        var copy = new ArrayList<>(devices);
        copy.forEach(d -> {
            if (d.getBus() != bus)
                d.connectToBus(bus);
        });
        return bus;
    }

    /**
     * @param name   name of the packet
     * @param data   data of the packet
     * @param target can be null -> broadcast
     */
    public void sendPacket(String name, Object data, @Nullable JUBDevice target, JUBDevice sender) {
        if (target == null) {
            devices.forEach(d -> d.packetReceived(name, data, sender));
            return;
        }
        if (!devices.contains(target)) return;
        target.packetReceived(name, data, sender);
    }
}
