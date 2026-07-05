package dev.tauri.jsg.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.irisshaders.iris.pipeline.IrisRenderingPipeline", remap = false)
public class MixinIrisRenderingPipeline {
}
