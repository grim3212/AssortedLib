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
 * A model that decides at render time which other model draws, from the random source and the block
 * entity model data ({@link #handleBlockState}). Each baked model has one block state, so capture
 * what you need from it while baking.
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
     * Picks the model to draw. {@code random} is seeded from the block position; {@code modelData}
     * is {@linkplain IBlockModelData#empty() empty} if the call site could not supply any.
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

    // TODO(26.2): the item half (an ItemOverrides hook that let a smart model swap itself per
    //  stack) is gone. Per-stack variation is a registered ItemModel.Unbaked chosen by the item's
    //  model json, a resource change this class cannot make for the model.
}
