package com.grim3212.assorted.lib.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;

/**
 * The id -> codec hook for {@link SpecialModelRenderer.Unbaked} types, which an item selects from
 * data with {@code "minecraft:special"}. Vanilla's id mapper is private, so each loader exposes it.
 */
public interface ISpecialModelRendererRegistry {
    /**
     * Registers a {@link SpecialModelRenderer.Unbaked} codec under {@code id}, which item models
     * name to select the renderer.
     */
    void registerSpecialModelRenderer(final Identifier id, final MapCodec<? extends SpecialModelRenderer.Unbaked<?>> renderer);
}
