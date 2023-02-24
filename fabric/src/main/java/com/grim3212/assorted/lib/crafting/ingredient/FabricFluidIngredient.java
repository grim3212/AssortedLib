package com.grim3212.assorted.lib.crafting.ingredient;

import com.grim3212.assorted.lib.core.crafting.ingredient.LibFluidIngredient;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class FabricFluidIngredient extends LibFluidIngredient implements CustomIngredient {

    public static final Serializer SERIALIZER = new Serializer();

    protected FabricFluidIngredient(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag) {
        this(itemTag, fluidTag, Services.FLUIDS.getBucketAmount());
    }

    protected FabricFluidIngredient(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag, long amount) {
        super(itemTag, fluidTag, amount);
    }

    public static FabricFluidIngredient of(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag) {
        return new FabricFluidIngredient(itemTag, fluidTag);
    }

    public static FabricFluidIngredient of(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag, long amount) {
        return new FabricFluidIngredient(itemTag, fluidTag, amount);
    }

    @Override
    public boolean requiresTesting() {
        return false;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    static class Serializer extends LibFluidIngredient.Serializer<FabricFluidIngredient> implements CustomIngredientSerializer<FabricFluidIngredient> {
        @Override
        protected FabricFluidIngredient create(@Nullable TagKey itemTag, TagKey fluidTag, long amount) {
            return new FabricFluidIngredient(itemTag, fluidTag, amount);
        }
    }
}
