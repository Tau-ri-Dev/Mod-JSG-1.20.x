package dev.tauri.jsg.common.state.stargate;

import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;

public class StargateVaporizeBlockParticlesRequest extends State {
    public StargateVaporizeBlockParticlesRequest() {
    }

    public BlockPos block;
    public boolean waterParticles;

    public StargateVaporizeBlockParticlesRequest(BlockPos block, boolean waterParticles) {
        this.block = block;
        this.waterParticles = waterParticles;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(block.asLong());
        buf.writeBoolean(waterParticles);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        block = BlockPos.of(buf.readLong());
        waterParticles = buf.readBoolean();
    }
}
