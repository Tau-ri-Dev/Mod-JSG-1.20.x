package dev.tauri.jsg.common.stargate.manager;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateWithIris;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.api.stargate.manager.IStargateIrisManager;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateClassicRenderer;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateClassicRendererState;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.item.linkable.gdo.GDOMessages;
import dev.tauri.jsg.common.item.stargate.IrisItem;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.stargate.animation.IrisAnimationState;
import dev.tauri.jsg.common.state.stargate.StargateRendererActionState;
import dev.tauri.jsg.common.util.JSGAdvancementsUtil;
import dev.tauri.jsg.core.common.blockentity.ScheduledTaskExecutorInterface;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.core.common.util.EnumKeyInterface;
import dev.tauri.jsg.core.common.util.EnumKeyMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE.IRIS_MAX_HEAT_TRINIUM;
import static dev.tauri.jsg.common.util.JSGAdvancementsUtil.tryTriggerRangedAdvancement;

public class StargateIrisManager extends AbstractStargateManager<StargateClassicBaseBE<?>> implements IStargateIrisManager, ScheduledTaskExecutorInterface {
    protected EnumIrisState state = EnumIrisState.OPENED;
    protected EnumIrisType type = EnumIrisType.NULL;
    protected EnumIrisMode mode = EnumIrisMode.OPENED;
    @NotNull
    protected String code = "";

    protected CodeSender codeSender;

    protected Runnable afterIrisDone;

    protected long irisAnimationStart;

    protected IrisAnimationState irisAnimationState = new IrisAnimationState();

    public StargateIrisManager(StargateClassicBaseBE<?> stargate) {
        super(stargate);
    }

    public StargateClassicRendererState.StargateClassicRendererStateBuilder processRenderState(StargateClassicRendererState.StargateClassicRendererStateBuilder builder) {
        return builder.setIrisState(state)
                .setIrisType(type)
                .setIrisMode(mode)
                .setIrisCode(code)
                .setIrisAnimation(irisAnimationStart);
    }

    public void setIrisAnimationState(IrisAnimationState irisAnimationState) {
        this.irisAnimationState = irisAnimationState;
    }

    public @NotNull IrisAnimationState getIrisAnimationState() {
        return this.irisAnimationState;
    }

    // ------------------------------------------------------------------------

    @Override
    @Nullable
    public CodeSender getCodeSender() {
        return codeSender;
    }

    @Override
    @NotNull
    public EnumIrisType getIrisType() {
        return type;
    }

    @Override
    @NotNull
    public EnumIrisMode getIrisMode() {
        return mode;
    }

    @Override
    @NotNull
    public String getIrisCode() {
        return code;
    }

    @Override
    @NotNull
    public EnumIrisState getIrisState() {
        return state;
    }

    @NotNull
    @Override
    public ItemStack getIrisItem() {
        return stargate.getItemHandler().getStackInSlot(8);
    }

    @Override
    public boolean canInsertItemAsIris(Item item) {
        return StargateIrisUpgradeEnum.contains(item);
    }

    // ------------------------------------------------------------------------

    public void onGateDisconnected() {
        codeSender = null;
        if (mode == EnumIrisMode.AUTO && isIrisClosed())
            toggleIris();
        stargate.setChanged();
    }

    public void onIncomingWormhole() {
        if (hasIris() && mode == EnumIrisMode.AUTO && isIrisOpened())
            toggleIris();
    }

    @Override
    public boolean receiveIrisCode(@NotNull CodeSender sender, @NotNull String code) {
        StargateComputerEvents.IRIS_CODE_RECEIVED.apply(code).sendVia(stargate);
        if (mode != EnumIrisMode.AUTO) {
            if (stargate.getListenerHandler().receiveIDC(sender, code)) {
                return true;
            }
            sender.sendMessage(GDOMessages.SEND_TO_COMPUTER.textComponent);
            codeSender = sender;
            return false;
        }
        if (Objects.equals(code, this.code)) {
            switch (this.state) {
                case OPENED:
                    sender.sendMessage(GDOMessages.OPENED.textComponent);
                    break;
                case CLOSED:
                    sender.sendMessage(GDOMessages.CODE_ACCEPTED.textComponent);
                    codeSender = sender;
                    toggleIris();
                    break;
                case OPENING:
                case CLOSING:
                    sender.sendMessage(GDOMessages.BUSY.textComponent);
                    break;
                default:
                    break;
            }
        } else {
            if (stargate.getListenerHandler().receiveIDC(sender, code)) {
                return true;
            }
            sender.sendMessage(GDOMessages.CODE_REJECTED.textComponent);
            return false;
        }
        stargate.setChanged();
        return true;
    }

