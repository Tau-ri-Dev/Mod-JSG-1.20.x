package dev.tauri.jsg.api.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RayTraceHelper {

    @Nullable
    public static BlockHitResult rayTraceEntity(Entity e, double reach) {
        if (e == null) return null;
        float f = e.getXRot();
        float f1 = e.getYRot();
        Vec3 vec3 = e.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec31 = vec3.add((double) f6 * reach, (double) f5 * reach, (double) f7 * reach);
        return e.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e));
    }

    @Nullable
    public static BlockEntity rayTraceTileEntity(@Nonnull Player player) {
        return rayTraceTileEntity(player, 8);
    }

    @Nullable
    public static BlockEntity rayTraceTileEntity(@Nonnull Player player, int distance) {
        try {
            BlockHitResult blockHitResult = rayTraceEntity(player, distance);
            if (blockHitResult != null && blockHitResult.getType() == BlockHitResult.Type.BLOCK) {
                return player.level().getBlockEntity(blockHitResult.getBlockPos());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    public static BlockPos rayTracePos(@Nonnull Player player, int distance) {
        try {
            BlockHitResult blockHitResult = rayTraceEntity(player, distance);
            if (blockHitResult != null && blockHitResult.getType() == BlockHitResult.Type.BLOCK) {
                return blockHitResult.getBlockPos();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
