/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.tabs.builtin;

import bwead.bweadclient.gui.GuiTheme;
import bwead.bweadclient.gui.tabs.Tab;
import bwead.bweadclient.gui.tabs.TabScreen;
import bwead.bweadclient.gui.tabs.WindowTabScreen;
import bwead.bweadclient.gui.widgets.containers.WHorizontalList;
import bwead.bweadclient.gui.widgets.containers.WTable;
import bwead.bweadclient.gui.widgets.input.WTextBox;
import bwead.bweadclient.gui.widgets.pressable.WMinus;
import bwead.bweadclient.gui.widgets.pressable.WPlus;
import bwead.bweadclient.systems.friends.Friend;
import bwead.bweadclient.systems.friends.Friends;
import bwead.bweadclient.utils.misc.NbtUtils;
import bwead.bweadclient.utils.network.BweadExecutor;
import net.minecraft.client.gui.screens.Screen;

import static bwead.bweadclient.BweadClient.mc;

public class FriendsTab extends Tab {
    public FriendsTab() {
        super("Friends");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new FriendsScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof FriendsScreen;
    }

    private static class FriendsScreen extends WindowTabScreen {
        public FriendsScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            WTable table = add(theme.table()).expandX().minWidth(400).widget();
            initTable(table);

            add(theme.horizontalSeparator()).expandX();

            // New
            WHorizontalList list = add(theme.horizontalList()).expandX().widget();

            WTextBox nameW = list.add(theme.textBox("", (_, c) -> c != ' ')).expandX().widget();
            nameW.setFocused(true);

            WPlus add = list.add(theme.plus()).widget();
            add.action = () -> {
                String name = nameW.get().trim();
                Friend friend = new Friend(name);

                if (Friends.get().add(friend)) {
                    nameW.set("");
                    initTable(table);
                    nameW.setFocused(true);

                    BweadExecutor.execute(() -> {
                        friend.updateInfo();
                        mc.execute(() -> {
                            initTable(table);
                            nameW.setFocused(true);
                        });
                    });
                }
            };

            enterAction = add.action;
        }

        private void initTable(WTable table) {
            table.clear();
            if (Friends.get().isEmpty()) return;

            Friends.get().forEach(friend ->
                BweadExecutor.execute(() -> {
                    if (friend.headTextureNeedsUpdate()) {
                        friend.updateInfo();
                    }
                })
            );

            for (Friend friend : Friends.get()) {
                table.add(theme.texture(32, 32, friend.getHead().needsRotate() ? 90 : 0, friend.getHead()));
                table.add(theme.label(friend.getName()));

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    Friends.get().remove(friend);
                    initTable(table);
                };

                table.row();
            }
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(Friends.get());
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(Friends.get());
        }
    }
}
