/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import bwead.bweadclient.commands.Command;
import bwead.bweadclient.renderer.Fonts;
import bwead.bweadclient.systems.Systems;
import bwead.bweadclient.systems.friends.Friend;
import bwead.bweadclient.systems.friends.Friends;
import bwead.bweadclient.utils.network.Capes;
import bwead.bweadclient.utils.network.BweadExecutor;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload", "Reloads many systems.");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(_ -> {
            warning("Reloading systems, this may take a while.");

            Systems.load();
            Capes.init();
            Fonts.refresh();
            BweadExecutor.execute(() -> Friends.get().forEach(Friend::updateInfo));

            return SINGLE_SUCCESS;
        });
    }
}
