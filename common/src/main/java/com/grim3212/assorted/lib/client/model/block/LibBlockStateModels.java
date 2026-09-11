package com.grim3212.assorted.lib.client.model.block;

import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.resources.Identifier;

/**
 * Ids of the library's own blockstate model types. The blockstate jsons are generated once and read
 * by both loaders, so each id must mean the same thing on both, though the interface is per loader.
 */
public final class LibBlockStateModels {

    /**
     * A blockstate model that bakes a model json's {@code IModelSpecification} into a whole
     * {@code BlockStateModel} rather than flattening it to geometry. Its json is a vanilla variant
     * ({@code model}, {@code x}, {@code y}, {@code z}, {@code uvlock}) with this {@code type}.
     */
    public static final Identifier SPECIFICATION = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "specification");

    /**
     * The key Fabric reads a custom model type from - a blockstate variant type and a model json loader
     * alike - where NeoForge reads {@code "type"} and {@code "loader"}. Each loader ignores the other key,
     * so a json both loaders read has to carry both.
     */
    public static final String FABRIC_TYPE_KEY = "fabric:type";

    private LibBlockStateModels() {
    }
}
