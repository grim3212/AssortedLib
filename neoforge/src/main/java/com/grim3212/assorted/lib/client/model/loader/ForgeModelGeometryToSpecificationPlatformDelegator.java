package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.AbstractUnbakedModel;
import net.neoforged.neoforge.client.model.ExtendedUnbakedGeometry;
import net.neoforged.neoforge.client.model.StandardModelParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapts an {@link IModelSpecification} onto the 26.2 unbaked model pipeline.
 * <p>
 * {@code IUnbakedGeometry} is gone. A custom model json is read into an {@link
 * net.minecraft.client.resources.model.UnbakedModel} now (see
 * {@link ForgePlatformModelLoaderPlatformDelegate}), whose {@link #geometry()} bakes into a
 * {@link QuadCollection}; the standard top level model properties are carried by
 * {@link StandardModelParameters} through {@link AbstractUnbakedModel}.
 * <p>
 * TODO(26.2): a model json loader can no longer contribute a whole {@link BlockStateModel}. The old
 *  {@code IUnbakedGeometry#bake} returned a {@code BakedModel} - the complete model for the block -
 *  so a specification could return something dynamic. In 26.2 the model json layer only produces
 *  geometry: {@code UnbakedGeometry#bake} must hand back a {@link QuadCollection}, and the object
 *  that owns per-render-call behaviour ({@link BlockStateModel}) is chosen in the *blockstate* json
 *  and registered separately through {@code RegisterBlockStateModels} as a
 *  {@code CustomUnbakedBlockStateModel} with a {@code MapCodec}, which has no
 *  {@code JsonDeserializationContext} to hand an {@link com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader}.
 *  The specification is therefore baked once at bake time and its parts are flattened into a single
 *  quad collection here: a specification whose {@code collectParts} varies with the random source or
 *  with {@link com.grim3212.assorted.lib.client.model.data.IBlockModelData} collapses to the parts it
 *  produces for an unseeded random and empty data. Models that need that behaviour have to be wrapped
 *  with {@link ForgeBakedModelDelegate} from the blockstate side instead.
 */
public final class ForgeModelGeometryToSpecificationPlatformDelegator<T extends IModelSpecification<T>> extends AbstractUnbakedModel {

    private static final Identifier UNKNOWN_MODEL = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "unknown_model");

    private final T delegate;

    public ForgeModelGeometryToSpecificationPlatformDelegator(final StandardModelParameters parameters, final T delegate) {
        super(parameters);
        this.delegate = delegate;
    }

    public T getDelegate() {
        return this.delegate;
    }

    @Override
    public UnbakedGeometry geometry() {
        return (ExtendedUnbakedGeometry) (textureSlots, baker, modelState, debugName, additionalProperties) -> {
            final ForgeModelBakingContextDelegate context = new ForgeModelBakingContextDelegate(baker, textureSlots, this.parameters);

            Identifier modelLocation = Identifier.tryParse(debugName.debugName());
            if (modelLocation == null) {
                modelLocation = UNKNOWN_MODEL;
            }

            final BlockStateModel model = this.delegate.bake(context, baker, modelState, modelLocation);

            final List<BlockStateModelPart> parts = new ArrayList<>();
            // Baking has no level or position, so this is the same empty context DynamicBlockStateModel
            // feeds the level aware overload with.
            model.collectParts(BlockAndTintGetter.EMPTY, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), RandomSource.create(), parts);

            final QuadCollection.Builder builder = new QuadCollection.Builder();
            for (BlockStateModelPart part : parts) {
                part.getQuads(null).forEach(builder::addUnculledFace);
                for (Direction direction : Direction.values()) {
                    part.getQuads(direction).forEach(quad -> builder.addCulledFace(direction, quad));
                }
            }

            return builder.build();
        };
    }
}
