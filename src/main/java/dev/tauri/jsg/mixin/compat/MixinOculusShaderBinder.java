package dev.tauri.jsg.mixin.compat;

import dev.tauri.jsg.client.renderer.shader.JSGShaders;
import net.irisshaders.iris.pipeline.programs.FallbackShader;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("all")
@Pseudo
@Mixin(targets = "net.irisshaders.iris.pipeline.programs.FallbackShader", remap = false)
public class MixinOculusShaderBinder {
    @Inject(method = "apply", at = @At("HEAD"))
    private void onBindShader(CallbackInfo ci) {
        var name = ((FallbackShader) (Object) this).getName();
        var programId = ((FallbackShader) (Object) this).getId();
        if (name == null) return;
        if (name.contains("event_horizon")) {
            var shader = JSGShaders.getEventHorizonShaderInstance();

            int timeLoc = GL20.glGetUniformLocation(programId, "Time");
            if (timeLoc != -1) {
                GL20.glUniform1f(timeLoc, shader.time);
            }

            int surfaceNormalLoc = GL20.glGetUniformLocation(programId, "SurfaceNormal");
            if (surfaceNormalLoc != -1) {
                GL20.glUniform3f(surfaceNormalLoc, shader.surfaceNormal.x, shader.surfaceNormal.y, shader.surfaceNormal.z);
            }

            int fillProgressLoc = GL20.glGetUniformLocation(programId, "FillProgress");
            if (fillProgressLoc != -1) {
                GL20.glUniform1f(fillProgressLoc, shader.fillProgress);
            }

            int whiteAmountLoc = GL20.glGetUniformLocation(programId, "WhiteAmount");
            if (whiteAmountLoc != -1) {
                GL20.glUniform1f(whiteAmountLoc, shader.whiteAmount);
            }
        }
    }
}
