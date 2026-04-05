package dev.tauri.jsg.stargate.manager.state;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.blockentity.stargate.StargateMovieBaseBE;
import dev.tauri.jsg.renderer.stargate.StargateClassicRendererState;
import dev.tauri.jsg.renderer.stargate.StargateMovieRendererState;
import dev.tauri.jsg.stargate.animation.chevron.StargateMovieChevronsState;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class StargateMovieStateManager extends StargateMilkyWayStateManager {
    public StargateMovieStateManager(StargateMovieBaseBE stargate) {
        super(stargate);
    }

    @Override
    protected StargateMovieChevronsState generateChevronsState() {
        return new StargateMovieChevronsState(this) {
            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState getEmptyState(StargateAbstractStateManager<?, ?> stateManager, ChevronEnum ch) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "movie/chevron", ch);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromNBT(StargateAbstractStateManager<?, ?> stateManager, CompoundTag tag) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "movie/chevron", tag);
            }

            @Override
            @NotNull
            @ParametersAreNonnullByDefault
            protected ChevronState chevronStateFromBytes(StargateAbstractStateManager<?, ?> stateManager, ByteBuf buff) {
                return new ChevronState(stateManager, JSGApi.JSG_LOADERS_HOLDER.texture(), "movie/chevron", buff);
            }
        };
    }

    @Override
    protected StargateClassicRendererState.StargateClassicRendererStateBuilder getRendererStateServer() {
        return new StargateMovieRendererState.StargateMovieRendererStateBuilder(super.getRendererStateServer());
    }

    @Override
    protected StargateMovieRendererState createRendererStateClient() {
        return new StargateMovieRendererState();
    }
}
