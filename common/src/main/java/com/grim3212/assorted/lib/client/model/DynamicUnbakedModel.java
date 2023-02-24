package com.grim3212.assorted.lib.client.model;

import com.grim3212.assorted.lib.mixin.client.AccessorBlockModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class DynamicUnbakedModel extends BlockModel {

    private final BlockModel originalModel;

    public DynamicUnbakedModel(BlockModel originalModel) {
        super(((AccessorBlockModel) originalModel).assortedlib_getParentLocation(), originalModel.getElements(), ((AccessorBlockModel) originalModel).assortedlib_getTextureMap(), originalModel.hasAmbientOcclusion(), originalModel.getGuiLight(), originalModel.getTransforms(), originalModel.getOverrides());
        this.originalModel = originalModel;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
        this.originalModel.resolveParents(function);
    }


    @Nullable
    @Override
    public abstract BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location);
}
