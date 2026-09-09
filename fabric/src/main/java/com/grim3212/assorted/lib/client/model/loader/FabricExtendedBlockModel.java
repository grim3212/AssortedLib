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
 * An {@link UnbakedModel} whose geometry is produced by an {@link IModelSpecification} instead of by
 * a list of json elements.
 * <p>
 * 1.20.1 did this by subclassing {@code BlockModel} and overriding its two {@code bake} methods, so
 * a specification could return an entire {@code BakedModel}. In 26.2 an {@code UnbakedModel} is a
 * plain data carrier - textures, parent, display, ambient occlusion, gui light and one
 * {@link UnbakedGeometry} - and the model json is only ever half of a model: the blockstate json
 * picks a {@link BlockStateModel.UnbakedRoot} which then bakes the referenced model's geometry. The
 * specification is therefore hooked in as the geometry, and everything else is answered from the
 * vanilla half of the same json.
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
     * Marks the models the specification resolves through the baker while baking, so discovery picks
     * them up. The vanilla {@code parent} of the json is walked separately, off {@link #parent()}.
     * <p>
     * This is not an override: {@code UnbakedModel} only carries {@code resolveDependencies} on
     * NeoForge, where it is an extension interface. On Fabric nothing calls it, so
     * {@link com.grim3212.assorted.lib.client.model.FabricUnbakedModelTracker} collects these models
     * as they load and drives them from an extra model instead.
     */
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        specification.resolveDependencies(resolver);
    }

    // TODO(26.2): the specification's BlockStateModel is flattened into a QuadCollection here.
    //  What is lost: a BlockStateModel can hand out several BlockStateModelParts, each with its own
    //  ambient occlusion flag and particle material, and an IDataAwareBakedModel can pick different
    //  parts per render pass. Model json geometry cannot express any of that - UnbakedGeometry#bake
    //  returns one QuadCollection - so the parts are merged and only their quads survive.
    //  How to get it back: a model that genuinely needs to stay a BlockStateModel has to be declared
    //  in the *blockstate* json instead, as a
    //  net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel registered by
    //  MapCodec. That is a codec based registration, so it cannot be driven by the gson based
    //  IModelSpecificationLoader this class is wired to.
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
