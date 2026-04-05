package dev.tauri.jsg.stargate.manager.state;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.blockentity.stargate.StargateOrlinBaseBE;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.particle.ParticleAnimationHandler;
import dev.tauri.jsg.renderer.stargate.StargateOrlinRendererState;
import dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState;
import dev.tauri.jsg.stargate.orlin.StargateOrlinOpeningParticleScenes;
import dev.tauri.jsg.state.stargate.StargateOrlinParticleState;
import dev.tauri.jsg.state.stargate.StargateRendererActionState;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

public class StargateOrlinStateManager extends StargateAbstractStateManager<StargateOrlinBaseBE, StargateOrlinRendererState> {
    public StargateOrlinStateManager(StargateOrlinBaseBE stargate) {
        super(stargate);
    }

    @Override
    protected StargateChevronsState generateChevronsState() {
        return new StargateChevronsState(this) {
            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState getEmptyState(StargateAbstractStateManager<?, ?> stateManager, ChevronEnum ch) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "milkyway/chevron", ch);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromNBT(StargateAbstractStateManager<?, ?> stateManager, CompoundTag tag) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "milkyway/chevron", tag);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromBytes(StargateAbstractStateManager<?, ?> stateManager, ByteBuf buff) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "milkyway/chevron", buff);
            }

            @Override
            @ParametersAreNonnullByDefault
            public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
                super.executeTask(scheduledTask, customData);
                if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_LIGHT_UP.get()) {
                    var chevrons = (customData.getBoolean("litAll") ? Arrays.stream(ChevronEnum.values()).toList() : List.of(ChevronEnum.valueOf(customData.getInt("chevron"))));
                    sendState(JSGStateTypes.ORLIN_PARTICLE_STATE.get(), new StargateOrlinParticleState(chevrons));
                }
            }
        };
    }

    @Override
    public State createState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.RENDERER_STATE, this::createRendererStateClient)
                .tryType(JSGStateTypes.ORLIN_PARTICLE_STATE, StargateOrlinParticleState::new)
                .orElseGet(() -> super.createState(stateType));
    }

    @Override
    public State getState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.RENDERER_STATE, () -> getRendererStateServer().build())
                .tryType(JSGStateTypes.ORLIN_PARTICLE_STATE, StargateOrlinParticleState::new)
                .orElseGet(() -> super.getState(stateType));
    }

    @Override
    public void setState(StateType stateType, State state) {
        stateType.stateExecutor()
                .tryType(JSGStateTypes.ORLIN_PARTICLE_STATE, () -> {
                    var particleState = (StargateOrlinParticleState) state;
                    if (particleState.breakEffect) return;
                    for (var chevron : particleState.chevrons) {
                        particleHandler.play(StargateOrlinOpeningParticleScenes.CHEVRON.apply(chevron));
                    }
                })
                .tryType(CoreStateTypes.RENDERER_UPDATE.get(), () -> {
                    super.setState(stateType, state);
                    if (((StargateRendererActionState) state).action == StargateRendererActionState.EnumGateAction.CLOSE_GATE) {
                        particleHandler.clear();
                    }
                    if (((StargateRendererActionState) state).action == StargateRendererActionState.EnumGateAction.OPEN_GATE) {
                        particleHandler.play(StargateOrlinOpeningParticleScenes.SMOKE.get());
                    }
                })
                .runOrElse(() -> super.setState(stateType, state));
    }

    @Override
    protected StargateOrlinRendererState.StargateOrlinRendererStateBuilder getRendererStateServer() {
        return (StargateOrlinRendererState.StargateOrlinRendererStateBuilder) new StargateOrlinRendererState.StargateOrlinRendererStateBuilder().setStargateState(stargate.getDialingManager().getStargateState());
    }

    @Override
    protected StargateOrlinRendererState createRendererStateClient() {
        return new StargateOrlinRendererState();
    }

    @Override
    public void tick(@NotNull Level level) {
        super.tick(level);

        particleHandler.tick(stargate);
    }

    // client
    public ParticleAnimationHandler<StargateOrlinBaseBE> particleHandler = new ParticleAnimationHandler<>();
}
