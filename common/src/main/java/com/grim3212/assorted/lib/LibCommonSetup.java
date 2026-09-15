package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.config.LibCommonConfig;
import com.grim3212.assorted.lib.crafting.SyncedRecipes;
import com.grim3212.assorted.lib.manual.LibItems;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.function.BooleanSupplier;

/** The library's own content, as opposed to its services. Called from both loaders' entry points. */
public class LibCommonSetup {

    public static final LibCommonConfig COMMON_CONFIG = new LibCommonConfig();

    public static void init() {
        LibItems.init();

        requireVanillaRecipes();
    }

    /**
     * Recipe pages need whole recipes.
     * The config is passed as a gate, not read here: NeoForge loads none during mod construction.
     */
    private static void requireVanillaRecipes() {
        BooleanSupplier enabled = () -> COMMON_CONFIG.syncVanillaRecipesForManual.get();

        SyncedRecipes.require(enabled, () -> RecipeType.CRAFTING, ShapedRecipe.SERIALIZER, ShapelessRecipe.SERIALIZER);
        SyncedRecipes.require(enabled, () -> RecipeType.SMELTING, SmeltingRecipe.SERIALIZER);
        SyncedRecipes.require(enabled, () -> RecipeType.BLASTING, BlastingRecipe.SERIALIZER);
        SyncedRecipes.require(enabled, () -> RecipeType.SMOKING, SmokingRecipe.SERIALIZER);
        SyncedRecipes.require(enabled, () -> RecipeType.CAMPFIRE_COOKING, CampfireCookingRecipe.SERIALIZER);
        SyncedRecipes.require(enabled, () -> RecipeType.STONECUTTING, StonecutterRecipe.SERIALIZER);
    }
}
