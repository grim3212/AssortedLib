package com.grim3212.assorted.lib.client.model.baked.simple;

import com.grim3212.assorted.lib.client.model.EmptyModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * A model that draws nothing.
 */
public class NullBakedModel implements BlockStateModel {
    public static final NullBakedModel instance = new NullBakedModel();

    // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its patched
    // jar; vanilla still declares these abstract, so they have to be implemented here.
    @SuppressWarnings("deprecation")
    @Override
    public void collectParts(final RandomSource random, final List<BlockStateModelPart> output) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial() {
        return EmptyModel.missingMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return 0;
    }
}
