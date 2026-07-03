package dev.tauri.jsg.client.renderer.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

/**
 * Core shader for the volumetric (raymarched) kawoosh. The proxy box is drawn in gate-local space, so the
 * fragment shader needs the gate pose ({@code ModelMat}) and the camera position in gate-local space
 * ({@code CameraLocal}) to reconstruct view rays, plus the burst {@code Progress}/{@code Time} and the
 * plume dimensions.
 */
public class KawooshShaderInstance extends ShaderInstance {
    private final Uniform MODEL_MAT;
    private final Uniform CAMERA_LOCAL;
    private final Uniform TIME;
    private final Uniform PROGRESS;
    private final Uniform PLUME_LENGTH;
    private final Uniform PLUME_RADIUS;

    public KawooshShaderInstance(ResourceProvider provider, ResourceLocation shaderLocation, VertexFormat format) throws IOException {
        super(provider, shaderLocation, format);
        this.MODEL_MAT = this.getUniform("ModelMat");
        this.CAMERA_LOCAL = this.getUniform("CameraLocal");
        this.TIME = this.getUniform("Time");
        this.PROGRESS = this.getUniform("Progress");
        this.PLUME_LENGTH = this.getUniform("PlumeLength");
        this.PLUME_RADIUS = this.getUniform("PlumeRadius");
    }

    public void setModelMat(Matrix4f mat) {
        if (MODEL_MAT != null) MODEL_MAT.set(mat);
    }

    public void setCameraLocal(Vector3f pos) {
        if (CAMERA_LOCAL != null) CAMERA_LOCAL.set(pos.x, pos.y, pos.z);
    }

    public void setTime(float seconds) {
        if (TIME != null) TIME.set(seconds);
    }

    public void setProgress(float progress) {
        if (PROGRESS != null) PROGRESS.set(progress);
    }

    public void setPlume(float length, float radius) {
        if (PLUME_LENGTH != null) PLUME_LENGTH.set(length);
        if (PLUME_RADIUS != null) PLUME_RADIUS.set(radius);
    }
}
