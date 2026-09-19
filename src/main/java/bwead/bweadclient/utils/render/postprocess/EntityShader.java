package bwead.bweadclient.utils.render.postprocess;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import bwead.bweadclient.mixininterface.ILevelRenderer;
import net.minecraft.world.entity.Entity;

import static bwead.bweadclient.BweadClient.mc;

public abstract class EntityShader extends PostProcessShader {
    protected EntityShader(RenderPipeline pipeline) {
        super(pipeline);
    }

    public abstract boolean shouldDraw(Entity entity);

    @Override
    protected void preDraw() {
        ((ILevelRenderer) mc.levelRenderer).meteor$pushEntityOutlineFramebuffer(framebuffer);
    }

    @Override
    protected void postDraw() {
        ((ILevelRenderer) mc.levelRenderer).meteor$popEntityOutlineFramebuffer();
    }

    public void submitVertices() {
    }
}
