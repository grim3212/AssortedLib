package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.client.model.RenderTypeGroup;
import com.grim3212.assorted.lib.client.model.data.*;
import com.grim3212.assorted.lib.client.model.loader.ForgeBakedModelDelegate;
import com.grim3212.assorted.lib.platform.services.IClientModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

    @Override
    public UnbakedModel getUnbakedModel(ResourceLocation unbakedModel) {
        return Minecraft.getInstance().getModelManager().getModelBakery().getModel(unbakedModel);
    }

    @Override
    public BakedModel adaptToPlatform(final BakedModel bakedModel) {
        if (bakedModel instanceof ForgeBakedModelDelegate)
            return bakedModel;

        return new ForgeBakedModelDelegate(bakedModel);
    }

    @Override
    public BakedModel adaptToPlatform(final SimpleBakedModel.Builder simpleModelBuilder, final RenderTypeGroup renderTypeGroup) {
        return adaptToPlatform(simpleModelBuilder.build(new net.minecraftforge.client.RenderTypeGroup(renderTypeGroup.block(), renderTypeGroup.entity())));
    }

    @Override
    public boolean canRenderInType(final BlockState blockState, final RenderType renderType) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getRenderTypes(
                blockState,
                RANDOM_SOURCE,
                ModelData.EMPTY
        ).contains(renderType);
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final RenderType renderType) {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final BakedModel model, final BlockState state, final RandomSource rand, final IBlockModelData data) {
        if (!(data instanceof ForgeBlockModelDataPlatformDelegate delegate)) {
            throw new IllegalArgumentException("data must be an instance of ForgeBlockModelData");
        }

        return model.getRenderTypes(state, rand, delegate.getDelegate()).asList();
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(BakedModel model, ItemStack stack, boolean isFabulous) {
        return model.getRenderTypes(stack, isFabulous);
    }
}
