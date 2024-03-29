package com.grim3212.assorted.lib.client.model.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.util.TransformationUtils;
import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public final class FabricPlatformModelLoaderPlatformDelegate<L extends IModelSpecificationLoader<S>, S extends IModelSpecification<S>> implements ModelResourceProvider {

    private final Gson gson;
    private final String name;

    public FabricPlatformModelLoaderPlatformDelegate(final ResourceLocation name, final L delegate) {
        this.name = name.toString();
        this.gson = (new GsonBuilder())
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                .registerTypeHierarchyAdapter(UnbakedModel.class, new FabricExtendedBlockModelDeserializer(this.name, delegate))
                .registerTypeHierarchyAdapter(BlockModel.class, new FabricExtendedBlockModelDeserializer(this.name, delegate))
                .registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer() {
                })
                .registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer() {
                })
                .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer() {
                })
                .registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer() {
                })
                .registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer() {
                })
                .registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer() {
                })
                .registerTypeAdapter(Transformation.class, new TransformationUtils.Deserializer())
                .create();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable UnbakedModel loadModelResource(final ResourceLocation resourceLocation, final ModelProviderContext modelProviderContext) throws ModelProviderException {
        try {
            final ResourceLocation target = new ResourceLocation(resourceLocation.getNamespace(), "models/" + resourceLocation.getPath() + ".json");

            final Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(target);
            if (resource.isEmpty())
                return null;

            final InputStream inputStream = resource.get().open();
            final InputStreamReader streamReader = new InputStreamReader(inputStream);

            final JsonElement specificationData = gson.fromJson(streamReader, JsonElement.class);

            streamReader.close();
            inputStream.close();

            if (!specificationData.isJsonObject())
                return null;

            final JsonObject modelSpecification = specificationData.getAsJsonObject();
            if (!modelSpecification.has("loader")) {
                if (modelSpecification.has("parent")) {
                    // If we have a parent model check if the parent model is of our loader
                    return getParentModel(new ResourceLocation(GsonHelper.getAsString(modelSpecification, "parent", "")));
                }
                return null;
            }

            if (!modelSpecification.get("loader").getAsString().equals(this.name))
                return null;

            return this.gson.fromJson(modelSpecification, UnbakedModel.class);
        } catch (IOException e) {
            throw new ModelProviderException("Failed to find and read resource", e);
        }
    }

    private @Nullable UnbakedModel getParentModel(ResourceLocation parentLocation) throws IOException {
        final ResourceLocation target = new ResourceLocation(parentLocation.getNamespace(), "models/" + parentLocation.getPath() + ".json");

        final Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(target);
        if (resource.isEmpty())
            return null;

        final InputStream inputStream = resource.get().open();
        final InputStreamReader streamReader = new InputStreamReader(inputStream);

        final JsonElement specificationData = gson.fromJson(streamReader, JsonElement.class);

        streamReader.close();
        inputStream.close();

        if (!specificationData.isJsonObject())
            return null;

        final JsonObject modelSpecification = specificationData.getAsJsonObject();
        if (!modelSpecification.has("loader"))
            return null;

        if (!modelSpecification.get("loader").getAsString().equals(this.name))
            return null;

        return this.gson.fromJson(modelSpecification, UnbakedModel.class);
    }
}
