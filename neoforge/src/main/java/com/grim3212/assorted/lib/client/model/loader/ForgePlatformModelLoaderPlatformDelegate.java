package com.grim3212.assorted.lib.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jetbrains.annotations.NotNull;

/**
 * {@code IGeometryLoader} became {@link UnbakedModelLoader}: it reads a whole
 * {@link net.minecraft.client.resources.model.UnbakedModel} rather than just the geometry, so the
 * standard top level model fields are parsed here with {@link StandardModelParameters#parse} and
 * handed to the delegator. Registration moved from {@code ModelEvent.RegisterGeometryLoaders} to
 * {@code ModelEvent.RegisterLoaders}, which is keyed by an {@code Identifier} rather than a string.
 */
public final class ForgePlatformModelLoaderPlatformDelegate<L extends IModelSpecificationLoader<T>, T extends IModelSpecification<T>>
        implements UnbakedModelLoader<ForgeModelGeometryToSpecificationPlatformDelegator<T>>, ResourceManagerReloadListener {

    private final L delegate;

    public ForgePlatformModelLoaderPlatformDelegate(final L delegate) {
        this.delegate = delegate;
    }

    @Override
    public ForgeModelGeometryToSpecificationPlatformDelegator<T> read(final JsonObject jsonObject, final JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new ForgeModelGeometryToSpecificationPlatformDelegator<>(
                StandardModelParameters.parse(jsonObject, deserializationContext),
                delegate.read(deserializationContext, jsonObject));
    }

    @Override
    public void onResourceManagerReload(final @NotNull ResourceManager p_10758_) {
        delegate.onResourceManagerReload(p_10758_);
    }
}
