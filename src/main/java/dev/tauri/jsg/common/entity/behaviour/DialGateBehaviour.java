package dev.tauri.jsg.common.entity.behaviour;

import com.google.common.collect.ImmutableMap;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargatePegasusBaseBE;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class DialGateBehaviour extends Behavior<Villager> {

    protected DHDAbstractBE dhd;
    protected Pair<BlockPos, DHDAbstractBE> lastFoundDHDForPos;
    protected Vec3 targetPos;
    protected StargateAbstractBaseBE<?, ?> gate;
    protected StargateAddressDynamic address;
    protected int addressPosition = 0;
    protected long canStartAt = -1;
    protected boolean gateShouldBeOpened = false;

    public DialGateBehaviour() {
        super(ImmutableMap.of());
    }

    @Nullable
    protected DHDAbstractBE findDHD(Villager owner) {
        if (lastFoundDHDForPos != null) {
            if (owner.blockPosition().closerThan(lastFoundDHDForPos.first(), 20)) {
                return lastFoundDHDForPos.second();
            }
        }
        var dhd = LinkingHelper.findClosestTile(owner.level(), owner.blockPosition(), JSGBlockTags.DHD_ANY, DHDAbstractBE.class, 50, 50);
        lastFoundDHDForPos = Pair.of(owner.blockPosition(), dhd);
        return lastFoundDHDForPos.second();
    }

    @Nullable
    protected StargateAddressDynamic getRandomVillageAddress(Villager owner) {
        if (dhd == null || gate == null) return null;
        var symbolType = dhd.getSymbolType();
        boolean hasUpgrade = dhd.hasUpgrade(DHDAbstractBE.DHDUpgradeEnum.CHEVRON_UPGRADE) && owner.getVillagerData().getLevel() > 1;
        var stargateType = hasUpgrade ? StargateType.getRandomClassic(owner.getRandom()) : gate.getStargateType();
        if (stargateType == null) return null;
        var randomGate = StargateNetwork.INSTANCE.getRandomAddress(owner.getRandom(), symbolType, stargateType, null);
        if (randomGate == null) return null;
        var neededSymbols = gate.getDialingManager().getMinimalSymbolsToDial(randomGate.first().getGateSymbolType(), randomGate.first());
        var address = new StargateAddressDynamic(symbolType, randomGate.second().subList(0, neededSymbols - 1));
        address.addSymbol(symbolType.getOrigin());
        return address;
    }


    @Override
    @ParametersAreNonnullByDefault
    public boolean checkExtraStartConditions(ServerLevel pLevel, Villager owner) {
        if (canStartAt == -1)
            canStartAt = pLevel.getGameTime() + (120 * 20);
        if (pLevel.getGameTime() < canStartAt) return false;
        if (owner.getRandom().nextFloat() > 0.3f) return false;
        dhd = findDHD(owner);
        if (dhd == null) return false;
        gate = dhd.getLinkedDevice();
        if (gate == null) return false;
        if (!gate.isMerged()) return false;
        if (!gate.getDialingManager().getStargateState().idle()) return false;
        if (gate.getDialingManager().getDialedAddressSize() != 0) return false;
        address = getRandomVillageAddress(owner);
        if (address == null) return false;
        return gate.getDialingManager().canDialAddress(address, true);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    @ParametersAreNonnullByDefault
    public boolean canStillUse(ServerLevel pLevel, Villager owner, long pGameTime) {
        return dhd != null && dhd.isLinked() && gate != null && !owner.getBrain().isActive(Activity.PANIC);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    @ParametersAreNonnullByDefault
    protected void start(ServerLevel pLevel, Villager owner, long pGameTime) {
        super.start(pLevel, owner, pGameTime);
        targetPos = dhd.getBlockPosInFront();
        addressPosition = 0;
        canStartAt = pLevel.getGameTime() + (120 * 20);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    @ParametersAreNonnullByDefault
    protected void stop(ServerLevel pLevel, Villager owner, long pGameTime) {
        super.stop(pLevel, owner, pGameTime);
        if (targetPos != null && owner.getNavigation().getPath() != null && !owner.getNavigation().getPath().getTarget().equals(BlockPos.containing(targetPos)))
            owner.getNavigation().stop();
        gate = null;
        dhd = null;
        address = null;
        gateShouldBeOpened = false;
        addressPosition = 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    @ParametersAreNonnullByDefault
    protected void tick(ServerLevel pLevel, Villager owner, long pGameTime) {
        if (this.getStatus() != Status.RUNNING) return;
        if (targetPos == null || dhd == null || gate == null || address == null || dhd.getLevel() == null || !(dhd.getLevel().getBlockEntity(dhd.getBlockPos()) instanceof DHDAbstractBE) || !dhd.isLinked()) {
            doStop(pLevel, owner, pGameTime);
            return;
        }
        if (owner.getNavigation().getPath() != null && !owner.getNavigation().getPath().getTarget().equals(BlockPos.containing(targetPos)))
            owner.getNavigation().stop();
        if ((!gateShouldBeOpened && (gate.getDialingManager().getStargateState().engaged() || gate.getDialingManager().getStargateState().unstable())) || gate.getDialingManager().getStargateState().failing()) {
            doStop(pLevel, owner, pGameTime);
            return;
        }
        if (gateShouldBeOpened && gate.getDialingManager().getStargateState().idle()) {
            doStop(pLevel, owner, pGameTime);
            return;
        }
        if (!gateShouldBeOpened && gate.getDialingManager().getDialedAddress().size() > addressPosition) {
            // in case somebody added symbol...
            doStop(pLevel, owner, pGameTime);
            return;
        }
        if (gateShouldBeOpened && gate.getDialingManager().getStargateState().initiating()) {
            targetPos = gate.getGateCenterPos().getCenter();
        }

        if (targetPos.distanceTo(owner.position()) > 50) {
            doStop(pLevel, owner, pGameTime);
            return;
        }

        var speed = owner.getAttributeValue(Attributes.MOVEMENT_SPEED) * (gateShouldBeOpened ? 1.3f : 1f);

        var tempPos = new Vec3(targetPos.x(), owner.getY(), targetPos.z());
        if (tempPos.distanceTo(owner.position()) <= (gateShouldBeOpened ? 1 : 0.5)) {
            if (gateShouldBeOpened) {
                // probably in gate
                if (gate.getDialingManager().getStargateState().initiating() || gate.getDialingManager().getStargateState().idle())
                    doStop(pLevel, owner, pGameTime);
                return;
            }
            owner.getLookControl().setLookAt(dhd.getBlockPos().getCenter());

            if (!(gate.getDialingManager().getStargateState().idle() || (gate instanceof StargatePegasusBaseBE pegasusBaseBE && pegasusBaseBE.getDialingManager().getStargateState().dialingDHD())))
                return;

            if (pLevel.getGameTime() % 20 != 0) return;

            if (gate.getDialingManager().getDialedAddress().getLast() == gate.getSymbolType().getOrigin()) {
                // origin was pressed before - press BRB
                dhd.pushSymbolButton(dhd.getSymbolType().getBRB(), null, false);
                owner.getLookControl().setLookAt(dhd.getBlockPos().getCenter().add(0, 1, 0));
                gateShouldBeOpened = true;
                return;
            }
            if (addressPosition > gate.getDialingManager().getDialedAddressSize())
                addressPosition--;

            var symbol = address.get(addressPosition);
            if (gate.getDialingManager().canAddSymbol(symbol, false) != StargateChevronEngageResult.OK) return;
            dhd.pushSymbolButton(symbol, null, false);
            owner.getLookControl().setLookAt(dhd.getBlockPos().getCenter().add(0, -0.5, 0));
            addressPosition++;
            return;
        }
        if (targetPos.distanceTo(owner.position()) <= 2.5) {
            owner.getMoveControl().setWantedPosition(targetPos.x(), targetPos.y(), targetPos.z(), speed);
            return;
        }
        owner.getNavigation().moveTo(owner.getNavigation().createPath(targetPos.x(), targetPos.y(), targetPos.z(), 1), speed);
    }

    @Override
    protected boolean timedOut(long pGameTime) {
        return false;
    }
}
