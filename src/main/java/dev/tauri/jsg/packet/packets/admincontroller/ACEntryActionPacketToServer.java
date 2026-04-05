package dev.tauri.jsg.packet.packets.admincontroller;

import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.api.registry.JSGNotebookPageTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.helper.BlockHelper;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.item.admincontroller.ACUtils;
import dev.tauri.jsg.item.admincontroller.AdminControllerAction;
import dev.tauri.jsg.stargate.teleportation.traveler.PlayerTraveler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ACEntryActionPacketToServer extends ACPacketToServer {
    BlockPos linkedGatePos;
    AdminControllerAction action;
    Map<SymbolType<?>, StargateAddress> addresses;

    @ParametersAreNonnullByDefault
    public ACEntryActionPacketToServer(@Nullable BlockPos linkedGatePos, StargatePos gatePos, AdminControllerAction action, Map<SymbolType<?>, StargateAddress> addresses) {
        super(gatePos);
        this.linkedGatePos = linkedGatePos;
        this.action = action;
        this.addresses = addresses;
    }

    public ACEntryActionPacketToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(action.ordinal());
        if (linkedGatePos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(linkedGatePos);
        } else buf.writeBoolean(false);
        buf.writeInt(addresses.size());
        for (Map.Entry<SymbolType<?>, StargateAddress> entry : addresses.entrySet()) {
            buf.writeResourceLocation(entry.getKey().getId());
            entry.getValue().toBytes(buf);
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        action = AdminControllerAction.values()[buf.readInt()];
        if (buf.readBoolean())
            linkedGatePos = buf.readBlockPos();
        addresses = new HashMap<>();
        var size = buf.readInt();
        for (int i = 0; i < size; i++) {
            var type = SymbolType.byId(buf.readResourceLocation());
            var address = new StargateAddress(buf);
            addresses.put(type, address);
        }
    }

    protected boolean checkAndSendErrorIfNotLinked(ServerPlayer player) {
        if (linkedGatePos == null) {
            response(player, ACUtils.getError(Component.translatable("gui.admincontroller.response.stargate.not_linked.error")));
            return false;
        }
        return true;
    }

    protected boolean checkMergedOrSendError(ServerPlayer player, Stargate<?> stargate) {
        if (!stargate.isMerged()) {
            response(player, ACUtils.getError(Component.translatable("gui.admincontroller.response.stargate.not_merged.error")));
            return false;
        }
        return true;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            var sender = ctx.getSender();
            if (sender == null) return;
            var linkedGate = Optional.ofNullable(linkedGatePos).map(pos -> sender.level().getBlockEntity(pos)).flatMap(tile -> {
                if (tile instanceof Stargate<?> sg) return Optional.of(sg);
                return Optional.empty();
            }).orElse(null);

            switch (action) {
                case SLOW_DIAL:
                case FAST_DIAL:
                case NOX_DIAL:
                    if (!checkAndSendErrorIfNotLinked(sender) || linkedGate == null) return;
                    if (!checkMergedOrSendError(sender, linkedGate)) return;
                    var address = addresses.get(linkedGate.getSymbolType());
                    var addressDynamic = StargateAddressDynamic.getMinimalDialable(address, linkedGate, gatePos);
                    var result = linkedGate.getDialingManager().dialAddress(addressDynamic, true, true, action.getDialingType());
                    if (!result.ok()) {
                        response(sender, ACUtils.getError(Component.translatable("gui.admincontroller.response.stargate.dial_address.error." + result.name().toLowerCase())));
                        return;
                    }
                    response(sender, ACUtils.getSuccess(Component.translatable("gui.admincontroller.response.stargate.dial_address.success")));
                    break;
                case TELEPORT:
                    var traveler = new PlayerTraveler(sender, gatePos.gatePos.above(3).getCenter(), new Vec3(0, 0, 0), new Vec3(0, 0, 0), sender.getYRot(), gatePos.getStargate(), gatePos.getStargate(), true);
                    traveler.confirmSend();
                    break;
                case ADDRESS_GIVE:
                    ListTag pages = new ListTag();
                    for (SymbolType<?> s : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
                        var a = addresses.get(s);
                        var pageCompound = JSGNotebookPageTypes.STARGATE_ADDRESS.get().createCompoundTag(new StargateAddressData(new StargateAddressDynamic(a)), PageNotebookItemFilled.getBiomeKeyFromWorld(Objects.requireNonNull(sender.getServer()).getLevel(gatePos.dimension), gatePos.gatePos));
                        PageNotebookItemFilled.setName(pageCompound, s.getId().getPath());
                        pages.add(pageCompound);
                    }

                    var notebook = NotebookItem.createNotebook(pages);
                    if (!gatePos.getName().isEmpty())
                        notebook.setHoverName(Component.literal(gatePos.getName()));
                    else
                        notebook.setHoverName(Component.literal(BlockHelper.blockPosToBetterString(gatePos.gatePos)));

                    sender.addItem(notebook);
                    response(sender, ACUtils.getSuccess(Component.translatable("gui.admincontroller.response.stargate.give_address.success")));
                    break;
                default:
                    break;
            }
        });
    }
}
