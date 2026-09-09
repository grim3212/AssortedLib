package com.grim3212.assorted.lib.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;

/**
 * Bridges an {@link IModelSpecificationLoader} onto Fabric's model deserializer registry.
 * <p>
 * 1.20.1 had to implement {@code ModelResourceProvider}, open the model file itself, build a private
 * gson with the whole vanilla model type adapter set and re-parse everything just to find out
 * whether the json's {@code "loader"} was this one. All of that is handled upstream now: a loader is
 * registered against an {@link Identifier} with
 * {@link UnbakedModelDeserializer#register(Identifier, UnbakedModelDeserializer)}, a model json opts
 * in with {@code "fabric:type": "<that id>"}, and the deserializer is called with the already parsed
 * object and the model gson's own deserialization context.
 * <p>
 * Note for resource packs: the field a model declares its loader with is {@code fabric:type}, not
 * {@code loader}.
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
