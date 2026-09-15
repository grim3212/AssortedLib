package com.grim3212.assorted.lib.client.events;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Something drawn on the HUD, after the crosshair. Both loaders hand a HUD element the same pair
 * since 26.2, where the gui draws from a render state a layer extracts rather than inline.
 */
@FunctionalInterface
public interface HudElementHandler {
    void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
}
