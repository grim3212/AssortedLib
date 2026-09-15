package com.grim3212.assorted.lib.client.manual;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/** One side of a spread. Draws in coordinates local to its box, so either side renders the same. */
public interface ManualPage {

    MapCodec<? extends ManualPage> codec();

    /** The page's heading; absent falls back to the chapter's name. */
    Optional<Component> title();

    /** @param animationTick drive any cycling from this; a page keeps no state of its own */
    void render(ManualPageView view, int animationTick);
}
