package dev.tauri.jsg.stargate.manager.state;

import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.renderer.stargate.StargateClassicRendererState;
import dev.tauri.jsg.screen.inventory.stargate.StargateContainerGuiState;
import dev.tauri.jsg.screen.inventory.stargate.StargateContainerGuiUpdate;
import dev.tauri.jsg.stargate.animation.IrisAnimationState;
import dev.tauri.jsg.stargate.animation.spinning.ClassicSpinHelper;
import dev.tauri.jsg.state.stargate.StargateBiomeOverrideState;
import dev.tauri.jsg.state.stargate.StargateRendererActionState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class StargateClassicStateManager<SG extends StargateClassicBaseBE<?>, S extends StargateClassicRendererState> extends StargateAbstractStateManager<SG, S> {
    public StargateClassicStateManager(SG stargate) {
        super(stargate);
    }

    @Override
    public void onLoad(@NotNull Level level) {
        super.onLoad(level);
        if (level.isClientSide())
            requestState(JSGStateTypes.SPIN_STATE.get());
    }

    @Override
    public void tick(@NotNull Level level) {
        super.tick(level);
    }

    @Override
    protected StargateClassicRendererState.StargateClassicRendererStateBuilder getRendererStateServer() {
        var b = new StargateClassicRendererState.StargateClassicRendererStateBuilder(super.getRendererStateServer())
                .setSymbolType(stargate.getSymbolType()).setBiomeOverride(stargate.getBiomeOverlayWithOverride());
        return stargate.getIrisManager().processRenderState(stargate.getDialingManager().processRenderState(b));
    }

    @Override
    public void setState(StateType stateType, State state) {
        stateType.stateExecutor()
                .tryType(CoreStateTypes.RENDERER_UPDATE.get(), () -> {
                    if (getRendererStateClient() == null) return;
                    StargateRendererActionState gateActionState = (StargateRendererActionState) state;

                    switch (gateActionState.action) {
                        case IRIS_UPDATE:
                            getRendererStateClient().irisState = gateActionState.irisState;
                            getRendererStateClient().irisType = gateActionState.irisType;
                            if (gateActionState.irisState == EnumIrisState.CLOSING || gateActionState.irisState == EnumIrisState.OPENING) {
                                getRendererStateClient().irisAnimation = gateActionState.irisAnimation;
                            }
                            break;
                        case HEAT_UPDATE:
                            getRendererStateClient().irisHeat = gateActionState.irisHeat;
                            getRendererStateClient().gateHeat = gateActionState.gateHeat;
                            stargate.irisHeat = gateActionState.irisHeat;
                            stargate.gateHeat = gateActionState.gateHeat;
                            stargate.setChanged();
                            break;
                        default:
                            break;
                    }
                })
                .tryType(JSGStateTypes.IRIS_ANIMATION, () -> stargate.getIrisManager().setIrisAnimationState((IrisAnimationState) state))
                .tryType(CoreStateTypes.GUI_STATE, () -> {
                    StargateContainerGuiState guiState = (StargateContainerGuiState) state;
                    stargate.gateAddressMapClient = guiState.gateAdddressMap;
                    stargate.setConfig(guiState.config);
                    stargate.setChanged();
                })
                .tryType(CoreStateTypes.GUI_UPDATE, () -> {
                    StargateContainerGuiUpdate guiUpdate = (StargateContainerGuiUpdate) state;
                    stargate.getEnergyManager().getStorage().setEnergyStoredInternally(guiUpdate.energyStored);
                    stargate.getEnergyManager().setSecondsToClose((long) guiUpdate.secondsToClose);
                    stargate.getEnergyManager().setTransferredLastTick(guiUpdate.transferredLastTick);
                    stargate.getIrisManager().setIrisMode(guiUpdate.irisMode);
                    stargate.getIrisManager().setIrisCode(guiUpdate.irisCode);
                    stargate.getDialingManager().getConnection().setStatusSince(guiUpdate.stargateConnection.getSince());
                    stargate.getDialingManager().getConnection().setStatusUnsafe(guiUpdate.stargateConnection.getStatus());
                    stargate.gateHeat = guiUpdate.gateTemp;
                    stargate.irisHeat = guiUpdate.irisTemp;
                    stargate.setPageProgress((short) guiUpdate.pageProgress);
                    stargate.setChanged();
                })
                .tryType(JSGStateTypes.SPIN_STATE, () -> stargate.getDialingManager().getSpinHelper().from((ClassicSpinHelper) state))
                .tryType(CoreStateTypes.BIOME_OVERRIDE_STATE, () -> {
                    StargateBiomeOverrideState overrideState = (StargateBiomeOverrideState) state;

                    if (rendererStateClient != null) {
                        getRendererStateClient().biomeOverride = overrideState.biomeOverride;
                    }
                })
                .run();
        super.setState(stateType, state);
    }

    @Override
    public State getState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.GUI_STATE, () -> new StargateContainerGuiState(stargate.getAddressMap(), stargate.getConfig()))
                .tryType(CoreStateTypes.GUI_UPDATE, () -> new StargateContainerGuiUpdate(
                        stargate.getEnergyManager().getStorage().getEnergyStoredInternally(),
                        stargate.getEnergyManager().getTransferredLastTick(),
                        stargate.getEnergyManager().getSecondsToClose(),
                        stargate.getIrisManager().getIrisMode(),
                        stargate.getIrisManager().getIrisCode(),
                        stargate.getDialingManager().getConnection(),
                        stargate.gateHeat,
                        stargate.irisHeat,
                        stargate.getPageProgress()
                ))
                .tryType(JSGStateTypes.SPIN_STATE, () -> stargate.getDialingManager().getSpinHelper())
                .tryType(JSGStateTypes.IRIS_ANIMATION, () -> stargate.getIrisManager().getIrisAnimationState())
                .orElseGet(() -> super.getState(stateType));
    }

    @Override
    public State createState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.GUI_STATE, () -> new StargateContainerGuiState(stargate.getConfig()))
                .tryType(CoreStateTypes.GUI_UPDATE, () -> new StargateContainerGuiUpdate(stargate.getDialingManager().getConnection()))
                .tryType(JSGStateTypes.SPIN_STATE, () -> stargate.getDialingManager().generateSpinHelper())
                .tryType(CoreStateTypes.BIOME_OVERRIDE_STATE, StargateBiomeOverrideState::new)
                .tryType(JSGStateTypes.IRIS_ANIMATION, IrisAnimationState::new)
                .orElseGet(() -> super.createState(stateType));
    }
}
