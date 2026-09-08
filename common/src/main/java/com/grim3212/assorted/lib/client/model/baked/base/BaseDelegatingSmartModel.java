package com.grim3212.assorted.lib.client.model.baked.base;

import com.grim3212.assorted.lib.client.model.baked.IDelegatingBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;

public abstract class BaseDelegatingSmartModel extends BaseSmartModel implements IDelegatingBakedModel {

    private final BlockStateModel delegate;

    protected BaseDelegatingSmartModel(final BlockStateModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public BlockStateModel getDelegate() {
        return delegate;
    }

    @Override
    public BlockStateModel handleBlockState(final RandomSource random, final IBlockModelData modelData) {
        return getDelegate();
    }

    @Override
    public Material.Baked particleMaterial() {
        return getDelegate().particleMaterial();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return getDelegate().materialFlags();
    }
}
