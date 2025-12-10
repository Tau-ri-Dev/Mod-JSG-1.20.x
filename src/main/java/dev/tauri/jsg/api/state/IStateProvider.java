package dev.tauri.jsg.api.state;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IStateProvider {

    /**
     * Server-side method. Called on {@link BlockEntity} to get specified {@link State}.
     *
     * @param stateType {@link StateType} State to be collected/returned
     * @return {@link State} instance
     */
    State getState(StateType stateType);

    /**
     * Client-side method. Called on {@link BlockEntity} to get specified {@link State} instance
     * to recreate State by deserialization
     *
     * @param stateType {@link StateType} State to be deserialized
     * @return deserialized {@link State}
     */
    State createState(StateType stateType);

    /**
     * Client-side method. Sets appropriate fields in client-side tile entity for it
     * to mirror the server-side tile entity
     *
     * @param stateType {@link StateType} State to be applied
     * @param state     {@link State} instance obtained from packet
     */
    @OnlyIn(Dist.CLIENT)
    void setState(StateType stateType, State state);


    default void getAndSendState(StateType type) {
        sendState(type, getState(type));
    }

    /**
     * Server sending state to client
     */
    void sendState(StateType type, State state);

    /**
     * Client requested state from server
     */
    void requestState(StateType type);

    BlockPos getBlockPos();
}
