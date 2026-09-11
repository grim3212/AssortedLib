package com.grim3212.assorted.lib.client.model.loaders.context;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * An {@link IModelBakingContext} answered from a {@link ResolvedModel}, for a blockstate baking a
 * specification model it only knows by location. It reads the {@code getTop*} accessors, so ambient
 * occlusion, gui light and transforms come from wherever the parent chain declared them, not only
 * from the leaf json.
 */
public final class ResolvedModelBakingContext implements IModelBakingContext {

    private final ModelBaker baker;
    private final ResolvedModel model;
    private final TextureSlots textureSlots;

    public ResolvedModelBakingContext(final ModelBaker baker, final ResolvedModel model, final TextureSlots textureSlots) {
        this.baker = baker;
        this.model = model;
        this.textureSlots = textureSlots;
    }

    @Override
    public UnbakedModel getUnbakedModel(final Identifier unbakedModel) {
        return this.baker.getModel(unbakedModel).wrapped();
    }

    @Override
    public Optional<Material> getMaterial(final String name) {
        return Optional.ofNullable(this.textureSlots.getMaterial(name));
    }

    @Override
    public boolean isGui3d() {
        return this.model.getTopGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useBlockLight() {
        return this.model.getTopGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.model.getTopAmbientOcclusion();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.model.getTopTransforms();
    }
}
