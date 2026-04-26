package dev.tauri.jsg.common.block.stargate.redstone;

import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public class StargateRedstoneDialerI extends AbstractStargateRedstoneIO {

    @Override
    public boolean shouldUpdateNeighbours(BlockState state, ServerLevel level, BlockPos pos, StargateClassicBaseBE<?> gateTile) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getOutputSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction, StargateClassicBaseBE<?> gateTile) {
        return 0;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void processInputSignal(BlockState state, BlockGetter level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, StargateClassicBaseBE<?> gateTile) {
        if (!(level instanceof Level l) || l.isClientSide) return;
        int symbolId = signals.get(Direction.WEST) * 16 + signals.get(Direction.NORTH);
        var symbol = gateTile.getSymbolType().valueOf(symbolId);
        boolean dialByDHD = signals.get(Direction.DOWN) > 0;
        boolean engageGate = signals.get(Direction.UP) > 0;
        boolean addSymbol = signals.get(Direction.EAST) > 0;
        var dialer = gateTile.getDialingManager();
        var sgState = dialer.getStargateState();
        if (addSymbol) {
            if (symbol != null && sgState.idle() && (symbol.isValidForAddress() || symbol.origin())) {
                if (dialByDHD) {
                    if (dialer.canAddSymbol(symbol, false).ok())
                        dialer.engageSymbolDHD(symbol, false, false);
                } else
                    dialer.engageSymbolBySpin(symbol, false, false);
            }
        } else if (engageGate) {
            if (sgState.initiating())
                dialer.attemptClose(StargateClosedReasonEnum.REQUESTED);
            else if (sgState.dialing())
                dialer.abortDialingSequence();
            else
                dialer.attemptOpenDialed();
        }
    }
}
