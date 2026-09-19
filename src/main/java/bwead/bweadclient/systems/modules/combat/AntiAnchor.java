/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.systems.modules.combat;

import bwead.bweadclient.events.world.TickEvent;
import bwead.bweadclient.settings.BoolSetting;
import bwead.bweadclient.settings.Setting;
import bwead.bweadclient.settings.SettingGroup;
import bwead.bweadclient.systems.modules.Categories;
import bwead.bweadclient.systems.modules.Module;
import bwead.bweadclient.utils.player.InvUtils;
import bwead.bweadclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;

public class AntiAnchor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Makes you rotate when placing.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("Swings your hand when placing.")
        .defaultValue(true)
        .build()
    );

    public AntiAnchor() {
        super(Categories.Combat, "anti-anchor", "Automatically prevents Anchor Aura by placing a slab on your head.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.level.getBlockState(mc.player.blockPosition().above(2)).getBlock() == Blocks.RESPAWN_ANCHOR
            && mc.level.getBlockState(mc.player.blockPosition().above()).getBlock() == Blocks.AIR) {

            BlockUtils.place(
                mc.player.blockPosition().offset(0, 1, 0),
                InvUtils.findInHotbar(itemStack -> Block.byItem(itemStack.getItem()) instanceof SlabBlock),
                rotate.get(),
                15,
                swing.get(),
                false,
                true
            );
        }
    }
}
