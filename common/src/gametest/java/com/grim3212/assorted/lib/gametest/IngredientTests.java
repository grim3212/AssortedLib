package com.grim3212.assorted.lib.gametest;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.gametest.LibTestSupport.*;

/**
 * Composed ingredients - OR, AND and DIFFERENCE - accepting the same items on both loaders.
 */
final class IngredientTests {

    private IngredientTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("ingredients_combine", IngredientTests::ingredientsCombine);
    }

    /**
     * Composed ingredients accept and reject the same stacks on both loaders. Each loader builds
     * them from its own types, so nothing here is shared code.
     */
    private static void ingredientsCombine(GameTestHelper helper) {
        Ingredient either = Services.INGREDIENTS.or(Ingredient.of(Items.STICK), Ingredient.of(Items.STONE));
        helper.assertTrue(either.test(new ItemStack(Items.STICK)), "an OR ingredient rejected its first branch");
        helper.assertTrue(either.test(new ItemStack(Items.STONE)), "an OR ingredient rejected its second branch");
        helper.assertFalse(either.test(new ItemStack(Items.DIRT)), "an OR ingredient accepted an item in neither branch");

        // A single element OR has to stay usable: an Ingredient may not be empty in 26.2, and both
        // helpers special-case the one-argument call rather than wrapping it.
        Ingredient single = Services.INGREDIENTS.or(Ingredient.of(Items.STICK));
        helper.assertTrue(single.test(new ItemStack(Items.STICK)), "a single branch OR rejected its own item");
        helper.assertFalse(single.test(new ItemStack(Items.STONE)), "a single branch OR accepted a foreign item");

        Ingredient allButStone = Services.INGREDIENTS.difference(
                Ingredient.of(Items.STICK, Items.STONE, Items.DIRT), Ingredient.of(Items.STONE));
        helper.assertTrue(allButStone.test(new ItemStack(Items.STICK)), "a DIFFERENCE ingredient dropped an item it should keep");
        helper.assertTrue(allButStone.test(new ItemStack(Items.DIRT)), "a DIFFERENCE ingredient dropped an item it should keep");
        helper.assertFalse(allButStone.test(new ItemStack(Items.STONE)), "a DIFFERENCE ingredient kept the item it subtracts");

        Ingredient both = Services.INGREDIENTS.and(Ingredient.of(Items.STICK, Items.STONE), Ingredient.of(Items.STONE, Items.DIRT));
        helper.assertTrue(both.test(new ItemStack(Items.STONE)), "an AND ingredient rejected the item both branches accept");
        helper.assertFalse(both.test(new ItemStack(Items.STICK)), "an AND ingredient accepted an item only its first branch accepts");
        helper.assertFalse(both.test(new ItemStack(Items.DIRT)), "an AND ingredient accepted an item only its second branch accepts");

        Ingredient singleAnd = Services.INGREDIENTS.and(Ingredient.of(Items.STICK));
        helper.assertTrue(singleAnd.test(new ItemStack(Items.STICK)), "a single branch AND rejected its own item");
        helper.assertFalse(singleAnd.test(new ItemStack(Items.STONE)), "a single branch AND accepted a foreign item");

        helper.succeed();
    }
}
