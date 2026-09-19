/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.mixininterface;

public interface IServerboundMovePlayerPacket {
    int meteor$getTag();

    void meteor$setTag(int tag);
}
