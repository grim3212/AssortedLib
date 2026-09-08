package com.grim3212.assorted.lib.client.model.baked.simple;

import com.grim3212.assorted.lib.client.model.baked.base.BaseBakedBlockModel;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

/**
 * A full block cube generated from a single sprite, with every face using the whole texture.
 * <p>
 * 1.20.1 built this by hand: bake a quad per side, unpack the resulting {@code int[]} vertex data and
 * repack it with {@link com.grim3212.assorted.lib.client.util.LightUtil#diffuseLight(Direction)}
 * folded into the vertex colours. {@link BakedQuad} is an immutable record of four positions, four
 * packed uvs, a direction and a {@link BakedQuad.MaterialInfo} in 26.2 - there is no vertex colour to
 * write to - so the diffuse term is left to the renderer by baking the quads with {@code shade = true},
 * which is what it is for.
 */
public class SimpleGeneratedModel extends BaseBakedBlockModel {

    private static final Vector3fc FROM = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3fc TO = new Vector3f(16.0F, 16.0F, 16.0F);
    private static final CuboidFace.UVs FULL_FACE = new CuboidFace.UVs(0.0F, 0.0F, 16.0F, 16.0F);

    /**
     * {@link FaceBakery} interns the vectors and material infos it produces through the
     * {@link ModelBaker} it is baking under, so quads baked outside of a model bake need one of these.
     */
    private static final ModelBaker.Interner NO_INTERNING = new ModelBaker.Interner() {
        @Override
        public Vector3fc vector(Vector3fc vector) {
            return vector;
        }

        @Override
        public BakedQuad.MaterialInfo materialInfo(BakedQuad.MaterialInfo material) {
            return material;
        }
    };

    @SuppressWarnings("unchecked")
    private final List<BakedQuad>[] face = new List[Direction.values().length];

    private final Material.Baked material;

    public SimpleGeneratedModel(final TextureAtlasSprite texture) {
        this.material = new Material.Baked(texture, false);

        final Transparency transparency = texture.contents().computeTransparency(0.0F, 0.0F, 1.0F, 1.0F);
        final BakedQuad.MaterialInfo materialInfo = BakedQuad.MaterialInfo.of(this.material, transparency, 1, true, 0);

        for (final Direction side : Direction.values()) {
            final BakedQuad quad = FaceBakery.bakeQuad(NO_INTERNING, FROM, TO, FULL_FACE, Quadrant.R0, materialInfo, side, BlockModelRotation.IDENTITY, null);
            face[side.ordinal()] = List.of(quad);
        }
    }

    public List<BakedQuad>[] getFace() {
        return face;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable final Direction direction) {
        if (direction == null) {
            return List.of();
        }

        return face[direction.ordinal()];
    }

    @Override
    public Material.Baked particleMaterial() {
        return material;
    }
}
