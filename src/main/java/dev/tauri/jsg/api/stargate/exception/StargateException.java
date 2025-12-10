package dev.tauri.jsg.api.stargate.exception;

import dev.tauri.jsg.api.helper.BlockHelper;
import dev.tauri.jsg.api.stargate.Stargate;

public class StargateException extends RuntimeException {
    protected final Stargate<?> stargate;

    public StargateException(String message, Stargate<?> stargate) {
        super(message);
        this.stargate = stargate;
    }

    @Override
    @SuppressWarnings("all")
    public String getMessage() {
        return "[stargate at " + BlockHelper.blockPosToBetterString(stargate.getBlockPos()) + " in " + stargate.getStargatePos().dimension.location() + "]: " + super.getMessage();
    }
}
