package dev.tauri.jsg.listener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.util.CreativeItemsChecker;
import dev.tauri.jsg.entity.behaviour.DialGateBehaviour;
import dev.tauri.jsg.registry.JSGVillagers;
import dev.tauri.jsg.registry.util.VillagerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;


@Mod.EventBusSubscriber(modid = JSG.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeListener {
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent e) {
        var p = e.getEntity();
        var i = e.getItem().getItem();
        if (CreativeItemsChecker.canInteractWith(i, p.isCreative())) return;
        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onVillagerSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (JSGVillagers.isPriest(villager.getVillagerData().getProfession())) {
            villager.getBrain().addActivity(Activity.IDLE, ImmutableList.of(Pair.of(0, new DialGateBehaviour())));
        }

        // todo: fix activity not being set after profession change
        if (villager.getVillagerData().getProfession() == JSGVillagers.SLAVE_MINER.get()) {
            villager.getBrain().addActivityWithConditions(Activity.WORK, VillagerUtil.getWorkPackageForSlave((blockState) -> CoreBlocks.ORE_NAQUADAH.values().stream().map(RegistryObject::get).anyMatch(block -> block == blockState.getBlock()), 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
            villager.getInventory().setItem(0, new ItemStack(Items.IRON_PICKAXE));
        }
        JSGVillagers.checkAndGetCustomType(villager).ifPresent(villagerType -> villager.setVillagerData(villager.getVillagerData().setType(villagerType)));
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        JSGVillagers.registerTrades(event);
    }

    @SubscribeEvent
    public static void onArrowHit(ProjectileImpactEvent event) {
        boolean isSnowball = event.getProjectile() instanceof Snowball;
        var result = event.getRayTraceResult();
        if (result.getType() != HitResult.Type.BLOCK) return;
        if (event.getEntity().level().isClientSide) return;
        var be = event.getProjectile().level().getBlockEntity(BlockPos.containing(event.getRayTraceResult().getLocation()));
        var be2 = event.getProjectile().level().getBlockEntity(BlockPos.containing(event.getRayTraceResult().getLocation().add(0, -1, 0)));
        DHDAbstractBE dhdTile;
        if (!(be instanceof DHDAbstractBE dhdTile1)) {
            if (!(be2 instanceof DHDAbstractBE dhdTile2)) return;
            else dhdTile = dhdTile2;
        } else dhdTile = dhdTile1;
        if (!dhdTile.isLinked()) return;
        var gate = dhdTile.getLinkedDevice();
        if (gate != null) {
            if (gate.getDialingManager().getDialedAddress().size() == 6) {
                dhdTile.pushSymbolButton(dhdTile.getSymbolType().getOrigin(), null, false);
                return;
            }
            if (gate.getDialingManager().getDialedAddress().size() == 7 && dhdTile.getSymbolType().getBRB() != null) {
                dhdTile.pushSymbolButton(dhdTile.getSymbolType().getBRB(), null, false);
                return;
            }
        }

        int symbolsCount = isSnowball ? 3 : 1;
        var r = new Random();
        for (int i = 0; i < symbolsCount; i++)
            dhdTile.pushSymbolButton(dhdTile.getSymbolType().getRandomSymbol(r), null, false);
    }
}
