package com.grim3212.assorted.lib.client.model.renderable;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * A renderable object composed of a hierarchy of parts, each made up of a number of meshes.
 * <p>
 * Each mesh renders a set of quads using a different texture.
 *
 * @see Builder
 */
public class CompositeModelRenderable implements IModelRenderable<CompositeModelRenderable.Transforms> {
    private final List<Component> components = new ArrayList<>();

    private CompositeModelRenderable() {
    }

    @Override
    public void render(PoseStack poseStack, SubmitNodeCollector collector, IModelRenderable.ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, Transforms context) {
        for (var component : components)
            component.render(poseStack, collector, textureRenderTypeLookup, lightmap, overlay, context);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Component {
        private final String name;
        private final List<Component> children = new ArrayList<>();
        private final List<Mesh> meshes = new ArrayList<>();

        public Component(String name) {
            this.name = name;
        }

        public void render(PoseStack poseStack, SubmitNodeCollector collector, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, Transforms context) {
            Matrix4f matrix = context.getTransform(name);
            if (matrix != null) {
                poseStack.pushPose();
                poseStack.mulPose(matrix);
            }

            for (var part : children)
                part.render(poseStack, collector, textureRenderTypeLookup, lightmap, overlay, context);

            for (var mesh : meshes)
                mesh.render(poseStack, collector, textureRenderTypeLookup, lightmap, overlay);

            if (matrix != null)
                poseStack.popPose();
        }
    }

    private static class Mesh {
        private final Identifier texture;
        private final List<BakedQuad> quads = new ArrayList<>();

        public Mesh(Identifier texture) {
            this.texture = texture;
        }

        public void render(PoseStack poseStack, SubmitNodeCollector collector, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay) {
            // A mesh is a loose bag of quads sharing one texture, which does not fit any of the typed
            // submit calls (submitBlockModel wants BlockStateModelParts, submitItem wants a display
            // context), so it goes through the custom geometry escape hatch - the one place a raw
            // VertexConsumer still exists. Colour and light are no longer baked into the quads, they
            // are carried by the QuadInstance handed to putBakedQuad.
            var instance = new QuadInstance();
            instance.setColor(-1);
            instance.setLightCoords(lightmap);
            instance.setOverlayCoords(overlay);

            collector.submitCustomGeometry(poseStack, textureRenderTypeLookup.get(texture), (pose, buffer) -> {
                for (var quad : quads) {
                    buffer.putBakedQuad(pose, quad, instance);
                }
            });
        }
    }

    public static class Builder {
        private final CompositeModelRenderable renderable = new CompositeModelRenderable();

        private Builder() {
        }

        public PartBuilder<Builder> child(String name) {
            var child = new Component(name);
            renderable.components.add(child);
            return new PartBuilder<>(this, child);
        }

        public CompositeModelRenderable get() {
            return renderable;
        }
    }

    public static class PartBuilder<T> {
        private final T parent;
        private final Component component;

        private PartBuilder(T parent, Component component) {
            this.parent = parent;
            this.component = component;
        }

        public PartBuilder<PartBuilder<T>> child(String name) {
            var child = new Component(component.name + "/" + name);
            this.component.children.add(child);
            return new PartBuilder<>(this, child);
        }

        public PartBuilder<T> addMesh(Identifier texture, List<BakedQuad> quads) {
            var mesh = new Mesh(texture);
            mesh.quads.addAll(quads);
            component.meshes.add(mesh);
            return this;
        }

        public T end() {
            return parent;
        }
    }

    /**
     * A context value that provides {@link Matrix4f} transforms for certain parts of the model.
     */
    public static class Transforms {
        /**
         * A default instance that has no transforms specified.
         */
        public static final Transforms EMPTY = new Transforms(ImmutableMap.of());

        /**
         * Builds a MultipartTransforms object with the given mapping.
         */
        public static Transforms of(ImmutableMap<String, Matrix4f> parts) {
            return new Transforms(parts);
        }

        private final ImmutableMap<String, Matrix4f> parts;

        private Transforms(ImmutableMap<String, Matrix4f> parts) {
            this.parts = parts;
        }

        @Nullable
        public Matrix4f getTransform(String part) {
            return parts.get(part);
        }
    }
}
