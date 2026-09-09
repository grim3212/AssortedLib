package com.grim3212.assorted.lib.client.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps every {@link UnbakedModel} the client loads addressable by its {@link Identifier}.
 * <p>
 * 1.20.1 got at these through the {@code ModelBakery} a mixin captured off the {@code ModelManager}.
 * In 26.2 the bakery is built, used and thrown away inside a private static
 * {@code ModelManager#loadModels} and never keeps the unbaked models around - {@code ModelBaker}
 * hands out {@link net.minecraft.client.resources.model.ResolvedModel}s and only while a bake is in
 * progress - so there is nothing left to shadow. Fabric's model loading plugin does expose the
 * models as they are loaded, so they are simply recorded here as they go past.
 * <p>
 * Entries survive a resource reload: a model that is loaded again replaces its entry, and one that
 * disappeared from the packs keeps its last known value rather than being dropped, because the
 * on-load event is the only signal there is.
 */
public final class FabricUnbakedModelTracker implements ModelLoadingPlugin {

    private static final Map<Identifier, UnbakedModel> LOADED_MODELS = new ConcurrentHashMap<>();

    private FabricUnbakedModelTracker() {
    }

    /**
     * Installs the tracker. Has to be called from the client initialiser.
     */
    public static void register() {
        ModelLoadingPlugin.register(new FabricUnbakedModelTracker());
    }

    /**
     * The unbaked model registered under the given location.
     *
     * @param unbakedModel The location of the model.
     * @return The model, or {@code null} if no model was loaded under that location.
     */
    public static @Nullable UnbakedModel getUnbakedModel(final Identifier unbakedModel) {
        return LOADED_MODELS.get(unbakedModel);
    }

    @Override
    public void initialize(final Context context) {
        context.modifyModelOnLoad().register((model, modelContext) -> {
            LOADED_MODELS.put(modelContext.id(), model);
            return model;
        });
    }
}
