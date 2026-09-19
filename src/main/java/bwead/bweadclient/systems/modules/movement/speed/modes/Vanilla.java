/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.systems.modules.movement.speed.modes;

import bwead.bweadclient.events.entity.player.PlayerMoveEvent;
import bwead.bweadclient.mixininterface.IVec3;
import bwead.bweadclient.systems.modules.Modules;
import bwead.bweadclient.systems.modules.movement.Anchor;
import bwead.bweadclient.systems.modules.movement.speed.SpeedMode;
import bwead.bweadclient.systems.modules.movement.speed.SpeedModes;
import bwead.bweadclient.utils.player.PlayerUtils;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class Vanilla extends SpeedMode {
    public Vanilla() {
        super(SpeedModes.Vanilla);
    }

    @Override
    public void onMove(PlayerMoveEvent event) {
        Vec3 vel = PlayerUtils.getHorizontalVelocity(settings.vanillaSpeed.get());
        double velX = vel.x();
        double velZ = vel.z();

        if (mc.player.hasEffect(MobEffects.SPEED)) {
            double value = (mc.player.getEffect(MobEffects.SPEED).getAmplifier() + 1) * 0.205;
            velX += velX * value;
            velZ += velZ * value;
        }

        Anchor anchor = Modules.get().get(Anchor.class);
        if (anchor.isActive() && anchor.controlMovement) {
            velX = anchor.deltaX;
            velZ = anchor.deltaZ;
        }

        ((IVec3) event.movement).meteor$set(velX, event.movement.y, velZ);
    }
}
