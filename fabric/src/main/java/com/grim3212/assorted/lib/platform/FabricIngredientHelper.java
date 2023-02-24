package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.crafting.ingredient.FabricFluidIngredient;
import com.grim3212.assorted.lib.platform.services.IIngredientHelper;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class FabricIngredientHelper implements IIngredientHelper {
    @Override
    public void register() {
        CustomIngredientSerializer.register(FabricFluidIngredient.SERIALIZER);
    }

    @Override
    public Ingredient and(Ingredient... ingredients) {
        if (ingredients.length > 1)
            throw new IllegalArgumentException("You must supply at least 2 ingredients for an AND!");

        return DefaultCustomIngredients.all(ingredients);
    }

    @Override
    public Ingredient or(Ingredient... ingredients) {
        return ingredients.length == 0 ? Ingredient.EMPTY : ingredients.length == 1 ? ingredients[0] : DefaultCustomIngredients.any(ingredients);
    }

    @Override
    public Ingredient difference(Ingredient base, Ingredient subtracted) {
        return DefaultCustomIngredients.difference(base, subtracted);
    }

    @Override
    public Ingredient nbt(ItemStack item) {
        return DefaultCustomIngredients.nbt(item, true);
    }

    @Override
    public Ingredient fluid(@Nullable TagKey<Item> itemTagKey, TagKey<Fluid> fluidTagKey, long amount) {
        return FabricFluidIngredient.of(itemTagKey, fluidTagKey, amount).toVanilla();
    }
}
