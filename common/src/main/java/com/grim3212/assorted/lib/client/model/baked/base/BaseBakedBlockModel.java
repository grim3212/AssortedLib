package com.grim3212.assorted.lib.client.model.baked.base;

import com.grim3212.assorted.lib.client.model.EmptyModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * Base for a hand built block model that draws one set of quads: it is both the
 * {@link BlockStateModel} and its single {@link BlockStateModelPart}, handing itself out of
 * {@link #collectParts(RandomSource, List)}. Subclasses implement
 * {@link BlockStateModelPart#getQuads(Direction)}.
 */
public abstract class BaseBakedBlockModel implements BlockStateModel, BlockStateModelPart {

    // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its patched
    // jar; vanilla still declares these abstract, so they have to be implemented here.
    @SuppressWarnings("deprecation")
    @Override
    public void collectParts(final RandomSource random, final List<BlockStateModelPart> output) {
        output.add(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial() {
        return EmptyModel.missingMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        int flags = 0;
        for (final BakedQuad quad : getQuads(null)) {
            flags |= quad.materialInfo().flags();
        }
        for (final Direction direction : Direction.values()) {
            for (final BakedQuad quad : getQuads(direction)) {
                flags |= quad.materialInfo().flags();
            }
        }
        return flags;
    }
}
