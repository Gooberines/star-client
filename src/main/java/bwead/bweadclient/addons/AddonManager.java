/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.addons;

import bwead.bweadclient.BweadClient;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.ArrayList;
import java.util.List;

public class AddonManager {
    public static final List<BweadAddon> ADDONS = new ArrayList<>();

    public static void init() {
        // Bwead pseudo addon
        {
            BweadClient.ADDON = new BweadAddon() {
                @Override
                public void onInitialize() {}

                @Override
                public String getPackage() {
                    return "bwead.bweadclient";
                }

                @Override
                public String getWebsite() {
                    return "https://meteorclient.com";
                }

                @Override
                public GithubRepo getRepo() {
                    return new GithubRepo("MeteorDevelopment", "bwead-client");
                }

                @Override
                public String getCommit() {
                    String commit = BweadClient.MOD_META.getCustomValue(BweadClient.MOD_ID + ":commit").getAsString();
                    return commit.isEmpty() ? null : commit;
                }
            };

            ModMetadata metadata = FabricLoader.getInstance().getModContainer(BweadClient.MOD_ID).get().getMetadata();

            BweadClient.ADDON.name = metadata.getName();
            BweadClient.ADDON.authors = new String[metadata.getAuthors().size()];
            if (metadata.containsCustomValue(BweadClient.MOD_ID + ":color")) {
                BweadClient.ADDON.color.parse(metadata.getCustomValue(BweadClient.MOD_ID + ":color").getAsString());
            }

            int i = 0;
            for (Person author : metadata.getAuthors()) {
                BweadClient.ADDON.authors[i++] = author.getName();
            }

            ADDONS.add(BweadClient.ADDON);
        }

        // Addons
        for (EntrypointContainer<BweadAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("meteor", BweadAddon.class)) {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            BweadAddon addon;
            try {
                addon = entrypoint.getEntrypoint();
            } catch (Throwable throwable) {
                throw new RuntimeException("Exception during addon init \"%s\".".formatted(metadata.getName()), throwable);
            }

            addon.name = metadata.getName();

            if (metadata.getAuthors().isEmpty()) throw new RuntimeException("Addon \"%s\" requires at least 1 author to be defined in it's fabric.mod.json. See https://fabricmc.net/wiki/documentation:fabric_mod_json_spec".formatted(addon.name));
            addon.authors = new String[metadata.getAuthors().size()];

            if (metadata.containsCustomValue(BweadClient.MOD_ID + ":color")) {
                addon.color.parse(metadata.getCustomValue(BweadClient.MOD_ID + ":color").getAsString());
            }

            int i = 0;
            for (Person author : metadata.getAuthors()) {
                addon.authors[i++] = author.getName();
            }

            ADDONS.add(addon);
        }
    }
}
