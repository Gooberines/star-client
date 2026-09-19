/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.gui.screens.settings;

import bwead.bweadclient.gui.GuiTheme;
import bwead.bweadclient.gui.renderer.GuiRenderer;
import bwead.bweadclient.gui.screens.settings.base.CollectionMapSettingScreen;
import bwead.bweadclient.gui.widgets.WWidget;
import bwead.bweadclient.gui.widgets.pressable.WButton;
import bwead.bweadclient.settings.BlockDataSetting;
import bwead.bweadclient.settings.IBlockData;
import bwead.bweadclient.utils.misc.IChangeable;
import bwead.bweadclient.utils.render.DisplayItemUtils;
import bwead.bweadclient.utils.misc.ICopyable;
import bwead.bweadclient.utils.misc.ISerializable;
import bwead.bweadclient.utils.misc.Names;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

import static bwead.bweadclient.BweadClient.mc;

public class BlockDataSettingScreen<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> extends CollectionMapSettingScreen<Block, T> {
    private final BlockDataSetting<T> setting;
    private boolean invalidate;

    public BlockDataSettingScreen(GuiTheme theme, BlockDataSetting<T> setting) {
        super(theme, "Configure Blocks", setting, setting.get(), BuiltInRegistries.BLOCK);

        this.setting = setting;
    }

    @Override
    protected boolean includeValue(Block value) {
        return value != Blocks.AIR;
    }

    @Override
    protected WWidget getValueWidget(Block block) {
        return theme.itemWithLabel(DisplayItemUtils.toStack(block), Names.get(block));
    }

    @Override
    protected WWidget getDataWidget(Block block, @Nullable T blockData) {
        WButton edit = theme.button(GuiRenderer.EDIT);
        edit.action = () -> {
            T data = blockData;
            if (data == null) data = setting.defaultData.get().copy();

            mc.gui.setScreen(data.createScreen(theme, block, setting));
            invalidate = true;
        };
        return edit;
    }

    @Override
    protected void onRenderBefore(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (invalidate) {
            this.invalidateTable();
            invalidate = false;
        }
    }

    @Override
    protected String[] getValueNames(Block block) {
        return new String[]{
            Names.get(block),
            BuiltInRegistries.BLOCK.getKey(block).toString()
        };
    }
}
