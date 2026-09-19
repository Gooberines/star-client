/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.themes.meteor.widgets;

import bwead.bweadclient.gui.WidgetScreen;
import bwead.bweadclient.gui.themes.meteor.BweadWidget;
import bwead.bweadclient.gui.widgets.WAccount;
import bwead.bweadclient.systems.accounts.Account;
import bwead.bweadclient.utils.render.color.Color;

public class WMeteorAccount extends WAccount implements BweadWidget {
    public WMeteorAccount(WidgetScreen screen, Account<?> account) {
        super(screen, account);
    }

    @Override
    protected Color loggedInColor() {
        return theme().loggedInColor.get();
    }

    @Override
    protected Color accountTypeColor() {
        return theme().textSecondaryColor.get();
    }
}
