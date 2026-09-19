/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.screens.accounts;

import bwead.bweadclient.gui.GuiTheme;
import bwead.bweadclient.gui.WindowScreen;
import bwead.bweadclient.gui.widgets.containers.WHorizontalList;
import bwead.bweadclient.gui.widgets.pressable.WButton;
import bwead.bweadclient.systems.accounts.Account;
import bwead.bweadclient.systems.accounts.AccountType;
import bwead.bweadclient.systems.accounts.TokenAccount;
import bwead.bweadclient.utils.render.color.Color;

import static bwead.bweadclient.BweadClient.mc;

public class AccountInfoScreen extends WindowScreen {
    private final Account<?> account;

    public AccountInfoScreen(GuiTheme theme, Account<?> account) {
        super(theme, account.getUsername() + " details");
        this.account = account;
    }

    @Override
    public void initWidgets() {
        TokenAccount e = (TokenAccount) account;
        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        String tokenLabel = account.getType() + " token:";
        if (account.getType() == AccountType.Session) tokenLabel = "";

        WButton copy = theme.button("Copy");
        copy.action = () -> mc.keyboardHandler.setClipboard(e.getToken());

        l.add(theme.label(tokenLabel));
        l.add(theme.label(account.getType() == AccountType.Session ? "Click to copy Token" : e.getToken()).color(Color.GRAY)).pad(5);
        l.add(copy);
    }
}
