package dev.tauri.jsg.common.packet.packets.stargate;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.common.stargate.manager.StargateEventHorizonManager;
import dev.tauri.jsg.common.stargate.teleportation.traveler.PlayerTraveler;
import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class StargatePlayerMotionToServer extends PositionedPacket {
    private int entityId;
    private Vec3 motion;
    private boolean wrongWayTravel;

    public StargatePlayerMotionToServer(int entityId, BlockPos pos, Vec3 motion, boolean wrongWayTravel) {
        super(pos);

        this.entityId = entityId;
        this.motion = motion;
        this.wrongWayTravel = wrongWayTravel;
    }

    public StargatePlayerMotionToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(entityId);
        buf.writeDouble(motion.x);
        buf.writeDouble(motion.y);
        buf.writeDouble(motion.z);
        buf.writeBoolean(wrongWayTravel);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);

        entityId = buf.readInt();

        var motionX = buf.readDouble();
        var motionY = buf.readDouble();
        var motionZ = buf.readDouble();
        motion = new Vec3(motionX, motionY, motionZ);
        wrongWayTravel = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        var player = ctx.getSender();
        if (player == null) return;
        var world = player.level();
        ctx.enqueueWork(() -> {
            var blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof Stargate<?> stargate)) return;
            var eh = (StargateEventHorizonManager) stargate.getEventHorizonManager();
            var traveler = eh.getTraveler(player, motion);
            if (!(traveler instanceof PlayerTraveler pTraveler)) {
                if (wrongWayTravel)
                    return;
                if (traveler != null)
                    eh.remove(traveler);
                return;
            }
            if (wrongWayTravel) {
                pTraveler.confirmKillWrongTravel();
                return;
            }
            pTraveler.confirmSend();
        });
    }
}
