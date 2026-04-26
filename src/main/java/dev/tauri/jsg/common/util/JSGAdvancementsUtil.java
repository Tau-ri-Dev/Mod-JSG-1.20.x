package dev.tauri.jsg.common.util;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.common.advancements.JSGAdvancements;
import dev.tauri.jsg.common.blockentity.stargate.*;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class JSGAdvancementsUtil {
    public enum EnumAdvancementType {
        GATE_OPEN,
        GATE_MERGE,
        GATE_FLICKER,
        IRIS_IMPACT,

        ZPM_HUB,
        ZPM_SLOT
    }

    public static void tryTriggerRangedAdvancement(BlockEntity tile, EnumAdvancementType advancementType) {
        Level world = tile.getLevel();
        if (world == null) return;
        BlockPos pos = tile.getBlockPos();
        tryTriggerRangedAdvancement(tile, world, pos, advancementType);
    }

    public static void tryTriggerRangedAdvancement(Stargate<?> tile, EnumAdvancementType advancementType) {
        Level world = tile.getStargateLevel();
        if (world == null) return;
        BlockPos pos = tile.blockPosition();
        tryTriggerRangedAdvancement(tile, world, pos, advancementType);
    }

    public static void tryTriggerRangedAdvancement(Object tile, Level level, BlockPos pos, EnumAdvancementType advancementType) {

        int radius = 25; //JSGConfig.General.advancementsRadius;

        List<ServerPlayer> players = level.getNearbyEntities(ServerPlayer.class, TargetingConditions.forNonCombat(), null, new JSGAxisAlignedBB(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius)));
        for (ServerPlayer player : players) {
            switch (advancementType) {
                case GATE_OPEN:
                    if (tile instanceof StargateOrlinBaseBE) {
                        JSGAdvancements.STATIC_ADDRESS_LOCKED.trigger(player);
                        break;
                    }
                    if (!(tile instanceof StargateClassicBaseBE<?> classicTile)) break;
                    int dialedSize = classicTile.getDialingManager().getDialedAddressSize();
                    if (dialedSize == 7)
                        JSGAdvancements.CHEVRON_SEVEN_LOCKED.trigger(player);
                    if (dialedSize == 8)
                        JSGAdvancements.CHEVRON_EIGHT_LOCKED.trigger(player);
                    if (dialedSize == 9)
                        JSGAdvancements.CHEVRON_NINE_LOCKED.trigger(player);
                    break;
                case GATE_MERGE:
                    if (!(tile instanceof StargateAbstractBaseBE)) break;
                    if (tile instanceof StargateTollanBaseBE) {
                        JSGAdvancements.MERGED_TOLLAN.trigger(player);
                        break;
                    }
                    if (tile instanceof StargateMovieBaseBE) {
                        JSGAdvancements.MERGED_MOVIE.trigger(player);
                        break;
                    }
                    if (tile instanceof StargateOrlinBaseBE)
                        JSGAdvancements.MERGED_ORLIN.trigger(player);
                    if (tile instanceof StargateMilkyWayBaseBE)
                        JSGAdvancements.MERGED_MILKYWAY.trigger(player);
                    if (tile instanceof StargatePegasusBaseBE)
                        JSGAdvancements.MERGED_PEGASUS.trigger(player);
                    if (tile instanceof StargateUniverseBaseBE)
                        JSGAdvancements.MERGED_UNIVERSE.trigger(player);
                    break;
                case IRIS_IMPACT:
                    JSGAdvancements.IRIS_IMPACT.trigger(player);
                    break;
                default:
                    break;
            }
        }
    }
}
