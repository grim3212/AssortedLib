package com.grim3212.assorted.lib.client.model.loaders.context;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

public class SimpleModelBakingContext implements IModelBakingContext {
    private final Map<String, Material> materials;
    private final boolean isGui3d;
    private final boolean useBlockLight;
    private final boolean useAmbientOcclusion;
    private final ItemTransforms itemTransforms;

    public SimpleModelBakingContext(final Map<String, Material> materials, final boolean isGui3d, final boolean useBlockLight, final boolean useAmbientOcclusion, final ItemTransforms itemTransforms) {
        this.materials = materials;
        this.isGui3d = isGui3d;
        this.useBlockLight = useBlockLight;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.itemTransforms = itemTransforms;
    }

    @Override
    public UnbakedModel getUnbakedModel(final Identifier unbakedModel) {
        return ClientServices.MODELS.getUnbakedModel(unbakedModel);
    }

    @Override
    public Optional<Material> getMaterial(final String name) {
        return Optional.ofNullable(materials.get(name));
    }

    @Override
    public boolean isGui3d() {
        return isGui3d;
    }

    @Override
    public boolean useBlockLight() {
        return useBlockLight;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return useAmbientOcclusion;
    }

    @Override
    public ItemTransforms getTransforms() {
        return itemTransforms;
    }


    public static final class SimpleModelBakingContextBuilder {
        private final Map<String, Material> materials = Maps.newHashMap();
        private boolean isGui3d = false;
        private boolean useBlockLight = false;
        private boolean useAmbientOcclusion = false;
        private ItemTransforms itemTransforms = ItemTransforms.NO_TRANSFORMS;

        private SimpleModelBakingContextBuilder() {
        }

        public static SimpleModelBakingContextBuilder builder() {
            return new SimpleModelBakingContextBuilder();
        }

        public SimpleModelBakingContextBuilder withMaterial(final String name, final Material material) {
            this.materials.put(name, material);
            return this;
        }

        public SimpleModelBakingContextBuilder withMaterials(Map<String, Material> materials) {
            this.materials.putAll(materials);
            return this;
        }

        public SimpleModelBakingContextBuilder withIsGui3d(boolean isGui3d) {
            this.isGui3d = isGui3d;
            return this;
        }

        public SimpleModelBakingContextBuilder withUseBlockLight(boolean useBlockLight) {
            this.useBlockLight = useBlockLight;
            return this;
        }

        public SimpleModelBakingContextBuilder withUseAmbientOcclusion(boolean useAmbientOcclusion) {
            this.useAmbientOcclusion = useAmbientOcclusion;
            return this;
        }

        public SimpleModelBakingContextBuilder withItemTransforms(ItemTransforms itemTransforms) {
            this.itemTransforms = itemTransforms;
            return this;
        }

        public SimpleModelBakingContextBuilder but() {
            return builder().withMaterials(materials).withIsGui3d(isGui3d).withUseBlockLight(useBlockLight).withUseAmbientOcclusion(useAmbientOcclusion).withItemTransforms(itemTransforms);
        }

        public SimpleModelBakingContext build() {
            return new SimpleModelBakingContext(materials, isGui3d, useBlockLight, useAmbientOcclusion, itemTransforms);
        }
    }
}
