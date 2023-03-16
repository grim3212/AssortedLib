package com.grim3212.assorted.lib.client.render;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;

public interface IBEWLR {
    /**
     * Registers a new {@link BlockEntityWithoutLevelRenderer} for a specific item.
     *
     * @param item     The item to register the {@link BlockEntityWithoutLevelRenderer} for.
     * @param renderer The {@link BlockEntityWithoutLevelRenderer} to register.
     */
    void registerBlockEntityWithoutLevelRenderer(final Item item, final BlockEntityWithoutLevelRenderer renderer);
}
