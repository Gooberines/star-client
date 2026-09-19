/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.themes.meteor.widgets.pressable;

import bwead.bweadclient.gui.renderer.GuiRenderer;
import bwead.bweadclient.gui.themes.meteor.BweadWidget;
import bwead.bweadclient.gui.widgets.pressable.WMinus;

public class WMeteorMinus extends WMinus implements BweadWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double pad = pad();
        double s = theme.scale(3);

        renderBackground(renderer, this, pressed, mouseOver);
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, theme().minusColor.get());
    }
}
