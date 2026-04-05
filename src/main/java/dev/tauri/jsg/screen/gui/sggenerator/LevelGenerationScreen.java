package dev.tauri.jsg.screen.gui.sggenerator;

import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.common.util.JSGColorUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LevelGenerationScreen extends AbstractStargateGeneratorScreen {
    protected final StoringChunkProgressListener progressListener;

    public LevelGenerationScreen() {
        super(GameNarrator.NO_TITLE, () -> "menu.preparingSpawn", () -> 1, ConcurrentHashMap::new, Component::empty);
        this.progressListener = Minecraft.getInstance().getProgressListener();
    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public float getProgress() {
        if (progressListener == null)
            return 1f;
        return Mth.clamp(progressListener.getProgress(), 0f, 100f) / 100f;
    }

    @Override
    protected void renderGateAssembly(@NotNull GuiGraphics graphics, int mouseX, int mouseY, double ticks) {
        int x = this.width / 2;
        int y = this.height / 2;
        renderChunks(graphics, progressListener, x, y - 20, 4, 0, mouseX, mouseY, true);
    }

    @Override
    protected void renderProgress(@NotNull GuiGraphics graphics, int x, int y, int progressSize, int mouseX, int mouseY, double ticks) {
        graphics.renderOutline(x, y, progressSize, 10, 0xffffffff);

        var loaderCoef = (ticks % 100) / 100.0;
        var loaderX = Math.max(0, (-10 + (loaderCoef * progressSize)));
        var loaderXRight = Math.min(progressSize - 4, (loaderCoef * progressSize));
        graphics.fill((int) (x + 2 + loaderX), y + 2, (int) (x + 2 + loaderXRight), y + 8, 0xffffffff);
        graphics.fill(x + 2, y + 2, x + 2 + (int) (getProgress() * (progressSize - 4)), y + 8, 0xffd2d2d2);
    }

    private static final Object2IntMap<ChunkStatus> COLORS = Util.make(new Object2IntOpenHashMap<>(), (map) -> {
        map.defaultReturnValue(0);
        map.put(ChunkStatus.EMPTY, 5526612);
        map.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
        map.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
        map.put(ChunkStatus.BIOMES, 8434258);
        map.put(ChunkStatus.NOISE, 13750737);
        map.put(ChunkStatus.SURFACE, 7497737);
        map.put(ChunkStatus.CARVERS, 3159410);
        map.put(ChunkStatus.FEATURES, 2213376);
        map.put(ChunkStatus.INITIALIZE_LIGHT, 13421772);
        map.put(ChunkStatus.LIGHT, 16769184);
        map.put(ChunkStatus.SPAWN, 15884384);
        map.put(ChunkStatus.FULL, 16777215);
    });

    public void renderChunks(GuiGraphics graphics, StoringChunkProgressListener pProgressListener, int pX, int pY, int tileSize, int offset, int mouseX, int mouseY, boolean renderTooltip) {
        int i = tileSize + offset;
        int j = pProgressListener.getFullDiameter();
        int k = j * i - offset;
        int l = pProgressListener.getDiameter();
        int i1 = l * i - offset;
        int j1 = pX - i1 / 2;
        int k1 = pY - i1 / 2;
        int l1 = k / 2 + 1;
        int i2 = -16772609;
        graphics.drawManaged(() -> {
            if (offset != 0) {
                graphics.fill(pX - l1, pY - l1, pX - l1 + 1, pY + l1, i2);
                graphics.fill(pX + l1 - 1, pY - l1, pX + l1, pY + l1, i2);
                graphics.fill(pX - l1, pY - l1, pX + l1, pY - l1 + 1, i2);
                graphics.fill(pX - l1, pY + l1 - 1, pX + l1, pY + l1, i2);
            }

            for (int j2 = 0; j2 < l; ++j2) {
                for (int k2 = 0; k2 < l; ++k2) {
                    ChunkStatus chunkstatus = pProgressListener.getStatus(j2, k2);
                    int l2 = j1 + j2 * i;
                    int i3 = k1 + k2 * i;
                    var color = COLORS.getInt(chunkstatus) | -16777216;
                    var hover = false;
                    if (GuiHelper.isPointInRegion(l2, i3, tileSize, tileSize, mouseX, mouseY)) {
                        color = JSGColorUtil.blendColors(color, 0xffffffff, 0.5f);
                        hover = true;
                    }
                    graphics.fill(l2, i3, l2 + tileSize, i3 + tileSize, color);
                    if (hover) {
                        if (chunkstatus == null) chunkstatus = ChunkStatus.EMPTY;
                        graphics.renderTooltip(font, List.of(Component.literal(chunkstatus.toString())), Optional.empty(), mouseX, mouseY);
                    }
                }
            }

        });
    }
}
