package com.grim3212.assorted.lib.client.model;

import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("resource")
public class UnbakedGeometryHelper {

    private UnbakedGeometryHelper() {
        throw new IllegalStateException("Can not instantiate an instance of: UnbakedGeometryHelper. This is a utility class");
    }

    // TODO(26.2): createUnbakedItemElements(int, SpriteContents) has no replacement.
    //  What it used to do: ItemModelGenerator#processFrames(layerIndex, "layer" + layerIndex, sprite)
    //  turned a sprite into the list of BlockElements vanilla uses for "builtin/generated" items -
    //  one flat element for the north/south faces plus one element per extruded edge run - so callers
    //  could re-bake that shape with a different texture.
    //  Why it cannot be expressed: ItemModelGenerator is an UnbakedModel now. processFrames is gone
    //  and every replacement (bake, bakeExtrudedSprite, bakeSideFaces, getSideFaces, the SideFace /
    //  SideDirection helper types) is private static, producing a QuadCollection directly rather than
    //  a list of elements - there is no public entry point that hands back geometry description
    //  instead of baked quads. A model that wants the generated item shape has to parent onto
    //  ItemModelGenerator.GENERATED_ITEM_MODEL_ID ("minecraft:builtin/generated") and let the baker
    //  run ItemModelGenerator#geometry() over its layer0..layer4 slots.

    /**
     * Creates a list of {@linkplain CuboidModelElement cuboid elements} covering only the opaque pixels
     * of the specified sprite, so a flat item texture becomes a mask instead of a full quad.
     * <p>
     * Unlike the 1.20.1 version this returns <em>only</em> the mask elements. That version started from
     * {@link #createUnbakedItemElements} and dropped its first (north/south) element to keep the
     * extruded edge elements, which are no longer obtainable - see the note on that method.
     */
    public static List<CuboidModelElement> createUnbakedItemMaskElements(int layerIndex, TextureAtlasSprite sprite) {
        var elements = new ArrayList<CuboidModelElement>();

        int width = sprite.contents().width(), height = sprite.contents().height();
        var bits = new BitSet(width * height);

        // For every frame in the texture, mark all the opaque pixels (this is what vanilla does too)
        sprite.contents().getUniqueFrames().forEach(frame -> {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    if (!sprite.contents().isTransparent(frame, x, y))
                        bits.set(x + y * width);
        });

        // Scan in search of opaque pixels
        for (int y = 0; y < height; y++) {
            int xStart = -1;
            for (int x = 0; x < width; x++) {
                var opaque = bits.get(x + y * width);
                if (opaque == (xStart == -1)) // (opaque && -1) || (!opaque && !-1)
                {
                    if (xStart == -1) {
                        // We have found the start of a new segment, continue
                        xStart = x;
                        continue;
                    }

                    // The segment is over, expand down as far as possible
                    int yEnd = y + 1;
                    expand:
                    for (; yEnd < height; yEnd++)
                        for (int x2 = xStart; x2 <= x; x2++)
                            if (!bits.get(x2 + yEnd * width))
                                break expand;

                    // Mark all pixels in the area as visited
                    for (int i = xStart; i < x; i++)
                        for (int j = y; j < yEnd; j++)
                            bits.clear(i + j * width);

                    // Create element
                    int finalX = x, finalY = y, finalYEnd = yEnd, finalXStart = xStart;
                    elements.add(new CuboidModelElement(
                            new Vector3f(16 * finalXStart / (float) width, 16 - 16 * finalYEnd / (float) height, 7.5F),
                            new Vector3f(16 * finalX / (float) width, 16 - 16 * finalY / (float) height, 8.5F),
                            Util.make(new HashMap<>(), map -> {
                                for (Direction direction : Direction.values())
                                    map.put(direction, new CuboidFace(null, layerIndex, "layer" + layerIndex, null, Quadrant.R0));
                            })
                    ));

                    // Reset xStart
                    xStart = -1;
                }
            }
        }
        return elements;
    }

