package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.manual.LibItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

/** The library ships one recipe, for the one item it has. */
public class LibRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public LibRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, LibConstants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.TOOLS, LibItems.INSTRUCTION_MANUAL.get())
                .requires(Items.BOOK)
                .requires(Items.LEATHER)
                .requires(this.tag(ItemTags.DYES))
                .unlockedBy("has_book", this.has(Items.BOOK))
                .save(this.output);
    }

    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, LibConstants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new LibRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + LibConstants.MOD_ID;
        }
    }
}
