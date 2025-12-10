package dev.tauri.jsg.api.state;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Base class for all states(ex. RendererStates or GuiStates)
 * <p>
 * Defines methods to write NBT tags from abstract byte methods(used by TileUpdatePacketToClient)
 *
 */
public abstract class State implements INBTSerializable<CompoundTag> {
	/**
	 * Should write all parameters that matter to client-side renderer(ex. vortexState in StargateRenderer)
	 * to a ByteBuf.
	 * <p>
	 * Data should be put and read in the same order!
	 * 
	 * @param buf - Buffer object you write into.
	 */
	public abstract void toBytes(ByteBuf buf);
	
	/**
	 * Should set all parameters that matter to client-side renderer(ex. vortexState in StargateRenderer)
	 * 
	 * @param buf - Buffer object you read from.
	 */
	public abstract void fromBytes(ByteBuf buf);
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag compound = new CompoundTag();
		
		ByteBuf buf = Unpooled.buffer();
		toBytes(buf);
		
		byte[] dst = new byte[buf.readableBytes()];
		buf.readBytes(dst);
		
		compound.putByteArray("byteArray", dst);
		
		return compound;
	}
	
	@Override
	public void deserializeNBT(CompoundTag compound) {
		if (compound == null)
			return;
		
		byte[] dst = compound.getByteArray("byteArray");
		
		if (dst.length > 0) {
			ByteBuf buf = Unpooled.copiedBuffer(dst);
			fromBytes(buf);
		}
	}
}
