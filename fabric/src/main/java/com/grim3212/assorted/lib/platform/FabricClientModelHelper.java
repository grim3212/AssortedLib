package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.client.model.IModelBakeryAccessor;
import com.grim3212.assorted.lib.client.model.RenderTypeGroup;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.*;
import com.grim3212.assorted.lib.client.model.loader.FabricBakedModelDelegate;
import com.grim3212.assorted.lib.platform.services.IClientModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class FabricClientModelHelper implements IClientModelHelper {
    @Override
    public void requestModelDataRefresh(BlockEntity blockEntity) {
        // NO-OP
    }

    private static final IBlockModelData EMPTY = new ModelDataBuilder().build();

    @Override
    public IBlockModelData empty() {
        return EMPTY;
    }

    @Override
    public @NotNull IModelDataBuilder createNewModelDataBuilder() {
        return new ModelDataBuilder();
    }

    @Override
    public @NotNull <T> IModelDataKey<T> createNewModelDataKey() {
        return new ModelDataKey<>();
    }

    @Override
    public UnbakedModel getUnbakedModel(ResourceLocation unbakedModel) {
        final IModelBakeryAccessor accessor = (IModelBakeryAccessor) Minecraft.getInstance().getModelManager();
        return accessor.getModelBakery().getModel(unbakedModel);
    }

    @Override
    public BakedModel adaptToPlatform(BakedModel bakedModel) {
        if (bakedModel instanceof FabricBakedModelDelegate) {
            return bakedModel;
        }

        return new FabricBakedModelDelegate(bakedModel);
    }

    @Override
    public BakedModel adaptToPlatform(SimpleBakedModel.Builder simpleModelBuilder, RenderTypeGroup renderTypeGroup) {
        return adaptToPlatform(simpleModelBuilder.build());
    }

    @Override
    public boolean canRenderInType(final BlockState blockState, final RenderType renderType) {
        return ItemBlockRenderTypes.getChunkRenderType(blockState) == renderType;
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final RenderType renderType) {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final BakedModel model, final BlockState state, final RandomSource rand, final IBlockModelData data) {
        return Collections.singletonList(ItemBlockRenderTypes.getChunkRenderType(state));
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(BakedModel model, ItemStack stack, boolean isFabulous) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            final Collection<RenderType> renderTypes;
            if (model instanceof IDataAwareBakedModel dataAwareBakedModel) {
                renderTypes = dataAwareBakedModel.getSupportedRenderTypes(blockItem.getBlock().defaultBlockState(), RandomSource.create(42), IBlockModelData.empty());
            } else {
                renderTypes = getRenderTypesFor(model, blockItem.getBlock().defaultBlockState(), RandomSource.create(42), IBlockModelData.empty());
            }
            if (renderTypes.contains(RenderType.translucent())) {
                return Collections.singletonList(isFabulous || !Minecraft.useShaderTransparency() ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
            }
            return Collections.singletonList(Sheets.cutoutBlockSheet());
        }
        return Collections.singletonList(isFabulous ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
    }
}
