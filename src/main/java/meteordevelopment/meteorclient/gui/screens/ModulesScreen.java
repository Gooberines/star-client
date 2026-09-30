/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.WindowConfig;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static meteordevelopment.meteorclient.utils.Utils.getWindowHeight;
import static meteordevelopment.meteorclient.utils.Utils.getWindowWidth;
import static org.lwjgl.glfw.GLFW.*;

public class ModulesScreen extends TabScreen {
    private WCategoryController controller;
    private WWindow searchWindow;
    private WTextBox searchTextBox;

    public ModulesScreen(GuiTheme theme) {
        super(theme, Tabs.get().getFirst());
    }

    @Override
    public void initWidgets() {
        controller = add(new WCategoryController()).widget();

        // Help
        WVerticalList help = add(theme.verticalList()).pad(4).bottom().widget();
        help.add(theme.label("Left click - Toggle module"));
        help.add(theme.label("Right click - Open module settings"));
    }

    @Override
    protected void init() {
        super.init();
        controller.refresh();
    }

    // Category

    protected WWindow createCategory(WContainer c, Category category, List<Module> moduleList) {
        WWindow w = theme.window(category.name);
        w.id = category.name;
        w.padding = 0;
        w.spacing = 0;

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.item(category.icon)).pad(2);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = true;
        w.view.spacing = theme.scale(3); // gaps between grid rows

        // Lay modules out in a fixed 5-wide grid, wrapping down as many rows as needed.
        int cols = 5;
        double cardW = theme.scale(96);
        WHorizontalList row = null;
        int i = 0;
        for (Module module : moduleList) {
            if (i % cols == 0) {
                row = theme.horizontalList();
                row.spacing = theme.scale(3);
                w.add(row);
            }
            row.add(theme.module(module)).minWidth(cardW);
            i++;
        }

