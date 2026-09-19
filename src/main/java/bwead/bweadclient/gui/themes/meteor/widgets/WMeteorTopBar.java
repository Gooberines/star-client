/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.themes.meteor.widgets;

import bwead.bweadclient.gui.themes.meteor.BweadWidget;
import bwead.bweadclient.gui.widgets.WTopBar;
import bwead.bweadclient.utils.render.color.Color;

public class WMeteorTopBar extends WTopBar implements BweadWidget {
    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColor.get();
    }
}
