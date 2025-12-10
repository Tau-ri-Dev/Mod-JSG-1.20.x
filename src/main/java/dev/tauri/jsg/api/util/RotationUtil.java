package dev.tauri.jsg.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class RotationUtil {
    public static BlockPos rotate(BlockPos pos, Quaternionf rotation) {
        return rotate(pos, rotation, BlockPos.ZERO);
    }

    public static BlockPos rotate(BlockPos pos, Quaternionf rotation, BlockPos pivot) {
        var v = rotate(new Vec3(pos.getX() - pivot.getX(), pos.getY() - pivot.getY(), pos.getZ() - pivot.getZ()), rotation);
        return new BlockPos((int) v.x + pivot.getX(), (int) v.y + pivot.getY(), (int) v.z + pivot.getZ());
    }

    public static AABB rotate(AABB box, Quaternionf rotation) {
        return rotate(box, rotation, new Vec3(0, 0, 0));
    }

    public static AABB rotate(AABB box, Quaternionf rotation, Vec3 pivot) {
        var p1 = rotate(new Vec3(box.minX, box.minY, box.minZ).subtract(pivot), rotation);
        var p2 = rotate(new Vec3(box.maxX, box.maxY, box.maxZ).subtract(pivot), rotation);
        return new AABB(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z).move(pivot);
    }

    public static JSGAxisAlignedBB rotate(JSGAxisAlignedBB box, Quaternionf rotation) {
        return rotate(box, rotation, new Vec3(0, 0, 0));
    }

    public static JSGAxisAlignedBB rotate(JSGAxisAlignedBB box, Quaternionf rotation, Vec3 pivot) {
        var p1 = rotate(new Vec3(box.minX, box.minY, box.minZ).subtract(pivot), rotation);
        var p2 = rotate(new Vec3(box.maxX, box.maxY, box.maxZ).subtract(pivot), rotation);
        return new JSGAxisAlignedBB(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z).offset(pivot);
    }

    public static Vec3 rotate(Vec3 vec3, Quaternionf rotation) {
        return rotate(vec3, rotation, new Vec3(0, 0, 0));
    }

    public static Vec3 rotate(Vec3 vec3, Quaternionf rotation, Vec3 pivot) {
        var q = rotation.normalize().transform(new Vector3d(vec3.x - pivot.x, vec3.y - pivot.y, vec3.z - pivot.z));
        return new Vec3(q.x, q.y, q.z).add(pivot);
    }

    public static Quaternionf getRotation(Direction verticalFacing, Direction horizontalFacing) {
        var h = horizontalFacing.getRotation();
        if (verticalFacing != Direction.UP && verticalFacing != Direction.DOWN)
            return h.mul(Direction.SOUTH.getRotation().rotateX((float) Math.PI));
        return h.mul(verticalFacing.getRotation().rotateX((float) Math.PI));
    }

    public static Quaternionf getRotationToZero(Direction verticalFacing, Direction horizontalFacing) {
        return getRotation((verticalFacing == null ? Direction.NORTH : verticalFacing.getOpposite()), horizontalFacing.getOpposite()).invert();
    }

    public static Vec3 yawToVector(float yaw) {
        float rad = (float) Math.toRadians(-yaw);
        double x = Math.sin(rad);
        double z = Math.cos(rad);
        return new Vec3(x, 0, z);
    }

    public static float vectorToYaw(Vec3 v) {
        return (float) Math.toDegrees(Math.atan2(-v.x, v.z));
    }
}
