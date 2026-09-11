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
 * Keeps every {@link UnbakedModel} the client loads addressable by {@link Identifier}. Vanilla
 * keeps none past baking, so they are recorded as Fabric's model loading plugin hands them over. A
 * model gone from the packs after a reload keeps its last value: loading is the only signal there
 * is. Also declares specifications' model dependencies, see {@link #DEPENDENCY_MARKER}.
 */
public final class FabricUnbakedModelTracker implements ModelLoadingPlugin {

    private static final Map<Identifier, UnbakedModel> LOADED_MODELS = new ConcurrentHashMap<>();

    /**
     * The specification-backed models of the reload in progress, in load order. Rebuilt every reload
     * from {@link #initialize}, which Fabric calls once per model reload before any model is loaded.
     */
    private static final List<FabricExtendedBlockModel> SPECIFICATION_MODELS = new CopyOnWriteArrayList<>();

    /**
     * Declares the dependencies of every specification-backed model loaded this reload. Unlike
     * NeoForge, Fabric never asks an {@code UnbakedModel} for its dependencies, but it does feed
     * extra models to discovery; this one contributes no geometry and only marks those
     * dependencies, or {@link ModelBaker#getModel} would hand back the missing model.
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

    /** The unbaked model loaded under {@code unbakedModel}, or {@code null} if none. */
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
