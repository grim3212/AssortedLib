package com.grim3212.assorted.lib.client.texture;

import com.grim3212.assorted.lib.LibConstants;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;

/**
 * A sprite whose UVs span the whole texture, for baking quads not used with an atlas. It is
 * stitched as a 1x1 sprite into a 1x1 atlas, so {@link TextureAtlasSprite#getU(float)} and {@link
 * TextureAtlasSprite#getV(float)} (which take a 0-1 offset) already return their argument.
 */
public class UnitTextureAtlasSprite extends TextureAtlasSprite {
    public static final Identifier LOCATION = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "unit");
    public static final UnitTextureAtlasSprite INSTANCE = new UnitTextureAtlasSprite();

    private UnitTextureAtlasSprite() {
        super(LOCATION, new SpriteContents(LOCATION, new FrameSize(1, 1), new NativeImage(1, 1, false)), 1, 1, 0, 0, 0);
    }
}
