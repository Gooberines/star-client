/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package bwead.bweadclient.mixin;

import bwead.bweadclient.renderer.MeshUniforms;
import bwead.bweadclient.systems.modules.Modules;
import bwead.bweadclient.systems.modules.misc.InventoryTweaks;
import bwead.bweadclient.utils.render.postprocess.ChamsShader;
import bwead.bweadclient.utils.render.postprocess.OutlineUniforms;
import bwead.bweadclient.utils.render.postprocess.PostProcessShader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static bwead.bweadclient.BweadClient.mc;

@Mixin(Minecraft.class)
public abstract class MinecraftRenderFrameMixin {
    @Inject(method = "renderFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;endFrame()V", shift = At.Shift.AFTER))
    private void meteor$afterRenderFrame(boolean advanceGameTime, CallbackInfo ci) {
        MeshUniforms.flipFrame();
        PostProcessShader.flipFrame();
        ChamsShader.flipFrame();
        OutlineUniforms.flipFrame();

        Modules modules = Modules.get();
        if (modules == null || mc.player == null) return;

        InventoryTweaks inventoryTweaks = modules.get(InventoryTweaks.class);
        if (inventoryTweaks != null && inventoryTweaks.frameInput()) {
            ((MinecraftAccessor) mc).meteor$handleInputEvents();
        }
    }
}
