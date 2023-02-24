package com.grim3212.assorted.lib.platform.services;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public interface IIngredientHelper {

    /**
     * Register the ingredients for this platform
     */
    void register();

    Ingredient and(Ingredient... ingredients);

    Ingredient or(Ingredient... ingredients);

    Ingredient difference(Ingredient base, Ingredient subtracted);

    Ingredient nbt(ItemStack item);

    Ingredient fluid(@Nullable TagKey<Item> itemTagKey, TagKey<Fluid> fluidTagKey, long amount);
}
