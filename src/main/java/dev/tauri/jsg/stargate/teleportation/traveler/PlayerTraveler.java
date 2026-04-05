package dev.tauri.jsg.stargate.teleportation.traveler;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.effect.StargateWormholeHandler;
import dev.tauri.jsg.effect.StargateWormholeType;
import dev.tauri.jsg.packet.JSGPacketHandler;
import dev.tauri.jsg.packet.packets.stargate.StargateMotionAndRotationToClient;
import dev.tauri.jsg.packet.packets.stargate.StargatePlayerMotionRequestToClient;
import dev.tauri.jsg.stargate.teleportation.StargateTeleporter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

public class PlayerTraveler extends LivingTraveler<ServerPlayer> {
    public PlayerTraveler(ServerPlayer entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> receivingGate, boolean isStatic) {
        super(entity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, receivingGate, isStatic);
    }

    @Override
    public boolean canBeKilledByIris() {
        return !JSGConfig.Stargate.allowCreative.get() || !get().isCreative();
    }

    @Override
    public void setMotion(Vec3 newMotion) {
        super.setMotion(newMotion);
        entity.hurtMarked = true;
        JSGPacketHandler.sendTo(new StargateMotionAndRotationToClient(newMotion, entity.getXRot(), getDestinationYaw()), get());
    }

    @Override
    public boolean canBeSendToTarget() {
        if (sourceGate.getStargatePos() == null || receivingGate.getStargatePos() == null || sourceGate.getStargatePos().dimension == receivingGate.getStargatePos().dimension)
            return true;
        return ForgeHooks.onTravelToDimension(entity, receivingGate.getStargatePos().dimension);
    }

    @Override
    public void sendChangeDimension(ServerLevel targetLevel) {
        if (isStatic()) {
            get().changeDimension(targetLevel, new StargateTeleporter(this, (traveler) -> setMotion(getDestinationMotion())));
            return;
        }
        StargateWormholeHandler.handle(get(), (afterPlace) -> get().changeDimension(targetLevel, new StargateTeleporter(this, (traveler) -> {
            afterPlace.accept((ServerPlayer) traveler.get());
            setMotion(getDestinationMotion());
        })), StargateWormholeType.fromTileEntity(receivingGate), 5);
    }

    @Override
    public void sendSameDimension() {
        if (isStatic()) {
            receivingGate.getEventHorizonManager().receive(this);
            return;
        }
        StargateWormholeHandler.handle(get(), (afterPlace) -> {
            receivingGate.getEventHorizonManager().receive(this);
            afterPlace.accept(get());
        }, StargateWormholeType.fromTileEntity(receivingGate), 3);
    }

    @Override
    public void setPos(Vec3 newPos) {
        get().teleportTo(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public void send() {
        JSGPacketHandler.sendTo(new StargatePlayerMotionRequestToClient(getTransmitter().blockPosition(), false), get());
    }

    @Override
    public void killWrongTravel() {
        JSGPacketHandler.sendTo(new StargatePlayerMotionRequestToClient(getTransmitter().blockPosition(), true), get());
    }

    public void confirmSend() {
        super.send();
    }

    @Override
    public boolean isSameDimension() {
        if (isStatic())
            return getReceiver().getStargatePos().dimension == get().level().dimension();
        return super.isSameDimension();
    }

    public void confirmKillWrongTravel() {
        super.killWrongTravel();
    }

    @Override
    public void killIris() {
        get().setInvulnerable(false);
        get().setInvisible(false);
        get().noPhysics = false;
        get().getAbilities().invulnerable = false;
        get().getAbilities().mayBuild = true;
        get().gameMode.getGameModeForPlayer().updatePlayerAbilities(get().getAbilities());
        get().onUpdateAbilities();
        super.killIris();
    }

    @Override
    public int hashCode() {
        return get().getUUID().hashCode();
    }
}
