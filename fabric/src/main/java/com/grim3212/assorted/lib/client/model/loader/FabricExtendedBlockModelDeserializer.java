package com.grim3212.assorted.lib.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.resources.Identifier;

/**
 * Parses a model json into a {@link FabricExtendedBlockModel}. Fabric has already dispatched on
 * {@code "fabric:type"}, so the whole object belongs to this loader. The vanilla half
 * ({@code parent}, {@code textures}, {@code display}, ...) goes to the vanilla deserializer
 * directly: {@code context.deserialize} would dispatch back into Fabric's deserializer and recurse.
 */
public class FabricExtendedBlockModelDeserializer extends CuboidModel.Deserializer {

    private final Identifier name;
    private final IModelSpecificationLoader<?> delegate;

    public FabricExtendedBlockModelDeserializer(final Identifier name, final IModelSpecificationLoader<?> delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    public UnbakedModel deserializeModel(final JsonObject modelContents, final JsonDeserializationContext deserializationContext) {
        final CuboidModel base = super.deserialize(modelContents, CuboidModel.class, deserializationContext);

        final IModelSpecification<?> specification = delegate.read(deserializationContext, modelContents);
        if (specification == null)
            throw new JsonParseException("The model loader " + name + " did not produce a model specification");

        return new FabricExtendedBlockModel(base, specification);
    }
}
