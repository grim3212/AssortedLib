package com.grim3212.assorted.lib.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

public final class ForgePlatformModelLoaderPlatformDelegate<L extends IModelSpecificationLoader<T>, T extends IModelSpecification<T>>
        implements IGeometryLoader<ForgeModelGeometryToSpecificationPlatformDelegator<T>>, ResourceManagerReloadListener {

    private final L delegate;

    public ForgePlatformModelLoaderPlatformDelegate(final L delegate) {
        this.delegate = delegate;
    }

    @Override
    public ForgeModelGeometryToSpecificationPlatformDelegator<T> read(final JsonObject jsonObject, final JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new ForgeModelGeometryToSpecificationPlatformDelegator<>(delegate.read(deserializationContext, jsonObject));
    }

    @Override
    public void onResourceManagerReload(final @NotNull ResourceManager p_10758_) {
        delegate.onResourceManagerReload(p_10758_);
    }
}
