package dev.tauri.jsg.client.screen.gui.admincontroller.element;

import com.google.common.collect.ImmutableList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ACTabNavigationBar extends AbstractContainerEventHandler implements Renderable, GuiEventListener, NarratableEntry {
    private static final Component USAGE_NARRATION = Component.translatable("narration.tab_navigation.usage");
    private final GridLayout layout;
    private int width;
    private final int x;
    private final int y;
    public int selectedTab = 0;
    private final TabManager tabManager;
    private final ImmutableList<Tab> tabs;
    private final ImmutableList<ACTabButton> tabButtons;

    ACTabNavigationBar(int x, int y, int pWidth, TabManager pTabManager, Iterable<Tab> pTabs) {
        this.x = x;
        this.y = y;
        this.width = pWidth;
        this.tabManager = pTabManager;
        this.tabs = ImmutableList.copyOf(pTabs);
        this.layout = new GridLayout(x, y);
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        ImmutableList.Builder<ACTabButton> builder = ImmutableList.builder();
        int i = 0;

        for (Tab tab : pTabs) {
            builder.add(this.layout.addChild(new ACTabButton(pTabManager, tab, 0), 0, i++));
        }

        this.tabButtons = builder.build();
    }

    public static ACTabNavigationBar.Builder builder(TabManager pTabManager, int x, int y, int pWidth) {
        return new ACTabNavigationBar.Builder(pTabManager, x, y, pWidth);
    }

    public void setWidth(int pWidth) {
        this.width = pWidth;
    }

    /**
     * Sets the focus state of the GUI element.
     *
     * @param pFocused {@code true} to apply focus, {@code false} to remove focus
     */
    public void setFocused(boolean pFocused) {
        super.setFocused(pFocused);
        if (this.getFocused() != null) {
            this.getFocused().setFocused(pFocused);
        }

    }

    /**
     * Sets the focus state of the GUI element.
     *
     * @param pFocused the focused GUI element.
     */
    public void setFocused(@Nullable GuiEventListener pFocused) {
        super.setFocused(pFocused);
        if (pFocused instanceof ACTabButton tabbutton) {
            this.tabManager.setCurrentTab(tabbutton.tab(), true);
        }
    }

    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent pEvent) {
        if (!this.isFocused()) {
            var tabbutton = this.currentTabButton();
            if (tabbutton != null) {
                return ComponentPath.path(this, ComponentPath.leaf(tabbutton));
            }
        }

        return pEvent instanceof FocusNavigationEvent.TabNavigation ? null : super.nextFocusPath(pEvent);
    }

    /**
     * {@return a List containing all GUI element children of this GUI element}
     */
    public List<? extends GuiEventListener> children() {
        return this.tabButtons;
    }

    /**
     * {@return the narration priority}
     */
    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.tabButtons.stream().map(AbstractWidget::narrationPriority).max(Comparator.naturalOrder()).orElse(NarratableEntry.NarrationPriority.NONE);
    }

    /**
     * Updates the narration output with the current narration information.
     *
     * @param pNarrationElementOutput the output to update with narration information.
     */
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        var optional = this.tabButtons.stream().filter(AbstractWidget::isHovered).findFirst().or(() -> Optional.ofNullable(this.currentTabButton()));
        optional.ifPresent((p_274663_) -> {
            this.narrateListElementPosition(pNarrationElementOutput.nest(), p_274663_);
            p_274663_.updateNarration(pNarrationElementOutput);
        });
        if (this.isFocused()) {
            pNarrationElementOutput.add(NarratedElementType.USAGE, USAGE_NARRATION);
        }

    }

    /**
     * Narrates the position of a list element (tab button).
     *
     * @param pNarrationElementOutput the narration output to update.
     * @param pTabButton              the tab button whose position is being narrated.
     */
    protected void narrateListElementPosition(NarrationElementOutput pNarrationElementOutput, ACTabButton pTabButton) {
        if (this.tabs.size() > 1) {
            int i = this.tabButtons.indexOf(pTabButton);
            if (i != -1) {
                pNarrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.tab", i + 1, this.tabs.size()));
            }
        }

    }

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param pGuiGraphics the GuiGraphics object used for rendering.
     * @param pMouseX      the x-coordinate of the mouse cursor.
     * @param pMouseY      the y-coordinate of the mouse cursor.
     * @param pPartialTick the partial tick time.
     */
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (var tabbutton : this.tabButtons) {
            tabbutton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    /**
     * {@return the {@link ScreenRectangle } occupied by the GUI element}
     */
    public ScreenRectangle getRectangle() {
        return this.layout.getRectangle();
    }

    /**
     * Arranges the elements within the tabbed layout.
     */
    public void arrangeElements() {
        int w = this.width;
        int btnW = w / this.tabs.size();

        for (var btn : this.tabButtons) {
            btn.setWidth(btnW);
        }

        this.layout.arrangeElements();
        this.layout.setX(this.x);
        this.layout.setY(this.y);
    }

    /**
     * Selects the tab at the specified index.
     *
     * @param pIndex          the index of the tab to select.
     * @param pPlayClickSound whether to play a click sound when selecting the tab.
     */
    public void selectTab(int pIndex, boolean pPlayClickSound) {
        selectedTab = pIndex;
        if (this.isFocused()) {
            this.setFocused(this.tabButtons.get(pIndex));
        } else {
            this.tabManager.setCurrentTab(this.tabs.get(pIndex), pPlayClickSound);
        }
    }

    /**
     * Handles key pressed events.
     * <p>
     *
     * @param pKeycode the keycode of the pressed key.
     * @return {@code true} if the key press was handled, {@code false} otherwise.
     */
    public boolean keyPressed(int pKeycode) {
        if (Screen.hasControlDown()) {
            int i = this.getNextTabIndex(pKeycode);
            if (i != -1) {
                this.selectTab(Mth.clamp(i, 0, this.tabs.size() - 1), true);
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the index of the next tab based on the given key code.
     * <p>
     *
     * @param pKeycode the keycode of the pressed key.
     * @return the index of the next tab, or -1 if no valid tab index is found.
     */
    private int getNextTabIndex(int pKeycode) {
        if (pKeycode >= 49 && pKeycode <= 57) {
            return pKeycode - 49;
        } else {
            if (pKeycode == 258) {
                int i = this.currentTabIndex();
                if (i != -1) {
                    int j = Screen.hasShiftDown() ? i - 1 : i + 1;
                    return Math.floorMod(j, this.tabs.size());
                }
            }

            return -1;
        }
    }

    /**
     * Returns the index of the current tab.
     * <p>
     *
     * @return the index of the current tab, or -1 if no current tab is set.
     */
    private int currentTabIndex() {
        Tab tab = this.tabManager.getCurrentTab();
        return this.tabs.indexOf(tab);
    }

    private @Nullable ACTabButton currentTabButton() {
        int i = this.currentTabIndex();
        return i != -1 ? this.tabButtons.get(i) : null;
    }

    /**
     * Builder class for creating a ACTabNavigationBar instance.
     */
    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final int x;
        private final int y;
        private final int width;
        private final TabManager tabManager;
        private final List<Tab> tabs = new ArrayList<>();

        Builder(TabManager pTabManager, int x, int y, int pWidth) {
            this.tabManager = pTabManager;
            this.x = x;
            this.y = y;
            this.width = pWidth;
        }

        /**
         * Adds multiple tabs to the ACTabNavigationBar.
         * <p>
         *
         * @param pTabs the tabs to add.
         * @return the {@link ACTabNavigationBar.Builder} instance.
         */
        public ACTabNavigationBar.Builder addTabs(Tab... pTabs) {
            Collections.addAll(this.tabs, pTabs);
            return this;
        }

        /**
         * Builds and returns a new ACTabNavigationBar instance.
         * <p>
         *
         * @return a new ACTabNavigationBar instance.
         */
        public ACTabNavigationBar build() {
            return new ACTabNavigationBar(this.x, this.y, this.width, this.tabManager, this.tabs);
        }
    }
}
