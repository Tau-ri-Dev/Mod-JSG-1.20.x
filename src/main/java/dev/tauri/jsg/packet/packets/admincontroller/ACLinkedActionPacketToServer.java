package dev.tauri.jsg.packet.packets.admincontroller;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.StargateWithIris;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.item.admincontroller.ACUtils;
import dev.tauri.jsg.item.admincontroller.AdminControllerAction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public class ACLinkedActionPacketToServer extends ACPacketToServer {
    AdminControllerAction action;

    public ACLinkedActionPacketToServer(StargatePos gatePos, AdminControllerAction action) {
        super(gatePos);
        this.action = action;
    }

    public ACLinkedActionPacketToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(action.ordinal());
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        action = AdminControllerAction.values()[buf.readInt()];
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            var sender = ctx.getSender();
            if (sender == null) return;
            var linkedGate = Optional.ofNullable(gatePos).map(pos -> sender.level().getBlockEntity(pos.gatePos)).flatMap(tile -> {
                if (tile instanceof Stargate<?> sg) return Optional.of(sg);
                return Optional.empty();
            }).orElse(null);
            if (linkedGate == null) return;
            switch (action) {
                case TOGGLE_IRIS:
                    if (!(linkedGate instanceof StargateWithIris<?> stargateWithIris) || !stargateWithIris.getIrisManager().hasIris()) {
                        response(sender, ACUtils.getError(Component.translatable("gui.admincontroller.response.stargate.iris.empty")));
                        return;
                    }
                    var state = stargateWithIris.getIrisManager().getIrisState();
                    if (!stargateWithIris.getIrisManager().toggleIris()) {
                        response(sender, ACUtils.getError(Component.translatable("gui.admincontroller.response.stargate.iris.busy")));
                        return;
                    }
                    response(sender, ACUtils.getSuccess(Component.translatable("gui.admincontroller.response.stargate.iris." + (state == EnumIrisState.OPENED ? "close" : "open"))));
                    break;
                case ABORT_DIALING:
                    if (linkedGate.getDialingManager().abortDialingSequence()) {
                        response(sender, ACUtils.getSuccess(Component.translatable("gui.admincontroller.response.stargate.abort.success")));
                        return;
                    }
                    response(sender, ACUtils.getError(Component.translatable("gui.admincontroller.response.stargate.abort.error")));
                    break;
                case CLOSE_GATE:
                    var closeResult = linkedGate.getDialingManager().attemptClose(StargateClosedReasonEnum.REQUESTED);
                    if (closeResult.ok()) {
                        response(sender, ACUtils.getSuccess(Component.translatable("gui.admincontroller.response.stargate.close.success")));
                        return;
                    }
                    response(sender, ACUtils.getError(Component.translatable("gui.admincontroller.response.stargate.close.error." + closeResult.name().toLowerCase())));
                    break;
                default:
                    break;
            }
        });
    }
}
