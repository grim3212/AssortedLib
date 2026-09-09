package com.grim3212.assorted.lib.client.model.baked.base;

import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.baked.simple.NullBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A model that decides at render time which other model actually draws.
 * <p>
 * The block state a smart model varies on is no longer a render time parameter: 26.2 bakes one
 * {@link BlockStateModel} per {@link net.minecraft.world.level.block.state.BlockState} through
 * {@link BlockStateModel.UnbakedRoot#bake(net.minecraft.world.level.block.state.BlockState, net.minecraft.client.resources.model.ModelBaker)},
 * so a subclass should capture whatever it needs from the state while baking. What is still dynamic
 * is the random source and the block entity model data, which is what {@link #handleBlockState} gets.
 */
public abstract class BaseSmartModel implements IDataAwareBakedModel {

    // The BlockStateModel members below are deprecated by NeoForge in favour of level/pos aware
    // overloads that only exist in its patched jar; vanilla still declares them abstract.
    @SuppressWarnings("deprecation")
    @Override
    public void collectParts(final @NotNull RandomSource random, final @NotNull IBlockModelData extraData, final @NotNull List<BlockStateModelPart> output) {
        handleBlockState(random, extraData).collectParts(random, output);
    }

    /**
     * Picks the model to draw for the given random source and model data.
     *
     * @param random    The random source, seeded from the block position.
     * @param modelData The model data for the position, {@linkplain IBlockModelData#empty() empty} if
     *                  the call site could not supply any.
     * @return The model to draw.
     */
    public BlockStateModel handleBlockState(final RandomSource random, final IBlockModelData modelData) {
        return NullBakedModel.instance;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial() {
        return handleBlockState(RandomSource.create(), IBlockModelData.empty()).particleMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return handleBlockState(RandomSource.create(), IBlockModelData.empty()).materialFlags();
    }

    // TODO(26.2): the item half of this class is gone. It used to install an ItemOverrides subclass
    //  (OverrideHelper) whose resolve(BakedModel, ItemStack, ClientLevel, LivingEntity, int) called
    //  back into BaseSmartModel#resolve, which let a smart model swap itself for a different model
    //  depending on the stack. ItemOverrides and ItemOverride were deleted outright in 26.2 and there
    //  is no code-registered override list to hook: item variation is data driven now through
    //  net.minecraft.client.renderer.item.ItemModel and its SelectItemModel / ConditionalItemModel /
    //  RangeSelectItemModel implementations, selected by codec-registered properties under
    //  client.renderer.item.properties.**, and declared in the item's own model json. The equivalent
    //  of the old resolve() is to register an ItemModel.Unbaked type and let the json choose it -
    //  which is a resource pack change, not something this class can do on the model's behalf.
}
