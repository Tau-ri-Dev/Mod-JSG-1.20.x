package dev.tauri.jsg.common.integration.cctweaked;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.blockentity.PrinterBE;
import dev.tauri.jsg.core.common.integration.cctweaked.CCTweakedHelper;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.AbstractCCMethods;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;

public class PrinterCCMethods extends AbstractCCMethods<PrinterBE> {
    public PrinterCCMethods(BlockEntity printerTile) {
        super((PrinterBE) printerTile, CCDevices.PRINTER);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] getJSGVersion() {
        return new Object[]{JSG.MOD_VERSION};
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] moveCursor() {
        return deviceTile.buttonClickPC(0, false);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] switchSymbolType() {
        return deviceTile.buttonClickPC(0, true);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] moveToNextSymbol() {
        return deviceTile.buttonClickPC(1, false);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] moveToPrevSymbol() {
        return deviceTile.buttonClickPC(2, false);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] toggleSymbol() {
        return deviceTile.buttonClickPC(1, true);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] print() {
        return deviceTile.buttonClickPC(3, false);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Object[] printCustomText(ILuaContext ctx, IArguments args) throws LuaException {
        var text = new LinkedList<String>();
        text.addLast(args.getString(0));
        var table = CCTweakedHelper.getCorrectlyOrderedTableValues(args.getTable(1));
        for (var line : table) {
            text.addLast(line.toString());
        }
        return deviceTile.buttonClickPC(3, false, text);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final boolean getInkStatus() {
        return !deviceTile.noInk();
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final boolean isBusy() {
        return deviceTile.printStarted > 0;
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final String getSetAddress() {
        return deviceTile.address.toString();
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final Integer[] getEnabledSymbols() {
        return deviceTile.symbolsToPrint.toArray(new Integer[0]);
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final String getOriginId() {
        var origin = deviceTile.origin;
        if (origin == null) origin = deviceTile.address.getSymbolType().getPointOfOriginType().getDefaultPoO();
        if (origin == null) return null;
        return origin.id.getNamespace() + ":" + origin.forType.getPoONamespaceIdentifier() + "/" + origin.id.getPath();
    }

    @SuppressWarnings("unused")
    @LuaFunction(mainThread = true)
    public final int getCursorPos() {
        return deviceTile.editPos;
    }
}
