package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class LibCommonRecipeProvider extends ConditionalRecipeProvider {

    public LibCommonRecipeProvider(PackOutput output) {
        super(output, LibConstants.MOD_ID);
    }

    @Override
    public void registerConditions() {
//        this.addConditions(itemTagExists(LibCommonTags.Items.DUSTS), new ResourceLocation("assortedlib:test2"), new ResourceLocation("assortedlib:test4"));
//        this.addConditions(and(partEnabled("FALSE"), partEnabled("TRUE")), new ResourceLocation("assortedlib:test4"));
//        this.addConditions(and(partEnabled("TRUE"), partEnabled("TRUE2")), new ResourceLocation("assortedlib:test4"));
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        super.buildRecipes(consumer);

//        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Items.REDSTONE).define('R', Items.AMETHYST_SHARD).pattern("RR").unlockedBy("has_amethyst", has(Items.AMETHYST_SHARD)).save(consumer, new ResourceLocation("assortedlib:test1"));
//        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Items.REDSTONE).define('R', or(Ingredient.of(Items.BRICK), Ingredient.of(Items.BLAZE_ROD))).pattern("RR").unlockedBy("has_amethyst", has(Items.AMETHYST_SHARD)).save(consumer, new ResourceLocation("assortedlib:test2"));
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, Items.AXOLOTL_BUCKET).requires(fluid(FluidTags.WATER)).unlockedBy("has_water", has(Items.WATER_BUCKET)).save(consumer, new ResourceLocation("assortedlib:test3"));
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, Items.BARRIER).requires(Items.HOPPER).unlockedBy("has_hopper", has(Items.HOPPER)).save(consumer, new ResourceLocation("assortedlib:test4"));
    }
}
