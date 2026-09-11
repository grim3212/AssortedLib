package com.grim3212.assorted.lib.client.model.block;

import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.resources.Identifier;

/**
 * The ids the library's own blockstate model types are registered under.
 * <p>
 * These are shared across loaders on purpose: the blockstate jsons are generated once (by the
 * NeoForge datagen) and read by both, so the {@code "type"} they name has to mean the same thing on
 * each side even though the interface it implements is loader specific.
 */
public final class LibBlockStateModels {

    /**
     * A blockstate model that bakes a model json's
     * {@link com.grim3212.assorted.lib.client.model.loaders.IModelSpecification} into a whole
     * {@code BlockStateModel} rather than flattening it to geometry.
     * <p>
     * Its json shape is exactly a vanilla variant - {@code model} plus the optional {@code x},
     * {@code y}, {@code z} and {@code uvlock} - so switching a blockstate over to it is only a
     * question of the {@code type} key.
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
