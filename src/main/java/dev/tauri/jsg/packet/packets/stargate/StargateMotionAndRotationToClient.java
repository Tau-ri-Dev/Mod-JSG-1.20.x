package dev.tauri.jsg.packet.packets.stargate;

import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class StargateMotionAndRotationToClient extends JSGPacket {

    private Vec3 motion;
    private Float rotationX;
    private Float rotationY;
    private boolean setRotation;
    private boolean addMotion;

    public StargateMotionAndRotationToClient(Vec3 motion, float rotationX, float rotationY) {
        this(motion, rotationX, rotationY, true, false);
    }

    public StargateMotionAndRotationToClient(Vec3 motion, float rotationX, float rotationY, boolean setRotation, boolean addMotion) {
        this.motion = motion;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.setRotation = setRotation;
        this.addMotion = addMotion;
    }

    public StargateMotionAndRotationToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVector3f(motion.toVector3f());
        buf.writeFloat(rotationX);
        buf.writeFloat(rotationY);
        buf.writeBoolean(setRotation);
        buf.writeBoolean(addMotion);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        motion = new Vec3(buf.readVector3f());
        rotationX = buf.readFloat();
        rotationY = buf.readFloat();
        setRotation = buf.readBoolean();
        addMotion = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        LocalPlayer player = Minecraft.getInstance().player;
        ctx.enqueueWork(() -> {
            if (player != null) {
                if (addMotion)
                    player.setDeltaMovement(player.getDeltaMovement().add(motion));
                else
                    player.setDeltaMovement(motion);
                if (!setRotation) return;
                player.setXRot(rotationX);
                player.setYRot(rotationY);
            }
        });
    }
}
