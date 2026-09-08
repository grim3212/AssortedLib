package com.grim3212.assorted.lib.client.texture;

import com.grim3212.assorted.lib.LibConstants;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;

/**
 * A helper sprite with UVs spanning the entire texture.
 * <p>
 * Useful for baking quads that won't be used with an atlas.
 * <p>
 * The sprite is stitched as a 1x1 sprite into a 1x1 atlas, so u0/v0 are 0 and u1/v1 are 1.
 * {@link TextureAtlasSprite#getU(float)} / {@link TextureAtlasSprite#getV(float)} therefore
 * already return their argument unchanged and no longer need to be overridden. Note that
 * those take a 0-1 offset in 26.2, where the 1.20.1 versions took a 0-16 model coordinate.
 */
public class UnitTextureAtlasSprite extends TextureAtlasSprite {
    public static final Identifier LOCATION = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "unit");
    public static final UnitTextureAtlasSprite INSTANCE = new UnitTextureAtlasSprite();

    private UnitTextureAtlasSprite() {
        super(LOCATION, new SpriteContents(LOCATION, new FrameSize(1, 1), new NativeImage(1, 1, false)), 1, 1, 0, 0, 0);
    }
}
