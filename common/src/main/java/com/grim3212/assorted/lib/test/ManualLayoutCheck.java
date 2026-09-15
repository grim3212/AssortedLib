package com.grim3212.assorted.lib.test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks a recipe layout's region against the raw texture, for the mistake of starting it a row too
 * high and taking in the screen's black outline or white bevel.
 * <p>
 * A test helper rather than part of the loader because it reads the png directly; the game goes
 * through the atlas. Only for a mod's own art: vanilla's gui textures are client distribution and
 * are not on a dedicated server's classpath.
 */
public final class ManualLayoutCheck {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private ManualLayoutCheck() {
    }

    /**
     * Every complaint about one layout, empty when it is clean.
     *
     * @param owner       a class of the mod whose files these are; each mod is its own module and
     *                    cannot read another's resources
     * @param layoutPath  e.g. {@code /assets/mymod/manual/recipe_layouts/kiln.json}
     * @param textureRoot prefix the layout's {@code texture} id resolves against, usually
     *                    {@code /assets/}
     */
    public static List<String> problems(Class<?> owner, String layoutPath, String textureRoot) {
        List<String> problems = new ArrayList<>();

        JsonObject layout = readJson(owner, layoutPath);
        if (layout == null) {
            return List.of(layoutPath + " is not on the classpath");
        }

        String texture = layout.get("texture").getAsString();
        BufferedImage image = readImage(owner, textureRoot + texture.replace(':', '/'));
        if (image == null) {
            return List.of(layoutPath + ": cannot read texture " + texture);
        }

        int u = layout.get("u").getAsInt();
        int v = layout.get("v").getAsInt();
        int width = layout.get("width").getAsInt();
        int height = layout.get("height").getAsInt();

        if (u < 0 || v < 0 || u + width > image.getWidth() || v + height > image.getHeight()) {
            return List.of(layoutPath + ": region runs outside " + texture);
        }

        checkEdge(problems, layoutPath, "top", image, u, v, width, 1);
        checkEdge(problems, layoutPath, "bottom", image, u, v + height - 1, width, 1);
        checkEdge(problems, layoutPath, "left", image, u, v, 1, height);
        checkEdge(problems, layoutPath, "right", image, u + width - 1, v, 1, height);

        return problems;
    }

    /** One flat border colour the whole way along is the screen's frame; a face varies. */
    private static void checkEdge(List<String> problems, String layoutPath, String edge, BufferedImage image,
                                  int x, int y, int width, int height) {
        int first = image.getRGB(x, y);
        if (first != WHITE && first != BLACK) {
            return;
        }

        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                if (image.getRGB(x + dx, y + dy) != first) {
                    return;
                }
            }
        }

        problems.add(layoutPath + ": the " + edge + " of the region is the screen's own "
                + (first == WHITE ? "highlight" : "outline") + ", move it in by a row");
    }

    private static JsonObject readJson(Class<?> owner, String path) {
        try (InputStream in = owner.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            return null;
        }
    }

    private static BufferedImage readImage(Class<?> owner, String path) {
        try (InputStream in = owner.getResourceAsStream(path)) {
            return in == null ? null : ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }
    }
}
