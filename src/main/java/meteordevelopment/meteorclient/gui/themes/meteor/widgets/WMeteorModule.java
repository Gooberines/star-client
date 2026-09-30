/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.MathHelper;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class WMeteorModule extends WPressable implements MeteorWidget {
    private final Module module;
    private final String title;

    private double titleWidth;

    private double animationProgress1;

    private double animationProgress2;

    public WMeteorModule(Module module, String title) {
        this.module = module;
        this.title = title;
        this.tooltip = module.description;

        if (module.isActive()) {
            animationProgress1 = 1;
            animationProgress2 = 1;
        } else {
            animationProgress1 = 0;
            animationProgress2 = 0;
        }
    }

    @Override
    public double pad() {
        return theme.scale(6);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (titleWidth == 0) titleWidth = theme.textWidth(title);

        width = pad + titleWidth + pad;
        height = pad + theme.textHeight() + pad;
    }

    @Override
    protected void onPressed(int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) module.toggle();
        else if (button == GLFW_MOUSE_BUTTON_RIGHT) mc.setScreen(theme.moduleScreen(module));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        MeteorGuiTheme theme = theme();
        double pad = pad();

        animationProgress1 += delta * 4 * ((module.isActive() || mouseOver) ? 1 : -1);
        animationProgress1 = MathHelper.clamp(animationProgress1, 0, 1);

        animationProgress2 += delta * 6 * (module.isActive() ? 1 : -1);
        animationProgress2 = MathHelper.clamp(animationProgress2, 0, 1);

        // Light frosted base card behind every row so the list reads as wide cards.
        renderer.roundedQuad(x, y, width, height, theme.round(), new Color(255, 255, 255, 34));

        if (animationProgress1 > 0) {
            renderer.roundedQuad(x, y, width * animationProgress1, height, theme.round(), theme.moduleBackground.get());
        }
        if (animationProgress2 > 0) {
            renderer.quad(x, y + height * (1 - animationProgress2), theme.scale(2), height * animationProgress2, theme.accentColor.get());
        }

        double x = this.x + pad;
        double w = width - pad * 2;

        if (theme.moduleAlignment.get() == AlignmentX.Center) {
            x += w / 2 - titleWidth / 2;
        }
        else if (theme.moduleAlignment.get() == AlignmentX.Right) {
            x += w - titleWidth;
        }

        renderer.text(title, x, y + pad, theme.textColor.get(), false);

        // Toggle switch on the right edge (on = accent gradient, sliding white knob).
        double trackH = theme.scale(13);
        double trackW = theme.scale(26);

        // Only draw when the row is wide enough that the switch won't collide with the title.
        if (width - titleWidth > trackW + pad * 3) {
            double sx = this.x + width - pad - trackW;
            double sy = y + (height - trackH) / 2.0;
            double rad = trackH / 2.0;

            // Off track (neutral glass) always, on track (accent) fading in with activation.
            renderer.roundedQuad(sx, sy, trackW, trackH, rad, new Color(255, 255, 255, 55));
            if (animationProgress2 > 0) {
                Color a1 = theme.accentColor.get();
                Color a2 = theme.accentColor2.get();
                int al = (int) (animationProgress2 * 255);
                renderer.roundedQuad(sx, sy, trackW, trackH, rad,
                    new Color(a1.r, a1.g, a1.b, a1.a * al / 255),
                    new Color(a2.r, a2.g, a2.b, a2.a * al / 255));
            }

            // Knob slides left (off) to right (on).
            double knobD = trackH - theme.scale(4);
            double knobX = sx + theme.scale(2) + (trackW - knobD - theme.scale(4)) * animationProgress2;
            double knobY = sy + theme.scale(2);
            renderer.roundedQuad(knobX, knobY, knobD, knobD, knobD / 2.0, new Color(255, 255, 255, 245));
        }
    }
}
