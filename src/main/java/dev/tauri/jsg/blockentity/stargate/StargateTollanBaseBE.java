package dev.tauri.jsg.blockentity.stargate;

import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.multistructure.mergehelper.StargateTollanMergeHelper;
import dev.tauri.jsg.registry.JSGBlockEntities;
import dev.tauri.jsg.renderer.stargate.StargateMilkyWayRendererState;
import dev.tauri.jsg.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.stargate.manager.dialing.StargateTollanDialingManager;
import dev.tauri.jsg.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.stargate.manager.state.StargateTollanStateManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StargateTollanBaseBE extends StargateMilkyWayBaseBE {
    public StargateTollanBaseBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.STARGATE_TOLLAN_BASE_BE.get(), pos, state);
    }

    @Override
    public void generateMergeHelper() {
        // setup merge helper - client and server
        mergeHelper = new StargateTollanMergeHelper(this);
        mergeHelper.horizontalFacing = getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        mergeHelper.verticalFacing = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));
        mergeHelper.basePos = getBlockPos();
    }

    @Override
    public IPointOfOriginType getPointOfOriginType() {
        return StargateTypes.MILKYWAY.get();
    }

    @Override
    public StargateType<?> getStargateType() {
        return StargateTypes.TOLLAN.get();
    }

    @Override
    public StargateAbstractStateManager<StargateMilkyWayBaseBE, StargateMilkyWayRendererState> createStateManager() {
        return new StargateTollanStateManager(this);
    }

    @Override
    public StargateAbstractDialingManager<?> createDialingManager() {
        return new StargateTollanDialingManager(this);
    }
}
