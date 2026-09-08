package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.client.model.IBlockModelAccessor;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.function.Function;

public class FabricExtendedBlockModel extends BlockModel {
    private final IModelSpecification<?> specification;

    public FabricExtendedBlockModel(final IBlockModelAccessor blockModel, final IModelSpecification<?> specification) {
        super(blockModel.parentLocation(), Collections.emptyList(), blockModel.textureMap(), blockModel.usesAmbientOcclusion(), blockModel.guiLight(), blockModel.transforms(), blockModel.overrides());
        this.specification = specification;
    }

    @Override
    public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState, Identifier resourceLocation) {
        final FabricModelBakingContextDelegate context = new FabricModelBakingContextDelegate(this);

        final BakedModel bakedModel = specification.bake(context, modelBaker, function, modelState, resourceLocation);
        return new FabricBakedModelDelegate(bakedModel);
    }

    @Override
    public BakedModel bake(ModelBaker modelBaker, BlockModel blockModel, Function<Material, TextureAtlasSprite> function, ModelState modelState, Identifier resourceLocation, boolean bl) {
        final FabricModelBakingContextDelegate context = new FabricModelBakingContextDelegate(this);

        final BakedModel bakedModel = specification.bake(context, modelBaker, function, modelState, resourceLocation);
        return new FabricBakedModelDelegate(bakedModel);
    }
}
