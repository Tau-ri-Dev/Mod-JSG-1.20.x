package dev.tauri.jsg.common.capability;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.blockentity.jub.JUBCableBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractMemberBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.JSGCore;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Central capability registration replacing the 1.20.1 getCapability overrides.
 * Providers are registered for every JSG block entity type and dispatch on the
 * instance, mirroring the old override chain.
 */
@EventBusSubscriber(modid = JSG.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class JSGCapabilityRegistration {
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        for (var holder : JSGApi.REGISTRY_HELPER.be().getEntries()) {
            BlockEntityType<?> type = holder.get();

            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (be, side) -> {
                if (be instanceof StargateAbstractBaseBE gate)
                    return gate.getEnergyManager().getStorageForCaps();
                if (be instanceof StargateAbstractMemberBE member
                        && member.getLevel() != null && member.getBasePos() != null
                        && member.getLevel().getBlockEntity(member.getBasePos()) instanceof Stargate<?> baseGate)
                    return baseGate.getEnergyManager().getStorageForCaps();
                return null;
            });

            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (be, side) ->
                    be instanceof StargateClassicBaseBE gate ? gate.getInventoryHandler() : null);

            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, (be, side) ->
                    be instanceof DHDAbstractBE dhd ? dhd.getExposedFluidHandler() : null);

            event.registerBlockEntity(JSGCapabilities.JUST_UNIVERSAL_BUS, type, (be, side) -> {
                if (be instanceof StargateClassicBaseBE gate) return gate.getJubDevice();
                if (be instanceof JUBCableBE cable) return cable.jubDevice;
                return null;
            });

            // CC:Tweaked peripherals (no-op when CC is absent)
            JSGCore.ccWrapper.registerPeripheralBE(event, type);
        }
    }
}
