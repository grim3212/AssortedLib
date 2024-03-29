package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.client.model.IBlockModelAccessor;
import com.grim3212.assorted.lib.client.model.IModelBakeryAccessor;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class FabricModelBakingContextDelegate implements IModelBakingContext {

    private final FabricExtendedBlockModel source;
    private final IBlockModelAccessor sourceAccessor;

    public FabricModelBakingContextDelegate(final FabricExtendedBlockModel source) {
        this.source = source;
        this.sourceAccessor = (IBlockModelAccessor) source;
    }

    @Override
    public UnbakedModel getUnbakedModel(final ResourceLocation unbakedModel) {
        final IModelBakeryAccessor modelBakeryAccessor = (IModelBakeryAccessor) Minecraft.getInstance().getModelManager();
        return modelBakeryAccessor.getModelBakery().getModel(unbakedModel);
    }

    @Override
    public Optional<Material> getMaterial(final String name) {
        if (source.hasTexture(name)) {
            return Optional.of(source.getMaterial(name));
        }

        return Optional.empty();
    }

    @Override
    public boolean isGui3d() {
        return sourceAccessor.guiLight().lightLikeBlock();
    }

    @Override
    public boolean useBlockLight() {
        return sourceAccessor.guiLight().lightLikeBlock();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return sourceAccessor.usesAmbientOcclusion();
    }

    @Override
    public ItemTransforms getTransforms() {
        return sourceAccessor.transforms();
    }

    @Override
    public ItemOverrides getItemOverrides(ModelBaker baker) {
        return sourceAccessor.overrides(baker, source);
    }
}
