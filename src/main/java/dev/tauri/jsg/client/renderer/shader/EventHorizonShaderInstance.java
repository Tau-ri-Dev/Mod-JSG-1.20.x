package dev.tauri.jsg.client.renderer.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Vector3f;

import java.io.IOException;

/**
 * Core shader for the procedural Stargate event horizon. Adds an animated {@code Time} uniform (seconds)
 * and a {@code SurfaceNormal} uniform (the disc's facing direction, used to displace the 3D waves in the
 * vertex stage) on top of the built-in matrix/colour uniforms; the rest of the look lives in the GLSL.
 */
public class EventHorizonShaderInstance extends ShaderInstance {
    private final Uniform TIME;
    private final Uniform SURFACE_NORMAL;
    private final Uniform FILL_PROGRESS;
    private final Uniform WHITE_AMOUNT;

    public EventHorizonShaderInstance(ResourceProvider provider, ResourceLocation shaderLocation, VertexFormat format) throws IOException {
        super(provider, shaderLocation, format);
        this.TIME = this.getUniform("Time");
        this.SURFACE_NORMAL = this.getUniform("SurfaceNormal");
        this.FILL_PROGRESS = this.getUniform("FillProgress");
        this.WHITE_AMOUNT = this.getUniform("WhiteAmount");
    }

    public void setTime(float seconds) {
        if (TIME != null) {
            TIME.set(seconds);
        }
    }

    /**
     * Formation state. {@code fillProgress} 0..1 = how far the puddle has grown in from the rim (0 empty,
     * 1 full); {@code whiteAmount} 0..1 = splash whiteness (1 = bright white flash, 0 = settled water).
     */
    public void setFormation(float fillProgress, float whiteAmount) {
        if (FILL_PROGRESS != null) FILL_PROGRESS.set(fillProgress);
        if (WHITE_AMOUNT != null) WHITE_AMOUNT.set(whiteAmount);
    }

    public void setSurfaceNormal(Vector3f normal) {
        if (SURFACE_NORMAL != null) {
            SURFACE_NORMAL.set(normal.x, normal.y, normal.z);
        }
    }
}
