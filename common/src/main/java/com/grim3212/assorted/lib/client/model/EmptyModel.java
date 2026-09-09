package com.grim3212.assorted.lib.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A completely empty model with no quads or texture dependencies.
 * <p>
 * You can access it as a {@link BlockStateModel} through {@link #BAKED}, or as a single
 * {@link BlockStateModelPart} through {@link #PART}.
 */
public class EmptyModel {
    public static final BlockStateModel BAKED = new Baked();
    public static final BlockStateModelPart PART = new Part();

    private static Material.Baked missingMaterial;

    private EmptyModel() {
    }

    /**
     * The missing texture on the block atlas, wrapped as a baked material.
     * <p>
     * Resolved lazily because there is no sprite to hand out until the atlases have been stitched.
     */
    public static Material.Baked missingMaterial() {
        if (missingMaterial == null) {
            missingMaterial = new Material.Baked(Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).missingSprite(), false);
        }

        return missingMaterial;
    }

    private static class Baked implements BlockStateModel {
        // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its
        // patched jar; vanilla still declares these abstract, so they have to be implemented here.
        @SuppressWarnings("deprecation")
        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
        }

        @SuppressWarnings("deprecation")
        @Override
        public Material.Baked particleMaterial() {
            return missingMaterial();
        }

        @SuppressWarnings("deprecation")
        @Override
        public @BakedQuad.MaterialFlags int materialFlags() {
            return 0;
        }
    }

    private static class Part implements BlockStateModelPart {
        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return List.of();
        }

        // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its
        // patched jar; vanilla still declares these abstract, so they have to be implemented here.
        @SuppressWarnings("deprecation")
        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public Material.Baked particleMaterial() {
            return missingMaterial();
        }

        @Override
        public @BakedQuad.MaterialFlags int materialFlags() {
            return 0;
        }
    }
}
