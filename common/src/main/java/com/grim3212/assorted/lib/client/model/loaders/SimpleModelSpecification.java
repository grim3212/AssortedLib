package com.grim3212.assorted.lib.client.model.loaders;

import com.grim3212.assorted.lib.client.model.IModelBuilder;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

/**
 * Base class for implementations of {@link IModelSpecification} which do not wish to handle model creation themselves,
 * instead supplying {@linkplain BakedQuad baked quads} through a builder.
 */
public abstract class SimpleModelSpecification<T extends SimpleModelSpecification<T>> implements IModelSpecification<T> {

    @Override
    public BlockStateModel bake(IModelBakingContext context, ModelBaker baker, ModelState modelState, Identifier modelLocation) {
        ModelDebugName debugName = modelLocation::toString;

        Material particle = context.getMaterial("particle").orElse(null);
        Material.Baked bakedParticle = particle != null ? baker.materials().get(particle, debugName) : baker.materials().reportMissingReference("particle", debugName);

        IModelBuilder<?> builder = IModelBuilder.of(context.useAmbientOcclusion(), bakedParticle);

        addQuads(context, builder, baker, modelState, modelLocation);

        // A specification produces a single part; SingleVariant is the vanilla BlockStateModel that
        // wraps exactly one part and is what the dispatcher hands to the renderer.
        BlockStateModelPart part = builder.build();
        return new SingleVariant(part);
    }

    protected abstract void addQuads(IModelBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, ModelState modelTransform, Identifier modelLocation);
}
