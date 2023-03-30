package com.grim3212.assorted.lib.client.model.loader;

import com.google.gson.*;
import com.grim3212.assorted.lib.client.model.IBlockModelAccessor;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class FabricExtendedBlockModelDeserializer extends BlockModel.Deserializer {

    private final String name;
    private final IModelSpecificationLoader<?> delegate;

    public FabricExtendedBlockModelDeserializer(final String name, final IModelSpecificationLoader<?> delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    public BlockModel deserialize(JsonElement element, Type targetType, JsonDeserializationContext deserializationContext) throws JsonParseException {
        if (!element.isJsonObject())
            throw new JsonSyntaxException("Model needs to be object");

        final BlockModel model = super.deserialize(element, targetType, deserializationContext);
        if (model == null)
            return null;

        JsonObject jsonobject = element.getAsJsonObject();
        final IModelSpecification<?> geometry = deserializeGeometry(deserializationContext, jsonobject);

        if (geometry != null) {
            return new FabricExtendedBlockModel(
                    (IBlockModelAccessor) model,
                    geometry
            );
        }

        return model;
    }

    @Nullable
    public IModelSpecification<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) throws JsonParseException {
        if (!object.has("loader"))
            return null;

        if (!object.get("loader").getAsString().equals(name.toString()))
            return null;

        return delegate.read(deserializationContext, object);
    }
}
