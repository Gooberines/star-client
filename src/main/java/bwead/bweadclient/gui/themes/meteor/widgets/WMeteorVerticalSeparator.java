/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.themes.meteor.widgets;

import bwead.bweadclient.gui.renderer.GuiRenderer;
import bwead.bweadclient.gui.themes.meteor.BweadGuiTheme;
import bwead.bweadclient.gui.themes.meteor.BweadWidget;
import bwead.bweadclient.gui.widgets.WVerticalSeparator;
import bwead.bweadclient.utils.render.color.Color;

public class WMeteorVerticalSeparator extends WVerticalSeparator implements BweadWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        BweadGuiTheme theme = theme();
        Color colorEdges = theme.separatorEdges.get();
        Color colorCenter = theme.separatorCenter.get();

        double s = theme.scale(1);
        double offsetX = Math.round(width / 2.0);

        renderer.quad(x + offsetX, y, s, height / 2, colorEdges, colorEdges, colorCenter, colorCenter);
        renderer.quad(x + offsetX, y + height / 2, s, height / 2, colorCenter, colorCenter, colorEdges, colorEdges);
    }
}
