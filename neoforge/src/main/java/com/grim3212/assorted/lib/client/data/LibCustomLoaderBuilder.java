package com.grim3212.assorted.lib.client.data;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.block.LibBlockStateModels;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;

/**
 * A {@link CustomLoaderBuilder} whose model json is read by both loaders.
 * <p>
 * The generated model jsons are written once, by NeoForge datagen, and read by both loaders. NeoForge
 * reads a model's loader from {@code "loader"}; Fabric's model loading API reads it from
 * {@code "fabric:type"} and ignores {@code "loader"} entirely - so a json carrying only NeoForge's key
 * loads on Fabric as a plain, static model, with nothing in the log. Every custom loader model an
 * Assorted mod generates goes through this, which writes both.
 */
public abstract class LibCustomLoaderBuilder extends CustomLoaderBuilder {

    protected LibCustomLoaderBuilder(Identifier loaderId, boolean allowInlineElements) {
        super(loaderId, allowInlineElements);
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        json.addProperty(LibBlockStateModels.FABRIC_TYPE_KEY, this.loaderId.toString());
        return json;
    }
}
