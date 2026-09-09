package com.grim3212.assorted.lib.client.model.baked.simple;

import com.grim3212.assorted.lib.client.model.EmptyModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * Draws several models on top of each other.
 * <p>
 * 1.20.1 had to flatten this by pulling every child's quads out per face at construction time, since
 * a {@code BakedModel} could only return one quad list. A {@link BlockStateModel} collects a list of
 * {@link BlockStateModelPart parts} instead, so combining models is simply forwarding the collect
 * call - which also keeps each child's own ambient occlusion flag and material flags intact.
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
