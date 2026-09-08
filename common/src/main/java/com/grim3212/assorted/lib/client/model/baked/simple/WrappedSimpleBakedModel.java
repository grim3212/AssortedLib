package com.grim3212.assorted.lib.client.model.baked.simple;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;

/**
 * Builds a plain {@linkplain BlockStateModelPart model part} out of loose quads.
 * <p>
 * The 1.20.1 version of this class subclassed {@code SimpleBakedModel} to bolt render types onto it.
 * Its 26.2 replacement, {@link SimpleModelWrapper}, is a record - and render types are no longer a
 * property of a model at all - so what is left is the builder, which collects culled and unculled
 * faces into a {@link QuadCollection} exactly like {@link QuadCollection.Builder} does and wraps the
 * result up with the ambient occlusion flag and particle material a part has to expose.
 */
public final class WrappedSimpleBakedModel {

    private WrappedSimpleBakedModel() {
    }

    public static class Builder {

        private final QuadCollection.Builder quads = new QuadCollection.Builder();
        private final boolean hasAmbientOcclusion;
        private Material.Baked particleMaterial;

        public Builder(boolean hasAmbientOcclusion) {
            this.hasAmbientOcclusion = hasAmbientOcclusion;
        }

        public WrappedSimpleBakedModel.Builder addCulledFace(Direction direction, BakedQuad quad) {
            this.quads.addCulledFace(direction, quad);
            return this;
        }

        public WrappedSimpleBakedModel.Builder addUnculledFace(BakedQuad quad) {
            this.quads.addUnculledFace(quad);
            return this;
        }

        public WrappedSimpleBakedModel.Builder particle(Material.Baked particleMaterial) {
            this.particleMaterial = particleMaterial;
            return this;
        }

        public BlockStateModelPart build() {
            if (this.particleMaterial == null) {
                throw new RuntimeException("Missing particle!");
            }

            return new SimpleModelWrapper(this.quads.build(), this.hasAmbientOcclusion, this.particleMaterial);
        }
    }
}
