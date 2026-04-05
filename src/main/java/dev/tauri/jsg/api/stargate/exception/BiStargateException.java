package dev.tauri.jsg.api.stargate.exception;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.core.common.helper.BlockHelper;

public class BiStargateException extends RuntimeException {
    protected final Stargate<?> stargate1;
    protected final Stargate<?> stargate2;

    public BiStargateException(String message, Stargate<?> stargate1, Stargate<?> stargate2) {
        super(message);
        this.stargate1 = stargate1;
        this.stargate2 = stargate2;
    }

    @Override
    @SuppressWarnings("all")
    public String getMessage() {
        return "[stargate at " + BlockHelper.blockPosToBetterString(stargate1.blockPosition()) + " in " + stargate1.getStargatePos().dimension.location() + "] & [stargate at " + BlockHelper.blockPosToBetterString(stargate2.blockPosition()) + " in " + stargate2.getStargatePos().dimension.location() + "]: " + super.getMessage();
    }
}
