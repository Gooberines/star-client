/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.screens.settings;

import bwead.bweadclient.gui.GuiTheme;
import bwead.bweadclient.gui.WindowScreen;
import bwead.bweadclient.gui.widgets.WItemWithLabel;
import bwead.bweadclient.gui.widgets.containers.WTable;
import bwead.bweadclient.gui.widgets.input.WTextBox;
import bwead.bweadclient.gui.widgets.pressable.WButton;
import bwead.bweadclient.settings.BlockSetting;
import bwead.bweadclient.utils.misc.Names;
import bwead.bweadclient.utils.render.DisplayItemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.Strings;

public class BlockSettingScreen extends WindowScreen {
    private final BlockSetting setting;

    private WTable table;

    private WTextBox filter;
    private String filterText = "";

    public BlockSettingScreen(GuiTheme theme, BlockSetting setting) {
        super(theme, "Select Block");

        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initTable();
        };

        table = add(theme.table()).expandX().widget();

        initTable();
    }

    private void initTable() {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (setting.filter != null && !setting.filter.test(block)) continue;
            if (skipValue(block)) continue;

            WItemWithLabel item = theme.itemWithLabel(DisplayItemUtils.toStack(block), Names.get(block));
            if (!filterText.isEmpty() && !Strings.CI.contains(item.getLabelText(), filterText)) continue;
            table.add(item);

            WButton select = table.add(theme.button("Select")).expandCellX().right().widget();
            select.action = () -> {
                setting.set(block);
                onClose();
            };

            table.row();
        }
    }

    protected boolean skipValue(Block value) {
        return value == Blocks.AIR || BuiltInRegistries.BLOCK.getKey(value).getPath().endsWith("_wall_banner");
    }
}
