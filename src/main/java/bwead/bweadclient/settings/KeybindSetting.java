/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.settings;

import bwead.bweadclient.BweadClient;
import bwead.bweadclient.events.meteor.KeyInputEvent;
import bwead.bweadclient.events.meteor.MouseClickEvent;
import bwead.bweadclient.gui.widgets.WKeybind;
import bwead.bweadclient.utils.misc.Keybind;
import bwead.bweadclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.nbt.CompoundTag;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.function.Consumer;

public class KeybindSetting extends Setting<Keybind> {
    private final Runnable action;
    public WKeybind widget;

    public KeybindSetting(String name, String description, Keybind defaultValue, Consumer<Keybind> onChanged, Consumer<Setting<Keybind>> onModuleActivated, IVisible visible, Runnable action) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.action = action;
        BweadClient.EVENT_BUS.subscribe(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onKeyBinding(KeyInputEvent event) {
        if (widget == null) return;
        if (event.action == KeyAction.Press && event.key() == InputConstants.KEY_ESCAPE && widget.onClear()) event.cancel();
        else if (event.action == KeyAction.Release && widget.onAction(true, event.key(), event.modifiers()))
            event.cancel();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMouseClickBinding(MouseClickEvent event) {
        if (event.action == KeyAction.Press && widget != null && widget.onAction(false, event.button(), 0))
            event.cancel();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyInputEvent event) {
        if (event.action == KeyAction.Release && get().matches(event.input) && (module == null || module.isActive()) && action != null) {
            action.run();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onMouseClick(MouseClickEvent event) {
        if (event.action == KeyAction.Release && get().matches(event.input) && (module == null || module.isActive()) && action != null) {
            action.run();
        }
    }

    @Override
    public void resetImpl() {
        if (value == null) value = defaultValue.copy();
        else value.set(defaultValue);

        if (widget != null) widget.reset();
    }

    @Override
    protected Keybind parseImpl(String str) {
        try {
            return Keybind.fromKey(Integer.parseInt(str.trim()));
        } catch (NumberFormatException _) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(Keybind value) {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("value", get().toTag());

        return tag;
    }

    @Override
    public Keybind load(CompoundTag tag) {
        get().fromTag(tag.getCompoundOrEmpty("value"));

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Keybind, KeybindSetting> {
        private Runnable action;

        public Builder() {
            super(Keybind.none());
        }

        public Builder action(Runnable action) {
            this.action = action;
            return this;
        }

        @Override
        public KeybindSetting build() {
            return new KeybindSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, action);
        }
    }
}