    /**
     * Turns a single {@link CuboidFace} into a {@link BakedQuad}.
     */
    public static BakedQuad bakeElementFace(ModelBaker baker, CuboidModelElement element, CuboidFace face, Material.Baked material, Direction direction, ModelState state) {
        return FaceBakery.bakeQuad(baker, element.from(), element.to(), face, material, direction, state, element.rotation(), element.shade(), element.lightEmission());
    }

    /**
     * Bakes a list of {@linkplain CuboidModelElement cuboid elements} and feeds the baked quads to a
     * {@linkplain IModelBuilder model builder}.
     */
    public static void bakeElements(ModelBaker baker, IModelBuilder<?> builder, List<CuboidModelElement> elements, TextureSlots textures, ModelState modelState, ModelDebugName name) {
        for (CuboidModelElement element : elements) {
            for (Map.Entry<Direction, CuboidFace> entry : element.faces().entrySet()) {
                var side = entry.getKey();
                var face = entry.getValue();
                var material = baker.materials().resolveSlot(textures, face.texture(), name);
                var quad = bakeElementFace(baker, element, face, material, side, modelState);
                if (face.cullForDirection() == null)
                    builder.addUnculledFace(quad);
                else
                    builder.addCulledFace(Direction.rotate(modelState.transformation().getMatrix(), face.cullForDirection()), quad);
            }
        }
    }

    /**
     * Bakes a list of {@linkplain CuboidModelElement cuboid elements} and returns the list of baked quads.
     */
    public static List<BakedQuad> bakeElements(ModelBaker baker, List<CuboidModelElement> elements, TextureSlots textures, ModelState modelState, ModelDebugName name) {
        if (elements.isEmpty())
            return List.of();
        return UnbakedCuboidGeometry.bake(elements, textures, baker, modelState, name).getAll();
    }

    /**
     * Explanation:
     * This takes anything that looks like a valid resourcepack texture location, and tries to extract a resourcelocation out of it.
     * 1. it will ignore anything up to and including an /assets/ folder,
     * 2. it will take the next path component as a namespace,
     * 3. it will match but skip the /textures/ part of the path,
     * 4. it will take the rest of the path up to but excluding the .png extension as the resource path
     * It's a best-effort situation, to allow model files exported by modelling software to be used without post-processing.
     * Example:
     * C:\Something\Or Other\src\main\resources\assets\mymodid\textures\item\my_thing.png
     * ........................................--------_______----------_____________----
     * <namespace>        <path>
     * Result after replacing '\' to '/': mymodid:item/my_thing
     */
    private static final Pattern FILESYSTEM_PATH_TO_RESLOC =
            Pattern.compile("(?:.*[\\\\/]assets[\\\\/](?<namespace>[a-z_-]+)[\\\\/]textures[\\\\/])?(?<path>[a-z_\\\\/-]+)\\.png");

    /**
     * Resolves a material that may have been defined with a filesystem path instead of a proper {@link Identifier}.
     * <p>
     * A {@link Material} no longer names an atlas: which atlas a sprite is stitched into is decided by
     * the {@link net.minecraft.client.resources.model.sprite.MaterialBaker} doing the baking.
     */
    public static Material resolveDirtyMaterial(@Nullable String tex, IModelBakingContext owner) {
        if (tex == null)
            return new Material(MissingTextureAtlasSprite.getLocation());
        if (tex.startsWith("#"))
            return owner.getMaterial(tex).orElse(null);

        // Attempt to convert a common (windows/linux/mac) filesystem path to a Identifier.
        // This makes no promises, if it doesn't work, too bad, fix your mtl file.
        Matcher match = FILESYSTEM_PATH_TO_RESLOC.matcher(tex);
        if (match.matches()) {
            String namespace = match.group("namespace");
            String path = match.group("path").replace("\\", "/");
            tex = namespace != null ? namespace + ":" + path : path;
        }

        return new Material(Identifier.parse(tex));
    }
}
