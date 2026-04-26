package dev.tauri.jsg.common.stargate.manager.state;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargatePegasusRendererState;
import dev.tauri.jsg.common.blockentity.stargate.StargatePegasusBaseBE;
import dev.tauri.jsg.common.stargate.animation.chevron.StargatePegasusChevronsState;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class StargatePegasusStateManager extends StargateClassicStateManager<StargatePegasusBaseBE, StargatePegasusRendererState> {
    public StargatePegasusStateManager(StargatePegasusBaseBE stargate) {
        super(stargate);
    }

    @Override
    public StargatePegasusChevronsState getChevronsState() {
        return (StargatePegasusChevronsState) super.getChevronsState();
    }

    @Override
    protected StargatePegasusChevronsState generateChevronsState() {
        return new StargatePegasusChevronsState(this) {
            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState getEmptyState(StargateAbstractStateManager<?, ?> stateManager, ChevronEnum ch) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "pegasus/chevron", ch);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromNBT(StargateAbstractStateManager<?, ?> stateManager, CompoundTag tag) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "pegasus/chevron", tag);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromBytes(StargateAbstractStateManager<?, ?> stateManager, ByteBuf buff) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "pegasus/chevron", buff);
            }
        };
    }

    @Override
    protected StargatePegasusRendererState.StargatePegasusRendererStateBuilder getRendererStateServer() {
        return (StargatePegasusRendererState.StargatePegasusRendererStateBuilder) new StargatePegasusRendererState.StargatePegasusRendererStateBuilder(super.getRendererStateServer());
    }

    @Override
    protected StargatePegasusRendererState createRendererStateClient() {
        return new StargatePegasusRendererState();
    }

    @Override
    public State createState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.RENDERER_STATE, StargatePegasusRendererState::new)
                .orElseGet(() -> super.createState(stateType));
    }
}
