package dev.tauri.jsg.api.raycaster.util;

import dev.tauri.jsg.api.util.vectors.Matrix3f;
import dev.tauri.jsg.api.util.vectors.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class RaycasterVertex {
    public final float x;
    public final float y;
    public final float z;

    private double xRotated;
    private double yRotated;
    private double zRotated;

    private double xGlobal;
    private double yGlobal;
    private double zGlobal;

    BlockPos oldGlobal = null;

    public RaycasterVertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("%f %f %f", x, y, z);
    }

    public RaycasterVertex rotate(double angle) {
        angle = Math.toRadians(angle);
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);

        Matrix3f rotationMatrix = new Matrix3f();
        Matrix3f vertex = new Matrix3f();
        Matrix3f out = new Matrix3f();

        // Z
        rotationMatrix.m00 = cos;
        rotationMatrix.m10 = -sin;
        rotationMatrix.m20 = 0;
        rotationMatrix.m01 = sin;
        rotationMatrix.m11 = cos;
        rotationMatrix.m21 = 0;
        rotationMatrix.m02 = 0;
        rotationMatrix.m12 = 0;
        rotationMatrix.m22 = 1;

        vertex.m00 = x;
        vertex.m01 = y;
        vertex.m02 = z;

        Matrix3f.mul(rotationMatrix, vertex, out);

        xRotated = out.m00;
        yRotated = out.m01;
        zRotated = out.m02;

        return this;
    }

    public RaycasterVertex localToGlobal(BlockPos pos, Vector3f translation, float scale) {
        if (!pos.equals(oldGlobal)) {
            xGlobal = (xRotated * scale) + translation.x + pos.getX();
            yGlobal = (zRotated * scale) + translation.y + pos.getY();
            zGlobal = (-yRotated * scale) + translation.z + pos.getZ(); // Blender coords are xzy

            oldGlobal = pos;
        }

        return this;
    }

    public Vec3 getVec3() {
        return new Vec3(xGlobal, yGlobal, zGlobal);
    }
}