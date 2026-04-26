package dev.tauri.jsg.common.stargate.manager;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.event.StargateReceiveTravelerEvent;
import dev.tauri.jsg.api.event.StargateSendTravelerEvent;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateWithIris;
import dev.tauri.jsg.api.stargate.manager.IStargateEventHorizonManager;
import dev.tauri.jsg.api.stargate.traveler.IStargateTeleporter;
import dev.tauri.jsg.api.stargate.traveler.IStargateTraveler;
import dev.tauri.jsg.api.stargate.traveler.TravelerSendResult;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateOrlinBaseBE;
import dev.tauri.jsg.common.config.JSGConfigUtil;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.stargate.teleportation.StargateKillingHelper;
import dev.tauri.jsg.common.stargate.teleportation.StargateTeleporter;
import dev.tauri.jsg.common.stargate.teleportation.traveler.*;
import dev.tauri.jsg.common.state.stargate.StargateFlashState;
import dev.tauri.jsg.common.state.stargate.StargateVaporizeBlockParticlesRequest;
import dev.tauri.jsg.common.util.JSGAdvancementsUtil;
import dev.tauri.jsg.core.common.blockentity.ScheduledTaskExecutorInterface;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.core.common.util.RotationUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static dev.tauri.jsg.common.util.JSGAdvancementsUtil.tryTriggerRangedAdvancement;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StargateEventHorizonManager extends AbstractStargateManager<Stargate<?>> implements IStargateEventHorizonManager, ScheduledTaskExecutorInterface {
    protected JSGAxisAlignedBB serverBoundingBoxTeleport;
    protected JSGAxisAlignedBB serverBoundingBoxFront;
    protected JSGAxisAlignedBB serverBoundingBoxBack;

    /**
     * Holds entities that have been in the front virtual box last tick. If they are in this tick in the back box, teleport them... (they have probably gone through the teleport box in subtick)
     */
    protected final List<Entity> entitiesInFrontBoxLastTick = new ArrayList<>();
    /**
     * Holds entities that have recently gone through the gate out - means its list of entities that the RECEIVING gate received
     */
    protected final List<Entity> entitiesRecentlyGoThrough = new ArrayList<>();
    protected List<JSGAxisAlignedBB> localInnerEntityBoxes;
    protected List<JSGAxisAlignedBB> localInnerBlockBoxes;
    protected List<JSGAxisAlignedBB> localKillingBoxes;

    protected boolean horizonKilling = false;
    protected int horizonSegments = 0;

    public StargateEventHorizonManager(Stargate<?> stargate) {
        super(stargate);
    }

    @Override
    public void onLoad(Level level) {
        initBoxes();
    }

    protected void initBoxes() {
        serverBoundingBoxTeleport = getTeleportBox().offset(0.5, -0.25, 0.5);
        serverBoundingBoxFront = serverBoundingBoxTeleport
                .offset(0, 0, serverBoundingBoxTeleport.maxZ)
                .setMaxZ(serverBoundingBoxTeleport.maxZ * 5);
        serverBoundingBoxBack = serverBoundingBoxTeleport
                .offset(0, 0, serverBoundingBoxTeleport.minZ - serverBoundingBoxTeleport.maxZ * 5)
                .setMaxZ(serverBoundingBoxTeleport.minZ);

        serverBoundingBoxTeleport = stargate.relative(serverBoundingBoxTeleport, new Vec3(0.5, 0.5, 0.5));
        serverBoundingBoxFront = stargate.relative(serverBoundingBoxFront, new Vec3(0.5, 0.5, 0.5));
        serverBoundingBoxBack = stargate.relative(serverBoundingBoxBack, new Vec3(0.5, 0.5, 0.5));

        var kBox = getHorizonKillingBox();
        double width = kBox.maxZ - kBox.minZ;
        width /= getKawooshSegmentsCount();

        Vec3 offset = new Vec3(0.5, 0, 0.5);

        localKillingBoxes = new ArrayList<>();
        for (int i = 0; i < getKawooshSegmentsCount(); i++) {
            JSGAxisAlignedBB box = new JSGAxisAlignedBB(kBox.minX, kBox.minY, kBox.minZ + width * i, kBox.maxX, kBox.maxY, kBox.minZ + width * (i + 1));
            localKillingBoxes.add(stargate.rotated(box.offset(offset), new Vec3(0.5, 0.5, 0.5)));
        }

        localInnerBlockBoxes = new ArrayList<>();
        localInnerEntityBoxes = new ArrayList<>();

        for (JSGAxisAlignedBB lBox : getGateVaporizingBoxes()) {
            localInnerBlockBoxes.add(stargate.rotated(lBox.offset(offset), new Vec3(0.5, 0.5, 0.5)));
            localInnerEntityBoxes.add(stargate.rotated(lBox.grow(0, 0, -0.25).offset(offset), new Vec3(0.5, 0.5, 0.5)));
        }
    }

    public void onFacingUpdated() {
        initBoxes();
    }

    public abstract JSGAxisAlignedBB getTeleportBox();

    public abstract boolean getForceUnstable();

    public List<JSGAxisAlignedBB> getLocalKillingBoxes() {
        return localKillingBoxes;
    }

    public List<JSGAxisAlignedBB> getLocalInnerBlockBoxes() {
        return localInnerBlockBoxes;
    }

    public JSGAxisAlignedBB getLocalTeleportBox() {
        return serverBoundingBoxTeleport.offset(stargate.blockPosition().multiply(-1));
    }

    public JSGAxisAlignedBB getLocalTeleportBoxBack() {
        return serverBoundingBoxBack.offset(stargate.blockPosition().multiply(-1));
    }

    public JSGAxisAlignedBB getLocalTeleportBoxFront() {
        return serverBoundingBoxFront.offset(stargate.blockPosition().multiply(-1));
    }

    /**
     * How many segments should the exclusion zone have.
     *
     * @return Count of subsegments of the killing box.
     */
    public abstract int getKawooshSegmentsCount();

    /**
     * The event horizon in the gate also should kill
     * and vaporize everything
     *
     * @return List of {@link AABB} for the inner gate area.
     */
    public abstract List<JSGAxisAlignedBB> getGateVaporizingBoxes();

    /**
     * Gets full {@link AABB} of the killing area.
     *
     * @return Approximate kawoosh size.
     */
    protected abstract JSGAxisAlignedBB getHorizonKillingBox();

    public int getTicksPerKawooshSegment() {
        return 12 / getKawooshSegmentsCount();
    }

    // --------------------------------------------------
    // EVENT HORIZON TELEPORTING

    public TravelerSendResult send(IStargateTraveler<?> traveler) {
        if (traveler.isStatic()) {
            traveler.send();
            return TravelerSendResult.OK;
        }
        if (!traveler.canBeSendToTarget()) {
            return TravelerSendResult.BLOCKED_BY_EVENT;
        }
        if (new StargateSendTravelerEvent(traveler.getTransmitter(), traveler.getReceiver(), traveler).post()) {
            return TravelerSendResult.BLOCKED_BY_EVENT;
        }
        traveler.send();
        StargateComputerEvents.EH_TRAVELER.apply(false, (traveler.get() instanceof Entity e ? e.getType() : null)).sendVia(stargate);
        return TravelerSendResult.OK;
    }

    public void receive(IStargateTraveler<?> traveler) {
        traveler.place();
        if (traveler.isStatic()) return;
        new StargateReceiveTravelerEvent(traveler.getTransmitter(), traveler.getReceiver(), traveler).post();
        if (traveler.getTransmitter() != null) // rig entity has null transmitter
            ((StargateEventHorizonManager) traveler.getTransmitter().getEventHorizonManager()).remove(traveler);
        stargate.playSoundEvent(JSGSoundEvents.WORMHOLE_GO);
        StargateComputerEvents.EH_TRAVELER.apply(true, (traveler.get() instanceof Entity e ? e.getType() : null)).sendVia(stargate);

        if (stargate instanceof StargateWithIris<?> irisGate && irisGate.getIrisManager().isIrisClosed()) {
            traveler.killIris();
            irisGate.getIrisManager().hitIris();
            return;
        }

        if (traveler.get() instanceof Entity e)
            entitiesRecentlyGoThrough.add(e);
    }

    public Vec3 processMotionVector(Vec3 vector, boolean transmittingEnd) {
        if (!transmittingEnd) return vector;
        if (stargate.getDialingManager().getDialedAddressSize() >= 9)
            return vector.multiply(1, 1, 2).add(0, 0, 0.5);
        return vector;
    }

    public Vec3 processPositionVector(Vec3 vector, boolean transmittingEnd) {
        if (transmittingEnd) {
            if (stargate instanceof StargateOrlinBaseBE)
                return new Vec3(vector.x, 2.3, vector.z);
            return vector;
        }
        if (stargate instanceof StargateOrlinBaseBE)
            return new Vec3(0.5, 2.3, 0.5);
        return vector;
    }

    public Vec3 getPositionVectorRotationPivot() {
        if (stargate instanceof StargateOrlinBaseBE)
            return new Vec3(0.5, 0.5, 0.5);
        return new Vec3(0, 0, 0);
    }

    @Nullable
    public IStargateTraveler<?> getTraveler(Entity entity) {
        return getTraveler(entity, entity.getDeltaMovement());
    }

    @Nullable
    public IStargateTraveler<?> getRIGTraveler(ServerLevel level, Entity entity, Vec3 originalMotion) {
        var connection = stargate.getDialingManager().getConnection();
        if (connection.getStatus().none()) return null;
        var newPos = new Vec3(
                stargate.getGateCenterPos().getX() - stargate.blockPosition().getX() + ((entity.level().random.nextFloat() * 2f) - 1f) + 0.5f,
                2 + entity.level().random.nextFloat(),
                0.5f);
        newPos = processPositionVector(newPos, connection.isInitiating());
        newPos = stargate.relative(newPos);

        var newMotionVector = originalMotion;
        newMotionVector = processMotionVector(newMotionVector, connection.isInitiating());
        newMotionVector = stargate.rotated(newMotionVector);

        var newYaw = stargate.getFacing().toYRot();
        entity.setPos(newPos);
        entity.setYRot(newYaw);
        if (!level.tryAddFreshEntityWithPassengers(entity)) return null;
        return getTraveler(entity, newPos, originalMotion, newMotionVector, newYaw, null, stargate);
    }

    @Nullable
    public IStargateTraveler<?> getTraveler(Entity entity, Vec3 originalMotion) {
        var connection = stargate.getDialingManager().getConnection();
        if (connection.getStatus().none()) return null;
        return connection.callConnected((c, targetGate) -> {
            // get entity position in relative to the receiving gate
            var newPos = stargate.getRelative(entity.position()).add(0, 0, 1);
            // process the position by gates (Orlin gate needs the positon to shrink because of it's size)
            var targetManager = ((StargateEventHorizonManager) targetGate.getEventHorizonManager());
            newPos = targetManager.processPositionVector(processPositionVector(newPos, !c.isInitiating()), c.isInitiating());
            // get the new position rotated by the target gate
            newPos = targetGate.relative(newPos, targetManager.getPositionVectorRotationPivot());

            // same for the motion except we are not offsetting it by the gate pos - only rotate
            var newMotionVector = stargate.getRelativeRotated(originalMotion);
            newMotionVector = ((StargateEventHorizonManager) targetGate.getEventHorizonManager()).processMotionVector(processMotionVector(newMotionVector, connection.isInitiating()), c.isInitiating());
            newMotionVector = targetGate.rotated(newMotionVector);

            float yaw = entity.getYRot();
            var yawVec = RotationUtil.yawToVector(yaw);
            yawVec = stargate.getRelativeRotated(yawVec);
            yawVec = targetGate.rotated(yawVec);
            var newYaw = RotationUtil.vectorToYaw(yawVec);
            return stargate.getEventHorizonManager().getTraveler(entity, newPos, originalMotion, newMotionVector, newYaw, stargate, targetGate);
        }, () -> null);
    }

    public IStargateTraveler<?> getStaticTraveler(Stargate<?> targetGate, Entity entity) {
        var newPos = new Vec3(0.5, 2.5, 0.5);
        newPos = ((StargateEventHorizonManager) targetGate.getEventHorizonManager()).processPositionVector(processPositionVector(newPos, true), false);
        newPos = targetGate.relative(newPos);

        float yaw = entity.getYRot();
        return getTraveler(entity, newPos, new Vec3(0, 0, 0), new Vec3(0, 0, 0), yaw, stargate, targetGate, true);
    }

    public IStargateTraveler<?> getTraveler(Entity entity, IStargateTraveler<?> traveler) {
        if (entity instanceof ServerPlayer sp)
            return new PlayerTraveler(sp, traveler.getDestinationPos(), traveler.getOriginalMotion(), traveler.getDestinationMotion(), traveler.getDestinationYaw(), traveler.getTransmitter(), traveler.getReceiver(), traveler.isStatic());
        if (entity instanceof AbstractHurtingProjectile fireball)
            return new FireballTraveler(fireball, traveler.getDestinationPos(), traveler.getOriginalMotion(), traveler.getDestinationMotion(), traveler.getDestinationYaw(), traveler.getTransmitter(), traveler.getReceiver(), traveler.isStatic());
        if (entity instanceof AbstractMinecart minecart)
            return new MinecartTraveler(minecart, traveler.getDestinationPos(), traveler.getOriginalMotion(), traveler.getDestinationMotion(), traveler.getDestinationYaw(), traveler.getTransmitter(), traveler.getReceiver(), traveler.isStatic());
        if (entity instanceof LivingEntity livingEntity)
            return new LivingTraveler<>(livingEntity, traveler.getDestinationPos(), traveler.getOriginalMotion(), traveler.getDestinationMotion(), traveler.getDestinationYaw(), traveler.getTransmitter(), traveler.getReceiver(), traveler.isStatic());
        return new EntityTraveler<>(entity, traveler.getDestinationPos(), traveler.getOriginalMotion(), traveler.getDestinationMotion(), traveler.getDestinationYaw(), traveler.getTransmitter(), traveler.getReceiver(), traveler.isStatic());
    }

    public IStargateTraveler<?> getTraveler(Entity entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> targetGate, boolean isStatic) {
        if (entity instanceof ServerPlayer sp)
            return new PlayerTraveler(sp, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, targetGate, isStatic);
        if (entity instanceof AbstractHurtingProjectile fireball)
            return new FireballTraveler(fireball, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, targetGate, isStatic);
        if (entity instanceof AbstractMinecart minecart)
            return new MinecartTraveler(minecart, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, targetGate, isStatic);
        if (entity instanceof LivingEntity livingEntity)
            return new LivingTraveler<>(livingEntity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, targetGate, isStatic);
        return new EntityTraveler<>(entity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, targetGate, isStatic);
    }

    @Override
    public IStargateTeleporter getTeleporter(IStargateTraveler<?> traveler, Consumer<IStargateTraveler<?>> afterPlace) {
        return new StargateTeleporter(traveler, afterPlace);
    }

    @Override
    public boolean canBeSend(IStargateTraveler<?> traveler) {
        if (!isMovingTowardsGate(traveler.getOriginalMotion())) {
            remove(traveler);
            return false;
        }

        traveler.getTransmitter().playSoundEvent(JSGSoundEvents.WORMHOLE_GO);

        if (traveler.isPlayer() || traveler.get() instanceof Villager)
            ((StargateAutoCloseManager) traveler.getTransmitter().getAutoCloseManager()).playerPassing();

        var irisKillAtDestination = JSGConfig.Stargate.killAtDestination.get();
        if (!irisKillAtDestination) {
            if (traveler.getReceiver() instanceof StargateClassicBaseBE<?> classicGate && classicGate.getIrisManager().isIrisClosed()) {
                classicGate.playSoundEvent(JSGSoundEvents.WORMHOLE_GO);
                classicGate.getIrisManager().hitIris();
                traveler.killIris();
                return false;
            }
        }
        return true;
    }

    protected final Map<IStargateTraveler<?>, Boolean> entitiesWaitingToTeleport = new HashMap<>();

    /**
     * @param teleport true if it should teleport them, false if it should kill them (wrong way travel)
     */
    protected void scanForEntities(boolean teleport) {
        var level = stargate.getStargateLevel();
        if (level == null) return;

        if (!teleport && !JSGConfig.Stargate.wrongSideKilling.get()) return;

        var entitiesTPBox = level.getEntities(null, serverBoundingBoxTeleport);
        var entitiesFrontBox = level.getEntities(null, serverBoundingBoxFront);
        var entitiesBackBox = level.getEntities(null, serverBoundingBoxBack);
        var entitiesInAnyBox = new ArrayList<Entity>();

        // scan teleport box for entities and add them
        for (var e : entitiesTPBox) {
            entitiesInAnyBox.add(e);
            if (entitiesRecentlyGoThrough.contains(e)) continue;
            var packet = getTraveler(e);
            if (entitiesWaitingToTeleport.containsKey(packet)) continue;
            if (packet == null) continue;
            entitiesWaitingToTeleport.put(packet, false);
        }

        // scan back box for entities and add them if they were in front box previous tick
        for (var e : entitiesBackBox) {
            entitiesInAnyBox.add(e);
            if (entitiesRecentlyGoThrough.contains(e)) continue;
            if (!entitiesInFrontBoxLastTick.contains(e)) continue;
            var packet = getTraveler(e);
            if (entitiesWaitingToTeleport.containsKey(packet)) continue;
            entitiesInFrontBoxLastTick.remove(e);
            if (packet == null) continue;
            entitiesWaitingToTeleport.put(packet, false);
        }

        // scan the front box for new entities that can potentially go through in next subtick
        entitiesInFrontBoxLastTick.clear();
        for (var e : entitiesFrontBox) {
            entitiesInAnyBox.add(e);
            if (entitiesInFrontBoxLastTick.contains(e)) continue;
            if (entitiesRecentlyGoThrough.contains(e)) continue;
            entitiesInFrontBoxLastTick.add(e);
        }

        // update the last teleported entities list
        for (var e : new ArrayList<>(entitiesRecentlyGoThrough)) {
            if (entitiesInAnyBox.contains(e)) continue;
            if (e instanceof ServerPlayer sp && sp.noPhysics) continue;
            entitiesRecentlyGoThrough.remove(e);
        }

        // teleport them
        for (var packet : new ArrayList<>(entitiesWaitingToTeleport.entrySet())) {
            var traveler = packet.getKey();
            if (packet.getValue()) continue;
            if (traveler.get() instanceof Entity e && entitiesRecentlyGoThrough.contains(e)) continue;

            entitiesWaitingToTeleport.put(traveler, true);
            if (traveler.canBeSendThisWay(teleport)) {
                if (isCurrentlyUnstable && level.random.nextFloat() < JSGConfig.Stargate.ehDeathChance.get()) {
                    traveler.killUnstable();
                    remove(traveler);
                    continue;
                }
                send(traveler);
            } else {
                traveler.killWrongTravel();
            }
        }
    }

    public boolean isMovingTowardsGate(Vec3 motionVec) {
        if ((Math.abs(motionVec.x) + Math.abs(motionVec.z) + Math.abs(motionVec.y)) < 0.01) return true;
        var sourceFacing = (stargate.getFacingVertical() == Direction.SOUTH ? stargate.getFacing() : stargate.getFacingVertical());
        var axisMotion = motionVec.multiply(Vec3.atLowerCornerOf(sourceFacing.getNormal()));
        return ((axisMotion.y + axisMotion.z + axisMotion.x) <= 0);
    }

    public void remove(IStargateTraveler<?> traveler) {
        entitiesWaitingToTeleport.remove(traveler);
    }

    protected void reset() {
        entitiesWaitingToTeleport.clear();
        entitiesInFrontBoxLastTick.clear();
        resetFlashingSequence();
    }

    public void onGateOpen(boolean isNox) {
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_HORIZON_LIGHT_BLOCK, stargate.getOpenSoundDelay() + 19 + getTicksPerKawooshSegment()));
        if (!isNox)
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_HORIZON_WIDEN, stargate.getOpenSoundDelay() + 23 + getTicksPerKawooshSegment()));
        reset();
    }

    public void onGateClose() {
        reset();
    }


    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putBoolean("horizonKilling", horizonKilling);
        compound.putInt("horizonSegments", horizonSegments);
        compound.put("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        horizonKilling = compound.getBoolean("horizonKilling");
        horizonSegments = compound.getInt("horizonSegments");
        try {
            ScheduledTask.deserializeList(compound.getCompound("scheduledTasks"), scheduledTasks, this);
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            JSG.logger.warn("Exception at reading NBT");
            JSG.logger.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it", e);
        }
    }

    @Override
    public void tick(Level level) {
        ScheduledTask.iterate(scheduledTasks, stargate.getTime());
        if (level.isClientSide()) return;
        var connection = stargate.getDialingManager().getConnection();
        if (connection.getStatus().full() && (!(stargate instanceof StargateClassicBaseBE<?> classicBase) || !classicBase.getIrisManager().isIrisClosed())) {
            scanForEntities(connection.isInitiating());
        }
        kawooshDestruction();
        if (horizonFlashTask != null && horizonFlashTask.isActive()) {
            horizonFlashTask.update(stargate.getTime());
        }
        if (horizonKilling && cannotHorizonKill()) {
            horizonKilling = false;
            horizonSegments = 0;
            stargate.setStargateChanged();
        }
    }

    protected void kawooshDestruction() {
        if (!horizonKilling) return;
        var level = stargate.getStargateLevel();
        if (level == null) return;
        var pos = stargate.blockPosition();

        List<Entity> entities = new ArrayList<>();

        var blockDestroyBoxes = new ArrayList<>(localInnerBlockBoxes);
        // Get all blocks and entities inside the kawoosh
        for (int i = 0; i < horizonSegments; i++) {
            if (localKillingBoxes.size() > i) {
                var gBox = localKillingBoxes.get(i).offset(pos);
                entities.addAll(level.getEntities(null, gBox));
                blockDestroyBoxes.add(new JSGAxisAlignedBB(gBox.offset(pos.multiply(-1))));
            }
        }

        // Get all entities inside the gate
        for (JSGAxisAlignedBB lBox : localInnerEntityBoxes)
            entities.addAll(level.getEntities(null, lBox.offset(pos)));

        // Kill them
        for (Entity entity : entities) {
            StargateKillingHelper.kawooshKill(entity);
            JSGCorePacketHandler.sendToClient(new StateUpdatePacketToClient(pos, JSGStateTypes.STARGATE_VAPORIZE_BLOCK_PARTICLES, new StargateVaporizeBlockParticlesRequest(entity.blockPosition(), false)), stargate.getStateManager().getTargetPoint());
        }

        // Vaporize blocks
        for (var lBox : blockDestroyBoxes) {
            var gBox = lBox.offset(pos);
            new BlockCollisions<>(level, null, gBox, false, (p, s) -> p).forEachRemaining(dPos -> {
                BlockState state = level.getBlockState(dPos);
                if (dPos.equals(stargate.getGateCenterPos()) && state.is(Blocks.LIGHT)) return;
                if (!level.getBlockState(dPos).isAir() && state.getDestroySpeed(level, dPos) >= 0.0f && JSGConfigUtil.canKawooshDestroyBlock(state)) {
                    if (state.is(Blocks.WATER)) {
                        var waterCount = 0;
                        for (var dir : Direction.values()) {
                            if (dir == Direction.DOWN) continue;
                            if (level.getBlockState(dPos.relative(dir)).is(Blocks.WATER) && level.getFluidState(dPos.relative(dir)).isSource())
                                waterCount++;
                        }
                        if (waterCount <= 2)
                            level.setBlockAndUpdate(dPos, Blocks.AIR.defaultBlockState());
                    } else
                        level.setBlockAndUpdate(dPos, Blocks.AIR.defaultBlockState());
                    JSGCorePacketHandler.sendToClient(new StateUpdatePacketToClient(pos, JSGStateTypes.STARGATE_VAPORIZE_BLOCK_PARTICLES, new StargateVaporizeBlockParticlesRequest(dPos, state.is(Blocks.WATER))), stargate.getStateManager().getTargetPoint());
                }
            });
        }
    }

    // -----------------------------------------------------------------
    // Horizon flashing

    protected ScheduledTask horizonFlashTask;

    protected void setHorizonFlashTask(ScheduledTask horizonFlashTask) {
        horizonFlashTask.setExecutor(this);
        horizonFlashTask.setTaskCreated(stargate.getTime());

        this.horizonFlashTask = horizonFlashTask;
        stargate.setStargateChanged();
    }

    protected int flashIndex = 0;
    public boolean isCurrentlyUnstable = false;

    /**
     * Defines if gate should be unstable
     * - this variable is used for OC methods
     */
    public boolean shouldBeUnstable = false;

    protected void resetFlashingSequence() {
        flashIndex = 0;
        isCurrentlyUnstable = false;
        shouldBeUnstable = false;
        horizonFlashTask = null;
    }

    protected void updateFlashState() {
        stargate.getDialingManager().getConnection().runOnBothConnected((conn, sg) -> {
            JSGCorePacketHandler.sendToClient(new StateUpdatePacketToClient(sg.blockPosition(), JSGStateTypes.FLASH_STATE, new StargateFlashState(isCurrentlyUnstable)), sg.getStateManager().getTargetPoint());

            if (isCurrentlyUnstable && flashIndex == 1) {
                tryTriggerRangedAdvancement(sg, JSGAdvancementsUtil.EnumAdvancementType.GATE_FLICKER);
                JSGSoundHelper.playSoundEvent(sg.getStargateLevel(), sg.getGateCenterPos(), JSGSoundEvents.WORMHOLE_FLICKER, 0.9f + (sg.getRandom().nextFloat() * 0.2f));
            }
        });
    }

    public void updateUnstability(double energySecondsToClose, int energyTransferredLastTick) {
        if (!stargate.getDialingManager().getStargateState().engaged()) {
            resetFlashingSequence();
            return;
        }
        boolean forceUnstable = getForceUnstable();
        // Horizon becomes unstable
        if (horizonFlashTask == null && (forceUnstable || (energySecondsToClose < JSGConfig.Stargate.instabilitySeconds.get() && energyTransferredLastTick < 0))) {
            resetFlashingSequence();
            shouldBeUnstable = true;
            // Schedule next flash sequence
            double mul = stargate.getEnergyManager().getSecondsToClose() / (double) JSGConfig.Stargate.instabilitySeconds.get();
            if (getForceUnstable()) {
                mul = 0.01;
            }
            var min = (15f * (mul * mul) * 2);
            var off = (20f * (mul * mul) * 2);
            min = Math.max(5, min);
            setHorizonFlashTask(new ScheduledTask(JSGScheduledTaskTypes.HORIZON_FLASH, (int) (min + (stargate.getRandom().nextDouble() * off))));
            StargateComputerEvents.EH_UNSTABLE.apply(stargate.getDialingManager().getConnection().isInitiating()).sendVia(stargate);
        }
        // Horizon becomes stable
        if (horizonFlashTask != null && (!forceUnstable && (energySecondsToClose > JSGConfig.Stargate.instabilitySeconds.get() || energyTransferredLastTick >= 0))) {
            horizonFlashTask = null;
            isCurrentlyUnstable = false;
            shouldBeUnstable = false;
            StargateComputerEvents.EH_STABILIZED.apply(stargate.getDialingManager().getConnection().isInitiating()).sendVia(stargate);

            updateFlashState();
        }
    }

    // ------------------------------------------------------------------------
    // Scheduled tasks

    protected List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(stargate.getTime());

        if (scheduledTask.getWaitTime() <= 0) {
            scheduledTask.execute();
            return;
        }

        scheduledTasks.add(scheduledTask);
        stargate.setStargateChanged();
    }

    public boolean cannotHorizonKill() {
        var sgState = stargate.getDialingManager().getStargateState();
        var connectionState = stargate.getDialingManager().getConnection().getStatus();
        if (sgState != EnumStargateState.UNSTABLE_OPENING) return true;
        return connectionState.full() || connectionState.none();
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_HORIZON_WIDEN.get()) {
            if (cannotHorizonKill()) {
                horizonKilling = false;
                horizonSegments = 0;
                stargate.setStargateChanged();
                return;
            }
            if (!horizonKilling) horizonKilling = true;

            horizonSegments++;
            if (horizonSegments < getKawooshSegmentsCount())
                addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_HORIZON_WIDEN, getTicksPerKawooshSegment()));
            else
                addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_HORIZON_SHRINK, getTicksPerKawooshSegment() + 12));
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_HORIZON_SHRINK.get()) {
            if (cannotHorizonKill()) {
                horizonKilling = false;
                horizonSegments = 0;
                stargate.setStargateChanged();
                return;
            }
            horizonSegments--;

            if (horizonSegments > 0)
                addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_HORIZON_SHRINK, getTicksPerKawooshSegment() + 1));
            else horizonKilling = false;

            stargate.setStargateChanged();
        } else if (scheduledTask == JSGScheduledTaskTypes.HORIZON_FLASH.get()) {
            isCurrentlyUnstable ^= true;

            if (isCurrentlyUnstable) {
                flashIndex++;
                // Schedule change into stable state
                if (flashIndex >= 2)
                    setHorizonFlashTask(new ScheduledTask(JSGScheduledTaskTypes.HORIZON_FLASH, 2));
                else
                    setHorizonFlashTask(new ScheduledTask(JSGScheduledTaskTypes.HORIZON_FLASH, (int) (stargate.getRandom().nextDouble() * 3) + 3));
            } else {
                if (flashIndex == 1)
                    // Schedule second flash
                    setHorizonFlashTask(new ScheduledTask(JSGScheduledTaskTypes.HORIZON_FLASH, (int) (stargate.getRandom().nextDouble() * 4) + 1));
                else {
                    if (shouldBeUnstable && stargate.getRandom().nextFloat() < 0.5f) {
                        flashIndex = 0;
                        setHorizonFlashTask(new ScheduledTask(JSGScheduledTaskTypes.HORIZON_FLASH, (int) (stargate.getRandom().nextDouble() * 4) + 1));
                    } else resetFlashingSequence();
                }
            }
            updateFlashState();
            stargate.setStargateChanged();
        }
    }
}
