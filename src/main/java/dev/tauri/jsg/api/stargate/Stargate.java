package dev.tauri.jsg.api.stargate;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.multistructure.merging.IMergeHelper;
import dev.tauri.jsg.api.power.JSGEnergyStorage;
import dev.tauri.jsg.api.registry.BiomeOverlayRegistry;
import dev.tauri.jsg.api.registry.ScheduledTaskType;
import dev.tauri.jsg.api.sound.IPositionedSound;
import dev.tauri.jsg.api.sound.ISoundEvent;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.api.stargate.listener.IStargateListenerHandler;
import dev.tauri.jsg.api.stargate.manager.*;
import dev.tauri.jsg.api.stargate.network.IStargateNetwork;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolUsage;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.state.IStateProvider;
import dev.tauri.jsg.api.util.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface Stargate<E extends JSGEnergyStorage> extends IPreparable, ITickable, ScheduledTaskExecutorInterface, IStateProvider {
    // stargate methods
    long getTime();

    void initStargatePos();

    @Nullable
    StargatePos getStargatePos();

    default boolean is(@Nullable Stargate<?> stargate) {
        if (stargate == null) return false;
        return stargate.getStargatePos() == getStargatePos();
    }

    default boolean is(@Nullable StargatePos stargatePos) {
        if (stargatePos == null) return false;
        return stargatePos == getStargatePos();
    }

    default BlockPos getGateCenterPos() {
        return relative(new BlockPos(0, 4, 0));
    }

    AbstractSymbolType<?> getSymbolType();

    StargateType getStargateType();

    default int getMaxChevrons() {
        return 7;
    }

    default boolean receiveIrisCode(CodeSender sender, String code) {
        return false;
    }

    default boolean isRIGAllowed() {
        return false;
    }

    boolean isBlackHoleEffected();

    default boolean isGateBurried() {
        return false;
    }

    default void onBlockBroken() {
        getNetwork().removeStargate(getStargatePos());
    }

    default void onGateBroken() {
        onBlockBroken();
        onGateUnmerged(false);
    }

    void onGateUnmerged(boolean external);

    void onGateMerged();

    default int getOpenSoundDelay() {
        return ScheduledTaskType.STARGATE_OPEN_SOUND.waitTicks;
    }

    default boolean sendIrisCode(CodeSender sender, String code) {
        return getDialingManager().getConnection().callConnected((conn, sg) -> {
            if (!conn.getStatus().full()) return false;
            return sg.receiveIrisCode(sender, code);
        }, () -> false);
    }

    IStargateNetwork getNetwork();

    void regenerateStargate(IStargateGenerator.PlacementConfig pConfig);

    void tryRegenerateStargateIfNeeded();

    Map<AbstractSymbolType<?>, StargateAddress> getAddressMap();

    @Nullable
    StargateAddress getStargateAddress(AbstractSymbolType<?> symbolType);

    void setGateAddress(AbstractSymbolType<?> symbolType, StargateAddress stargateAddress);

    default void generateAddresses(boolean reset) {
        var level = getLevel();
        if (level == null) return;
        initStargatePos();
        var stargatePos = getStargatePos();
        if (reset)
            getNetwork().removeStargate(getStargatePos());
        var seed = getBlockPos().hashCode() * 31L + level.dimension().location().hashCode();
        JSGApi.logger.debug("Seed for stargate {} address gen is {}", stargatePos.toString(), seed);
        Random random = new Random(seed);

        for (AbstractSymbolType<?> symbolType : AbstractSymbolType.values(SymbolUsage.STARGATES)) {
            var address = getStargateAddress(symbolType);
            if (address != null && !reset) continue;
            var sgn = getNetwork();
            address = new StargateAddress(symbolType);
            StargatePos sgnPos;
            do {
                address.clear();
                address.generate(random);
                sgnPos = sgn.getStargate(address);
                JSGApi.logger.debug("Generating address for gate at {}, symbol type {}: {} ", stargatePos.toString(), symbolType.getId(), address.getNameList());
            } while (sgnPos != null && !sgnPos.equals(stargatePos));
            // we can not check min symbols if dim group equal because you can have this situation:
            // 1st gate will be in nether
            // 2nd gate will be in the end
            // 3rd gate will be in overworld
            // gate 1 and 2 have same 6 symbols, but nether and the end are not in the same group ->
            // Nether groups: [OW_NETHER], The End groups: [OW_END], Overworld groups: [OW_END, OW_NETHER]
            // in this case you need 7 symbols to go from end to nether, but ONLY 6 to go from overworld to nether or the end!!!

            setGateAddress(symbolType, address);
        }
    }

    void renameStargatePos(String newName);

    void refresh();

    /**
     * Tries to find a {@link SymbolInterface} instance from
     * Integer index or String name of the symbol.
     *
     * @param nameIndex Name or index.
     * @return Symbol.
     * @throws IllegalArgumentException When symbol/index is invalid.
     */
    default SymbolInterface getSymbolFromNameIndex(Object nameIndex) throws IllegalArgumentException {
        SymbolInterface symbol = null;
        if (nameIndex instanceof Integer)
            symbol = getSymbolType().valueOf((Integer) nameIndex);
        else if (nameIndex instanceof Double)
            symbol = getSymbolType().valueOf(((Double) nameIndex).intValue());
        else if (nameIndex instanceof String)
            symbol = getSymbolType().fromEnglishName((String) nameIndex);
        else if (nameIndex instanceof byte[])
            symbol = getSymbolType().fromEnglishName(new String((byte[]) nameIndex));
        if (symbol == null)
            symbol = getSymbolType().fromEnglishName(nameIndex.toString());
        if (symbol == null)
            throw new IllegalArgumentException("bad argument (symbol name/index invalid) (tried: " + nameIndex + ")");
        return symbol;
    }

    ItemStack getAddressPage(AbstractSymbolType<?> symbolType, ItemStack defaultStack, int[] symbolsToDisplay);

    int getOriginId();

    default void setOriginId(CompoundTag compound) {
        compound.putInt("originId", getOriginId());
    }

    List<BiomeOverlayRegistry.BiomeOverlayInstance> getSupportedOverlays();

    default BiomeOverlayRegistry.BiomeOverlayInstance getBiomeOverlayWithOverride(boolean override) {
        var world = getLevel();
        if (world == null) return BiomeOverlayRegistry.NORMAL;
        return BiomeOverlayRegistry.getUpdatedBiomeOverlay(world, getMergeHelper().getTopBlock(), getSupportedOverlays());
    }

    default void clearDHDSymbols() {
    }

    default void activateDHDBRB() {
    }

    boolean shouldAutoclose();

    IStargateAutoCloseManager getAutoCloseManager();

    void updateFacing();

    default String getOpenedSecondsToDisplayAsMinutes() {
        long openedSeconds = getDialingManager().getConnection().getSecondsOpen();
        if (openedSeconds < 1) return "Closed!";
        int minutes = ((int) Math.floor((double) openedSeconds / 60));
        int seconds = ((int) (openedSeconds - (60 * minutes)));
        String secondsString = ((seconds < 10) ? "0" + seconds : "" + seconds);
        return minutes + ":" + secondsString + "min";
    }

    // merging
    void generateMergeHelper();

    IMergeHelper getMergeHelper();

    void setMerged(boolean merged);

    boolean isMerged();

    // -------------------------------
    // sounds

    @Nullable
    IPositionedSound getPositionedSound(StargateSoundPositionedEnum soundEnum);

    @Nullable
    ISoundEvent getSoundEvent(StargateSoundEventEnum soundEnum);

    default void playPositionedSound(StargateSoundPositionedEnum soundEnum, boolean play) {
        IPositionedSound positionedSound = getPositionedSound(soundEnum);
        if (positionedSound == null)
            throw new IllegalArgumentException("Tried to play " + soundEnum + " on " + getClass().getCanonicalName() + " which apparently doesn't support it.");
        playPositionedSound(positionedSound, play);
    }

    void playPositionedSound(IPositionedSound positionedSound, boolean play);

    default void playSoundEvent(StargateSoundEventEnum soundEnum) {
        var soundEvent = getSoundEvent(soundEnum);
        if (soundEvent == null)
            throw new IllegalArgumentException("Tried to play " + soundEnum + " on " + getClass().getCanonicalName() + " which apparently doesn't support it.");
        playSoundEvent(soundEvent, getSoundEventPitch(soundEnum));
    }

    default float getSoundEventPitch(StargateSoundEventEnum soundEvent) {
        return 1f;
    }

    default void playSoundEvent(ISoundEvent soundEnum) {
        playSoundEvent(soundEnum, 1f);
    }

    void playSoundEvent(ISoundEvent soundEnum, float pitch);

    // -------------------------------
    // managers
    IStargateDialingManager createDialingManager();

    IStargateStateManager createStateManager();

    IStargateEnergyManager<E> createEnergyManager();

    IStargateSoundManager createSoundManager();

    IStargateEventHorizonManager createEventHorizonManager();

    IStargateDialingManager getDialingManager();

    IStargateEnergyManager<E> getEnergyManager();

    IStargateStateManager getStateManager();

    IStargateSoundManager getSoundManager();

    IStargateRIGManager getRIGManager();

    IStargateListenerHandler getListenerHandler();

    IStargateEventHorizonManager getEventHorizonManager();


    // BE methods
    @Nullable
    Level getLevel();

    void setChanged();

    BlockPos getBlockPos();

    BlockState getBlockState();

    default void onLoad() {
        var level = getLevel();
        if (level == null) return;
        if (!level.isClientSide()) {
            getStateManager().onLoad(level);
            tryRegenerateStargateIfNeeded();
            generateAddresses(false);
            setChanged();
            updateFacing();
            generateMergeHelper();
        } else
            getStateManager().onLoad(level);
        getDialingManager().onLoad(level);
        getEventHorizonManager().onLoad(level);
        getSoundManager().onLoad(level);
    }

    @Override
    default void tick(Level level) {
        getStateManager().tick(level);
        getDialingManager().tick(level);
        getRIGManager().tick(level);
        getMergeHelper().tick(level);
        getEnergyManager().tick(level);
        getEventHorizonManager().tick(level);
        getSoundManager().tick(level);
        if (level.isClientSide) return;
        // autoclose
        if (getTime() % 20 == 0 && shouldAutoclose()) {
            getDialingManager().attemptClose(StargateClosedReasonEnum.AUTOCLOSE);
        }
    }

    default <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> getEnergyManager().getStorage()).cast();
        }
        return LazyOptional.empty();
    }

    default void saveAdditional(CompoundTag compound) {
        compound.put("soundManager", getSoundManager().serializeNBT());
        compound.put("stargateEnergyManager", getEnergyManager().serializeNBT());
        compound.put("stargateDialingManager", getDialingManager().serializeNBT());
        compound.put("stateManager", getStateManager().serializeNBT());
        compound.put("eventHorizon", getEventHorizonManager().serializeNBT());

        compound.put("autoCloseManager", getAutoCloseManager().serializeNBT());
        compound.putBoolean("isMerged", isMerged());
        compound.put("listenerHandler", getListenerHandler().serializeNBT());
    }

    default void load(CompoundTag compound) {
        if (compound.contains("soundManager")) {
            getSoundManager().deserializeNBT(compound.getCompound("soundManager"));
            getEnergyManager().deserializeNBT(compound.getCompound("stargateEnergyManager"));
            getDialingManager().deserializeNBT(compound.getCompound("stargateDialingManager"));
            getStateManager().deserializeNBT(compound.getCompound("stateManager"));
            getEventHorizonManager().deserializeNBT(compound.getCompound("eventHorizon"));
        }
        getAutoCloseManager().deserializeNBT(compound.getCompound("autoCloseManager"));
        setMerged(compound.getBoolean("isMerged"));
        getListenerHandler().deserializeNBT(compound.getCompound("listenerHandler"));
    }

    // util
    static int getOriginId(@javax.annotation.Nullable BiomeOverlayRegistry.BiomeOverlayInstance overlay, @javax.annotation.Nullable ResourceKey<Level> dim, int configOrigin) {
        if (configOrigin >= 0) return configOrigin;

        if (overlay == null) overlay = BiomeOverlayRegistry.NORMAL;

        int override = -1; // todo fix this when rewriting origins loading //StargateDimensionConfig.INSTANCE.getOrigin(dim, overlay);
        if (override >= 0)
            return override;

        if (overlay == BiomeOverlayRegistry.NORMAL) return (dim == Level.OVERWORLD ? 5 : 0);
        if (overlay == BiomeOverlayRegistry.SOOTY) return 2;
        if (overlay == BiomeOverlayRegistry.AGED) return 4;
        if (overlay == BiomeOverlayRegistry.FROST) return 3;
        return 0;
    }

    default Direction getFacing() {
        return getMergeHelper().getHorizontalFacing();
    }

    default Direction getFacingVertical() {
        return getMergeHelper().getVerticalFacing() == null ? Direction.SOUTH : getMergeHelper().getVerticalFacing();
    }

    default Vec3 rotated(Vec3 pos) {
        return RotationUtil.rotate(pos, RotationUtil.getRotation(getMergeHelper().getVerticalFacing(), getMergeHelper().getHorizontalFacing()));
    }

    default Vec3 rotated(Vec3 pos, Vec3 pivot) {
        return RotationUtil.rotate(pos, RotationUtil.getRotation(getMergeHelper().getVerticalFacing(), getMergeHelper().getHorizontalFacing()), pivot);
    }

    default JSGAxisAlignedBB rotated(JSGAxisAlignedBB box, Vec3 pivot) {
        return box.rotate(getMergeHelper().getHorizontalFacing(), getMergeHelper().getVerticalFacing(), pivot);
    }

    default BlockPos rotated(BlockPos pos) {
        return RotationUtil.rotate(pos, RotationUtil.getRotation(getMergeHelper().getVerticalFacing(), getMergeHelper().getHorizontalFacing()));
    }

    default BlockPos rotated(BlockPos pos, BlockPos pivot) {
        return RotationUtil.rotate(pos, RotationUtil.getRotation(getMergeHelper().getVerticalFacing(), getMergeHelper().getHorizontalFacing()), pivot);
    }

    default JSGAxisAlignedBB relative(JSGAxisAlignedBB box, Vec3 pivot) {
        var p = getBlockPos();
        return rotated(box, pivot).offset(p.getX(), p.getY(), p.getZ());
    }

    default Vec3 relative(Vec3 pos, Vec3 pivot) {
        var p = getBlockPos();
        return rotated(pos, pivot).add(p.getX(), p.getY(), p.getZ());
    }

    default Vec3 relative(Vec3 pos) {
        var p = getBlockPos();
        return rotated(pos).add(p.getX(), p.getY(), p.getZ());
    }

    default BlockPos relative(BlockPos pos) {
        var vec3 = relative(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
        return new BlockPos((int) vec3.x(), (int) vec3.y(), (int) vec3.z());
    }

    default BlockPos relative(BlockPos pos, BlockPos pivot) {
        var p = getBlockPos();
        return rotated(pos, pivot).offset(p.getX(), p.getY(), p.getZ());
    }

    default Vec3 getRelative(Vec3 vector) {
        var p = getBlockPos();
        return getRelativeRotated(vector.subtract(p.getX(), p.getY(), p.getZ()));
    }

    default Vec3 getRelativeRotated(Vec3 vector) {
        var rotationToRelative = RotationUtil.getRotationToZero(getMergeHelper().getVerticalFacing(), getMergeHelper().getHorizontalFacing());
        return RotationUtil.rotate(vector, rotationToRelative);
    }

    default ItemStack getDropBaseBlock(ServerPlayer player) {
        var stack = new ItemStack(this.getStargateType().getBaseBlock());
        var tag = stack.getOrCreateTag();
        tag.put("stargateEnergyManager", getEnergyManager().serializeNBT());
        stack.setTag(tag);
        return stack;
    }

    default void updateContainerItemsByItemStack(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.contains("stargateEnergyManager")) return;
        getEnergyManager().deserializeNBT(tag.getCompound("stargateEnergyManager"));
        setChanged();
    }
}
