package com.grim3212.assorted.lib.manual;

/**
 * How the manual should draw a {@link net.minecraft.world.item.crafting.Recipe} that publishes no
 * {@link net.minecraft.world.item.crafting.display.RecipeDisplay}, such as an {@code isSpecial}
 * machine recipe. Anything with a display is read from that instead.
 */
public interface IManualRecipeProvider {

    ManualRecipeView manualView();
}
