/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import bwead.bweadclient.systems.modules.Modules;
import bwead.bweadclient.systems.modules.movement.EntityControl;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin {
    @ModifyReturnValue(method = "isSaddled", at = @At("RETURN"))
    private boolean isSaddled(boolean original) {
        return Modules.get().get(EntityControl.class).spoofSaddle() || original;
    }
}
