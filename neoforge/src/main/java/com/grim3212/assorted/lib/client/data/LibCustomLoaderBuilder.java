package com.grim3212.assorted.lib.client.data;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.block.LibBlockStateModels;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;

/**
 * A {@link CustomLoaderBuilder} whose model json is read by both loaders: it writes Fabric's {@code
 * "fabric:type"} beside NeoForge's {@code "loader"}. With only {@code "loader"}, Fabric loads a
 * plain static model and logs nothing. Every custom loader model an Assorted mod generates goes
 * through this.
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
