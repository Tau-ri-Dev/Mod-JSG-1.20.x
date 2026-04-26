package dev.tauri.jsg.client.screen.gui.admincontroller.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.client.screen.gui.admincontroller.AdminControllerGUI;
import dev.tauri.jsg.common.item.admincontroller.AdminControllerAction;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACEntryActionPacketToServer;
import dev.tauri.jsg.common.packet.packets.admincontroller.ACRenameGatePacketToServer;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.widget.*;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NetworkTab extends AdminControllerTab {
    @SuppressWarnings("all")
    protected Optional<ResourceKey<Level>> dimension;
    public Map<StargatePos, Map<SymbolType<?>, StargateAddress>> gates;

    protected NetworkMap networkMap;
    protected SubScreenWithWidgets leftSide;
    protected AddressList addressList;

    protected CallbackEditBox searchBar;
    protected String searchQuery = "";

    public NetworkTab(AdminControllerGUI baseGUI) {
        super(baseGUI, Component.translatable("gui.admincontroller.tab.network.name"));
        dimension = Optional.empty();
        gates = new HashMap<>();
    }

    @Override
    public void doLayout(ScreenRectangle screenRectangle) {
        super.doLayout(screenRectangle);
        reloadNetwork();
    }

    @Override
    public void tick() {
        super.tick();
        if (networkMap != null)
            networkMap.tick();
        if (leftSide != null)
            leftSide.tick();
    }

    public void reloadNetwork() {
        var sgn = NetworkTab.this.baseGUI.sgNetwork;
        var gates = dimension.map(sgn::getStargatesByDimension).orElseGet(sgn::getAll);
        this.gates.clear();
        this.gates.putAll(gates);
        if (networkMap != null)
            networkMap.reloadNetworkMap(this.gates);
        if (addressList != null)
            addressList.reloadGatesList(this.gates);
    }

    public static class DimensionWrapper {
        @SuppressWarnings("all")
        public final Optional<ResourceKey<Level>> dimension;

        public DimensionWrapper(ResourceKey<Level> dimension) {
            this.dimension = Optional.of(dimension);
        }

        public DimensionWrapper() {
            this.dimension = Optional.empty();
        }

        public Component getTitle() {
            return dimension.map(levelResourceKey -> Component.literal(levelResourceKey.location().toString())).orElseGet(() -> Component.translatable("gui.admincontroller.tab.network.dimension_select.all.name"));
        }
    }

    @Override
    public void createLayout() {
        if (this.gates == null)
            this.gates = new HashMap<>();

        var leftW = baseGUI.xInnerSize / 4 - 1;
        addressList = new AddressList(0, 21, leftW, baseGUI.yInnerSize - 22 - 18, gates);
        searchBar = new BiCallbackEditBox(this.baseGUI.getMinecraft().font, 0, baseGUI.yInnerSize - 16, leftW, 16, Component.empty(), this::updateSearch, (val) -> {
        });
        searchBar.setValue("");
        var dims = baseGUI.sgNetwork.getAll().keySet().stream().map(k -> k.dimension).distinct().toList();
        var dimsList = dims.stream().map(DimensionWrapper::new).toList();
        DimensionWrapper allDims = new DimensionWrapper();
        var dimBtn = new CycleButton.Builder<>(DimensionWrapper::getTitle)
                .withValues(Stream.concat(dimsList.stream(), Stream.of(allDims)).toList())
                .withInitialValue(allDims)
                .displayOnlyValue()
                .create(0, 0, leftW, 20, Component.empty(), (btn, val) -> {
                    dimension = val.dimension;
                    reloadNetwork();
                });
        leftSide = new SubScreenWithWidgets(0, 0, leftW, baseGUI.yInnerSize, List.of(
                dimBtn,
                addressList,
                searchBar
        ));
        networkMap = new NetworkMap(0, 0, baseGUI.xInnerSize / 4 * 3 - 1, baseGUI.yInnerSize, this.gates);
        var helper = this.layout.columnSpacing(1)
                .rowSpacing(1)
                .createRowHelper(4);
        helper.addChild(leftSide, 1);
        helper.addChild(networkMap, 3);
    }

    public void updateSearch(String query) {
        searchQuery = query.toLowerCase();
        if (networkMap != null)
            networkMap.reloadNetworkMap(this.gates);
        if (addressList != null)
            addressList.reloadGatesList(this.gates);
    }

    public boolean stargateNotMatchSearchQuery(StargatePos stargatePos) {
        if (searchQuery == null || searchQuery.isEmpty()) return false;
        var name = stargatePos.getName();
        var pos = stargatePos.gatePos;
        var type = stargatePos.getStargateType();
        if (searchQuery.startsWith("name=")) {
            return !name.toLowerCase().contains(searchQuery.replaceFirst("name=", ""));
        }
        if (searchQuery.startsWith("pos=")) {
            return !pos.toShortString().startsWith(searchQuery.replaceFirst("pos=", ""));
        }
        if (searchQuery.startsWith("type=")) {
            return !type.getId().toString().toLowerCase().contains(searchQuery.replaceFirst("type=", "")) && !type.name.toLowerCase().contains(searchQuery.replaceFirst("type=", ""));
        }
        if (name.toLowerCase().contains(searchQuery)) return false;
        if (stargatePos.dimension.location().toString().toLowerCase().contains(searchQuery)) return false;
        if (pos.toShortString().toLowerCase().startsWith(searchQuery)) return false;
        if (type.getId().toString().toLowerCase().contains(searchQuery)) return false;
        return !type.name.toLowerCase().contains(searchQuery);
    }

    public class AddressList extends SubScreen {
        public static final int ENTRY_HEIGHT = 16;
        public static final int ENTRY_PADDING = 2;
        public static final ResourceLocation DIAL_DROPDOWN = JSGMapping.rl(JSG.MOD_ID, "textures/gui/admin_controller/dial_screen.png");

        protected final HashMap<StargatePos, Pair<? extends EditBox, ? extends IconButtonWithScreen>> entries = new HashMap<>();
        protected final LinkedList<Map.Entry<StargatePos, Pair<? extends EditBox, ? extends IconButtonWithScreen>>> sortedEntries = new LinkedList<>();

        public AddressList(int pX, int pY, int pWidth, int pHeight, Map<StargatePos, Map<SymbolType<?>, StargateAddress>> gates) {
            super(pX, pY, pWidth, pHeight, Component.translatable("gui.admincontroller.tab.network.address_list.name"), true);
            reloadGatesList(gates);
        }

        public void performAction(AdminControllerAction action, Map.Entry<StargatePos, Map<SymbolType<?>, StargateAddress>> gateEntry) {
            JSGPacketHandler.sendToServer(new ACEntryActionPacketToServer(NetworkTab.this.baseGUI.gatePos, gateEntry.getKey(), action, gateEntry.getValue()));
        }

        public void reloadGatesList(Map<StargatePos, Map<SymbolType<?>, StargateAddress>> gates) {
            clearWidgets();
            entries.clear();

            var btnWH = ENTRY_HEIGHT;
            var nameFieldWidth = (getWidth() - 9) - btnWH - 2 - 1;
            var nameFieldX = getX() + 1;
            var btnX = nameFieldX + nameFieldWidth + 2;

            for (var gate : gates.entrySet()) {
                var sgPos = gate.getKey();
                var posString = sgPos.gatePos.toShortString();
                if (stargateNotMatchSearchQuery(sgPos)) continue;
                List<Component> gateTooltip = List.of(
                        Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.name").withStyle(ChatFormatting.BOLD)).append(" ").append(sgPos.getName()),
                        Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.pos").withStyle(ChatFormatting.BOLD)).append(" ").append(posString),
                        Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.type").withStyle(ChatFormatting.BOLD)).append(" ").append(sgPos.getStargateType().name).append(" ").append(Component.literal("(" + sgPos.getStargateType().getId().toString() + ")").withStyle(ChatFormatting.DARK_GRAY)),
                        Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.dimension").withStyle(ChatFormatting.BOLD)).append(" ").append(sgPos.dimension.location().toString())
                );
                var nameBox = new BiCallbackEditBoxWithTooltip(NetworkTab.this.baseGUI.getMinecraft().font, nameFieldX, 0, nameFieldWidth, ENTRY_HEIGHT, Component.empty(), (val) -> {
                }, (val) -> {
                    if (val.equalsIgnoreCase(sgPos.gatePos.toShortString()))
                        val = null;
                    JSGPacketHandler.sendToServer(new ACRenameGatePacketToServer(sgPos, val));
                    NetworkTab.this.baseGUI.sgNetwork.renameStargate(sgPos, val);
                    reloadNetwork();
                }, gateTooltip) {
                    @Override
                    public void setFocused(boolean pFocused) {
                        super.setFocused(pFocused);
                        if (isFocused()) {
                            networkMap.select(sgPos);
                        }
                    }
                };
                nameBox.setValue(sgPos.getName().isEmpty() ? sgPos.gatePos.toShortString() : sgPos.getName());
                var buttonSubScreen = new DialOptionsScreen(gate, 1, -(5 * 18 + 2) / 2, 104, 5 * 18 + 2);
                var pair = Pair.of(
                        nameBox,
                        new IconButtonWithScreen(btnX, 0, btnWH, btnWH, Component.empty(), (btn) -> {
                        }, Supplier::get,
                                JSGMapping.rl(JSG.MOD_ID, "textures/gui/admin_controller/controller_mode.png"), 160, 0, 256, 32,
                                DIAL_DROPDOWN, 16, 0, 0, buttonSubScreen) {
                            @Override
                            public boolean updateHover(int mouseX, int mouseY) {
                                return isMouseInside(mouseX, mouseY) && super.updateHover(mouseX, mouseY);
                            }
                        }
                );
                entries.put(sgPos, pair);
            }
            sortEntries();

            sortedEntries.forEach(p -> {
                addRenderableWidget(p.getValue().first());
                addRenderableWidget(p.getValue().second());
            });

            setScrollAmount(0);
        }

        public void sortEntries() {
            sortedEntries.clear();
            sortedEntries.addAll(entries.entrySet().stream().sorted((e1, e2) -> {
                if (!e1.getKey().getName().isEmpty() && !e2.getKey().getName().isEmpty()) {
                    return e1.getKey().getName().compareTo(e2.getKey().getName());
                } else if (e1.getKey().getName().isEmpty() && !e2.getKey().getName().isEmpty()) {
                    return 1;
                } else if (!e1.getKey().getName().isEmpty() && e2.getKey().getName().isEmpty()) {
                    return -1;
                }
                var dist1 = baseGUI.player.position().distanceTo(e1.getKey().gatePos.getCenter());
                var dist2 = baseGUI.player.position().distanceTo(e2.getKey().gatePos.getCenter());
                return dist1 > dist2 ? 1 : -1;
            }).toList());
        }

        @Override
        public boolean isMouseInside(double pMouseX, double pMouseY) {
            return super.isMouseInside(pMouseX, pMouseY) || entries.values().stream().anyMatch(p -> p.second().isHovered());
        }

        @Override
        protected boolean isValidClickButton(int pButton) {
            return pButton == 0 || pButton == 1 || pButton == 2;
        }

        @Override
        public int getInnerHeight() {
            return sortedEntries.size() * ((ENTRY_PADDING * 2) + ENTRY_HEIGHT);
        }

        @Override
        public void setScrollAmount(double pScrollAmount) {
            super.setScrollAmount(pScrollAmount);
            for (var i = 0; i < sortedEntries.size(); i++) {
                var e = sortedEntries.get(i).getValue();
                var y = ((i + 1) * ENTRY_PADDING) + (i * (ENTRY_HEIGHT + ENTRY_PADDING)) - (int) scrollAmount;
                e.first().setY(y + getY());
                e.second().setY(y + getY());
            }
        }

        @Override
        public void tick() {
            super.tick();
            sortedEntries.forEach(p -> p.getValue().first().tick());
        }

        public class DialOptionsScreen extends SubScreen {
            public static final ResourceLocation ICONS = JSGMapping.rl(JSG.MOD_ID, "textures/gui/admin_controller/controller_mode.png");

            protected final Map.Entry<StargatePos, Map<SymbolType<?>, StargateAddress>> gateEntry;
            protected final List<Button> buttons;

            public DialOptionsScreen(Map.Entry<StargatePos, Map<SymbolType<?>, StargateAddress>> gateEntry, int pX, int pY, int pWidth, int pHeight) {
                super(pX, pY, pWidth, pHeight, Component.empty(), true);
                this.gateEntry = gateEntry;
                var isLinkedTogate = NetworkTab.this.baseGUI.gatePos != null;
                var isThisGate = (gateEntry.getKey().gatePos.equals(NetworkTab.this.baseGUI.gatePos) && gateEntry.getKey().dimension.equals(NetworkTab.this.baseGUI.level.dimension()));
                buttons = List.of(
                        ButtonWithIcon.builder(Component.translatable("gui.admincontroller.tab.network.address_list.slow_dail.name"), (btn) -> AddressList.this.performAction(AdminControllerAction.SLOW_DIAL, gateEntry))
                                .setIcon(ICONS, 0, 0, 256, 32)
                                .setActive(isLinkedTogate && !isThisGate)
                                .bounds(0, 0, 100, 16).build(),
                        ButtonWithIcon.builder(Component.translatable("gui.admincontroller.tab.network.address_list.fast_dial.name"), (btn) -> AddressList.this.performAction(AdminControllerAction.FAST_DIAL, gateEntry))
                                .setIcon(ICONS, 32, 0, 256, 32)
                                .setActive(isLinkedTogate && !isThisGate)
                                .bounds(0, 0, 100, 16).build(),
                        ButtonWithIcon.builder(Component.translatable("gui.admincontroller.tab.network.address_list.nox_dialing.name"), (btn) -> AddressList.this.performAction(AdminControllerAction.NOX_DIAL, gateEntry))
                                .setIcon(ICONS, 32 * 2, 0, 256, 32)
                                .setActive(isLinkedTogate && !isThisGate)
                                .bounds(0, 0, 100, 16).build(),
                        ButtonWithIcon.builder(Component.translatable("gui.admincontroller.tab.network.address_list.address.name"), (btn) -> AddressList.this.performAction(AdminControllerAction.ADDRESS_GIVE, gateEntry))
                                .setIcon(ICONS, 32 * 3, 0, 256, 32)
                                .bounds(0, 0, 100, 16).build(),
                        ButtonWithIcon.builder(Component.translatable("gui.admincontroller.tab.network.address_list.teleport.name"), (btn) -> AddressList.this.performAction(AdminControllerAction.TELEPORT, gateEntry))
                                .setIcon(ICONS, 32 * 4, 0, 256, 32)
                                .bounds(0, 0, 100, 16).build()
                );
            }


            @Override
            public void visitWidgetsInternal(Consumer<AbstractWidget> pConsumer) {
                super.visitWidgetsInternal(pConsumer);
                for (var i = 0; i < buttons.size(); i++) {
                    var btn = buttons.get(i);
                    btn.setX(getX() + 1);
                    btn.setY(getY() + 1 + 18 * i);
                    pConsumer.accept(btn);
                }
            }
        }
    }

    public class NetworkMap extends SubScreen {

        public class StargateWidget extends AbstractWidget implements ForegroundRenderable {
            public Map.Entry<StargatePos, Map<SymbolType<?>, StargateAddress>> entry;

            public StargateWidget(Map.Entry<StargatePos, Map<SymbolType<?>, StargateAddress>> entry) {
                super(0, 0, 20, 20, Component.empty());
                this.entry = entry;
            }

            public void updatePosition(double scale) {
                setX((int) (entry.getKey().gatePos.getX() * scale));
                setY((int) (entry.getKey().gatePos.getZ() * scale));
                var w = (int) (16 * MathHelper.clamp(scale / 2f, 0.5f, 1f));
                setWidth(w);
                setHeight(w);
            }

            public boolean isNameFieldFocused() {
                return Optional.ofNullable(addressList.entries.get(entry.getKey())).map(p -> p.first().isFocused()).orElse(false);
            }

            @Override
            protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                var sgPos = entry.getKey();
                this.isHovered =
                        ((pMouseX - currentX - NetworkMap.this.getX()) >= this.getX() &&
                                (pMouseY - currentY - NetworkMap.this.getY()) >= this.getY() &&
                                (pMouseX - currentX - NetworkMap.this.getX()) < this.getX() + this.getWidth() &&
                                (pMouseY - currentY - NetworkMap.this.getY()) < this.getY() + this.getHeight()) && NetworkMap.this.isMouseInside(pMouseX, pMouseY);
                var wh = getWidth();
                var id = sgPos.getStargateType().getId();

                var p = NetworkTab.this.baseGUI.player;
                if (dimension.isEmpty() || p.level().dimension() == dimension.get()) {
                    GuiHelper.line(graphics, (int) (p.position().x() * scale), (int) (p.position().z() * scale), getX() + wh / 2f, getY() + wh / 2f, 1, 0x4400A0A0);
                }

                var x = (Math.max(-currentX, Math.min(-currentX + NetworkMap.this.getWidth() - getWidth(), getX())));
                var y = (Math.max(-currentY, Math.min(-currentY + NetworkMap.this.getHeight() - getHeight(), getY())));

                float outsideFactor = (x != getX() || y != getY()) ? (float) (0.8f - (Math.min(1f, Math.sqrt((float) (x - getX()) * (float) (x - getX()) + (float) (y - getY()) * (float) (y - getY())) / 10000f) * 0.7f)) : 1f;

                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1, 1, 1, outsideFactor);
                if (outsideFactor >= 1f && !entry.getKey().getName().isEmpty()) {
                    graphics.drawCenteredString(NetworkTab.this.baseGUI.getMinecraft().font, entry.getKey().getName(), x + (wh / 2), y - 10, 0xffffff);
                }
                graphics.blit(JSGMapping.rl(id.getNamespace(), "textures/block/stargate/stargate_" + id.getPath() + "_base_front.png"),
                        x, y, 0, 0.0F, 0.0F, wh, wh, wh, wh);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
            }

            @Override
            public boolean renderTooltips(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
                if (otherRendered) return false;
                if (isHovered()) {
                    var sgPos = entry.getKey();
                    var posString = sgPos.gatePos.toShortString();
                    graphics.renderComponentTooltip(NetworkTab.this.baseGUI.getMinecraft().font, List.of(
                            Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.name").withStyle(ChatFormatting.BOLD)).append(" ").append(sgPos.getName()),
                            Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.pos").withStyle(ChatFormatting.BOLD)).append(" ").append(posString),
                            Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.type").withStyle(ChatFormatting.BOLD)).append(" ").append(sgPos.getStargateType().name),
                            Component.empty().append(Component.translatable("gui.admincontroller.tab.network.map.stargate.dimension").withStyle(ChatFormatting.BOLD)).append(" ").append(sgPos.dimension.location().toString())
                    ), pMouseX, pMouseY);
                    return true;
                }
                return false;
            }

            @Override
            public boolean renderForeground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
                if (otherRendered) return false;
                if ((isHoveredOrFocused() && NetworkMap.this.isMouseInside(pMouseX, pMouseY)) || isNameFieldFocused()) {
                    graphics.renderOutline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
                    return true;
                }
                return false;
            }

            @Override
            protected boolean clicked(double pMouseX, double pMouseY) {
                return isHovered();
            }

            @Override
            public void onClick(double pMouseX, double pMouseY) {
                super.onClick(pMouseX, pMouseY);
                select();
            }

            public void select() {
                addressList.sortedEntries.forEach((entry) -> {
                    var key = entry.getKey();
                    var entryPair = entry.getValue();
                    if (key == this.entry.getKey()) {
                        entryPair.first().setFocused(true);
                        addressList.setScrollAmount(addressList.getScrollAmount() + entryPair.first().getY() - addressList.getY() - ((double) addressList.getHeight() / 2f));
                    } else
                        entryPair.first().setFocused(false);
                });
            }
        }

        public int currentX;
        public int currentY;
        public double scale = 1;

        public int minXY = 0;
        public int maxXY = 0;

        public final Map<StargatePos, StargateWidget> gateWidgets = new HashMap<>();

        public NetworkMap(int pX, int pY, int pWidth, int pHeight, Map<StargatePos, Map<SymbolType<?>, StargateAddress>> gates) {
            super(pX, pY, pWidth, pHeight, Component.translatable("gui.admincontroller.tab.network.map.name"), true);
            reloadNetworkMap(gates);
        }

        public void reloadNetworkMap(Map<StargatePos, Map<SymbolType<?>, StargateAddress>> gates) {
            clearWidgets();
            gateWidgets.clear();
            minXY = 0;
            maxXY = 0;
            for (var gate : gates.entrySet()) {
                if (NetworkTab.this.stargateNotMatchSearchQuery(gate.getKey())) continue;

                var pos = gate.getKey().gatePos;
                if (pos.getX() < minXY) minXY = pos.getX();
                else if (pos.getX() > maxXY) maxXY = pos.getX();
                if (pos.getZ() < minXY) minXY = pos.getZ();
                else if (pos.getZ() > maxXY) maxXY = pos.getZ();

                var w = new StargateWidget(gate);
                w.updatePosition(scale);
                gateWidgets.put(gate.getKey(), w);
                addRenderableWidget(w);
            }
            minXY -= 50;
            maxXY += 50;
            currentX = minXY + (maxXY - minXY) / 2;
            currentY = minXY + (maxXY - minXY) / 2;

            rescale(0);
            moveCenterTo(0, 0, true);
            clampPosition();
        }

        protected void clampPosition() {
            if (currentX + (int) ((double) maxXY * scale) < getWidth())
                currentX = (getWidth() - (int) ((double) maxXY * scale));
            if (currentY + (int) ((double) maxXY * scale) < getHeight())
                currentY = (getHeight() - (int) ((double) maxXY * scale));
            if (currentX + (int) ((double) minXY * scale) > 0) currentX = (int) -((double) minXY * scale);
            if (currentY + (int) ((double) minXY * scale) > 0) currentY = (int) -((double) minXY * scale);
        }

        @Override
        public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            graphics.pose().translate((currentX) + getX(), (currentY) + getY(), 0);
            graphics.fillGradient((int) (minXY * scale), (int) (minXY * scale), (int) (maxXY * scale), (int) (maxXY * scale), 0xc52D2D2D, 0xc51D1D1D);
            renderRaster(graphics, pMouseX, pMouseY, pPartialTick);

            var p = NetworkTab.this.baseGUI.player;
            if (dimension.isEmpty() || p.level().dimension() == dimension.get()) {
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 5);
                var xOriginal = (int) (p.position().x() * scale) - 4;
                var yOriginal = (int) (p.position().z() * scale) - 4;
                var x = (Math.max(-currentX, Math.min(-currentX + NetworkMap.this.getWidth() - 14, xOriginal)));
                var y = (Math.max(-currentY, Math.min(-currentY + NetworkMap.this.getHeight() - 14, yOriginal)));

                float outsideFactor = (x != xOriginal || y != yOriginal) ? (float) (0.8f - (Math.min(1f, Math.sqrt((float) (x - xOriginal) * (float) (x - xOriginal) + (float) (y - yOriginal) * (float) (y - yOriginal)) / 10000f) * 0.7f)) : 1f;
                RenderSystem.setShaderColor(1, 1, 1, outsideFactor);
                graphics.blit(JSGMapping.rl("textures/gui/widgets.png"), x, y, 7, 7, 192, 0, 14, 14, 256, 256);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                if (outsideFactor >= 1f)
                    graphics.drawCenteredString(NetworkTab.this.baseGUI.getMinecraft().font, Component.translatable("gui.admincontroller.tab.network.map.you_are_here"), (int) (p.position().x() * scale), (int) (p.position().z() * scale) - 3 + 2 + 7, 0xffffff);
                graphics.pose().popPose();
            }
        }

        public void moveCenterTo(double x, double y, boolean scaleToFit) {
            currentX = (int) ((-x * scale) + (getWidth() / 2f));
            currentY = (int) ((-y * scale) + (getHeight() / 2f));
            if (scaleToFit) {
                setScale(Math.max((double) getWidth() / ((double) (maxXY - minXY)), (double) getHeight() / ((double) (maxXY - minXY))));
            }
            clampPosition();
        }

        public void select(StargatePos gate) {
            var widget = networkMap.gateWidgets.get(gate);
            if (widget == null) {
                moveCenterTo(0, 0, true);
                return;
            }
            networkMap.setScale(4);
            networkMap.moveCenterTo(gate.gatePos.getX(), gate.gatePos.getZ(), false);
        }

        protected void renderRaster(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            for (var i = 0; i < 2; i++) {
                for (var x = 0; x <= (i == 0 ? maxXY : -minXY); x += 16) {
                    if (x != 0 && scale < 0.25f) continue;
                    var mx = (int) (x * scale);
                    if (i == 1)
                        mx = (int) (-x * scale);
                    graphics.fill(mx - 1, (int) (minXY * scale), mx + 1, (int) (maxXY * scale), x == 0 ? 0x445A97AE : 0x44ABABAB);
                }
                for (var y = 0; y <= (i == 0 ? maxXY : -minXY); y += 16) {
                    if (y != 0 && scale < 0.25f) continue;
                    var my = (int) (y * scale);
                    if (i == 1)
                        my = (int) (-y * scale);
                    graphics.fill((int) (minXY * scale), my - 1, (int) (maxXY * scale), my + 1, y == 0 ? 0x445A97AE : 0x44ABABAB);
                }
            }
        }

        @Override
        public boolean isMouseInside(double pMouseX, double pMouseY) {
            return super.isMouseInside(pMouseX, pMouseY) && addressList.entries.values().stream().noneMatch(p -> p.second().isHovered());
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
                return true;
            }
            if (isMouseInside(pMouseX, pMouseY) && this.active && this.visible) {
                if (this.isValidClickButton(pButton)) {
                    boolean flag = this.clicked(pMouseX, pMouseY);
                    if (flag) {
                        this.onClick(pMouseX, pMouseY);
                        return true;
                    }
                }

            }
            return false;
        }

        @Override
        public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
            currentX = (int) (currentX + pDragX);
            currentY = (int) (currentY + pDragY);
            clampPosition();
            return true;
        }

        @Override
        public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
            rescale(pDelta);
            clampPosition();
            return true;
        }

        public void rescale(double delta) {
            var newScale = scale;
            if (delta > 0)
                newScale *= Math.abs(delta / 4) + 1;
            else
                newScale /= Math.abs(delta / 4) + 1;
            setScale(newScale);
        }

        public void setScale(double newScale) {
            if ((maxXY - minXY) <= 0) return;
            if ((maxXY - minXY) * newScale < getWidth())
                newScale = (double) getWidth() / (double) (maxXY - minXY);
            if ((maxXY - minXY) * newScale < getHeight())
                newScale = (double) getHeight() / (double) (maxXY - minXY);
            scale = newScale;
            this.gateWidgets.values().forEach((g) -> g.updatePosition(scale));
        }
    }
}