    @Override
    public void setIrisCode(String code) {
        if (code == null) code = "";
        this.code = code;
        stargate.setChanged();
    }

    @Override
    public void setIrisMode(@NotNull EnumIrisMode irisMode) {
        if (stargate.getLevel() == null || stargate.getLevel().isClientSide) {
            this.mode = irisMode;
            return;
        }
        if (this.mode != irisMode && hasIris()) {
            switch (irisMode) {
                case OPENED:
                case CLOSED:
                    irisModeAction(irisMode);
                    break;
                case AUTO:
                    var sgState = stargate.getDialingManager().getStargateState();
                    if (sgState.notInitiating() || sgState.incoming()) {
                        if (state == EnumIrisState.OPENED) toggleIris();
                    } else {
                        if (isIrisClosed()) toggleIris();
                    }
                    break;
                case OC:
                default:
                    break;
            }
        }
        this.mode = irisMode;
        stargate.setChanged();
    }

    public void onUnmerged() {
        if (type != EnumIrisType.NULL && state == EnumIrisState.CLOSED) {
            setIrisBlocks(false);
        }
        state = EnumIrisState.OPENED;
        type = EnumIrisType.NULL;
    }

    public void onLoad() {
        updateIrisType();
        boolean set = type != EnumIrisType.NULL;
        if (stargate.isMerged()) {
            setIrisBlocks(set && state == EnumIrisState.CLOSED);
        }
    }

    private void irisModeAction(EnumIrisMode mode) {
        EnumIrisState p, p2;
        if (mode == EnumIrisMode.OPENED) {
            p = EnumIrisState.CLOSED;
            p2 = EnumIrisState.CLOSING;
        } else if (mode == EnumIrisMode.CLOSED) {
            p = EnumIrisState.OPENED;
            p2 = EnumIrisState.OPENING;
        } else return;

        if (state == p) toggleIris();
        else if (state == p2) afterIrisDone = this::toggleIris;
    }

