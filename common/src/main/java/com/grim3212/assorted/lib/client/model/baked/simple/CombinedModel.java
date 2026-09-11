package com.grim3212.assorted.lib.client.model.baked.simple;

import com.grim3212.assorted.lib.client.model.EmptyModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * Draws several models on top of each other by forwarding the collect call to each, which keeps
 * every child's own ambient occlusion and material flags.
 */
public class CombinedModel implements BlockStateModel {

    private final BlockStateModel[] merged;

    public CombinedModel(final BlockStateModel... args) {
        this.merged = args;
    }

    // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its patched
    // jar; vanilla still declares these abstract, so they have to be implemented here.
    @SuppressWarnings("deprecation")
    @Override
    public void collectParts(final RandomSource random, final List<BlockStateModelPart> output) {
        for (final BlockStateModel model : merged) {
            model.collectParts(random, output);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial() {
        for (final BlockStateModel model : merged) {
            return model.particleMaterial();
        }

        return EmptyModel.missingMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        int flags = 0;
        for (final BlockStateModel model : merged) {
            flags |= model.materialFlags();
        }
        return flags;
    }
}
