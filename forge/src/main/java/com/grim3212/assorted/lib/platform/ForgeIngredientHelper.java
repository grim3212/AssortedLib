package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.crafting.ingredient.ForgeFluidIngredient;
import com.grim3212.assorted.lib.platform.services.IIngredientHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.*;
import org.jetbrains.annotations.Nullable;

public class ForgeIngredientHelper implements IIngredientHelper {
    @Override
    public void register() {
        CraftingHelper.register(ForgeFluidIngredient.SERIALIZER.getIdentifier(), ForgeFluidIngredient.SERIALIZER);
    }

    @Override
    public Ingredient and(Ingredient... ingredients) {
        return ingredients.length == 0 ? Ingredient.EMPTY : ingredients.length == 1 ? ingredients[0] : IntersectionIngredient.of(ingredients);
    }

    @Override
    public Ingredient or(Ingredient... ingredients) {
        return ingredients.length == 0 ? Ingredient.EMPTY : CompoundIngredient.of(ingredients);
    }

    @Override
    public Ingredient difference(Ingredient base, Ingredient subtracted) {
        return DifferenceIngredient.of(base, subtracted);
    }

    @Override
    public Ingredient nbt(ItemStack item) {
        return StrictNBTIngredient.of(item.copy());
    }

    @Override
    public Ingredient fluid(@Nullable TagKey<Item> itemTagKey, TagKey<Fluid> fluidTagKey, long amount) {
        return ForgeFluidIngredient.of(itemTagKey, fluidTagKey, (int) amount);
    }
}
