package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.ForgeBlockModelDataPlatformDelegate;
import com.grim3212.assorted.lib.client.model.data.ForgeModelDataMapBuilderPlatformDelegate;
import com.grim3212.assorted.lib.client.model.data.ForgeModelPropertyPlatformDelegate;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.client.model.data.IModelDataKey;
import com.grim3212.assorted.lib.client.model.loader.ForgeBakedModelDelegate;
import com.grim3212.assorted.lib.platform.services.IClientModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ForgeClientModelHelper implements IClientModelHelper {
    private static final RandomSource RANDOM_SOURCE = new LegacyRandomSource(0);

    @Override
    public void requestModelDataRefresh(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != null && blockEntity.getLevel().getModelDataManager() != null) {
            blockEntity.getLevel().getModelDataManager().requestRefresh(blockEntity);
        }
    }

    @Override
    public IBlockModelData empty() {
        return new ForgeBlockModelDataPlatformDelegate(ModelData.EMPTY);
    }

    @Override
    public @NotNull IModelDataBuilder createNewModelDataBuilder() {
        return new ForgeModelDataMapBuilderPlatformDelegate();
    }

    @Override
    public @NotNull <T> IModelDataKey<T> createNewModelDataKey() {
        return new ForgeModelPropertyPlatformDelegate<>(new ModelProperty<>());
    }

    // TODO(26.2): there is no way left to look an UnbakedModel up by id outside of baking.
    //  ModelBakery no longer exposes the models it was built from, and the only accessor is
    //  ModelBaker#getModel(Identifier), which hands back a ResolvedModel and only exists while a
    //  model is being baked - which is exactly the context ForgeModelBakingContextDelegate already
    //  has. Callers that need a model by id have to go through a baking context; failing loudly here
    //  beats handing back a stand-in model that would silently render as nothing.
    @Override
    public UnbakedModel getUnbakedModel(Identifier unbakedModel) {
        throw new UnsupportedOperationException("Unbaked models can only be resolved through a ModelBaker in 26.2: " + unbakedModel);
    }

    @Override
    public BlockStateModel adaptToPlatform(final BlockStateModel model) {
        if (model instanceof ForgeBakedModelDelegate)
            return model;

        return new ForgeBakedModelDelegate(model);
    }

    @Override
    public boolean canRenderInType(final BlockState blockState, final RenderType renderType) {
        final BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        return getRenderTypesFor(model, blockState, RANDOM_SOURCE, empty()).contains(renderType);
    }

    // TODO(26.2): a fluid has no RenderType any more. ItemBlockRenderTypes is gone and a fluid's pass
    //  is the ChunkSectionLayer on its baked FluidModel, which is a terrain bucket rather than a
    //  RenderType and has no RenderType counterpart to compare against. Answering false keeps callers
    //  from drawing a fluid in a pass this can no longer confirm; a caller that has to know should
    //  read Minecraft#getModelManager#getFluidStateModelSet#get(state)#layer() itself.
    @Override
    public boolean canRenderInType(final FluidState fluidState, final RenderType renderType) {
        return false;
    }

    // TODO(26.2): a block model no longer reports the render types it draws in. Terrain passes are
    //  ChunkSectionLayers picked per quad from BakedQuad.MaterialInfo, and the only RenderType a quad
    //  still carries is the item sheet it would be drawn on outside the world, so that is what this
    //  collects. It is the right answer for the item/BEWLR style call sites this was written for, and
    //  no longer comparable to a terrain layer.
    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final BlockStateModel model, final BlockState state, final RandomSource rand, final IBlockModelData data) {
        if (!(data instanceof ForgeBlockModelDataPlatformDelegate)) {
            throw new IllegalArgumentException("data must be an instance of ForgeBlockModelData");
        }

        // BlockStateModel#collectParts(RandomSource, List) is deprecated in favour of the level and
        // position aware overload, which there is nothing to feed here. Adapting the model first -
        // a no-op for one that already is - gives the data aware entry point instead.
        final List<BlockStateModelPart> parts = new ArrayList<>();
        if (adaptToPlatform(model) instanceof IDataAwareBakedModel dataAwareBakedModel) {
            dataAwareBakedModel.collectParts(rand, data, parts);
        }

        final Set<RenderType> renderTypes = new LinkedHashSet<>();
        for (BlockStateModelPart part : parts) {
            collectRenderTypes(part.getQuads(null), renderTypes);
            for (Direction direction : Direction.values()) {
                collectRenderTypes(part.getQuads(direction), renderTypes);
            }
        }

        return renderTypes;
    }

    private static void collectRenderTypes(final List<BakedQuad> quads, final Set<RenderType> output) {
        for (BakedQuad quad : quads) {
            output.add(quad.materialInfo().itemRenderType());
        }
    }

    @Override
    public RenderType getItemUnlitUnsortedTranslucentRenderType() {
        return NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get();
    }

    @Override
    public RenderType getItemUnsortedTranslucentRenderType() {
        return NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get();
    }
}
