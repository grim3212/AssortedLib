package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationHolder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link UnbakedModel} whose geometry comes from an {@link IModelSpecification} instead of json
 * elements. A model json only supplies geometry (the blockstate picks the
 * {@link BlockStateModel.UnbakedRoot}), so the specification is hooked in as the
 * {@link UnbakedGeometry} and the rest is read from the vanilla half of the same json.
 */
public class FabricExtendedBlockModel implements UnbakedModel, IModelSpecificationHolder {

    private static final Identifier UNKNOWN_MODEL_LOCATION = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "unknown_model");

    /**
     * The seed the specification is baked with. Baking happens once, so the parts a specification
     * collects have to be taken from a fixed random source.
     */
    private static final long BAKE_SEED = 42L;

    private final CuboidModel base;
    private final IModelSpecification<?> specification;

    public FabricExtendedBlockModel(final CuboidModel base, final IModelSpecification<?> specification) {
        this.base = base;
        this.specification = specification;
    }

    @Override
    public @Nullable Boolean ambientOcclusion() {
        return base.ambientOcclusion();
    }

    @Override
    public @Nullable GuiLight guiLight() {
        return base.guiLight();
    }

    @Override
    public @Nullable ItemTransforms transforms() {
        return base.transforms();
    }

    @Override
    public TextureSlots.Data textureSlots() {
        return base.textureSlots();
    }

    @Override
    public @Nullable Identifier parent() {
        return base.parent();
    }

    @Override
    public IModelSpecification<?> getModelSpecification() {
        return this.specification;
    }

    @Override
    public UnbakedGeometry geometry() {
        return this::bakeGeometry;
    }

    /**
     * Marks the models the specification resolves through the baker, so discovery finds them; the
     * json's {@code parent} is walked separately. Not an override: only NeoForge declares this on
     * {@code UnbakedModel}, so on Fabric
     * {@link com.grim3212.assorted.lib.client.model.FabricUnbakedModelTracker} calls it from an
     * extra model.
     */
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        specification.resolveDependencies(resolver);
    }

    // TODO(26.2): the specification's BlockStateModel is flattened into one QuadCollection here,
    //  losing per-part ambient occlusion and particles and any parts chosen per render. Model json
    //  geometry cannot express more; a model that needs them must go through the blockstate json
    //  instead (the assortedlib:specification type, FabricSpecificationBlockStateModel).
    private QuadCollection bakeGeometry(final TextureSlots textureSlots, final ModelBaker baker, final ModelState modelState, final ModelDebugName debugName) {
        final Identifier modelLocation = resolveModelLocation(debugName);
        final FabricModelBakingContextDelegate context = new FabricModelBakingContextDelegate(this, textureSlots);

        final BlockStateModel model = specification.bake(context, baker, modelState, modelLocation);

        final List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(BAKE_SEED), parts);

        final QuadCollection.Builder builder = new QuadCollection.Builder();
        for (final BlockStateModelPart part : parts) {
            for (final BakedQuad quad : part.getQuads(null)) {
                builder.addUnculledFace(quad);
            }

            for (final Direction direction : Direction.values()) {
                for (final BakedQuad quad : part.getQuads(direction)) {
                    builder.addCulledFace(direction, quad);
                }
            }
        }

        return builder.build();
    }

    /**
     * The model's own location is not handed to an {@link UnbakedGeometry}; only the debug name is,
     * which for a resource loaded model is its location printed back out.
     */
    private static Identifier resolveModelLocation(final ModelDebugName debugName) {
        final Identifier parsed = Identifier.tryParse(debugName.debugName());
        return parsed != null ? parsed : UNKNOWN_MODEL_LOCATION;
    }
}