    @Override
    public boolean toggleIris() {
        updateIrisType();
        if (type == EnumIrisType.NULL) return false;
        if (isIrisClosed() || isIrisOpened())
            irisAnimationStart = stargate.getTime();
        SoundEvent openSound;
        SoundEvent closeSound;
        if (hasPhysicalIris()) {
            openSound = JSGSoundEvents.IRIS_OPENING;
            closeSound = JSGSoundEvents.IRIS_CLOSING;
        } else {
            openSound = JSGSoundEvents.SHIELD_OPENING;
            closeSound = JSGSoundEvents.SHIELD_CLOSING;
        }
        switch (state) {
            case OPENED:
            case OPENING:
                if (hasShield() && stargate.getEnergyManager().getStorage().getEnergyStored() < JSGConfig.Stargate.irisShieldPowerDraw.get() * 3)
                    return false;

                StargateComputerEvents.IRIS_TOGGLED.apply(true).sendVia(stargate);
                StargateComputerEvents.IRIS_STATE_CHANGED.apply(state, EnumIrisState.CLOSING).sendVia(stargate);
                state = EnumIrisState.CLOSING;
                if (mode == EnumIrisMode.OPENED) {
                    StargateComputerEvents.IRIS_MODE_CHANGED.apply(mode, EnumIrisMode.CLOSED).sendVia(stargate);
                    mode = EnumIrisMode.CLOSED;
                }
                stargate.getStateManager().sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, true, type, state, irisAnimationStart);
                stargate.playSoundEvent(closeSound);
                if (hasShield())
                    stargate.getSoundManager().updateShieldHummingSound(true);
                if (stargate.getDialingManager().getConnection().getStatus().full())
                    stargate.executeTask(JSGScheduledTaskTypes.STARGATE_HORIZON_LIGHT_BLOCK.get(), new CompoundTag());
                stargate.getListenerHandler().irisCloses();
                break;
            case CLOSED:
            case CLOSING:
                StargateComputerEvents.IRIS_TOGGLED.apply(false).sendVia(stargate);
                StargateComputerEvents.IRIS_STATE_CHANGED.apply(state, EnumIrisState.OPENING).sendVia(stargate);
                state = EnumIrisState.OPENING;
                if (mode == EnumIrisMode.CLOSED) {
                    StargateComputerEvents.IRIS_MODE_CHANGED.apply(mode, EnumIrisMode.OPENED).sendVia(stargate);
                    mode = EnumIrisMode.OPENED;
                }
                setIrisBlocks(false);
                stargate.getStateManager().sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, true, type, state, irisAnimationStart);
                stargate.playSoundEvent(openSound);
                stargate.getSoundManager().updateShieldHummingSound(false);
                if (stargate.getDialingManager().getConnection().getStatus().full())
                    stargate.executeTask(JSGScheduledTaskTypes.STARGATE_HORIZON_LIGHT_BLOCK.get(), new CompoundTag());
                stargate.getListenerHandler().irisOpens();
                break;
            default:
                return false;
        }
        stargate.setChanged();
        return true;
    }

    @Override
    public void hitIris() {
        if (!hasIris()) return;
        tryTriggerRangedAdvancement((Stargate<?>) stargate, JSGAdvancementsUtil.EnumAdvancementType.IRIS_IMPACT);
        stargate.getListenerHandler().irisHit();
        ItemStack irisItem = getIrisItem();
        stargate.getStateManager().sendState(JSGStateTypes.IRIS_ANIMATION.get(), IrisAnimationState.hit(stargate.getTime()));
        if (!hasCreativeIris() && irisItem.getItem() instanceof IrisItem && irisItem.isDamageableItem()) {
            // different damages per source
            int chance = EnchantmentHelper.getEnchantments(irisItem).containsKey(Enchantments.UNBREAKING) ? (JSGConfig.Stargate.irisUnbreakingChance.get() * irisItem.getEnchantmentLevel(Enchantments.UNBREAKING)) : 0;
            int random = (new Random()).nextInt(100);

            if (random > chance) {
                irisItem.getItem().setDamage(irisItem, irisItem.getItem().getDamage(irisItem) + 1);
            }
            if (irisItem.getCount() == 0) {
                updateIrisType();
            }
            stargate.tryHeatUp(true, 2);
        } else if (!hasCreativeIris()) {
            stargate.getEnergyManager().getStorage().extractEnergy(500, false);
        }
        if (hasPhysicalIris()) {
            stargate.playSoundEvent(JSGSoundEvents.IRIS_HIT);
        } else if (hasShield()) {
            stargate.playSoundEvent(JSGSoundEvents.SHIELD_HIT);
        }
        StargateComputerEvents.IRIS_HIT.get().sendVia(stargate);
    }

    public void updateIrisType() {
        var newType = EnumIrisType.byItem(getIrisItem().getItem());
        if (stargate.getLevel() == null || stargate.getLevel().isClientSide) {
            type = newType;
            return;
        }
        if (newType != type) {
            StargateComputerEvents.IRIS_TYPE_CHANGED.apply(type, newType).sendVia(stargate);
        }
        type = newType;
        //irisAnimationStart = stargate.getTime();
        if (type == EnumIrisType.NULL) {
            StargateComputerEvents.IRIS_STATE_CHANGED.apply(state, EnumIrisState.OPENED).sendVia(stargate);
            state = EnumIrisState.OPENED;
        }
        stargate.getStateManager().sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, false, type, state, irisAnimationStart);
        stargate.setChanged();
        if (isIrisOpened()) {
            stargate.getSoundManager().updateShieldHummingSound(false);
            setIrisBlocks(false);
        }
    }

    public BlockPos[] getIrisBlocksPattern() {
        return StargateWithIris.IRIS_PATTER;
    }

    private void setIrisBlocks(boolean set) {
        var world = stargate.getLevel();
        if (world == null) return;
        BlockState invBlockState = JSGBlocks.IRIS_BLOCK.get().defaultBlockState().setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, stargate.getFacing()).setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, dev.tauri.jsg.core.common.blockstate.JSGProperties.getVerticalFacingByDirection(stargate.getFacingVertical()));
        for (BlockPos invPos : getIrisBlocksPattern()) {
            BlockPos newPos = stargate.relative(invPos);
            if (set) {
                if (world.getBlockState(newPos).getBlock() != Blocks.AIR) {
                    if (!JSGConfig.Stargate.irisDestroysBlocks.get() || (world.getBlockState(newPos).getBlock() == Blocks.LIGHT && newPos == stargate.getGateCenterPos()))
                        continue;
                    world.destroyBlock(newPos, true);
                }
                world.setBlock(newPos, invBlockState, 3);

            } else {
                if (newPos == stargate.getGateCenterPos() && stargate.getDialingManager().getConnection().getStatus().full())
                    stargate.executeTask(JSGScheduledTaskTypes.STARGATE_HORIZON_LIGHT_BLOCK.get(), new CompoundTag());
                if (world.getBlockState(newPos).getBlock() == JSGBlocks.IRIS_BLOCK.get())
                    world.setBlock(newPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }


    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        if (state == null) {
            if (codeSender != null) {
                codeSender.sendMessage(GDOMessages.OPENED.textComponent);
                codeSender = null;
            }
            state = EnumIrisState.OPENED;
        }
        compound.putByte("irisState", state.id);
        compound.putString("irisCode", code);
        compound.putByte("irisMode", mode.id);
        compound.putByte("irisType", type.id);
        compound.putLong("irisAnimationStart", irisAnimationStart);
        compound.put("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));
        var level = stargate.getLevel();
        if (codeSender != null && level != null && !level.isClientSide) {
            compound.put("codeSender", codeSender.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        state = EnumIrisState.getValue(compound.getByte("irisState"));
        code = !compound.getString("irisCode").isEmpty() ? compound.getString("irisCode") : "";
        mode = EnumIrisMode.getValue(compound.getByte("irisMode"));
        type = EnumIrisType.byId(compound.getByte("irisType"));
        irisAnimationStart = compound.getLong("irisAnimationStart");
        var level = stargate.getLevel();
        if (level != null && compound.contains("codeSender") && !level.isClientSide) {
            CompoundTag nbt = compound.getCompound("codeSender");
            codeSender = CodeSender.fromNBT(nbt, level);
        }
        try {
            ScheduledTask.deserializeList(compound.getCompound("scheduledTasks"), scheduledTasks, this);
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            JSG.logger.warn("Exception at reading NBT");
            JSG.logger.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it", e);
        }
    }

    @Override
    public void tick(@NotNull Level level) {
        if (!level.isClientSide) {
            if (hasShield()) {
                var shieldKeepAlive = JSGConfig.Stargate.irisShieldPowerDraw.get();
                shieldKeepAlive += (int) (stargate.irisHeat * (stargate.irisHeat / IRIS_MAX_HEAT_TRINIUM));
                if (isIrisClosed()) {
                    stargate.getEnergyManager().getStorage().extractEnergy(shieldKeepAlive, false);
                }
                if (stargate.getEnergyManager().getStorage().getEnergyStored() < shieldKeepAlive) {
                    StargateComputerEvents.IRIS_OUT_OF_POWER.get().sendVia(stargate);
                    toggleIris();
                } else if (mode == EnumIrisMode.CLOSED && state != EnumIrisState.CLOSING && state != EnumIrisState.CLOSED) {
                    toggleIris();
                }
            }
            if (!(isIrisClosed() || isIrisOpened()) && (stargate.getTime() - irisAnimationStart) > (hasPhysicalIris() ? StargateClassicRenderer.PHYSICAL_IRIS_ANIMATION_LENGTH : StargateClassicRenderer.SHIELD_IRIS_ANIMATION_LENGTH)) {
                switch (state) {
                    case OPENING:
                        StargateComputerEvents.IRIS_STATE_CHANGED.apply(state, EnumIrisState.OPENED).sendVia(stargate);
                        state = EnumIrisState.OPENED;
                        stargate.getStateManager().sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, false, type, state, irisAnimationStart);
                        if (afterIrisDone != null) afterIrisDone.run();
                        afterIrisDone = null;
                        break;
                    case CLOSING:
                        StargateComputerEvents.IRIS_STATE_CHANGED.apply(state, EnumIrisState.CLOSED).sendVia(stargate);
                        state = EnumIrisState.CLOSED;
                        setIrisBlocks(true);
                        stargate.getStateManager().sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, false, type, state, irisAnimationStart);
                        if (afterIrisDone != null) afterIrisDone.run();
                        afterIrisDone = null;
                        break;
                    default:
                        break;
                }
                stargate.setChanged();
            }
        }
    }


    // -------------------------------------------
    // SCHEDULED TASKS

    protected final List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(stargate.getTime());

        scheduledTasks.add(scheduledTask);
        stargate.setChanged();
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, @NotNull CompoundTag customData) {

    }


    public enum StargateIrisUpgradeEnum implements EnumKeyInterface<Item> {
        IRIS_UPGRADE_CLASSIC(JSGItems.UPGRADE_IRIS.get()),
        IRIS_UPGRADE_TRINIUM(JSGItems.UPGRADE_IRIS_TRINIUM.get()),
        IRIS_UPGRADE_CREATIVE(JSGItems.UPGRADE_IRIS_CREATIVE.get()),
        IRIS_UPGRADE_SHIELD(JSGItems.UPGRADE_SHIELD.get());

        public final Item item;

        StargateIrisUpgradeEnum(Item item) {
            this.item = item;
        }

        @Override
        public Item getKey() {
            return item;
        }

        private static final EnumKeyMap<Item, StargateIrisUpgradeEnum> idMap = new EnumKeyMap<>(values());

        public static StargateIrisUpgradeEnum valueOf(Item item) {
            return idMap.valueOf(item);
        }

        public static boolean contains(Item item) {
            return idMap.contains(item);
        }
    }
}
