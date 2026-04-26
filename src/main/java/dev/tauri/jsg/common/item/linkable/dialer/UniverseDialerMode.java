package dev.tauri.jsg.common.item.linkable.dialer;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.client.renderer.item.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.common.item.linkable.dialer.utils.UDCommonUtils;
import dev.tauri.jsg.common.packet.packets.linkable.UniverseDialerActionPacketToServer;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.util.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiPredicate;

public abstract class UniverseDialerMode {
    public static final String C_LINKED_POS = "linkedPos";
    private static final HashMap<ResourceLocation, UniverseDialerMode> MODES = new HashMap<>();
    private static UniverseDialerMode lastMode;
    private static UniverseDialerMode firstMode;

    public final ResourceLocation id;
    public final String title;
    @Nullable
    public final TagKey<Block> matchBlocks;
    public final BiPredicate<Level, BlockPos> linkMatchTest;

    private UniverseDialerMode prev;
    private UniverseDialerMode next;

    public UniverseDialerMode(ResourceLocation id, String title) {
        this(id, title, null, (a, b) -> false);
    }

    public UniverseDialerMode(ResourceLocation id, String title, @Nullable TagKey<Block> matchBlocks, BiPredicate<Level, BlockPos> linkMatchTest) {
        this.id = id;
        this.title = title;
        this.matchBlocks = matchBlocks;
        this.linkMatchTest = linkMatchTest;
        MODES.put(id, this);

        if (firstMode == null) firstMode = this;
        if (lastMode == null) lastMode = this;
        firstMode.prev = this;
        lastMode.next = this;
        prev = lastMode;
        next = firstMode;
        lastMode = this;
    }

    @Nonnull
    public abstract IUniverseDialerScreen getScreen();

    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack stack, CompoundTag compound, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClientSide) return;
        if (matchBlocks == null) return;
        if (!isSelected) return;
        if (world.getGameTime() % 20 != 0) return;

        BlockPos pos = entity.blockPosition();
        int reachSquared = JSGConfig.DialHomeDevice.universeDialerReach.get() * JSGConfig.DialHomeDevice.universeDialerReach.get() * 2;

        // we have linked gate - check if still in range
        if (compound.contains(C_LINKED_POS)) {
            BlockPos tilePos = BlockPos.of(compound.getLong(C_LINKED_POS));

            if (!world.getBlockState(tilePos).is(this.matchBlocks) || tilePos.distSqr(pos) > reachSquared) {
                compound.remove(C_LINKED_POS);
                onLinkUpdated(false, tilePos, compound, stack, (ServerLevel) world, entity);
            }
            return;
        }

        // we don't have linked gate - check if there is no new in range
        BlockPos targetPos;
        ArrayList<BlockPos> blacklist = new ArrayList<>();
        int loop = 0;
        do {
            loop++;
            targetPos = UDCommonUtils.getNearestLinkable(world, pos, blacklist, this);
            if (targetPos == null)
                break;
            if (!this.linkMatchTest.test(world, targetPos)) {
                blacklist.add(targetPos);
                continue;
            }
            compound.putLong(C_LINKED_POS, targetPos.asLong());
            onLinkUpdated(true, targetPos, compound, stack, (ServerLevel) world, entity);
            break;
        } while (loop < 100);
    }

    @ParametersAreNonnullByDefault
    public void onLinkUpdated(boolean isLinked, BlockPos linkedPos, CompoundTag compound, ItemStack stack, ServerLevel world, Entity entity) {
        if (isLinked && entity instanceof ServerPlayer sp)
            JSGSoundHelper.playSoundToPlayer(sp, JSGSoundEvents.UNIVERSE_DIALER_CONNECTED, entity.blockPosition());
    }

    @ParametersAreNonnullByDefault
    public void handlePacketToServer(UniverseDialerClientActionEnum action, CompoundTag compound, UniverseDialerActionPacketToServer packet, NetworkEvent.Context ctx) {

    }

    @ParametersAreNonnullByDefault
    public boolean onUse(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, boolean shift) {
        return false;
    }

    @ParametersAreNonnullByDefault
    public void keyPressed(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, char keyCode, boolean backspace, boolean shift, boolean alt, boolean ctrl) {

    }

    public CompoundTag getTag(CompoundTag stackTag) {
        if (!stackTag.contains(id + UniverseDialerItem.C_MODE_TAG))
            stackTag.put(id + UniverseDialerItem.C_MODE_TAG, new CompoundTag());
        return stackTag.getCompound(id + UniverseDialerItem.C_MODE_TAG);
    }

    @Nonnull
    public UniverseDialerMode next() {
        return this.next;
    }

    @Nonnull
    public UniverseDialerMode prev() {
        return this.prev;
    }

    @OnlyIn(Dist.CLIENT)
    public String localize() {
        return I18n.format(title);
    }

    public static Optional<UniverseDialerMode> valueOf(ResourceLocation id) {
        return Optional.ofNullable(MODES.get(id));
    }

    public static UniverseDialerMode getDefault() {
        return firstMode;
    }

    public static Collection<UniverseDialerMode> values() {
        return MODES.values();
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UniverseDialerMode mode)) return false;
        return Objects.equals(mode.id, this.id);
    }

    public static void addrToBytes(StargateAddress address, CompoundTag compound, String baseName) {
        if (compound == null || address == null || baseName == null) return;
        compound.putByte(baseName + "_addressLength", (byte) address.getSize());
        compound.putString(baseName + "_symbolType", address.getSymbolType().getId().toString());
        for (int i = 0; i < address.getSize(); i++) {
            compound.putByte(baseName + "_" + i, (byte) address.get(i).getId());
        }
    }
}