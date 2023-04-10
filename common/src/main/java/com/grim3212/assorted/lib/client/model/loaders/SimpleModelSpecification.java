package com.grim3212.assorted.lib.client.model.loaders;

import com.grim3212.assorted.lib.client.model.IModelBuilder;
import com.grim3212.assorted.lib.client.model.RenderTypeGroup;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * Base class for implementations of {@link IModelSpecification} which do not wish to handle model creation themselves,
 * instead supplying {@linkplain BakedQuad baked quads} through a builder.
 */
public abstract class SimpleModelSpecification<T extends SimpleModelSpecification<T>> implements IModelSpecification<T> {

    @Override
    public BakedModel bake(IModelBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle").orElse(null));

        IModelBuilder<?> builder = IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), context.getTransforms(), context.getItemOverrides(baker), particle, RenderTypeGroup.EMPTY);

        addQuads(context, builder, baker, spriteGetter, modelState, modelLocation);

        return builder.build();
    }

    protected abstract void addQuads(IModelBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation);
}
