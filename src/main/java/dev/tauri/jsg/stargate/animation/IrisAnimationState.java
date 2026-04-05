package dev.tauri.jsg.stargate.animation;

import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

public class IrisAnimationState extends State {
    public static final int IRIS_HIT_ANIMATION_LENGTH = 5;

    protected long started;
    protected boolean impact = false;
    protected boolean collapse = false;
    protected boolean collapseBlackHole = false;

    public IrisAnimationState() {
    }

    public static IrisAnimationState hit(long time) {
        var state = new IrisAnimationState();
        state.started = time;
        state.impact = true;
        state.collapse = false;
        state.collapseBlackHole = false;
        return state;
    }

    public static IrisAnimationState collapse(long time, boolean blackHole) {
        var state = new IrisAnimationState();
        state.started = time;
        state.impact = false;
        state.collapse = !blackHole;
        state.collapseBlackHole = blackHole;
        return state;
    }


    public float getIrisImpactOffset(long tick, float partialTicks) {
        if (!impact) return 0f;
        float coef = (float) (tick - started) / (float) IRIS_HIT_ANIMATION_LENGTH;
        if (coef < 0) coef = 0;
        if (coef > 1) {
            impact = false;
            return 0;
        }
        return (float) Math.sin(coef * Math.PI);
    }

    public float getIrisCollapseCoef(long tick, float partialTicks) {
        return 0;
    }

    public float getIrisCollapseBlackHoleCoef(long tick, float partialTicks) {
        return 0;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(started);
        buf.writeBoolean(impact);
        buf.writeBoolean(collapse);
        buf.writeBoolean(collapseBlackHole);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        started = buf.readLong();
        impact = buf.readBoolean();
        collapse = buf.readBoolean();
        collapseBlackHole = buf.readBoolean();
    }
}
