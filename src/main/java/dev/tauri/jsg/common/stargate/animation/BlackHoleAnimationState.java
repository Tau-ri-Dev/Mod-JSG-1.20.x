package dev.tauri.jsg.common.stargate.animation;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateWithIris;
import dev.tauri.jsg.api.stargate.animation.AbstractBlackHoleAnimationState;
import dev.tauri.jsg.common.config.JSGConfigUtil;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.stargate.StargateMotionAndRotationToClient;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlackHoleAnimationState extends AbstractBlackHoleAnimationState {
    public static final int TIME_BEFORE_DEPTH_CHANGE = 400; // 20 sec
    public static final int MAX_GRAVITY_FIELD_RADIUS = 75; // blocks
    public static final int BACK_VORTEX_FORMING_ANIMATION_TIME = 10 * 60 * 20; // ticks

    public BlackHoleAnimationState(@Nonnull Stargate<?> stargate) {
        super(stargate);
    }

    public void setConnectedToBlackHole(boolean connected, boolean isSource) {
        if (connected && !isConnectedToBlackHole)
            isConnectedToBlackHoleFrom = stargate.getDialingManager().getConnection().getSince();
        isConnectedToBlackHole = connected;
        this.isSource = isSource;
        stargate.setStargateChanged();
        sendUpdateToClient();
    }

    public void sendUpdateToClient() {
        stargate.getAndSendState(JSGStateTypes.BLACK_HOLE_ANIMATION_UPDATE.get());
    }

    public void tick(@NotNull Level level) {
        if (!isConnectedToBlackHole) return;
        if (level.isClientSide) return;
        if (!stargate.getDialingManager().getStargateState().engaged() && !stargate.getDialingManager().getStargateState().unstable()) {
            setConnectedToBlackHole(false, false);
            return;
        }
        if (stargate.getDialingManager().getStargateState().unstable())
            return;
        if (getGravitationalFieldStrength() > 0) {
            suckEntities((ServerLevel) level);
        }
        if (getBackVortexDepth() != 0 && JSGConfig.Stargate.blackHoleCanDestroyBlocks.get()) {
            destroyBlocksInBackVortex((ServerLevel) level);
        }
    }

    protected void destroyBlocksInBackVortex(ServerLevel level) {
        var depth = getBackVortexDepth();
        var center = stargate.getGateCenterPos().offset(stargate.blockPosition().multiply(-1));
        if (depth * 5 <= 0.5) return;
        for (var i = 0; i < Math.abs(depth * 5); i++) {
            var z = (i + 1) * (depth > 0 ? -1 : 1);
            var minXY = Math.ceil(((depth * 5) - i) / 2f);
            var maxXY = -Math.ceil(((depth * 5) - i) / 2f);
            var partBox = new JSGAxisAlignedBB(minXY, minXY, z, maxXY, maxXY, z + 1).offset(center);
            partBox = stargate.relative(partBox, new Vec3(0, 0, 0));
            BlockPos.betweenClosedStream(partBox).forEach(blockPos -> {
                if (!JSGConfigUtil.canBlackHoleDestroyBlock(level.getBlockState(blockPos))) return;
                level.destroyBlock(blockPos, false);
            });
        }
    }

    // suck entities into the gate
    protected void suckEntities(@NotNull ServerLevel level) {
        var gateCenter = stargate.getGateCenterPos();
        var strength = getGravitationalFieldStrength();
        if (stargate instanceof StargateWithIris<?> irisCapable && irisCapable.getIrisManager().isIrisClosed()) {
            if (irisCapable.getIrisManager().hasPhysicalIris() && strength >= 0.5f && level.random.nextFloat() < (strength - 0.5f)) {
                irisCapable.getIrisManager().hitIris();
            }
            strength /= 2;
        }
        var currentRadius = (int) (MAX_GRAVITY_FIELD_RADIUS * (strength / 2f));
        if (currentRadius <= 0) return;
        var box = new JSGAxisAlignedBB(-currentRadius / 4f, -currentRadius / 4f, 0, currentRadius / 4f, currentRadius / 4f, currentRadius * 2f);
        box = stargate.relative(box, new Vec3(0, 0, 0)).move(stargate.getGateCenterPos().subtract(stargate.blockPosition()));
        var entities = level.getEntities(null, box);
        for (var entity : entities) {
            var ePos = entity.position();
            double distanceToGateCenter = Math.sqrt(gateCenter.distToCenterSqr(ePos));
            var motionFactor = ((currentRadius * 2) - distanceToGateCenter) / (currentRadius * 10);
            if (motionFactor <= 0) continue;
            var vectorToGate = new Vec3(gateCenter.getX() - ePos.x(), gateCenter.getY() - ePos.y(), gateCenter.getZ() - ePos.z()).normalize();
            vectorToGate = vectorToGate.multiply(motionFactor, motionFactor, motionFactor);
            var newMotion = entity.getDeltaMovement().add(vectorToGate);
            if (isSource) newMotion = newMotion.reverse();
            entity.setDeltaMovement(newMotion);
            if (entity instanceof ServerPlayer sp) {
                // handle SP
                // send packet to client
                JSGPacketHandler.sendTo(new StargateMotionAndRotationToClient(newMotion, entity.getXRot(), entity.getYRot(), false, true), sp);
            }
        }

        if (isSource || !JSGConfig.Stargate.blackHoleCanSuckBlocks.get()) return;
        BlockPos.betweenClosedStream(box).forEach(blockPos -> {
            final var pos = blockPos.immutable();
            double distanceToGateCenter = Math.sqrt(gateCenter.distToCenterSqr(pos.getCenter())) * 1.5f;
            var motionFactor = ((currentRadius * 2) - distanceToGateCenter) / (currentRadius * 10);
            if (motionFactor <= 0) return;
            var vectorToGate = new Vec3(gateCenter.getX() - pos.getX(), gateCenter.getY() - pos.getY(), gateCenter.getZ() - pos.getZ()).normalize();
            vectorToGate = vectorToGate.multiply(motionFactor, motionFactor, motionFactor);
            suckBlock(level, blockPos, vectorToGate);
        });
    }

    @ParametersAreNonnullByDefault
    protected void suckBlock(ServerLevel level, BlockPos pos, Vec3 forceVector) {
        if (level.random.nextFloat() * 3f > forceVector.length()) return;
        var forceScalar = Math.abs(forceVector.length() * 10f + (level.random.nextFloat() - level.random.nextFloat()) * 2f);
        var blockstate = level.getBlockState(pos);
        if (blockstate.isAir()) return;
        if (!JSGConfigUtil.canBlackHoleDestroyBlock(blockstate)) return;
        var strength = blockstate.getDestroySpeed(level, pos);
        if ((strength * Math.min(1f, level.random.nextFloat() * 1.5f)) > forceScalar) return;
        level.destroyBlock(pos, true);
    }

    public float getBackVortexDepth() {
        if (!isConnectedToBlackHole) return 0;
        float f = (stargate.getTime() - isConnectedToBlackHoleFrom - TIME_BEFORE_DEPTH_CHANGE) / (float) BACK_VORTEX_FORMING_ANIMATION_TIME;
        return (isSource ? -1 : 1) * MathHelper.clamp(f, 0, 1);
    }

    public double getBackVortexAngle() {
        if (!isConnectedToBlackHole) return 0;
        double f = ((double) (stargate.getTime() - isConnectedToBlackHoleFrom) / (double) BACK_VORTEX_FORMING_ANIMATION_TIME);
        return (MathHelper.clamp(f, 0f, 4f) * (double) (stargate.getTime() - isConnectedToBlackHoleFrom)) / 2f;
    }

    public float getBackVortexRed() {
        if (!isConnectedToBlackHole) return 0;
        return MathHelper.clamp((Math.abs(getBackVortexDepth()) - 0.3f) / 0.7f, 0, 1f);
    }

    public float getGravitationalFieldStrength() {
        if (!isConnectedToBlackHole) return 0;
        return Math.abs(getBackVortexDepth());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isConnectedToBlackHole);
        buf.writeLong(isConnectedToBlackHoleFrom);
        buf.writeBoolean(isSource);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isConnectedToBlackHole = buf.readBoolean();
        isConnectedToBlackHoleFrom = buf.readLong();
        isSource = buf.readBoolean();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("isConnectedToBlackHole", isConnectedToBlackHole);
        compound.putLong("isConnectedToBlackHoleFrom", isConnectedToBlackHoleFrom);
        compound.putBoolean("isIncoming", isSource);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        isConnectedToBlackHole = compound.getBoolean("isConnectedToBlackHole");
        isConnectedToBlackHoleFrom = compound.getLong("isConnectedToBlackHoleFrom");
        isSource = compound.getBoolean("isIncoming");
    }
}
