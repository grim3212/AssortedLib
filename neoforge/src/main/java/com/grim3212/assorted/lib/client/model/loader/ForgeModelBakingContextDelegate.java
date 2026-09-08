package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.StandardModelParameters;

import java.util.Optional;

/**
 * {@code IGeometryBakingContext} has no NeoForge equivalent. The information it carried is split in
 * 26.2: the resolved texture references are a {@link TextureSlots} handed to
 * {@link net.minecraft.client.resources.model.geometry.UnbakedGeometry#bake} and the remaining top
 * level model properties are the {@link StandardModelParameters} the loader parsed out of the model
 * json, so this context is built from both.
 */
public class ForgeModelBakingContextDelegate implements IModelBakingContext {

    private final ModelBaker baker;
    private final TextureSlots textureSlots;
    private final StandardModelParameters parameters;

    public ForgeModelBakingContextDelegate(final ModelBaker baker, final TextureSlots textureSlots, final StandardModelParameters parameters) {
        this.baker = baker;
        this.textureSlots = textureSlots;
        this.parameters = parameters;
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
        final UnbakedModel.GuiLight guiLight = this.parameters.guiLight();
        return guiLight == null || guiLight.lightLikeBlock();
    }

    @Override
    public boolean useBlockLight() {
        final UnbakedModel.GuiLight guiLight = this.parameters.guiLight();
        return guiLight == null || guiLight.lightLikeBlock();
    }

    @Override
    public boolean useAmbientOcclusion() {
        final Boolean ambientOcclusion = this.parameters.ambientOcclusion();
        return ambientOcclusion == null || ambientOcclusion;
    }

    @Override
    public ItemTransforms getTransforms() {
        final ItemTransforms transforms = this.parameters.itemTransforms();
        return transforms == null ? ItemTransforms.NO_TRANSFORMS : transforms;
    }
}
