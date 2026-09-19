/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.screens.settings;

import bwead.bweadclient.gui.GuiTheme;
import bwead.bweadclient.gui.screens.settings.base.CollectionListSettingScreen;
import bwead.bweadclient.gui.widgets.WWidget;
import bwead.bweadclient.settings.PacketListSetting;
import bwead.bweadclient.settings.Setting;
import bwead.bweadclient.utils.network.PacketUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.function.Predicate;

public class PacketBoolSettingScreen extends CollectionListSettingScreen<PacketType<? extends @NonNull Packet<?>>> {
    public PacketBoolSettingScreen(GuiTheme theme, Setting<Set<PacketType<? extends @NonNull Packet<?>>>> setting) {
        super(theme, "Select Packets", setting, setting.get(), PacketUtils.getPackets());
    }

    @Override
    protected boolean includeValue(PacketType<? extends @NonNull Packet<?>> value) {
        Predicate<PacketType<? extends @NonNull Packet<?>>> filter = ((PacketListSetting) setting).filter;

        if (filter == null) return true;
        return filter.test(value);
    }

    @Override
    protected WWidget getValueWidget(PacketType<? extends @NonNull Packet<?>> value) {
        return theme.label(value.toString());
    }

    @Override
    protected String[] getValueNames(PacketType<? extends @NonNull Packet<?>> value) {
        return new String[]{
            value.toString()
        };
    }
}
