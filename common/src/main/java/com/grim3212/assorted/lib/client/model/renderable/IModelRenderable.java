package com.grim3212.assorted.lib.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/**
 * A standard interface for things that can be rendered through a {@link SubmitNodeCollector}.
 * <p>
 * 26.2 is retained mode: instead of pulling a {@code VertexConsumer} out of a {@code MultiBufferSource}
 * and writing to it, geometry is handed to a collector which batches and draws it later.
 *
 * @param <T> The type of context object used by the rendering logic
 */
@FunctionalInterface
public interface IModelRenderable<T> {
    /**
     * Draws the renderable by submitting its geometry to the provided {@link SubmitNodeCollector}
     *
     * @param poseStack               The pose stack
     * @param collector               The collector the geometry is submitted to
     * @param textureRenderTypeLookup A function that provides a RenderType for the given texture
     * @param lightmap                The lightmap coordinates representing the current lighting conditions
     * @param overlay                 The overlay coordinates representing the current overlay status. See {@link net.minecraft.client.renderer.texture.OverlayTexture}
     * @param partialTick             The current time expressed in the fraction of a tick elapsed since the last client tick
     * @param context                 The context used for rendering
     */
    void render(PoseStack poseStack, SubmitNodeCollector collector, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, T context);

    /**
     * Wraps the current renderable along with a context.
     * Useful for keeping a list of various renderables paired with their contexts.
     *
     * @param context The context used for rendering
     * @return A renderable that accepts {@link Unit#INSTANCE} as context, but uses the provided {@code context} instead
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
