/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.grim3212.assorted.lib.client.model;

import com.grim3212.assorted.lib.client.model.baked.simple.WrappedSimpleBakedModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;

import java.util.List;

/**
 * Base interface for any object that collects culled and unculled faces and bakes them into a model.
 * <p>
 * Provides a generic base implementation via {@link #of(boolean, Material.Baked)} and a
 * quad-collecting alternative via {@link #collecting(List)}.
 * <p>
 * The result is a {@link BlockStateModelPart} rather than a whole model: in 26.2 a part is the unit
 * that owns quads, ambient occlusion and a particle material, which is everything this builder was
 * ever able to fill in. Item transforms, item overrides and render type groups used to be passed in
 * here too - transforms belong to the item pipeline now, overrides no longer exist, and the render
 * layer is derived per quad from {@link BakedQuad.MaterialInfo#layer()}.
 *
 * @see QuadCollection.Builder the vanilla equivalent, if you do not need the extra part metadata
 */
public interface IModelBuilder<T extends IModelBuilder<T>> {
    /**
     * Creates a new model builder that uses the provided attributes in the final model part.
     */
    static IModelBuilder<?> of(boolean hasAmbientOcclusion, Material.Baked particle) {
        return new Simple(hasAmbientOcclusion, particle);
    }

    /**
     * Creates a new model builder that collects quads to the provided list, returning
     * {@linkplain EmptyModel#PART an empty model part} if you call {@link #build()}.
     */
    static IModelBuilder<?> collecting(List<BakedQuad> quads) {
        return new Collecting(quads);
    }

    T addCulledFace(Direction facing, BakedQuad quad);

    T addUnculledFace(BakedQuad quad);

    BlockStateModelPart build();

    class Simple implements IModelBuilder<Simple> {
        private final WrappedSimpleBakedModel.Builder builder;

        private Simple(boolean hasAmbientOcclusion, Material.Baked particle) {
            this.builder = new WrappedSimpleBakedModel.Builder(hasAmbientOcclusion).particle(particle);
        }

        @Override
        public Simple addCulledFace(Direction facing, BakedQuad quad) {
            builder.addCulledFace(facing, quad);
            return this;
        }

        @Override
        public Simple addUnculledFace(BakedQuad quad) {
            builder.addUnculledFace(quad);
            return this;
        }

        @Override
        public BlockStateModelPart build() {
            return builder.build();
        }
    }

    class Collecting implements IModelBuilder<Collecting> {
        private final List<BakedQuad> quads;

        private Collecting(List<BakedQuad> quads) {
            this.quads = quads;
        }

        @Override
        public Collecting addCulledFace(Direction facing, BakedQuad quad) {
            quads.add(quad);
            return this;
        }

        @Override
        public Collecting addUnculledFace(BakedQuad quad) {
            quads.add(quad);
            return this;
        }

        @Override
        public BlockStateModelPart build() {
            return EmptyModel.PART;
        }
    }
}
