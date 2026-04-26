package dev.tauri.jsg.common.stargate.rig;

import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.StargateWithIris;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.result.StargateConnectionStatus;
import dev.tauri.jsg.api.stargate.rig.IRIGWave;
import dev.tauri.jsg.common.stargate.manager.StargateConnection;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class RIGInstance {
    private final StargateRIGManager manager;
    private final int chevronCount;
    private final int animationLength;
    private final IRIGWave wave;
    private final long started;
    private boolean shouldOpenIris;

    private int state;
    private boolean isRunning;

    public RIGInstance(StargateRIGManager manager, IRIGWave wave, int chevronCount, int animationLength, boolean shouldOpenIris) {
        this.manager = manager;
        this.wave = wave;
        this.chevronCount = chevronCount;
        this.animationLength = animationLength;
        this.shouldOpenIris = shouldOpenIris;
        this.isRunning = true;
        this.started = manager.stargate.getTime();
        this.state = 0;
    }

    public void tick() {
        if (!isRunning) return;
        if (!manager.stargate.isMerged()) end(false);
        var tick = manager.stargate.getTime();
        if (state == 0) {
            doIncomingAnimation();
            state = 1;
        }
        if (state == 1 && (tick - started) >= animationLength) {
            openGate();
            state = 2;
        }
        if (state == 2 && (tick - started) >= (animationLength + 60)) {
            if (manager.stargate.getDialingManager().getStargateState().engaged())
                trySpawnEntity();
            else if (manager.stargate.getDialingManager().getStargateState().idle())
                end(false);
        }
    }

    public void end(boolean closeGate) {
        if (!isRunning) return;
        isRunning = false;
        state = 0;
        manager.stargate.setStargateChanged();
        if (!closeGate || manager.stargate.getDialingManager().getStargateState().idle()) return;
        manager.stargate.getDialingManager().attemptClose(StargateClosedReasonEnum.AUTOCLOSE);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isGateActive() {
        return isRunning && state > 0;
    }

    // STATES

    private void doIncomingAnimation() {
        var gate = manager.stargate;
        var dialingManager = (StargateAbstractDialingManager<?>) gate.getDialingManager();
        var connection = dialingManager.getConnection();
        if (!connection.getStatus().none() || !dialingManager.canAcceptConnectionFrom(null)) {
            end(false);
            return;
        }
        if (!connection.establishRIG(false, false)) {
            end(false);
            return;
        }
        connection.updateStatus(StargateConnectionStatus.WAITING_FOR_WORMHOLE);
        dialingManager.runIncomingWormhole(chevronCount, animationLength - 2);
    }

    private void openGate() {
        var gate = manager.stargate;
        var dialingManager = gate.getDialingManager();
        var connection = (StargateConnection) dialingManager.getConnection();
        if (!connection.isRIG() || !dialingManager.getStargateState().incoming() || dialingManager.getStargateState().engaged() || dialingManager.getStargateState().unstable()) {
            end(true);
            return;
        }
        if (!dialingManager.attemptOpenDialed().ok()) {
            end(true);
            return;
        }

        nextEntitySpawnIn = 0;
        manager.stargate.setStargateChanged();
    }

    private long nextEntitySpawnIn;

    private void trySpawnEntity() {
        if (wave.hasFinished()) {
            end(true);
            return;
        }
        var gate = manager.stargate;
        var level = (ServerLevel) gate.getStargateLevel();
        if (level == null) {
            end(true);
            return;
        }
        var tick = level.getGameTime();
        if (nextEntitySpawnIn < 1)
            nextEntitySpawnIn = (long) (tick + (((level.getRandom().nextDouble() * 5) + 1) * 7L));
        if (tick < nextEntitySpawnIn) return;
        nextEntitySpawnIn = 0;
        if (gate instanceof StargateWithIris<?> irisGate) {
            var irisManager = irisGate.getIrisManager();
            if (irisManager.isIrisClosed()) {
                if (shouldOpenIris && (irisManager.getIrisMode() == EnumIrisMode.AUTO || irisManager.getIrisMode() == EnumIrisMode.OC)) {
                    nextEntitySpawnIn = (tick + 4 * 20L);
                    irisManager.toggleIris();
                    shouldOpenIris = false;
                    return;
                }

                irisManager.hitIris();
                // move to next entity
                wave.getNextEntity(level);
                return;
            }
        }

        var entity = wave.getNextEntity(level);
        var traveler = gate.getEventHorizonManager().getRIGTraveler(level, entity, new Vec3(0, 0, -0.01f));
        if (traveler != null)
            gate.getEventHorizonManager().receive(traveler);
    }
}