        return w;
    }

    // Search

    protected void createSearchW(WContainer w, String text) {
        if (!text.isEmpty()) {
            // Titles
            List<Pair<Module, String>> modules = Modules.get().searchTitles(text);

            if (!modules.isEmpty()) {
                WSection section = w.add(theme.section("Modules")).expandX().widget();
                section.spacing = 0;

                int count = 0;
                for (Pair<Module, String> p : modules) {
                    if (count >= Config.get().moduleSearchCount.get() || count >= modules.size()) break;
                    section.add(theme.module(p.getLeft(), p.getRight())).expandX();
                    count++;
                }
            }

            // Settings
            Set<Module> settings = Modules.get().searchSettingTitles(text);

            if (!settings.isEmpty()) {
                WSection section = w.add(theme.section("Settings")).expandX().widget();
                section.spacing = 0;

                int count = 0;
                for (Module module : settings) {
                    if (count >= Config.get().moduleSearchCount.get() || count >= settings.size()) break;
                    section.add(theme.module(module)).expandX();
                    count++;
                }
            }
        }
    }

    protected WWindow createSearch(WContainer c) {
        WWindow w = theme.window("Search");
        w.id = "search";
        searchWindow = w;

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.item(Items.COMPASS.getDefaultStack())).pad(2);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.maxHeight -= 20;

        WVerticalList l = theme.verticalList();

        WTextBox text = w.add(theme.textBox("")).minWidth(140).expandX().widget();
        text.setFocused(true);
        searchTextBox = text;
        text.action = () -> {
            l.clear();
            createSearchW(l, text.get());
        };

        w.add(l).expandX();
        createSearchW(l, text.get());

        return w;
    }

    @Override
    public boolean keyPressed(KeyInput value) {
        if (locked) return false;

        boolean cntrl = MacWindowUtil.IS_MAC ? value.modifiers() == GLFW_MOD_SUPER : value.modifiers() == GLFW_MOD_CONTROL;

        if (cntrl && value.key() == GLFW_KEY_F) {
            if (searchWindow != null) searchWindow.setExpanded(true);
            if (searchTextBox != null) {
                searchTextBox.setFocused(true);
                searchTextBox.setCursorMax();
            }

            return true;
        }

        return super.keyPressed(value);
    }

    // Favorites

    protected Cell<WWindow> createFavorites(WContainer c) {
        boolean hasFavorites = Modules.get().getAll().stream().anyMatch(module -> module.favorite);
        if (!hasFavorites) return null;

        WWindow w = theme.window("Favorites");
        w.id = "favorites";
        w.padding = 0;
        w.spacing = 0;

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.item(Items.NETHER_STAR.getDefaultStack())).pad(2);
        }

        Cell<WWindow> cell = c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.spacing = 0;

        createFavoritesW(w);
        return cell;
    }

    protected boolean createFavoritesW(WWindow w) {
        List<Module> modules = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            if (module.favorite) {
                modules.add(module);
            }
        }

        modules.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name));

        for (Module module : modules) {
            w.add(theme.module(module)).expandX();
        }

        return !modules.isEmpty();
    }

    @Override
    public boolean toClipboard() {
        return NbtUtils.toClipboard(Modules.get());
    }

    @Override
    public boolean fromClipboard() {
        return NbtUtils.fromClipboard(Modules.get());
    }

    @Override
    public void reload() {
    }

    // Stuff

    protected class WCategoryController extends WContainer {
        public final List<WWindow> windows = new ArrayList<>();
        private Cell<WWindow> favorites;

        private WContainer tabBar;
        private WWindow selected;

        // Parallel lists of tab windows and their tab buttons, so the active tab can be highlighted.
        private final List<WWindow> tabWins = new ArrayList<>();
        private final List<WButton> tabBtns = new ArrayList<>();
        private WButton selectedButton;

        // Tab-strip glass panel bounds (computed in layout, drawn in onRender).
        private double stripX, stripY, stripW, stripH;

        // Colored dot palette for the category tabs (cycled by index).
        private final Color[] TAB_COLORS = {
            new Color(255, 111, 208), new Color(255, 194, 122), new Color(95, 212, 255),
            new Color(184, 77, 255), new Color(143, 224, 168), new Color(201, 166, 255),
            new Color(92, 141, 255), new Color(255, 211, 111)
        };

        @Override
        public void init() {
            // Vertical tab strip that sits to the left of the central menu.
            tabBar = add(theme.verticalList()).widget();

            // Still create one window per category so addon mixins on createCategory keep working.
            List<Module> moduleList = new ArrayList<>();
            for (Category category : Modules.loopCategories()) {
                for (Module module : Modules.get().getGroup(category)) {
                    if (!Config.get().hiddenModules.get().contains(module)) {
                        moduleList.add(module);
                    }
                }

                // Ensure empty categories are not shown
                if (!moduleList.isEmpty()) {
                    windows.add(createCategory(this, category, moduleList));
                    moduleList.clear();
                }
            }

            windows.add(createSearch(this));

            refresh();
            buildTabs();
        }

        protected void refresh() {
            if (favorites == null) {
                favorites = createFavorites(this);
                if (favorites != null) windows.add(favorites.widget());
            } else {
                favorites.widget().clear();

                if (!createFavoritesW(favorites.widget())) {
                    remove(favorites);
                    windows.remove(favorites.widget());
                    favorites = null;
                }
            }

            if (tabBar != null) buildTabs();
        }

        /** Category windows are the WWindow children of this container (authoritative; never null). */
        private List<WWindow> tabWindows() {
            List<WWindow> list = new ArrayList<>();
            for (Cell<?> cell : cells) {
                if (cell.widget() instanceof WWindow window) list.add(window);
            }
            return list;
        }

        private void buildTabs() {
            if (tabBar == null) return;
            tabBar.clear();
            tabWins.clear();
            tabBtns.clear();

            // Brand at the top of the strip.
            tabBar.add(theme.label(MeteorClient.NAME, true)).pad(6);

            for (WWindow window : tabWindows()) {
                WButton button = tabBar.add(theme.button(tabName(window))).expandX().widget();
                button.action = () -> select(window);

                tabWins.add(window);
                tabBtns.add(button);
            }

            // Keep a valid selection.
            if (selected == null || !tabWins.contains(selected)) {
                select(tabWins.isEmpty() ? null : tabWins.getFirst());
            } else {
                updateSelectedButton();
            }
        }

        private void select(WWindow window) {
            selected = window;
            if (window != null) window.setExpanded(true);
            updateSelectedButton();
            invalidate();
        }

        private void updateSelectedButton() {
            int i = tabWins.indexOf(selected);
            selectedButton = (i >= 0 && i < tabBtns.size()) ? tabBtns.get(i) : null;
        }

        private String tabName(WWindow window) {
            if (window == null) return "?";
            String s = window.id;
            if (s == null || s.isEmpty()) return "?";
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (tabBar == null) return;

            double pad = theme.scale(6);
            double r = theme.scale(14);

            // Full-height frosted glass tab-strip panel (matches the module panel height).
            double px = stripX - pad, py = stripY;
            double pw = stripW + pad * 2, ph = stripH;
            renderer.roundedQuad(px, py, pw, ph, r, new Color(255, 255, 255, 30));
            renderer.roundedQuad(px, py, pw, ph * 0.16, r, true, false, new Color(255, 255, 255, 28), new Color(255, 255, 255, 28));

            // Gradient pill behind the active tab (shows through the translucent tab button).
            if (selectedButton != null) {
                Color a1 = theme instanceof MeteorGuiTheme m1 ? m1.accentColor.get() : Color.WHITE;
                Color a2 = theme instanceof MeteorGuiTheme m2 ? m2.accentColor2.get() : a1;

                double ex = theme.scale(2);
                renderer.roundedQuad(selectedButton.x - ex, selectedButton.y - ex / 2, selectedButton.width + ex * 2, selectedButton.height + ex, theme.scale(9), a1, a2);
            }
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            boolean r = super.render(renderer, mouseX, mouseY, delta);

            // Colored dot beside each category tab, drawn AFTER children so it sits on top of the button.
            double dotR = theme.scale(3);
            for (int i = 0; i < tabBtns.size(); i++) {
                WButton b = tabBtns.get(i);
                Color c = TAB_COLORS[i % TAB_COLORS.length];
                renderer.roundedQuad(b.x + theme.scale(10), b.y + b.height / 2.0 - dotR, dotR * 2, dotR * 2, dotR, c);
            }

            return r;
        }

        @Override
        protected void onCalculateWidgetPositions() {
            double pad = theme.scale(6);
            double gap = theme.scale(12);
            double windowWidth = getWindowWidth();
            double windowHeight = getWindowHeight();

            // Clear any saved (dragged) window positions so our fixed tabbed layout always wins.
            for (Cell<?> cell : cells) {
                if (cell.widget() instanceof WWindow window && window.id != null) {
                    WindowConfig cfg = theme.getWindowConfig(window.id);
                    cfg.x = -1;
                    cfg.y = -1;
                }
            }

            // The module panel's height (WView caps itself to fit the screen); the tab strip matches it
            // so both panels are the same height and vertically centered.
            double panelH = selected != null ? selected.height : (tabBar.height + theme.scale(24));

            double contentWidth = selected != null ? selected.width : theme.scale(240);
            double groupWidth = tabBar.width + gap + contentWidth;

            double startX = Math.max(pad, windowWidth / 2.0 - groupWidth / 2.0);
            double contentX = startX + tabBar.width + gap;
            double topY = theme.scale(28); // fixed top so switching categories doesn't move the panels

            // Tab-strip glass panel bounds for onRender (matches the module panel height).
            stripX = startX;
            stripY = topY;
            stripW = tabBar.width;
            stripH = panelH;

            for (Cell<?> cell : cells) {
                WWidget widget = cell.widget();

                if (widget == tabBar) {
                    cell.x = startX;
                    cell.y = topY + theme.scale(8); // tabs sit near the top of the strip
                }
                else if (widget instanceof WWindow window) {
                    // Only the selected category's window is shown; the rest are hidden.
                    boolean show = (window == selected);
                    window.visible = show;

                    if (show) {
                        cell.x = contentX;
                        cell.y = topY; // top-aligned with the tab strip
                    } else {
                        cell.x = -100000;
                        cell.y = -100000;
                    }
                }
                else {
                    cell.x = this.x;
                    cell.y = this.y;
                }

                cell.width = widget.width;
                cell.height = widget.height;

                cell.alignWidget();
            }
        }
    }
}
