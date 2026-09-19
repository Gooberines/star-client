/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.settings;

import bwead.bweadclient.gui.GuiTheme;
import bwead.bweadclient.gui.WidgetScreen;
import bwead.bweadclient.utils.misc.ICopyable;
import bwead.bweadclient.utils.misc.ISerializable;

public interface IGeneric<T extends IGeneric<T>> extends ICopyable<T>, ISerializable<T> {
    WidgetScreen createScreen(GuiTheme theme, GenericSetting<T> setting);
}
