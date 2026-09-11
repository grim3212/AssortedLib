package com.grim3212.assorted.lib.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/**
 * Something rendered by submitting its geometry to a {@link SubmitNodeCollector}, which batches it
 * and draws it later.
 *
 * @param <T> The type of context object used by the rendering logic
 */
@FunctionalInterface
public interface IModelRenderable<T> {
    /**
     * Submits this renderable's geometry to {@code collector}. {@code lightmap} and {@code overlay}
     * are packed coordinates (see {@link net.minecraft.client.renderer.texture.OverlayTexture});
     * {@code textureRenderTypeLookup} gives the render type for each texture.
     */
    void render(PoseStack poseStack, SubmitNodeCollector collector, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, T context);

    /**
     * Pairs this renderable with {@code context}, giving one that takes {@link Unit#INSTANCE}
     * instead; useful for keeping a list of renderables with their contexts.
     */
    default IModelRenderable<Unit> withContext(T context) {
        return (poseStack, collector, textureRenderTypeLookup, lightmap, overlay, partialTick, unused) ->
                this.render(poseStack, collector, textureRenderTypeLookup, lightmap, overlay, partialTick, context);
    }

    /**
     * A generic lookup for {@link RenderType} implementations that use the specified texture.
     */
    @FunctionalInterface
    interface ITextureRenderTypeLookup {
        RenderType get(Identifier name);
    }
}
