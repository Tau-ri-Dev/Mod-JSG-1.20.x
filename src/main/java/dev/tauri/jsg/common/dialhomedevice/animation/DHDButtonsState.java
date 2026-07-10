package dev.tauri.jsg.common.dialhomedevice.animation;

import dev.tauri.jsg.api.dialhomedevice.animation.IButtonsState;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.client.renderer.activation.DHDActivation;
import dev.tauri.jsg.common.dialhomedevice.manager.state.DHDAbstractStateManager;
import dev.tauri.jsg.core.client.renderer.Activation;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DHDButtonsState extends State implements IButtonsState {
    protected final Map<SymbolInterface, ButtonState> states = new HashMap<>();
    protected final DHDAbstractStateManager<?, ?> stateManager;
    protected final SymbolType<?> symbolType;
    protected final Function<SymbolInterface, String> textureBase;


    public DHDButtonsState(DHDAbstractStateManager<?, ?> stateManager, SymbolType<?> symbolType, Function<SymbolInterface, String> textureBase) {
        this.stateManager = stateManager;
        this.symbolType = symbolType;
        this.textureBase = textureBase;
        for (var symbol : symbolType.getValues()) {
            states.put(symbol, new ButtonState(stateManager, symbolType, getTextureLoader(), textureBase, symbol));
        }
    }

    public ITextureLoader getTextureLoader() {
        return symbolType.getTextureLoader();
    }

    @Override
    public ButtonState get(SymbolInterface symbol) {
        return states.get(symbol);
    }

    @Override
    public List<SymbolInterface> getActivatedButtons() {
        return states.entrySet().stream()
                .filter(e -> e.getValue().isActive())
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        var states = new ListTag();
        for (var symbol : symbolType.getValues()) {
            var stateTag = this.states.get(symbol).serializeNBT();
            stateTag.putInt("symbol", symbol.getId());
            states.add(stateTag);
        }
        compound.put("states", states);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        var states = compound.getList("states", Tag.TAG_COMPOUND);
        for (var s : states) {
            var sCompound = (CompoundTag) s;
            var symbol = symbolType.valueOf(sCompound.getInt("symbol"));
            var state = new ButtonState(stateManager, symbolType, getTextureLoader(), textureBase, symbol, sCompound);
            this.states.put(symbol, state);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for (var symbol : symbolType.getValues()) {
            this.states.get(symbol).toBytes(buf);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        for (var symbol : symbolType.getValues()) {
            this.states.put(symbol, new ButtonState(stateManager, symbolType, getTextureLoader(), textureBase, symbol, buf));
        }
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide()) return;
        for (var s : states.values()) {
            var activation = s.activation;
            var lightStage = s.lightStage;

            s.tick(level);

            if (activation != s.activation || lightStage != s.lightStage) {
                stateManager.dhd.setChanged();
            }
        }
    }

    public static class ButtonState extends State implements IButtonState, INBTSerializable<CompoundTag>, ITickable {
        private final Map<BiomeOverlayInstance, Map<Integer, ResourceLocation>> BIOME_TEXTURE_MAP = new HashMap<>();

        protected final DHDAbstractStateManager<?, ?> stateManager;
        protected final SymbolType<?> symbolType;
        protected final SymbolInterface symbol;
        @Nullable
        protected Activation<SymbolInterface> activation;
        protected float lightStage;

        protected Activation<SymbolInterface> getActivation(CompoundTag tag) {
            return new DHDActivation(symbol, tag);
        }

        protected Activation<SymbolInterface> getActivation(ByteBuf buf) {
            return new DHDActivation(symbol, buf);
        }

        protected ButtonState(DHDAbstractStateManager<?, ?> stateManager, SymbolType<?> symbolType, ITextureLoader textureLoader, Function<SymbolInterface, String> buttonTexBase, SymbolInterface symbol) {
            this.stateManager = stateManager;
            this.symbolType = symbolType;
            this.symbol = symbol;
            for (BiomeOverlayInstance biomeOverlay : BiomeOverlayInstance.values()) {
                BIOME_TEXTURE_MAP.put(biomeOverlay, Util.make(new HashMap<>(), (map) -> {
                    for (int i = 0; i <= 5; i++) {
                        map.put(i, textureLoader.getTextureResource(buttonTexBase.apply(symbol) + i + biomeOverlay.suffix()));
                    }
                }));
            }
        }

        public ButtonState(DHDAbstractStateManager<?, ?> stateManager, SymbolType<?> symbolType, ITextureLoader textureLoader, Function<SymbolInterface, String> buttonTexBase, SymbolInterface symbol, CompoundTag compound) {
            this(stateManager, symbolType, textureLoader, buttonTexBase, symbol);
            deserializeNBT(compound);
        }

        public ButtonState(DHDAbstractStateManager<?, ?> stateManager, SymbolType<?> symbolType, ITextureLoader textureLoader, Function<SymbolInterface, String> buttonTexBase, SymbolInterface symbol, ByteBuf buf) {
            this(stateManager, symbolType, textureLoader, buttonTexBase, symbol);
            fromBytes(buf);
        }

        protected double getTickCompensation() {
            return 2;
        }

        @Override
        public void activate() {
            if (activation != null && activation.isActive() && activation.dim)
                activation = new DHDActivation(symbol, activation.stateChange, false);
            else if (isActive()) return;
            else
                activation = new DHDActivation(symbol, stateManager.dhd.getTime(), false);
            stateManager.dhd.setChanged();
            send();
        }

        @Override
        public void deactivate() {
            if (activation != null && activation.isActive() && !activation.dim)
                activation = new DHDActivation(symbol, activation.stateChange, true);
            else if (!isActive()) return;
            else
                activation = new DHDActivation(symbol, stateManager.dhd.getTime(), true);
            stateManager.dhd.setChanged();
            send();
        }

        @Override
        public boolean isActive() {
            return lightStage > 0;
        }

        public float getState() {
            return lightStage;
        }

        public ResourceLocation getTexture(BiomeOverlayInstance biomeOverlay) {
            if (getState() > 0)
                return BIOME_TEXTURE_MAP.get(biomeOverlay).get((int) getState());
            return BIOME_TEXTURE_MAP.get(biomeOverlay).get(0);
        }

        public void update(float partialTicks) {
            if (activation != null) {
                var result = activation.activate(stateManager.dhd.getTime(), partialTicks);
                lightStage = result.stage;
                if (result.remove) {
                    lightStage = activation.getFinalState();
                    activation = null;
                }
            }
        }

        public void send() {
            onServer(() -> stateManager.getAndSendState(JSGStateTypes.BUTTONS_STATE.get()));
        }

        public void onServer(Runnable runnable) {
            if (stateManager.dhd.getLevel() == null || stateManager.dhd.getLevel().isClientSide()) return;
            runnable.run();
        }

        @Override
        public void tick(@NotNull Level level) {
            // update button light on server too
            update(0);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeFloat(lightStage);
            if (activation != null) {
                buf.writeBoolean(true);
                activation.toBytes(buf);
            } else buf.writeBoolean(false);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            lightStage = buf.readFloat();
            if (buf.readBoolean()) {
                activation = getActivation(buf);
                activation.stateChange = (long) Math.ceil(activation.stateChange + getTickCompensation());
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            var compound = new CompoundTag();
            compound.putFloat("lightStage", lightStage);
            if (activation != null)
                compound.put("activation", activation.serializeNBT());
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
            lightStage = compound.getFloat("lightStage");
            if (compound.contains("activation"))
                activation = getActivation(compound.getCompound("activation"));
        }
    }
}
