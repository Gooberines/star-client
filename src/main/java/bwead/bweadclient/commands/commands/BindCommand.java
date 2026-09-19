/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import bwead.bweadclient.commands.Command;
import bwead.bweadclient.commands.arguments.ModuleArgumentType;
import bwead.bweadclient.systems.modules.Module;
import bwead.bweadclient.systems.modules.Modules;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind", "Binds a specified module to the next pressed key.");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            Modules.get().setModuleToBind(module);
            Modules.get().awaitKeyRelease();
            module.info("Press a key to bind the module to.");
            return SINGLE_SUCCESS;
        }));
    }
}
