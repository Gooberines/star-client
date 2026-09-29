/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Cohesive, browser-style ClickGUI: a single window with a category tab bar at the top and a
 * swappable module list below. Selecting a tab replaces the content pane (like switching browser tabs).
 */
public class ModulesScreen extends WindowTabScreen {
    private static final Object FAVORITES = new Object();
    private static final Object SEARCH = new Object();

    private WHorizontalList tabBar;
    private WVerticalList content;
    private WTextBox searchTextBox;
    private Object current;

    public ModulesScreen(GuiTheme theme) {
        super(theme, Tabs.get().getFirst());

        window.id = "modules";
        window.padding = 0;
        window.spacing = 0;
    }

    @Override
    public void initWidgets() {
        // Tab bar: one tab per non-empty category, plus Favorites and Search.
        tabBar = add(theme.horizontalList()).expandX().widget();

        Category firstCategory = null;
        for (Category category : Modules.loopCategories()) {
            if (!hasVisibleModules(category)) continue;
            if (firstCategory == null) firstCategory = category;

            Category c = category; // effectively final for the lambda
            addTab(category.name, () -> selectCategory(c));
        }

        addTab("Favorites", this::selectFavorites);
        addTab("Search", this::selectSearch);

        // Content pane (the window itself scrolls when a category is tall).
        content = add(theme.verticalList()).expandX().widget();
        content.spacing = 0;

        // Default view.
        if (firstCategory != null) selectCategory(firstCategory);
        else selectSearch();
    }

    private void addTab(String name, Runnable onSelect) {
        WButton button = tabBar.add(theme.button(name)).widget();
        button.action = onSelect;
    }

    private boolean hasVisibleModules(Category category) {
        for (Module module : Modules.get().getGroup(category)) {
            if (!Config.get().hiddenModules.get().contains(module)) return true;
        }
        return false;
    }

    // Tab content

    private void selectCategory(Category category) {
        current = category;
        content.clear();

        for (Module module : Modules.get().getGroup(category)) {
            if (!Config.get().hiddenModules.get().contains(module)) {
                content.add(theme.module(module)).expandX();
            }
        }

        invalidate();
    }

    private void selectFavorites() {
        current = FAVORITES;
        content.clear();

        List<Module> modules = new ArrayList<>();
        for (Module module : Modules.get().getAll()) {
            if (module.favorite) modules.add(module);
        }
        modules.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name));

        if (modules.isEmpty()) {
            content.add(theme.label("No favorite modules.")).pad(4);
        } else {
            for (Module module : modules) content.add(theme.module(module)).expandX();
        }

        invalidate();
    }

    private void selectSearch() {
        current = SEARCH;
        content.clear();

        WTextBox text = content.add(theme.textBox("")).minWidth(140).expandX().widget();
        text.setFocused(true);
        searchTextBox = text;

        WVerticalList results = content.add(theme.verticalList()).expandX().widget();
        text.action = () -> {
            results.clear();
            createSearchW(results, text.get());
            results.invalidate();
        };

        createSearchW(results, text.get());
        invalidate();
    }

    protected void createSearchW(WContainer w, String text) {
        if (text.isEmpty()) return;

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

    @Override
    public boolean keyPressed(KeyInput value) {
        if (locked) return false;

        boolean cntrl = MacWindowUtil.IS_MAC ? value.modifiers() == GLFW_MOD_SUPER : value.modifiers() == GLFW_MOD_CONTROL;

        if (cntrl && value.key() == GLFW_KEY_F) {
            if (current != SEARCH) selectSearch();
            if (searchTextBox != null) {
                searchTextBox.setFocused(true);
                searchTextBox.setCursorMax();
            }
            return true;
        }

        return super.keyPressed(value);
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
}
