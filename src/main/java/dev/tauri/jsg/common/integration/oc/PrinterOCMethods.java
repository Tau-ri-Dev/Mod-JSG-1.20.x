package dev.tauri.jsg.common.integration.oc;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.blockentity.PrinterBE;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc.methods.AbstractOCMethods;
import li.cil.oc.api.machine.Callback;

public class PrinterOCMethods extends AbstractOCMethods<PrinterBE> {
    public PrinterOCMethods(ComputerDeviceProvider printerTile) {
        super((PrinterBE) printerTile, OCDevices.PRINTER);
    }

    @SuppressWarnings("unused")
    @Callback(value = "getJSGVersion")
    public final Object[] getJSGVersion() {
        return new Object[]{JSG.MOD_VERSION};
    }

    @SuppressWarnings("unused")
    @Callback(value = "moveCursor")
    public Object[] moveCursor() {
        return deviceTile.buttonClickPC(0, false);
    }

    @SuppressWarnings("unused")
    @Callback(value = "switchSymbolType")
    public Object[] switchSymbolType() {
        return deviceTile.buttonClickPC(0, true);
    }

    @SuppressWarnings("unused")
    @Callback(value = "moveToNextSymbol")
    public Object[] moveToNextSymbol() {
        return deviceTile.buttonClickPC(1, false);
    }

    @SuppressWarnings("unused")
    @Callback(value = "moveToPrevSymbol")
    public Object[] moveToPrevSymbol() {
        return deviceTile.buttonClickPC(2, false);
    }

    @SuppressWarnings("unused")
    @Callback(value = "toggleSymbol")
    public Object[] toggleSymbol() {
        return deviceTile.buttonClickPC(1, true);
    }

    @SuppressWarnings("unused")
    @Callback(value = "print")
    public Object[] print() {
        return deviceTile.buttonClickPC(3, false);
    }

    @SuppressWarnings("unused")
    @Callback(value = "getInkStatus")
    public boolean getInkStatus() {
        return !deviceTile.noInk();
    }

    @SuppressWarnings("unused")
    @Callback(value = "getInkStatus")
    public boolean isBusy() {
        return deviceTile.printStarted > 0;
    }

    @SuppressWarnings("unused")
    @Callback(value = "getSetAddress")
    public String getSetAddress() {
        return deviceTile.address.toString();
    }

    @SuppressWarnings("unused")
    @Callback(value = "getEnabledSymbols")
    public Integer[] getEnabledSymbols() {
        return deviceTile.symbolsToPrint.toArray(new Integer[0]);
    }

    @SuppressWarnings("unused")
    @Callback(value = "getOriginId")
    public String getOriginId() {
        var origin = deviceTile.origin;
        if (origin == null) origin = deviceTile.address.getSymbolType().getPointOfOriginType().getDefaultPoO();
        if (origin == null) return null;
        return origin.id.getNamespace() + ":" + origin.forType.getPoONamespaceIdentifier() + "/" + origin.id.getPath();
    }

    @SuppressWarnings("unused")
    @Callback(value = "getCursorPos")
    public int getCursorPos() {
        return deviceTile.editPos;
    }
}
