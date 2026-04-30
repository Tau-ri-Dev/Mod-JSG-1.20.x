package dev.tauri.jsg.common.block.stargate;

import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.container.StargateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public abstract class StargateClassicBaseBlock extends StargateAbstractBaseBlock {

    public StargateClassicBaseBlock() {
        super(StargateAbstractBaseBlock.STARGATE_BASE_PROPS);
    }

    @Override
    protected boolean showGateInfo(Player player, InteractionHand hand, Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof StargateAbstractBaseBE<?, ?> gate) {
            if (!gate.isMerged()) return false;

            if (player instanceof ServerPlayer sp) {
                NetworkHooks.openScreen(sp, new SimpleMenuProvider((id, pInv, p) -> new StargateContainer(id, pInv, gate), Component.empty()), pos);
                //sp.openMenu(new SimpleMenuProvider((id, pInv, p) -> new StargateContainer(id, pInv, gate), Component.empty()), pos);
                return true;
            }
        }
        return false;
    }
}
