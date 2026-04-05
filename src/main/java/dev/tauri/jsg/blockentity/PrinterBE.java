package dev.tauri.jsg.blockentity;

import com.google.common.collect.Maps;
import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.api.registry.JSGNotebookPageTypes;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockentity.StateProviderInterface;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.integration.ComputerDeviceHolder;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemEmpty;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.item.CartridgeItem;
import dev.tauri.jsg.registry.JSGBlockEntities;
import dev.tauri.jsg.registry.JSGPositionedSounds;
import dev.tauri.jsg.renderer.machine.PrinterRendererState;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PrinterBE extends BlockEntity implements ITickable, ComputerDeviceProvider, StateProviderInterface {
    public PrinterBE(BlockPos pPos, BlockState pBlockState) {
        super(JSGBlockEntities.PRINTER.get(), pPos, pBlockState);
        if (level != null && !level.isClientSide)
            createDeviceHolder();
    }

    public ItemStack inputPages = ItemStack.EMPTY;
    public final LinkedList<ItemStack> outputPages = new LinkedList<>();
    public List<ItemStack> cartridges = new ArrayList<>();
    public long printStarted;
    public int editPos;
    @Nullable
    public PointOfOrigin origin;
    public int originPos;
    public StargateAddressDynamic address;
    public final List<Integer> symbolsToPrint = new ArrayList<>();

    public LinkedList<Component> printCustomText = null;
    public String titleOverride = null;


    public boolean noInk() {
        if (cartridges.size() < 4) return true;
        return cartridges.stream().anyMatch((c) -> getInkStatus(c) < ((CartridgeItem) c.getItem()).inkPerPage);
    }

    public static double getInkStatus(ItemStack stack) {
        if (!stack.hasTag()) return 1;
        var tag = stack.getOrCreateTag();
        if (!tag.contains("inkStatus")) return 1;
        return tag.getDouble("inkStatus");
    }

    public void shrinkInk() {
        cartridges.forEach((c) -> {
            var tag = c.getOrCreateTag();
            tag.putDouble("inkStatus", getInkStatus(c) - ((CartridgeItem) c.getItem()).inkPerPage);
            c.setTag(tag);
        });
        setChanged();
    }

    public Map<Color, Pair<Double, Boolean>> getInkStatus() {
        var map = new HashMap<Color, Pair<Double, Boolean>>();
        cartridges.forEach((c) -> {
            var amount = getInkStatus(c);
            var item = ((CartridgeItem) c.getItem());
            map.put(item.renderColor, Pair.of(amount, amount >= item.inkPerPage));
        });
        return map;
    }

    public Map<Color, Double> getInkStatusAmountOnly() {
        var map = new HashMap<Color, Double>();
        cartridges.forEach((c) -> {
            var amount = getInkStatus(c);
            var color = ((CartridgeItem) c.getItem()).renderColor;
            map.put(color, amount);
        });
        return map;
    }

    public ItemStack getNextEmptyAndRemove() {
        var empties = cartridges.stream().filter((c) -> {
            if (!c.hasTag()) return false;
            var tag = c.getOrCreateTag();
            var item = (CartridgeItem) c.getItem();
            return (tag.getDouble("inkStatus") < item.inkPerPage);
        }).toList();
        if (empties.isEmpty()) return null;
        var stack = empties.get(0);
        cartridges.remove(stack);

        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());

        return stack;
    }

    public boolean tryInsertCartridge(ItemStack stack) {
        if (cartridges.size() >= 4) return false;
        if (!(stack.getItem() instanceof CartridgeItem cartridgeItem))
            return false;
        if (cartridges.stream().anyMatch((c) -> stack.getItem() == c.getItem())) return false;

        if (getInkStatus(stack) < cartridgeItem.inkPerPage) return false;

        var copy = stack.copy();
        copy.setCount(1);
        cartridges.add(copy);
        stack.shrink(1);

        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
        return true;
    }


    public static final double PRINTING_TIME = 20 * 5.777f;
    private boolean addedToNetwork = false;

    @Override
    public void tick(@NotNull Level level) {
        if (!level.isClientSide) {
            if (targetPoint == null) {
                targetPoint = new PacketDistributor.TargetPoint(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 512, level.dimension());
                setChanged();
                getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
            }

            if (!addedToNetwork) {
                getDeviceHolder().connectToWirelessNetwork();
                addedToNetwork = true;
            }

            if (address == null) {
                switchAddressType(JSGSymbolTypes.MILKYWAY.get());
            }

            if (printStarted > 0) {
                if (level.getGameTime() - printStarted > PRINTING_TIME) {
                    printStarted = 0;
                    inputPages.shrink(1);
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    JSGSoundHelper.playPositionedSound(level, getBlockPos(), JSGPositionedSounds.PRINTER_PRINTING, false);
                    sendSignal("printer_print_done");
                }
            }
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        getDeviceHolder().disconnectFromWirelessNetwork();
    }

    @Override
    public void invalidateCaps() {
        getDeviceHolder().disconnectFromWirelessNetwork();
        super.invalidateCaps();
    }

    public void switchAddressType(SymbolType<?> symbolTypeEnum) {
        address = new StargateAddressDynamic(symbolTypeEnum);
        symbolsToPrint.clear();
        for (var i = 0; i < 8; i++) {
            address.addSymbol(symbolTypeEnum.getFirstValidForAddress());
            symbolsToPrint.add(i + 1);
        }
        symbolsToPrint.add(9);
        address.addSymbol(symbolTypeEnum.getOrigin());
        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
    }

    protected void createOutputAndPrint() {
        if (level == null) return;
        var outputPage = new ItemStack(CoreItems.NOTEBOOK_PAGE_FILLED.get(), 1);
        CompoundTag nbt;
        if (printCustomText == null) {
            nbt = JSGNotebookPageTypes.STARGATE_ADDRESS.get().createCompoundTag(new StargateAddressData(address, symbolsToPrint, origin), PageNotebookItemFilled.getBiomeKeyFromWorld(level, getBlockPos()));
        } else {
            // TODO: Refactor to use custom page type
            nbt = new CompoundTag();
            var customText = new CompoundTag();
            var lines = new ListTag();
            for (var line : printCustomText) {
                var lineTag = new CompoundTag();
                lineTag.putString("component", Component.Serializer.toJson(line));
                lines.add(lineTag);
            }
            customText.put("lines", lines);
            nbt.put("customText", customText);
            printCustomText = null;
            if (titleOverride != null) {
                PageNotebookItemFilled.setName(nbt, titleOverride);
                titleOverride = null;
            }
        }

        outputPage.setTag(nbt);
        printStarted = level.getGameTime();
        outputPages.addLast(outputPage);
        shrinkInk();
        getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
        JSGSoundHelper.playPositionedSound(level, getBlockPos(), JSGPositionedSounds.PRINTER_PRINTING, true);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) return;
        if (!level.isClientSide) {
            targetPoint = new PacketDistributor.TargetPoint(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 512, level.dimension());
            getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
        } else {
            requestState(CoreStateTypes.RENDERER_UPDATE.get());
        }
    }


    // ------------------------------------------------------------------------
    // NBT
    @Override
    @ParametersAreNonnullByDefault
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("inputItem", inputPages.save(new CompoundTag()));
        var size = outputPages.size();
        compound.putInt("outputPagesSize", size);
        for (var i = 0; i < size; i++) {
            compound.put("outputItem" + i, outputPages.get(i).save(new CompoundTag()));
        }
        compound.putLong("printStarted", printStarted);
        compound.putIntArray("symbolsToPrint", symbolsToPrint);
        if (origin != null)
            compound.put("origin", origin.serializeNBT());
        compound.putInt("originPos", originPos);
        if (address != null)
            compound.put("address", address.serializeNBT());
        compound.putInt("inksCount", cartridges.size());
        var i = 0;
        for (var cartridge : cartridges) {
            compound.put("cartridge" + i, cartridge.save(new CompoundTag()));
            i++;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void load(CompoundTag compound) {
        super.load(compound);
        cartridges.clear();
        outputPages.clear();
        inputPages = ItemStack.of(compound.getCompound("inputItem"));
        var size = compound.getInt("outputPagesSize");
        for (int i = 0; i < size; i++) {
            outputPages.addLast(ItemStack.of(compound.getCompound("outputItem" + i)));
        }
        printStarted = compound.getLong("printStarted");
        var array = compound.getIntArray("symbolsToPrint");
        symbolsToPrint.clear();
        for (var s : array) {
            symbolsToPrint.add(s);
        }
        if (compound.contains("origin"))
            origin = PointOfOrigin.fromNBT(compound.getCompound("origin"), null);
        originPos = compound.getInt("originPos");
        if (compound.contains("address"))
            address = new StargateAddressDynamic(compound.getCompound("address"));
        var inksCount = compound.getInt("inksCount");
        for (var i = 0; i < inksCount; i++) {
            cartridges.add(ItemStack.of(compound.getCompound("cartridge" + i)));
        }
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        var computerCaps = getDeviceHolder().getOrCreateDeviceBasedOnCap(capability);
        if (computerCaps.isPresent())
            return computerCaps;
        return super.getCapability(capability, facing);
    }


    // ------------------------------------------------------------------------
    // OC/CC
    @Override
    public String getDeviceType() {
        return "PRINTER";
    }

    public ComputerDeviceHolder computerDeviceHolder;

    public void createDeviceHolder() {
        computerDeviceHolder = new ComputerDeviceHolder(this);
    }

    @Override
    public ComputerDeviceHolder getDeviceHolder() {
        if (computerDeviceHolder == null) createDeviceHolder();
        return computerDeviceHolder;
    }

    // ------------------------------------------------------------------------
    // Networking

    // client
    public PrinterRendererState rendererState = new PrinterRendererState();

    @Override
    public State getState(StateType stateType) {
        if (stateType == CoreStateTypes.RENDERER_UPDATE.get()) {
            rendererState.addressDynamic = address;
            rendererState.output = outputPages;
            rendererState.input = inputPages;
            rendererState.pos = editPos;
            rendererState.printStarted = printStarted;
            rendererState.symbolsToPrint.clear();
            rendererState.symbolsToPrint.addAll(symbolsToPrint);
            rendererState.origin = origin;
            rendererState.cartridges = cartridges;
            return rendererState;
        }
        return null;
    }

    @Override
    public State createState(StateType stateType) {
        if (stateType == CoreStateTypes.RENDERER_UPDATE.get()) {
            return new PrinterRendererState();
        }
        return null;
    }

    @Override
    public void setState(StateType stateType, State state) {
        if (stateType == CoreStateTypes.RENDERER_UPDATE.get()) {
            this.outputPages.clear();
            this.rendererState = (PrinterRendererState) state;
            this.inputPages = rendererState.input;
            this.outputPages.addAll(rendererState.output);
            this.editPos = rendererState.pos;
            this.printStarted = rendererState.printStarted;
            this.address = rendererState.addressDynamic;
            this.symbolsToPrint.clear();
            this.symbolsToPrint.addAll(rendererState.symbolsToPrint);
            this.origin = rendererState.origin;
            this.cartridges = rendererState.cartridges;
            setChanged();
        }
    }

    protected PacketDistributor.TargetPoint targetPoint;

    @Override
    public PacketDistributor.TargetPoint getTargetPoint() {
        if (targetPoint == null && level != null) {
            targetPoint = new PacketDistributor.TargetPoint(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 512, level.dimension());
        }
        return targetPoint;
    }

    // server
    public void buttonClick(int button, ServerPlayer player) {
        if (level == null) return;
        if (level.getGameTime() - lastActivated < 5) return;
        lastActivated = level.getGameTime();

        if (printStarted > 0) {
            player.displayClientMessage(Component.translatable("block.jsg.printer.busy"), true);
            return;
        }

        level.playSound(null, getBlockPos(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.BLOCKS, 1f, 1f);

        if (button == 0) {
            // next pos
            editPos += 1;
            editPos = editPos % 9;

            if (player.isCrouching()) {
                // switch symbolType
                editPos = 0;
                switchAddressType(SymbolType.getNext(address.getSymbolType(), false));
            }
        } else if (button == 1) {
            // up
            if (player.isCrouching()) {
                if (symbolsToPrint.contains(editPos + 1)) {
                    symbolsToPrint.removeIf((i) -> i == (editPos + 1)); // removing using this f**king way, because Java is retarded and is trying to remove by index instead of object
                } else {
                    symbolsToPrint.add(editPos + 1);
                }
            } else {
                if (editPos == 8) {
                    var origins = PointOfOriginsLoader.INSTANCE.getLoadedOrigins(address.getSymbolType().getPointOfOriginType()).orElse(Maps.newHashMap()).values().stream().toList();
                    originPos += 1;
                    originPos = originPos % origins.size();
                    origin = origins.get(originPos);
                } else {
                    var symbol = this.address.get(editPos);
                    var next = symbol.getNext(false);
                    this.address.set(editPos, next);
                }
            }
        } else if (button == 2) {
            // down
            if (player.isCrouching()) {
                if (symbolsToPrint.contains(editPos + 1)) {
                    symbolsToPrint.removeIf((i) -> i == (editPos + 1)); // removing using this f**king way, because Java is retarded and is trying to remove by index instead of object
                } else {
                    symbolsToPrint.add(editPos + 1);
                }
            } else {
                if (editPos == 8) {
                    var origins = PointOfOriginsLoader.INSTANCE.getLoadedOrigins(address.getSymbolType().getPointOfOriginType()).orElse(Maps.newHashMap()).values().stream().toList();
                    originPos -= 1;
                    if (originPos < 0) originPos = origins.size() - 1;
                    origin = origins.get(originPos);
                } else {
                    var symbol = this.address.get(editPos);
                    var prev = symbol.getNext(true);
                    this.address.set(editPos, prev);
                }
            }
        } else if (button == 3) {
            // enter
            if (inputPages.isEmpty()) {
                player.displayClientMessage(Component.translatable("block.jsg.printer.empty_input"), true);
                return;
            }

            if (outputPages.size() >= 10) {
                player.displayClientMessage(Component.translatable("block.jsg.printer.full_output"), true);
                return;
            }

            if (noInk()) {
                var status = getInkStatus();
                for (var c : status.entrySet()) {
                    if (!c.getValue().second()) {
                        var color = "magenta";
                        if (c.getKey() == Color.BLACK) color = "black";
                        else if (c.getKey() == Color.YELLOW) color = "yellow";
                        else if (c.getKey() == Color.CYAN) color = "cyan";
                        player.displayClientMessage(Component.translatable("block.jsg.printer.no_ink." + color), true);
                        return;
                    }
                }
                player.displayClientMessage(Component.translatable("block.jsg.printer.no_ink.general"), true);
                return;
            }
            createOutputAndPrint();
            return;
        }

        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
    }

    private long lastActivated;

    public ItemStack insertPage(ItemStack notebookPage, boolean simulate, boolean shift) {
        if (level == null) return ItemStack.EMPTY;
        if (printStarted > 0) return ItemStack.EMPTY;
        if (level.getGameTime() - lastActivated < 5) return ItemStack.EMPTY;
        lastActivated = level.getGameTime();
        ItemStack stack = ItemStack.EMPTY;
        if (!shift || (notebookPage != null && notebookPage.getItem() == CoreItems.NOTEBOOK_PAGE_EMPTY.get())) {
            if (notebookPage == null || notebookPage.isEmpty()) return ItemStack.EMPTY;
            if (!(notebookPage.getItem() instanceof PageNotebookItemEmpty))
                return ItemStack.EMPTY;
            var size = Math.min(10 - inputPages.getCount(), Math.min(notebookPage.getCount(), (shift ? 64 : 1)));
            if (size <= 0) return ItemStack.EMPTY;
            if (!simulate) {
                if (inputPages.isEmpty()) inputPages = new ItemStack(CoreItems.NOTEBOOK_PAGE_EMPTY.get(), size);
                else inputPages.grow(size);
                setChanged();
                getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
            }
            notebookPage.shrink(size);
        } else {
            if (notebookPage != null && !notebookPage.isEmpty()) {
                return ItemStack.EMPTY;
            }
            stack = inputPages.copy();
            stack.setCount(1);
            if (!simulate) {
                inputPages.shrink(1);
                setChanged();
                getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
            }
        }
        return stack;
    }

    @Nullable
    public ItemStack takePage(boolean simulate) {
        if (level == null) return ItemStack.EMPTY;
        if (printStarted > 0) return ItemStack.EMPTY;
        if (level.getGameTime() - lastActivated < 5) return ItemStack.EMPTY;
        if (level.getGameTime() - printStarted < PRINTING_TIME) return ItemStack.EMPTY;
        lastActivated = level.getGameTime();
        if (outputPages.isEmpty()) return ItemStack.EMPTY;
        var stack = outputPages.getLast();
        if (!simulate) {
            outputPages.removeLast();
            setChanged();
            getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
        }
        return stack;
    }


    // COMPUTERS
    public Object[] buttonClickPC(int button, boolean shift) {
        return buttonClickPC(button, shift, null);
    }

    public Object[] buttonClickPC(int button, boolean shift, @Nullable List<String> customTexts) {
        if (level == null) return new Object[]{false, "level_null", "Level is null!"};
        if (printStarted > 0) return new Object[]{false, "printer_busy", "Printer is busy!"};

        if (button == 0) {
            // next pos
            editPos += 1;
            editPos = editPos % 9;

            if (shift) {
                // switch symbolType
                editPos = 0;
                switchAddressType(SymbolType.getNext(address.getSymbolType(), false));
                setChanged();
                getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                return new Object[]{true, "symbol_type_switched", "Symbol type switched!"};
            }
            setChanged();
            getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
            return new Object[]{true, "edit_pos_moved", "Edit position moved!"};
        } else if (button == 1) {
            // up
            if (shift) {
                if (symbolsToPrint.contains(editPos + 1)) {
                    symbolsToPrint.removeIf((i) -> i == (editPos + 1)); // removing using this f**king way, because Java is retarded and is trying to remove by index instead of object
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "symbol_disabled", "Symbol disabled!"};
                } else {
                    symbolsToPrint.add(editPos + 1);
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "symbol_enabled", "Symbol enabled!"};
                }
            } else {
                if (editPos == 8) {
                    var origins = PointOfOriginsLoader.INSTANCE.getLoadedOrigins(address.getSymbolType().getPointOfOriginType()).orElse(Maps.newHashMap()).values().stream().toList();
                    originPos += 1;
                    originPos = originPos % origins.size();
                    origin = origins.get(originPos);
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "origin_changed_up", "Origin type changed up!"};
                } else {
                    var symbol = this.address.get(editPos);
                    var next = symbol.getNext(false);
                    this.address.set(editPos, next);
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "symbol_changed_up", "Symbol changed up!"};
                }
            }
        } else if (button == 2) {
            // down
            if (shift) {
                if (symbolsToPrint.contains(editPos + 1)) {
                    symbolsToPrint.removeIf((i) -> i == (editPos + 1)); // removing using this f**king way, because Java is retarded and is trying to remove by index instead of object
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "symbol_disabled", "Symbol disabled!"};
                } else {
                    symbolsToPrint.add(editPos + 1);
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "symbol_enabled", "Symbol enabled!"};
                }
            } else {
                if (editPos == 8) {
                    var origins = PointOfOriginsLoader.INSTANCE.getLoadedOrigins(address.getSymbolType().getPointOfOriginType()).orElse(Maps.newHashMap()).values().stream().toList();
                    originPos -= 1;
                    if (originPos < 0) originPos = origins.size() - 1;
                    origin = origins.get(originPos);
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "origin_changed_down", "Origin type changed down!"};
                } else {
                    var symbol = this.address.get(editPos);
                    var prev = symbol.getNext(true);
                    this.address.set(editPos, prev);
                    setChanged();
                    getAndSendState(CoreStateTypes.RENDERER_UPDATE.get());
                    return new Object[]{true, "symbol_changed_down", "Symbol changed down!"};
                }
            }
        } else if (button == 3) {
            // enter
            if (inputPages.isEmpty()) {
                return new Object[]{false, "print_error_no_page", "Input for pages is empty!"};
            }

            if (outputPages.size() >= 10) {
                return new Object[]{false, "print_error_full_output", "Output for pages in full!"};
            }

            if (noInk()) {
                var status = getInkStatus();
                for (var c : status.entrySet()) {
                    if (!c.getValue().second()) {
                        var color = "magenta";
                        if (c.getKey() == Color.BLACK) color = "black";
                        else if (c.getKey() == Color.YELLOW) color = "yellow";
                        else if (c.getKey() == Color.CYAN) color = "cyan";
                        return new Object[]{false, "print_error_no_ink", "Not enough " + color + "!"};
                    }
                }
                return new Object[]{false, "print_error_no_ink", "Some cartridges are missing!"};
            }
            if (customTexts != null) {
                this.printCustomText = new LinkedList<>();
                int i = 0;
                for (var lineStr : customTexts) {
                    i++;
                    if (i == 1) {
                        titleOverride = lineStr;
                        continue;
                    }
                    if (!lineStr.startsWith("{") && !lineStr.startsWith("[") && !lineStr.startsWith("\"")) {
                        lineStr = ("\"" + lineStr + "\"");
                    }
                    var line = lineStr.startsWith("\"") ? Component.literal(lineStr.substring(1, lineStr.length() - 1)) : Component.Serializer.fromJson(lineStr);
                    printCustomText.addLast(line);
                }
                if (customTexts.size() == 1) {
                    printCustomText = null;
                }
            }

            createOutputAndPrint();
            return new Object[]{true, "print_started", "Printing..."};
        }
        return new Object[]{true, "printer_error_input_malformed", "Wrong input!"};
    }
}
