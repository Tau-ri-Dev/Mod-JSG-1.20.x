package dev.tauri.jsg.state.stargate;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class StargateOrlinParticleState extends State {
    public List<ChevronEnum> chevrons = new ArrayList<>();
    public boolean breakEffect;


    public StargateOrlinParticleState() {
    }

    public StargateOrlinParticleState(List<ChevronEnum> chevrons) {
        this.chevrons = chevrons;
        this.breakEffect = false;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(chevrons.size());
        for (var c : chevrons) {
            buf.writeInt(c.ordinal());
        }
        buf.writeBoolean(breakEffect);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        chevrons = new ArrayList<>();
        var size = buf.readInt();
        for (int i = 0; i < size; i++) {
            chevrons.add(ChevronEnum.values()[buf.readInt()]);
        }
        breakEffect = buf.readBoolean();
    }
}
