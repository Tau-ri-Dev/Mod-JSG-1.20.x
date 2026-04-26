package dev.tauri.jsg.common.worldgen;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.client.screen.provider.SGGeneratorGuiProvider;
import dev.tauri.jsg.common.registry.JSGDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ModMismatchEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AbydosUpdateHandler {

    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onVersionMismatch(ModMismatchEvent e) {
        var jsgOldVersion = e.getPreviousVersion(JSG.MOD_ID);
        if (jsgOldVersion == null) return;
        var splitVersion = jsgOldVersion.getQualifier().split("\\.");
        if (Integer.parseInt(splitVersion[0]) > 5)
            return;
        if (Integer.parseInt(splitVersion[0]) >= 5 && Integer.parseInt(splitVersion[1]) >= 1)
            return;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            SGGeneratorGuiProvider.showAbydosWarning((proceed) -> {
                if (proceed) {
                    var abydosDimFolder = Path.of(e.getLevelDirectory().path().toString(), "/dimensions/jsg/abydos/").toFile();
                    if (abydosDimFolder.exists() && abydosDimFolder.isDirectory()) {
                        Minecraft.getInstance().forceSetScreen(new GenericDirtMessageScreen(Component.literal("joinWorld.jsg.abydos_update.deleting_world")));
                        JSG.logger.warn("Deleting {}", abydosDimFolder.getAbsolutePath());
                        try {
                            FileUtils.deleteDirectory(abydosDimFolder);
                            JSG.logger.info("Deleted {}", abydosDimFolder.getAbsolutePath());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    try {
                        JSG.logger.info("Rewriting {}", Path.of(e.getLevelDirectory().path().toString(), "/level.dat").toString());
                        var levelDat = NbtIo.readCompressed(Path.of(e.getLevelDirectory().path().toString(), "/level.dat").toFile());
                        var data = levelDat.getCompound("Data");
                        var wgs = data.getCompound("WorldGenSettings");
                        var dims = wgs.getCompound("dimensions");
                        dims.remove(JSGDimensions.ABYDOS.location().toString());
                        wgs.put("dimensions", dims);
                        data.put("WorldGenSettings", wgs);
                        levelDat.put("Data", data);

                        var fml = levelDat.getCompound("fml");
                        var lml = levelDat.getList("LoadingModList", Tag.TAG_COMPOUND);
                        var newList = new ListTag();
                        for (var m : lml) {
                            var id = ((CompoundTag) m).getString("ModId");
                            var version = ((CompoundTag) m).getString("ModVersion");
                            var newCompound = new CompoundTag();
                            newCompound.putString("ModId", id);
                            newCompound.putString("ModVersion", version);
                            if (id.equalsIgnoreCase(JSG.MOD_ID))
                                newCompound.putString("ModVersion", JSG.MOD_VERSION_ONLY);
                            newList.add(newCompound);
                        }
                        fml.put("LoadingModList", newList);
                        NbtIo.writeCompressed(levelDat, Path.of(e.getLevelDirectory().path().toString(), "/level.dat").toFile());
                        JSG.logger.info("Rewrite done!");
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    e.markResolved(JSG.MOD_ID);
                }
                Minecraft.getInstance().forceSetScreen(new SelectWorldScreen(null));
            });
        });
    }
}
