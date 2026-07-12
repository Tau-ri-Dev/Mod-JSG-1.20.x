package dev.tauri.jsg.common.state.energy;

import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

public class ZPMHubRendererState extends State {
    public ZPMHubRendererState() {
    }

    public long animationStart;
    public boolean isAnimating;
    public boolean slidingUp = true;

    public int zpm1Level = -1;
    public int zpm2Level = -1;
    public int zpm3Level = -1;

    public ZPMModelType zpm1Type = ZPMModelType.NORMAL;
    public ZPMModelType zpm2Type = ZPMModelType.NORMAL;
    public ZPMModelType zpm3Type = ZPMModelType.NORMAL;

    public float facing;

    public ZPMHubRendererState(long animationStart, boolean isAnimating, boolean slidingUp, int zpm1Level, int zpm2Level, int zpm3Level, ZPMModelType zpm1Type, ZPMModelType zpm2Type, ZPMModelType zpm3Type, float facing) {
        this.animationStart = animationStart;
        this.isAnimating = isAnimating;
        this.slidingUp = slidingUp;

        this.zpm1Level = zpm1Level;
        this.zpm2Level = zpm2Level;
        this.zpm3Level = zpm3Level;

        this.zpm1Type = zpm1Type;
        this.zpm2Type = zpm2Type;
        this.zpm3Type = zpm3Type;

        this.facing = facing;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(animationStart);
        buf.writeBoolean(isAnimating);
        buf.writeBoolean(slidingUp);

        buf.writeInt(zpm1Level);
        buf.writeInt(zpm2Level);
        buf.writeInt(zpm3Level);

        buf.writeInt(zpm1Type.id);
        buf.writeInt(zpm2Type.id);
        buf.writeInt(zpm3Type.id);

        buf.writeFloat(facing);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.animationStart = buf.readLong();
        this.isAnimating = buf.readBoolean();
        this.slidingUp = buf.readBoolean();

        this.zpm1Level = buf.readInt();
        this.zpm2Level = buf.readInt();
        this.zpm3Level = buf.readInt();

        this.zpm1Type = ZPMModelType.byId(buf.readInt());
        this.zpm2Type = ZPMModelType.byId(buf.readInt());
        this.zpm3Type = ZPMModelType.byId(buf.readInt());

        this.facing = buf.readFloat();
    }
}
