package com.grim3212.assorted.lib.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

/**
 * A standard interface for things that can be rendered to a {@link MultiBufferSource}.
 *
 * @param <T> The type of context object used by the rendering logic
 */
@FunctionalInterface
public interface IModelRenderable<T> {
    /**
     * Draws the renderable by adding the geometry to the provided {@link MultiBufferSource}
     *
     * @param poseStack               The pose stack
     * @param bufferSource            The buffer source where the vertex data should be output
     * @param textureRenderTypeLookup A function that provides a RenderType for the given texture
     * @param lightmap                The lightmap coordinates representing the current lighting conditions. See {@link net.minecraft.client.renderer.LightTexture}
     * @param overlay                 The overlay coordinates representing the current overlay status. See {@link net.minecraft.client.renderer.texture.OverlayTexture}
     * @param partialTick             The current time expressed in the fraction of a tick elapsed since the last client tick
     * @param context                 The context used for rendering
     */
    void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, T context);

    /**
     * Wraps the current renderable along with a context.
     * Useful for keeping a list of various renderables paired with their contexts.
     *
     * @param context The context used for rendering
     * @return A renderable that accepts {@link Unit#INSTANCE} as context, but uses the provided {@code context} instead
     */
    default IModelRenderable<Unit> withContext(T context) {
        return (poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, unused) ->
                this.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, context);
    }

    /**
     * A generic lookup for {@link RenderType} implementations that use the specified texture.
     */
    @FunctionalInterface
    interface ITextureRenderTypeLookup {
        RenderType get(ResourceLocation name);
    }
}
