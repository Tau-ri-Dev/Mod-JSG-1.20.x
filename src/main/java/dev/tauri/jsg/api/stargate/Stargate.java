package dev.tauri.jsg.api.stargate;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.api.stargate.listener.IStargateListenerHandler;
import dev.tauri.jsg.api.stargate.manager.*;
import dev.tauri.jsg.api.stargate.network.IStargateNetwork;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.util.IStargateGenerator;
import dev.tauri.jsg.core.common.blockentity.*;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.multistructure.merging.IMergeHelper;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.sound.IPositionedSound;
import dev.tauri.jsg.core.common.sound.ISoundEvent;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.SymbolUtil;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.core.common.util.RotationUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface Stargate<E extends JSGEnergyStorage> extends IPreparable, ITickable, ScheduledTaskExecutorInterface, IStateProvider, ComputerDeviceProvider {
    static int[] getRandomSymbolsToDisplay(int maxSymbols, Predicate<Integer> shouldDisplaySymbol) {
        var symbolsToDisplay = new ArrayList<Integer>();
        if (maxSymbols < 1) return new int[0];
        if (maxSymbols > 8) maxSymbols = 8;
        for (var i = 1; i <= maxSymbols; i++) {
            if (shouldDisplaySymbol.test(i))
                symbolsToDisplay.add(i);
        }
        if (shouldDisplaySymbol.test(9))
            symbolsToDisplay.add(9);
        var symbolToDisplayArray = new int[symbolsToDisplay.size()];
        for (var i = 0; i < symbolsToDisplay.size(); i++) {
            symbolToDisplayArray[i] = symbolsToDisplay.get(i);
        }
        return symbolToDisplayArray;
    }

    @Nullable
    @ParametersAreNonnullByDefault
    static PointOfOrigin getOriginFor(IPointOfOriginType pointOfOriginType, ResourceKey<Level> dimension, BiomeOverlayInstance biomeOverlayInstance) {
        return Optional.ofNullable(PointOfOriginsLoader.INSTANCE.getOriginFor(pointOfOriginType, dimension, biomeOverlayInstance)).orElse(pointOfOriginType.getDefaultPoO());
    }

    private BlockEntity self() {
        return (BlockEntity) this;
    }

    // stargate methods
    long getTime();

    void initStargatePos();

    StargatePos getStargatePos();

    default boolean is(@Nullable Stargate<?> stargate) {
        if (stargate == null) return false;
        return stargate.getStargatePos().equals(getStargatePos());
    }

    default boolean is(@Nullable StargatePos stargatePos) {
        if (stargatePos == null) return false;
        return stargatePos.equals(getStargatePos());
    }

    default BlockPos getGateCenterPos() {
        return relative(new BlockPos(0, 4, 0));
    }

    SymbolType<?> getSymbolType();

    StargateType<?> getStargateType();

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
        return JSGScheduledTaskTypes.STARGATE_OPEN_SOUND.get().waitTicks();
    }

    default int getCloseSoundDelay() {
        return JSGScheduledTaskTypes.STARGATE_CLOSE_SOUND.get().waitTicks();
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

    Map<SymbolType<?>, StargateAddress> getAddressMap();

    @Nullable
    StargateAddress getStargateAddress(SymbolType<?> symbolType);

    void setGateAddress(SymbolType<?> symbolType, StargateAddress stargateAddress);

    default void generateAddresses(boolean reset) {
        var level = getStargateLevel();
        if (level == null) return;
        initStargatePos();
        var stargatePos = getStargatePos();
        if (reset)
            getNetwork().removeStargate(getStargatePos());
        var seed = blockPosition().hashCode() * 31L + level.dimension().location().hashCode();
        JSGApi.logger.debug("Seed for stargate {} address gen is {}", stargatePos.toString(), seed);
        Random random = new Random(seed);

        for (SymbolType<?> symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
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
            // we can not check min symbols if dimension group equal because you can have this situation:
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
        return SymbolUtil.getSymbolFromNameIndexOrThrow(getSymbolType(), nameIndex);
    }

    ItemStack getAddressPage(SymbolType<?> symbolType, ItemStack defaultStack, int[] symbolsToDisplay);

    @Nullable
    PointOfOrigin getPointOfOrigin(SymbolType<?> symbolType);

    @Nullable
    PointOfOrigin getPointOfOrigin(IPointOfOriginType pointOfOriginType);

    @Nullable
    default PointOfOrigin getPointOfOrigin() {
        return getPointOfOrigin(getPointOfOriginType());
    }

    default IPointOfOriginType getPointOfOriginType() {
        return getStargateType();
    }

    List<Supplier<BiomeOverlayInstance>> getSupportedOverlays();

    default BiomeOverlayInstance getBiomeOverlayWithOverride() {
        var world = getStargateLevel();
        if (world == null) return CoreBiomeOverlays.NORMAL.get();
        return BiomeOverlayInstance.getUpdatedBiomeOverlay(world, getMergeHelper().getTopBlock(), getSupportedOverlays().stream().map(Supplier::get).toList());
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
        if (positionedSound == null) return;
        playPositionedSound(positionedSound, play);
    }

    void playPositionedSound(IPositionedSound positionedSound, boolean play);

    default void playSoundEvent(StargateSoundEventEnum soundEnum) {
        var soundEvent = getSoundEvent(soundEnum);
        if (soundEvent == null) return;
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

    IBELogManager getLogManager();


    // BE methods
    @Nullable
    default Level getStargateLevel() {
        return self().getLevel();
    }

    default RandomSource getRandom() {
        return (getStargateLevel() == null ? RandomSource.create() : getStargateLevel().random);
    }

    default BlockState getStargateBlockState() {
        return self().getBlockState();
    }

    default void setStargateChanged() {
        self().setChanged();
    }

    default void onStargateLoaded() {
        var level = getStargateLevel();
        if (level == null) return;
        if (!level.isClientSide()) {
            getStateManager().onLoad(level);
            tryRegenerateStargateIfNeeded();
            generateAddresses(false);
            setStargateChanged();
            updateFacing();
            generateMergeHelper();
        } else
            getStateManager().onLoad(level);
        getDialingManager().onLoad(level);
        getRIGManager().onLoad(level);
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

    default BlockPos blockPosition() {
        return self().getBlockPos();
    }

    default <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        return self().getCapability(capability, facing);
    }

    <T> LazyOptional<T> getStargateCapability(Capability<T> capability, @Nullable Direction facing);

    // util
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
        var p = blockPosition();
        return rotated(box, pivot).offset(p.getX(), p.getY(), p.getZ());
    }

    default Vec3 relative(Vec3 pos, Vec3 pivot) {
        var p = blockPosition();
        return rotated(pos, pivot).add(p.getX(), p.getY(), p.getZ());
    }

    default Vec3 relative(Vec3 pos) {
        var p = blockPosition();
        return rotated(pos).add(p.getX(), p.getY(), p.getZ());
    }

    default BlockPos relative(BlockPos pos) {
        var vec3 = relative(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
        return new BlockPos((int) vec3.x(), (int) vec3.y(), (int) vec3.z());
    }

    default BlockPos relative(BlockPos pos, BlockPos pivot) {
        var p = blockPosition();
        return rotated(pos, pivot).offset(p.getX(), p.getY(), p.getZ());
    }

    default Vec3 getRelative(Vec3 vector) {
        var p = blockPosition();
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
        setStargateChanged();
    }
}
