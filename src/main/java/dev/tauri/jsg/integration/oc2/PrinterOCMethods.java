package dev.tauri.jsg.integration.oc2;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.blockentity.PrinterBE;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.AbstractOCMethods;
import li.cil.oc2.api.bus.device.object.Callback;

public class PrinterOCMethods extends AbstractOCMethods<PrinterBE> {
    public PrinterOCMethods(ComputerDeviceProvider printerTile) {
        super((PrinterBE) printerTile, OCDevices.PRINTER);
    }

    @SuppressWarnings("unused")
    @Callback(name = "getJSGVersion")
    public final Object[] getJSGVersion() {
        return new Object[]{JSG.MOD_VERSION};
    }

    @SuppressWarnings("unused")
    @Callback(name = "moveCursor")
    public Object[] moveCursor() {
        return deviceTile.buttonClickPC(0, false);
    }

    @SuppressWarnings("unused")
    @Callback(name = "switchSymbolType")
    public Object[] switchSymbolType() {
        return deviceTile.buttonClickPC(0, true);
    }

    @SuppressWarnings("unused")
    @Callback(name = "moveToNextSymbol")
    public Object[] moveToNextSymbol() {
        return deviceTile.buttonClickPC(1, false);
    }

    @SuppressWarnings("unused")
    @Callback(name = "moveToPrevSymbol")
    public Object[] moveToPrevSymbol() {
        return deviceTile.buttonClickPC(2, false);
    }

    @SuppressWarnings("unused")
    @Callback(name = "toggleSymbol")
    public Object[] toggleSymbol() {
        return deviceTile.buttonClickPC(1, true);
    }

    @SuppressWarnings("unused")
    @Callback(name = "print")
    public Object[] print() {
        return deviceTile.buttonClickPC(3, false);
    }

    @SuppressWarnings("unused")
    @Callback(name = "getInkStatus")
    public boolean getInkStatus() {
        return !deviceTile.noInk();
    }

    @SuppressWarnings("unused")
    @Callback(name = "getInkStatus")
    public boolean isBusy() {
        return deviceTile.printStarted > 0;
    }

    @SuppressWarnings("unused")
    @Callback(name = "getSetAddress")
    public String getSetAddress() {
        return deviceTile.address.toString();
    }

    @SuppressWarnings("unused")
    @Callback(name = "getEnabledSymbols")
    public Integer[] getEnabledSymbols() {
        return deviceTile.symbolsToPrint.toArray(new Integer[0]);
    }

    @SuppressWarnings("unused")
    @Callback(name = "getOriginId")
    public String getOriginId() {
        var origin = deviceTile.origin;
        if (origin == null) origin = deviceTile.address.getSymbolType().getPointOfOriginType().getDefaultPoO();
        if (origin == null) return null;
        return origin.id.getNamespace() + ":" + origin.forType.getPoONamespaceIdentifier() + "/" + origin.id.getPath();
    }

    @SuppressWarnings("unused")
    @Callback(name = "getCursorPos")
    public int getCursorPos() {
        return deviceTile.editPos;
    }
}
