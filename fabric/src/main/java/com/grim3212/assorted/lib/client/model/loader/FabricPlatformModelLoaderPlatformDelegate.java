package com.grim3212.assorted.lib.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;

/**
 * Registers an {@link IModelSpecificationLoader} with Fabric's {@link UnbakedModelDeserializer}
 * under an {@link Identifier}. A model json selects it with {@code "fabric:type"}, not
 * {@code "loader"}.
 */
public final class FabricPlatformModelLoaderPlatformDelegate<L extends IModelSpecificationLoader<S>, S extends IModelSpecification<S>> implements UnbakedModelDeserializer {

    private final FabricExtendedBlockModelDeserializer deserializer;

    public FabricPlatformModelLoaderPlatformDelegate(final Identifier name, final L delegate) {
        this.deserializer = new FabricExtendedBlockModelDeserializer(name, delegate);
    }

    @Override
    public UnbakedModel deserialize(final JsonObject modelContents, final JsonDeserializationContext deserializationContext) {
        return deserializer.deserializeModel(modelContents, deserializationContext);
    }
}
