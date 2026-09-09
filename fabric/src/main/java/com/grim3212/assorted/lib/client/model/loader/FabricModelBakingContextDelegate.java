package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.client.model.FabricUnbakedModelTracker;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class FabricModelBakingContextDelegate implements IModelBakingContext {

    private final FabricExtendedBlockModel source;

    /**
     * The already resolved texture slots for the model being baked. The parent chain is walked by the
     * model pipeline before {@code UnbakedGeometry#bake} is called, so this is what the old
     * {@code BlockModel#getMaterial(String)} used to answer, only without needing the parent models.
     */
    private final TextureSlots textureSlots;

    public FabricModelBakingContextDelegate(final FabricExtendedBlockModel source, final TextureSlots textureSlots) {
        this.source = source;
        this.textureSlots = textureSlots;
    }

    @Override
    public UnbakedModel getUnbakedModel(final Identifier unbakedModel) {
        return FabricUnbakedModelTracker.getUnbakedModel(unbakedModel);
    }

    @Override
    public Optional<Material> getMaterial(final String name) {
        return Optional.ofNullable(textureSlots.getMaterial(name));
    }

    @Override
    public boolean isGui3d() {
        return guiLight().lightLikeBlock();
    }

    @Override
    public boolean useBlockLight() {
        return guiLight().lightLikeBlock();
    }

    @Override
    public boolean useAmbientOcclusion() {
        final Boolean ambientOcclusion = source.ambientOcclusion();
        return ambientOcclusion != null ? ambientOcclusion : ResolvedModel.DEFAULT_AMBIENT_OCCLUSION;
    }

    @Override
    public ItemTransforms getTransforms() {
        final ItemTransforms transforms = source.transforms();
        return transforms != null ? transforms : ItemTransforms.NO_TRANSFORMS;
    }

    private UnbakedModel.GuiLight guiLight() {
        final UnbakedModel.GuiLight guiLight = source.guiLight();
        return guiLight != null ? guiLight : ResolvedModel.DEFAULT_GUI_LIGHT;
    }
}
