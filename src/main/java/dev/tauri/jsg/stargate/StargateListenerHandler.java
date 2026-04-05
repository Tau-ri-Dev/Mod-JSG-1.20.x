package dev.tauri.jsg.stargate;

import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.api.stargate.listener.IStargateListener;
import dev.tauri.jsg.api.stargate.listener.IStargateListenerHandler;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.stargate.manager.AbstractStargateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class StargateListenerHandler extends AbstractStargateManager<StargateAbstractBaseBE<?, ?>> implements IStargateListenerHandler {
    private final List<BlockPos> LISTENERS = new ArrayList<>();

    /**
     * You should not use this constructor by yourself!
     * This is only to construct listener handler for each gate
     */
    public StargateListenerHandler(StargateAbstractBaseBE<?, ?> stargate) {
        super(stargate);
    }

    @Override
    public void addListener(IStargateListener listener) {
        LISTENERS.add(listener.getListenerBlockPos());
    }

    @Override
    public void removeListener(IStargateListener listener) {
        LISTENERS.remove(listener.getListenerBlockPos());
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        var ent = new CompoundTag();
        ent.putInt("size", LISTENERS.size());
        int i = 0;
        for (var pos : LISTENERS) {
            ent.putLong("pos_" + i, pos.asLong());
            i++;
        }
        compound.put("listeners", ent);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        LISTENERS.clear();
        var ent = compound.getCompound("listeners");
        var size = ent.getInt("size");
        for (int i = 0; i < size; i++) {
            LISTENERS.add(BlockPos.of(ent.getLong("pos_" + i)));
        }
    }

    private void mapListener(BlockPos pos, Consumer<IStargateListener> runnable) {
        mapListener(pos, (l) -> {
            runnable.accept(l);
            return true;
        });
    }

    private boolean mapListener(BlockPos pos, Predicate<IStargateListener> runnable) {
        var level = stargate.getLevel();
        if (level == null) return false;
        var entity = level.getBlockEntity(pos);
        var block = level.getBlockState(pos).getBlock();
        if (entity instanceof IStargateListener listener)
            return runnable.test(listener);
        else if (block instanceof IStargateListener listener)
            return runnable.test(listener);
        return false;
    }


    @Override
    public void gateOpen() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::gateOpen);
    }

    @Override
    public void gateClose(StargateClosedReasonEnum reason) {
        for (var p : LISTENERS)
            mapListener(p, (l) -> {
                l.gateClose(reason);
            });
    }

    @Override
    public void gateDisconnect() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::gateDisconnect);
    }

    @Override
    public void gateReset() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::gateReset);
    }

    @Override
    public void gateStateChanged(EnumStargateState oldState, EnumStargateState newState) {
        for (var p : LISTENERS)
            mapListener(p, (l) -> {
                l.gateStateChanged(oldState, newState);
            });
    }

    @Override
    public void gateSymbolEngage(SymbolInterface symbol) {
        for (var p : LISTENERS)
            mapListener(p, (l) -> {
                l.gateSymbolEngage(symbol);
            });
    }

    @Override
    public void gateFail(StargateOpenResult result) {
        for (var p : LISTENERS)
            mapListener(p, (l) -> {
                l.gateFail(result);
            });
    }

    @Override
    public void gateIncoming(int dialedAddressSize) {
        for (var p : LISTENERS)
            mapListener(p, (l) -> {
                l.gateIncoming(dialedAddressSize);
            });
    }

    @Override
    public void gateBeginDial() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::gateBeginDial);
    }

    @Override
    public void irisCloses() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::irisCloses);
    }

    @Override
    public void irisOpens() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::irisOpens);
    }

    @Override
    public void irisHit() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::irisHit);
    }

    @Override
    public boolean receiveIDC(CodeSender sender, String code) {
        var result = false;
        for (var p : LISTENERS)
            result |= mapListener(p, (l) -> {
                return l.receiveIDC(sender, code);
            });
        return result;
    }

    @Override
    public void gateRingSpin() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::gateRingSpin);
    }

    @Override
    public void gateDialAbort() {
        for (var p : LISTENERS)
            mapListener(p, IStargateListener::gateDialAbort);
    }
}
