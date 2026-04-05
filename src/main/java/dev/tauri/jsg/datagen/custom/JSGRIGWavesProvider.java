package dev.tauri.jsg.datagen.custom;

import com.mojang.serialization.JsonOps;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGRegistries;
import dev.tauri.jsg.config.stargate.StargateRIGConfig;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.stargate.rig.RIGWave;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JSGRIGWavesProvider implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;

    public JSGRIGWavesProvider(PackOutput pOutput) {
        this.pathProvider = pOutput.createPathProvider(PackOutput.Target.DATA_PACK, ForgeHooks.prefixNamespace(JSGRegistries.RIG_WAVES.location()));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        for (var wave : StargateRIGConfig.DEFAULT_VALUES.entrySet()) {
            list.add(DataProvider.saveStable(pOutput, RIGWave.CODEC.encodeStart(JsonOps.INSTANCE, wave.getValue()).get().orThrow(), pathProvider.json(JSGMapping.rl(JSG.MOD_ID, wave.getKey().toLowerCase()))));
        }

        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "RIG Waves";
    }
}
