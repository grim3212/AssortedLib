package com.grim3212.assorted.lib.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataKey;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * A model made up of several named child models drawn on top of each other.
 * <p>
 * Children are model references rather than inline json now. 26.2 loads json models as
 * {@link net.minecraft.client.resources.model.cuboid.CuboidModel} records through a private Gson whose
 * element and face adapters ({@code CuboidModelElement.Deserializer}, {@code CuboidFace.Deserializer})
 * are package private, so an inline child object cannot be parsed from a foreign deserialization
 * context; a model id resolved through {@link ModelBaker#getModel(Identifier)} can, and matches how
 * vanilla composites reference their parts.
 */
public class CombiningModel implements IModelSpecification<CombiningModel> {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableMap<String, Identifier> children;
    private final boolean logWarning;

    public CombiningModel(ImmutableMap<String, Identifier> children) {
        this(children, false);
    }

    private CombiningModel(ImmutableMap<String, Identifier> children, boolean logWarning) {
        this.children = children;
        this.logWarning = logWarning;
    }

    @Override
    public BlockStateModel bake(IModelBakingContext context, ModelBaker baker, ModelState modelState, Identifier modelLocation) {
        if (logWarning)
            LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated \"parts\" field in its composite model instead of \"children\".");

        ModelDebugName debugName = modelLocation::toString;
        Material particle = context.getMaterial("particle").orElse(null);
        Material.Baked bakedParticle = particle != null ? baker.materials().get(particle, debugName) : baker.materials().reportMissingReference("particle", debugName);

        var bakedPartsBuilder = ImmutableMap.<String, BlockStateModelPart>builder();
        for (var entry : children.entrySet()) {
            bakedPartsBuilder.put(entry.getKey(), SimpleModelWrapper.bake(baker, entry.getValue(), modelState));
        }

        return new Baked(bakedParticle, bakedPartsBuilder.build());
    }

    public static class Baked implements IDataAwareBakedModel {
        private final Material.Baked particle;
        private final ImmutableMap<String, BlockStateModelPart> children;

        public Baked(Material.Baked particle, ImmutableMap<String, BlockStateModelPart> children) {
            this.particle = particle;
            this.children = children;
        }

        public static Builder builder(Material.Baked particle) {
            return new Builder(particle);
        }

        @Override
        public void collectParts(final @NotNull RandomSource random, final @NotNull IBlockModelData extraData, final @NotNull List<BlockStateModelPart> output) {
            // Every child is drawn; there is no per render type filtering any more, the section
            // compiler buckets the individual quads by BakedQuad.MaterialInfo#layer.
            output.addAll(children.values());
        }

        // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its
        // patched jar; vanilla still declares these abstract, so they have to be implemented here.
        @SuppressWarnings("deprecation")
        @Override
        public Material.Baked particleMaterial() {
            return particle;
        }

        @SuppressWarnings("deprecation")
        @Override
        public @BakedQuad.MaterialFlags int materialFlags() {
            int flags = 0;
            for (BlockStateModelPart part : children.values()) {
                flags |= part.materialFlags();
            }
            return flags;
        }

        @Nullable
        public BlockStateModelPart getPart(String name) {
            return children.get(name);
        }

        public static class Builder {
            private final List<BlockStateModelPart> children = new ArrayList<>();
            private Material.Baked particle;

            private Builder(Material.Baked particle) {
                this.particle = particle;
            }

            public Builder addLayer(BlockStateModelPart part) {
                children.add(part);
                return this;
            }

            public Builder setParticle(Material.Baked particle) {
                this.particle = particle;
                return this;
            }

            public BlockStateModel build() {
                var childrenBuilder = ImmutableMap.<String, BlockStateModelPart>builder();
                int i = 0;
                for (var part : this.children) {
                    childrenBuilder.put("model_" + (i++), part);
                }
                return new Baked(particle, childrenBuilder.build());
            }
        }

    }

    /**
     * A model data container which stores data for child components.
     */
    public static class Data {
        public static final IModelDataKey<Data> PROPERTY = IModelDataKey.create();

        private final Map<String, IBlockModelData> partData;

        private Data(Map<String, IBlockModelData> partData) {
            this.partData = partData;
        }

        /**
         * Helper to get the data from a {@link IBlockModelData} instance.
         *
         * @param modelData The object to get data from
         * @param name      The name of the part to get data for
         * @return The data for the part, or the one passed in if not found
         */
        public static IBlockModelData resolve(IBlockModelData modelData, String name) {
            var compositeData = modelData.getData(PROPERTY);
            if (compositeData == null)
                return modelData;
            var partData = compositeData.get(name);
            return partData != null ? partData : modelData;
        }

        public static Builder builder() {
            return new Builder();
        }

        @Nullable
        public IBlockModelData get(String name) {
            return partData.get(name);
        }

        public static final class Builder {
            private final Map<String, IBlockModelData> partData = new IdentityHashMap<>();

            public Builder with(String name, IBlockModelData data) {
                partData.put(name, data);
                return this;
            }

            public Data build() {
                return new Data(partData);
            }
        }
    }

    public static final class Loader implements IModelSpecificationLoader<CombiningModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public CombiningModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) {
            ImmutableMap.Builder<String, Identifier> childrenBuilder = ImmutableMap.builder();
            readChildren(jsonObject, "children", childrenBuilder, false);
            boolean logWarning = readChildren(jsonObject, "parts", childrenBuilder, true);

            var children = childrenBuilder.build();
            if (children.isEmpty())
                throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");

            return new CombiningModel(children, logWarning);
        }

        private boolean readChildren(JsonObject jsonObject, String name, ImmutableMap.Builder<String, Identifier> children, boolean logWarning) {
            if (!jsonObject.has(name))
                return false;

            var childrenJsonObject = jsonObject.getAsJsonObject(name);
            for (Map.Entry<String, JsonElement> entry : childrenJsonObject.entrySet()) {
                children.put(entry.getKey(), Identifier.parse(entry.getValue().getAsString()));
            }
            return logWarning;
        }
    }
}
