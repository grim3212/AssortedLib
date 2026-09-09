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
 * Parses a model json into a {@link FabricExtendedBlockModel}.
 * <p>
 * The 1.20.1 version had to sniff the {@code "loader"} field itself and decide whether to hand back a
 * plain model or an extended one. Fabric does that dispatch now: a json names its loader with
 * {@code "fabric:type": "<namespace:path>"} and
 * {@link net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer} routes it to the
 * deserializer registered under that id, so by the time this runs the loader is already known and
 * the whole object belongs to it.
 * <p>
 * The vanilla half of the json - {@code parent}, {@code textures}, {@code display},
 * {@code gui_light}, {@code ambientocclusion} - is parsed by the vanilla deserializer, which is
 * invoked directly rather than through {@code context.deserialize} because the model gson dispatches
 * {@link UnbakedModel} back into Fabric's deserializer and would recurse.
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
