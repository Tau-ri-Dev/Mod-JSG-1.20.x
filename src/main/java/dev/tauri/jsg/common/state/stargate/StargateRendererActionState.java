package dev.tauri.jsg.common.state.stargate;

import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class StargateRendererActionState extends State {
    public enum EnumGateAction {
        OPEN_GATE(3),
        CLOSE_GATE(4),

        IRIS_UPDATE(13),
        HEAT_UPDATE(15),
        GATE_RENDER_CHANGED(16);

        public final int actionID;
        private static final Map<Integer, EnumGateAction> map = new HashMap<>();

        EnumGateAction(int actionID) {
            this.actionID = actionID;
        }

        static {
            for (EnumGateAction action : EnumGateAction.values()) {
                map.put(action.actionID, action);
            }
        }

        public static EnumGateAction valueOf(int actionID) {
            return map.get(actionID);
        }
    }

    public StargateRendererActionState() {
    }

    public EnumGateAction action;
    public int chevronCount = 0;
    public boolean modifyFinal = false;
    public EnumIrisState irisState = null;
    public EnumIrisType irisType = null;
    public long irisAnimation = 0;
    public double irisHeat = 0;
    public double gateHeat = 0;

    public StargateRendererActionState(EnumGateAction action) {
        this.action = action;
    }

    public StargateRendererActionState(EnumGateAction action, int chevronCount, boolean modifyFinal) {
        this.action = action;
        this.chevronCount = chevronCount;
        this.modifyFinal = modifyFinal;
    }

    public StargateRendererActionState(double irisHeat, double gateHeat) {
        this.action = EnumGateAction.HEAT_UPDATE;
        this.irisHeat = irisHeat;
        this.gateHeat = gateHeat;
    }

    public StargateRendererActionState(EnumGateAction action, int chevronCount, boolean modifyFinal, EnumIrisType irisType, EnumIrisState irisState, long irisAnimation) {
        this.action = action;
        this.chevronCount = chevronCount;
        this.modifyFinal = modifyFinal;
        this.irisState = irisState;
        this.irisType = irisType;
        this.irisAnimation = irisAnimation;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(action.actionID);
        buf.writeInt(chevronCount);
        buf.writeBoolean(modifyFinal);
        if (irisType != null) {
            buf.writeBoolean(true);
            buf.writeByte(irisState.id);
            buf.writeByte(irisType.id);
            buf.writeLong(irisAnimation);
        } else buf.writeBoolean(false);
        buf.writeDouble(irisHeat);
        buf.writeDouble(gateHeat);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = EnumGateAction.valueOf(buf.readInt());
        chevronCount = buf.readInt();
        modifyFinal = buf.readBoolean();
        if (buf.readBoolean()) {
            irisState = EnumIrisState.getValue(buf.readByte());
            irisType = EnumIrisType.byId(buf.readByte());
            irisAnimation = buf.readLong();
        }
        irisHeat = buf.readDouble();
        gateHeat = buf.readDouble();
    }

}
