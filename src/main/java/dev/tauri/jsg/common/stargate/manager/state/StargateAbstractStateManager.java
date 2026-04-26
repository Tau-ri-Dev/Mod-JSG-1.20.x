package dev.tauri.jsg.common.stargate.manager.state;

import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.api.stargate.manager.IStargateStateManager;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateAbstractRendererState;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.stargate.animation.BlackHoleAnimationState;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateChevronsState;
import dev.tauri.jsg.common.stargate.manager.AbstractStargateManager;
import dev.tauri.jsg.common.state.stargate.StargateFlashState;
import dev.tauri.jsg.common.state.stargate.StargateRendererActionState;
import dev.tauri.jsg.common.state.stargate.StargateSoundUpdateState;
import dev.tauri.jsg.common.state.stargate.StargateVaporizeBlockParticlesRequest;
import dev.tauri.jsg.core.common.blockentity.StateProviderInterface;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public abstract class StargateAbstractStateManager<SG extends StargateAbstractBaseBE<?, ?>, S extends StargateAbstractRendererState> extends AbstractStargateManager<SG> implements IStargateStateManager, StateProviderInterface {
    private PacketDistributor.TargetPoint targetPoint;
    protected StargateChevronsState chevronsState;
    protected BlackHoleAnimationState blackHoleAnimationState;

    public StargateAbstractStateManager(SG stargate) {
        super(stargate);
        this.chevronsState = generateChevronsState();
        this.blackHoleAnimationState = new BlackHoleAnimationState(stargate);
    }

    protected abstract StargateChevronsState generateChevronsState();

    public StargateChevronsState getChevronsState() {
        return chevronsState;
    }

    public BlackHoleAnimationState getBlackHoleAnimationState() {
        return blackHoleAnimationState;
    }


    @Override
    public State getState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(JSGStateTypes.CHEVRONS_STATE, () -> chevronsState)
                .tryType(CoreStateTypes.SOUND_UPDATE, () -> new StargateSoundUpdateState(stargate.getSoundManager()))
                .tryType(CoreStateTypes.RENDERER_STATE, () -> getRendererStateServer().build())
                .tryType(JSGStateTypes.BLACK_HOLE_ANIMATION_UPDATE, () -> blackHoleAnimationState)
                .orElseGet(() -> null);
    }

    @Override
    public State createState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.RENDERER_STATE, this::createRendererStateClient)
                .tryType(CoreStateTypes.SOUND_UPDATE, () -> new StargateSoundUpdateState(stargate.getSoundManager()))
                .tryType(CoreStateTypes.RENDERER_UPDATE, StargateRendererActionState::new)
                .tryType(JSGStateTypes.STARGATE_VAPORIZE_BLOCK_PARTICLES, StargateVaporizeBlockParticlesRequest::new)
                .tryType(JSGStateTypes.FLASH_STATE, StargateFlashState::new)
                .tryType(JSGStateTypes.CHEVRONS_STATE, this::generateChevronsState)
                .tryType(JSGStateTypes.BLACK_HOLE_ANIMATION_UPDATE, () -> blackHoleAnimationState)
                .orElseGet(() -> null);
    }

    @SuppressWarnings("unchecked")
    protected S castState(State state) {
        return (S) state;
    }

    @Override
    public void setState(StateType stateType, State state) {
        stateType.stateExecutor()
                .tryType(CoreStateTypes.SOUND_UPDATE, () -> stargate.getSoundManager().updateClient())
                .tryType(CoreStateTypes.RENDERER_STATE, () -> {
                    setRendererStateClient(castState(castState(state).initClient(getBlockPos())));
                    stargate.updateFacing();
                })
                .tryType(JSGStateTypes.CHEVRONS_STATE, () -> {
                    this.chevronsState = (StargateChevronsState) state;
                    stargate.setChanged();
                })
                .tryType(JSGStateTypes.BLACK_HOLE_ANIMATION_UPDATE, () -> {
                    blackHoleAnimationState = (BlackHoleAnimationState) state;
                    stargate.setChanged();
                })
                .tryType(CoreStateTypes.RENDERER_UPDATE, () -> {
                    if (getRendererStateClient() == null && ((StargateRendererActionState) state).action != StargateRendererActionState.EnumGateAction.GATE_RENDER_CHANGED)
                        return;
                    switch (((StargateRendererActionState) state).action) {
                        case OPEN_GATE:
                            boolean noxDialing = ((StargateRendererActionState) state).modifyFinal;

                            getRendererStateClient().openGate(stargate.getTime(), noxDialing);
                            break;

                        case CLOSE_GATE:
                            getRendererStateClient().closeGate(stargate.getTime());
                            break;

                        case GATE_RENDER_CHANGED:
                            stargate.setMerged(((StargateRendererActionState) state).modifyFinal);
                            stargate.setChanged();
                            break;

                        default:
                            break;
                    }
                })
                .tryType(JSGStateTypes.STARGATE_VAPORIZE_BLOCK_PARTICLES, () -> {
                    if (stargate.getLevel() == null) return;
                    var level = stargate.getLevel();
                    var random = level.random;
                    for (int i = 0; i < 20; ++i) {
                        var s = (StargateVaporizeBlockParticlesRequest) state;
                        if (s.waterParticles && !random.nextBoolean()) continue;
                        var d0 = random.nextGaussian() * 0.02D;
                        var d1 = random.nextGaussian() * 0.02D;
                        var d2 = random.nextGaussian() * 0.02D;
                        var x = s.block.getX() + random.nextFloat();
                        var y = s.block.getY() + random.nextFloat();
                        var z = s.block.getZ() + random.nextFloat();
                        level.addParticle(s.waterParticles ? ParticleTypes.BUBBLE_COLUMN_UP : ParticleTypes.POOF, x, y, z, d0, d1, d2);
                    }
                })
                .tryType(JSGStateTypes.FLASH_STATE, () -> {
                    if (getRendererStateClient() != null)
                        getRendererStateClient().horizonUnstable = ((StargateFlashState) state).flash;
                })
                .run();
    }


    // ------------------------------------------------------------------------
    // Rendering

    public S getRendererStateClient() {
        return rendererStateClient;
    }

    protected StargateAbstractRendererState.StargateAbstractRendererStateBuilder getRendererStateServer() {
        return StargateAbstractRendererState.builder().setStargateState(stargate.getDialingManager().getStargateState());
    }

    protected S rendererStateClient;

    protected abstract S createRendererStateClient();

    protected void setRendererStateClient(S rendererState) {
        this.rendererStateClient = rendererState;
        stargate.addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_LIGHTING_UPDATE_CLIENT, 10));
    }

    // ------------------------------------------------------------------------

    @Override
    public PacketDistributor.TargetPoint getTargetPoint() {
        if (stargate.getLevel() == null) return targetPoint;
        if (targetPoint == null) {
            var pos = getBlockPos();
            targetPoint = new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 512, stargate.getLevel().dimension());
        }
        return targetPoint;
    }

    public void sendRenderingUpdate(StargateRendererActionState.EnumGateAction gateAction, int chevronCount, boolean modifyFinal, EnumIrisType irisType, EnumIrisState irisState, long irisAnimation) {
        sendState(CoreStateTypes.RENDERER_UPDATE.get(), new StargateRendererActionState(gateAction, chevronCount, modifyFinal, irisType, irisState, irisAnimation));
    }

    public void sendRenderingUpdate(StargateRendererActionState.EnumGateAction gateAction, int chevronCount, boolean modifyFinal) {
        sendState(CoreStateTypes.RENDERER_UPDATE.get(), new StargateRendererActionState(gateAction, chevronCount, modifyFinal));
    }

    public void sendRenderingUpdate(StargateRendererActionState.EnumGateAction gateAction, boolean parameter) {
        sendState(CoreStateTypes.RENDERER_UPDATE.get(), new StargateRendererActionState(gateAction, -1, parameter));
    }

    @Override
    public BlockPos getBlockPos() {
        return stargate.getBlockPos();
    }

    @Override
    public void onLoad(@NotNull Level level) {
        var clientSide = level.isClientSide();
        if (clientSide) {
            requestState(CoreStateTypes.RENDERER_STATE.get());
            requestState(JSGStateTypes.CHEVRONS_STATE.get());
            requestState(JSGStateTypes.BLACK_HOLE_ANIMATION_UPDATE.get());
        } else {
            getAndSendState(CoreStateTypes.RENDERER_STATE.get());
            getAndSendState(JSGStateTypes.CHEVRONS_STATE.get());
        }
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide() && getRendererStateClient() == null) {
            requestState(CoreStateTypes.RENDERER_STATE.get());
            // Client
            // Each 2s check for the biome overlay
            if (rendererStateClient != null && stargate.getTime() % 40 == 0) {
                rendererStateClient.setBiomeOverlay(stargate.getBiomeOverlayWithOverride());
            }
        }
        getChevronsState().tick(level);
        blackHoleAnimationState.tick(level);
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("blackHoleAnimationManager", blackHoleAnimationState.serializeNBT());
        compound.put("chevronsState", chevronsState.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        blackHoleAnimationState.deserializeNBT(compound.getCompound("blackHoleAnimationManager"));
        chevronsState.deserializeNBT(compound.getCompound("chevronsState"));
    }
}
