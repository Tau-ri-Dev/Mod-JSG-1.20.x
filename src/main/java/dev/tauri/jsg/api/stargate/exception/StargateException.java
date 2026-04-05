package dev.tauri.jsg.api.stargate.exception;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.core.common.helper.BlockHelper;

public class StargateException extends RuntimeException {
    protected final Stargate<?> stargate;

    public StargateException(String message, Stargate<?> stargate) {
        super(message);
        this.stargate = stargate;
    }

    @Override
    @SuppressWarnings("all")
    public String getMessage() {
        return "[stargate at " + BlockHelper.blockPosToBetterString(stargate.blockPosition()) + " in " + stargate.getStargatePos().dimension.location() + "]: " + super.getMessage();
    }
}
