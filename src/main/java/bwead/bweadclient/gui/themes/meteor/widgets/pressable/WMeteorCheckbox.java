/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.themes.meteor.widgets.pressable;

import bwead.bweadclient.gui.renderer.GuiRenderer;
import bwead.bweadclient.gui.themes.meteor.BweadGuiTheme;
import bwead.bweadclient.gui.themes.meteor.BweadWidget;
import bwead.bweadclient.gui.widgets.pressable.WCheckbox;
import net.minecraft.util.Mth;

public class WMeteorCheckbox extends WCheckbox implements BweadWidget {
    private double animProgress;

    public WMeteorCheckbox(boolean checked) {
        super(checked);
        animProgress = checked ? 1 : 0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        BweadGuiTheme theme = theme();

        animProgress += (checked ? 1 : -1) * delta * 14;
        animProgress = Mth.clamp(animProgress, 0, 1);

        renderBackground(renderer, this, pressed, mouseOver);

        if (animProgress > 0) {
            double cs = (width - theme.scale(2)) / 1.75 * animProgress;
            renderer.quad(x + (width - cs) / 2, y + (height - cs) / 2, cs, cs, theme.checkboxColor.get());
        }
    }
}
