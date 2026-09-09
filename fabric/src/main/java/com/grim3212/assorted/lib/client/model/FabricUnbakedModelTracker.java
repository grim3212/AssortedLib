package com.grim3212.assorted.lib.client.model;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.model.loader.FabricExtendedBlockModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * <p>
 * This is also where a specification's model dependencies get declared - see
 * {@link #DEPENDENCY_MARKER}.
 */
public final class FabricUnbakedModelTracker implements ModelLoadingPlugin {

    private static final Map<Identifier, UnbakedModel> LOADED_MODELS = new ConcurrentHashMap<>();

    /**
     * The specification-backed models of the reload in progress, in load order. Rebuilt every reload
     * from {@link #initialize}, which Fabric calls once per model reload before any model is loaded.
     */
    private static final List<FabricExtendedBlockModel> SPECIFICATION_MODELS = new CopyOnWriteArrayList<>();

    /**
     * Declares the dependencies of every specification-backed model loaded this reload.
     * <p>
     * NeoForge asks each {@code UnbakedModel} for its dependencies during discovery; vanilla does not,
     * and Fabric does not add that hook, so on this side there is no per-model callback to answer
     * from. What Fabric does offer is extra models, which are fed to the same {@code ModelDiscovery} -
     * so one extra model that marks every specification's dependencies gets them discovered and into
     * the bakery's resolved map, which is the only place {@link ModelBaker#getModel} looks. Without
     * it a specification that resolves another model while baking gets the missing model back.
     * <p>
     * It contributes no geometry of its own; it exists purely for the {@code resolveDependencies}
     * call, which the discovery pass makes after every model json has been read.
     */
    private static final UnbakedExtraModel<Object> DEPENDENCY_MARKER = new UnbakedExtraModel<>() {
        @Override
        public void resolveDependencies(final ResolvableModel.Resolver resolver) {
            for (final FabricExtendedBlockModel model : SPECIFICATION_MODELS) {
                model.resolveDependencies(resolver);
            }
        }

        @Override
        public Object bake(final ModelBaker baker) {
            return this;
        }
    };

    private static final ExtraModelKey<Object> DEPENDENCY_KEY = ExtraModelKey.create(() -> LibConstants.MOD_ID + ":specification_dependencies");

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
        SPECIFICATION_MODELS.clear();

        context.modifyModelOnLoad().register((model, modelContext) -> {
            LOADED_MODELS.put(modelContext.id(), model);
            if (model instanceof FabricExtendedBlockModel specificationModel) {
                SPECIFICATION_MODELS.add(specificationModel);
            }
            return model;
        });

        context.addModel(DEPENDENCY_KEY, DEPENDENCY_MARKER);
    }
}
