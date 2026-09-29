/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.utils.Cell;
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
        w.view.hasScrollBar = false;
        w.view.spacing = 0;

        for (Module module : moduleList) {
            w.add(theme.module(module)).expandX();
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

        private WHorizontalList tabBar;
        private WWindow selected;

        @Override
        public void init() {
            // Tab bar sits above the content. Added first so it renders at the top.
            tabBar = add(theme.horizontalList()).widget();

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

            // Default to the first category tab.
            select(windows.isEmpty() ? null : windows.getFirst());
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

        private void buildTabs() {
            tabBar.clear();

            for (WWindow window : windows) {
                WButton button = tabBar.add(theme.button(tabName(window))).widget();
                button.action = () -> select(window);
            }
        }

        private void select(WWindow window) {
            selected = window;

            // Only the selected category's window is visible (browser-style tabs).
            for (WWindow w : windows) w.visible = (w == window);
            if (window != null) window.setExpanded(true);

            invalidate();
        }

        private String tabName(WWindow window) {
            String s = window.id;
            if (s == null || s.isEmpty()) return "?";
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

        @Override
        protected void onCalculateWidgetPositions() {
            double pad = theme.scale(4);
            double windowWidth = getWindowWidth();

            // Tab bar centered at the top.
            double tabX = Math.max(pad, windowWidth / 2.0 - tabBar.width / 2.0);
            double tabY = this.y + pad;
            double contentY = tabY + tabBar.height + pad;

            for (Cell<?> cell : cells) {
                WWidget widget = cell.widget();

                double cx, cy;
                if (widget == tabBar) {
                    cx = tabX;
                    cy = tabY;
                }
                else if (widget == selected) {
                    cx = Math.max(pad, windowWidth / 2.0 - widget.width / 2.0);
                    cy = contentY;
                }
                else {
                    // Non-selected windows are hidden; park them off-screen so they don't render.
                    cx = -100000;
                    cy = -100000;
                }

                cell.x = cx;
                cell.y = cy;
                cell.width = widget.width;
                cell.height = widget.height;

                cell.alignWidget();
            }
        }
    }
}
