package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationHolder;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ResolvableModel;
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
 * Adapts an {@link IModelSpecification} onto NeoForge's unbaked model pipeline: an {@link
 * AbstractUnbakedModel} carrying the json's {@link StandardModelParameters}, whose {@link
 * #geometry()} bakes into a {@link QuadCollection}.
 * <p> TODO(26.2): a model json loader can only contribute geometry, not a whole {@link
 * BlockStateModel}, so the specification is baked once and its parts flattened: one that varies
 * with the random source or {@link com.grim3212.assorted.lib.client.model.data.IBlockModelData}
 * draws its empty-data parts. Such models must be baked from the blockstate side ({@link
 * ForgeSpecificationBlockStateModel}, which wraps {@link ForgeBakedModelDelegate}).
 */
public final class ForgeModelGeometryToSpecificationPlatformDelegator<T extends IModelSpecification<T>> extends AbstractUnbakedModel implements IModelSpecificationHolder {

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
    public T getModelSpecification() {
        return this.delegate;
    }

    /**
     * Forwards to the specification so anything it resolves through the baker is discovered first.
     * The vanilla {@code parent} of this json is handled by {@link #parent()}; this covers the model
     * ids a specification reaches for on its own.
     */
    @Override
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        super.resolveDependencies(resolver);
        this.delegate.resolveDependencies(resolver);
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
