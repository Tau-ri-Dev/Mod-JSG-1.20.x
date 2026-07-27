package dev.tauri.jsg.common.dialhomedevice.manager.state;

import dev.tauri.jsg.api.dialhomedevice.manager.IDHDStateManager;
import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.client.renderer.blockentity.dialhomedevice.DHDAbstractRendererState;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.dialhomedevice.animation.DHDButtonsState;
import dev.tauri.jsg.common.dialhomedevice.manager.AbstractDHDManager;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.state.BiomeOverrideState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DHDAbstractStateManager<DHD extends DHDAbstractBE, S extends DHDAbstractRendererState> extends AbstractDHDManager<DHD> implements IDHDStateManager {
    private PacketDistributor.TargetPoint targetPoint;
    protected DHDButtonsState buttonsState;
    protected final List<IDHDPartItem> assembledParts = new ArrayList<>();

    public DHDAbstractStateManager(DHD dhd) {
        super(dhd);
        this.buttonsState = generateButtonsState();
    }

    protected abstract DHDButtonsState generateButtonsState();

    @Override
    public DHDButtonsState getButtonsState() {
        return buttonsState;
    }

    @ParametersAreNonnullByDefault
    public boolean isDHDPartAssembled(IDHDPartItem part) {
        return assembledParts.contains(part);
    }

    public void disassemblePart(IDHDPartItem part) {
        assembledParts.remove(part);
        getAndSendState(CoreStateTypes.RENDERER_STATE.get());
    }

    public void assemblePart(IDHDPartItem part) {
        assembledParts.add(part);
        getAndSendState(CoreStateTypes.RENDERER_STATE.get());
    }

    @Override
    public State getState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.BIOME_OVERRIDE_STATE, () -> new BiomeOverrideState(dhd.determineBiomeOverride()))
                .tryType(CoreStateTypes.RENDERER_STATE, this::getRenderStateServer)
                .tryType(JSGStateTypes.BUTTONS_STATE, this::getButtonsState)
                .orElseGet(() -> null);
    }

    @Override
    public State createState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(CoreStateTypes.BIOME_OVERRIDE_STATE, BiomeOverrideState::new)
                .tryType(CoreStateTypes.RENDERER_STATE, this::createRendererStateClient)
                .tryType(JSGStateTypes.BUTTONS_STATE, this::getButtonsState)
                .orElseThrow(this);
    }

    @SuppressWarnings("unchecked")
    protected S castState(State state) {
        return (S) state;
    }

    @Override
    public void setState(StateType stateType, State state) {
        stateType.stateExecutor()
                .tryType(CoreStateTypes.BIOME_OVERRIDE_STATE, () -> {
                    BiomeOverrideState overrideState = (BiomeOverrideState) state;
                    if (rendererStateClient != null) {
                        getRendererStateClient().biomeOverlay = overrideState.biomeOverride;
                    }
                })
                .tryType(CoreStateTypes.RENDERER_STATE, () -> {
                    setRendererStateClient(castState(state));
                    assembledParts.clear();
                    assembledParts.addAll(rendererStateClient.assembledParts);
                })
                .tryType(JSGStateTypes.BUTTONS_STATE, () -> {
                    buttonsState = (DHDButtonsState) state;
                })
                .run();
    }

    @Override
    public BlockPos getStateHandlerBlockPos() {
        return dhd.getBlockPos();
    }

    // ------------------------------------------------------------------------
    // Rendering

    protected S rendererStateClient = createRendererStateClient();

    public S getRendererStateClient() {
        return rendererStateClient;
    }

    public S getRenderStateServer() {
        rendererStateClient.assembledParts.clear();
        rendererStateClient.assembledParts.addAll(assembledParts);
        rendererStateClient.naquadahAmount = dhd.getReactorManager().getTank().getFluidAmount();
        rendererStateClient.naquadahMaxAmount = dhd.getReactorManager().getTank().getCapacity();
        rendererStateClient.reactorState = dhd.getReactorManager().getState();
        rendererStateClient.upgradeSlots.clear();
        dhd.getBehaviorsUsedSlots().forEach(upgradeSlot -> rendererStateClient.upgradeSlots.put(upgradeSlot, true));
        return rendererStateClient;
    }

    protected abstract S createRendererStateClient();

    protected void setRendererStateClient(S rendererState) {
        this.rendererStateClient = rendererState;
    }

    // ------------------------------------------------------------------------

    @Override
    public PacketDistributor.TargetPoint getTargetPoint() {
        if (dhd.getLevel() == null) return targetPoint;
        if (targetPoint == null) {
            var pos = getStateHandlerBlockPos();
            targetPoint = new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 512, dhd.getLevel().dimension());
        }
        return targetPoint;
    }

    @Override
    public void tick(@NotNull Level level) {
        getButtonsState().tick(level);
        if (level.isClientSide) {
            if (getRendererStateClient() == null) {
                requestState(CoreStateTypes.RENDERER_STATE.get());
            }
        }
    }

    @Override
    public void onLoad(@NotNull Level level) {
        if (level.isClientSide()) {
            requestState(CoreStateTypes.BIOME_OVERRIDE_STATE.get());
            requestState(CoreStateTypes.RENDERER_STATE.get());
            requestState(JSGStateTypes.BUTTONS_STATE.get());
        } else {
            getAndSendState(CoreStateTypes.BIOME_OVERRIDE_STATE.get());
            getAndSendState(JSGStateTypes.BUTTONS_STATE.get());
        }
    }

    public CompoundTag serializeAssemblyToNBT() {
        var compound = new CompoundTag();
        dhd.getAllParts().forEach(part -> compound.putBoolean(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(part.self())).toString(), isDHDPartAssembled(part)));
        return compound;
    }

    public void deserializeAssemblyFromNBT(CompoundTag compound) {
        assembledParts.clear();
        dhd.getAllParts().forEach(part -> {
            if (compound.isEmpty() || compound.getBoolean(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(part.self())).toString()))
                assembledParts.add(part);
        });

        // OLD versions compat
        var stackHandler = dhd.getItemStackHandler();
        if (!stackHandler.getStackInSlot(0).isEmpty() && !isDHDPartAssembled((IDHDPartItem) dhd.getControlCrystal())) {
            assembledParts.add((IDHDPartItem) dhd.getControlCrystal());
        }

        var level = dhd.getLevel();
        if (level != null && !level.isClientSide())
            getAndSendState(CoreStateTypes.RENDERER_STATE.get());
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("buttonsState", buttonsState.serializeNBT());
        compound.put("parts", serializeAssemblyToNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        buttonsState.deserializeNBT(compound.getCompound("buttonsState"));
        deserializeAssemblyFromNBT(compound.getCompound("parts"));
    }
}
