/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.settings;

import bwead.bweadclient.gui.GuiTheme;
import bwead.bweadclient.gui.WidgetScreen;
import bwead.bweadclient.utils.misc.IChangeable;
import bwead.bweadclient.utils.misc.ICopyable;
import bwead.bweadclient.utils.misc.ISerializable;
import net.minecraft.world.level.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(GuiTheme theme, Block block, BlockDataSetting<T> setting);
}
