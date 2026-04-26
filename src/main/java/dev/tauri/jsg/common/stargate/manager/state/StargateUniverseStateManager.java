package dev.tauri.jsg.common.stargate.manager.state;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateClassicRendererState;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateUniverseRendererState;
import dev.tauri.jsg.common.blockentity.stargate.StargateUniverseBaseBE;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateChevronsState;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateUniverseChevronsState;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class StargateUniverseStateManager extends StargateClassicStateManager<StargateUniverseBaseBE, StargateUniverseRendererState> {
    public StargateUniverseStateManager(StargateUniverseBaseBE stargate) {
        super(stargate);
    }

    @Override
    protected StargateChevronsState generateChevronsState() {
        return new StargateUniverseChevronsState(this) {
            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState getEmptyState(StargateAbstractStateManager<?, ?> stateManager, ChevronEnum ch) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "universe/universe_chevron", ch);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromNBT(StargateAbstractStateManager<?, ?> stateManager, CompoundTag tag) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "universe/universe_chevron", tag);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromBytes(StargateAbstractStateManager<?, ?> stateManager, ByteBuf buff) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "universe/universe_chevron", buff);
            }
        };
    }

    @Override
    protected StargateClassicRendererState.StargateClassicRendererStateBuilder getRendererStateServer() {
        return new StargateUniverseRendererState.StargateUniverseRendererStateBuilder(super.getRendererStateServer());
    }

    @Override
    protected StargateUniverseRendererState createRendererStateClient() {
        return new StargateUniverseRendererState();
    }
}
