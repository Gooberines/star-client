/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.systems.waypoints.events;

import bwead.bweadclient.systems.waypoints.Waypoint;

public record WaypointAddedEvent(Waypoint waypoint) {
}
