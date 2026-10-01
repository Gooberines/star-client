/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.network;

public class OnlinePlayers {
    private static long lastPingTime;

    private OnlinePlayers() {
    }

    // Online-player tracking to Meteor's servers is disabled so this client does
    // not report itself to meteorclient.com (separate footprint).
    public static void update() {
    }

    public static void leave() {
    }
}
