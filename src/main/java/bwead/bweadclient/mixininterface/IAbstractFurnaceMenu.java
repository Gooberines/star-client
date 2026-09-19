/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.mixininterface;

import net.minecraft.world.item.ItemStack;

public interface IAbstractFurnaceMenu {
    boolean meteor$canSmelt(ItemStack itemStack);
}
