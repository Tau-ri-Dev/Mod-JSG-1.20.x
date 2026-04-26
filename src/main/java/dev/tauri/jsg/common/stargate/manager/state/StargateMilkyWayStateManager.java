package dev.tauri.jsg.common.stargate.manager.state;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateClassicRendererState;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateMilkyWayRendererState;
import dev.tauri.jsg.common.blockentity.stargate.StargateMilkyWayBaseBE;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateChevronsState;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class StargateMilkyWayStateManager extends StargateClassicStateManager<StargateMilkyWayBaseBE, StargateMilkyWayRendererState> {
    public StargateMilkyWayStateManager(StargateMilkyWayBaseBE stargate) {
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
        };
    }

    @Override
    protected StargateClassicRendererState.StargateClassicRendererStateBuilder getRendererStateServer() {
        return new StargateMilkyWayRendererState.StargateMilkyWayRendererStateBuilder(super.getRendererStateServer());
    }

    @Override
    protected StargateMilkyWayRendererState createRendererStateClient() {
        return new StargateMilkyWayRendererState();
    }
}
