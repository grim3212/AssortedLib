package com.grim3212.assorted.lib.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.core.crafting.ingredient.LibFluidIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ForgeFluidIngredient extends AbstractIngredient {

    public static final Serializer SERIALIZER = new Serializer();
    protected final LibFluidIngredient fluidIngredient;

    protected ForgeFluidIngredient(LibFluidIngredient fluidIngredient) {
        this(fluidIngredient.getItemTag(), fluidIngredient.getFluidTag(), (int) fluidIngredient.getAmount());
    }

    protected ForgeFluidIngredient(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag) {
        this(itemTag, fluidTag, FluidType.BUCKET_VOLUME);
    }

    protected ForgeFluidIngredient(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag, int amount) {
        super(itemTag != null ? Stream.of(new Ingredient.TagValue(itemTag)) : Stream.of());
        this.fluidIngredient = new LibFluidIngredient(itemTag, fluidTag, amount);
    }

    public static ForgeFluidIngredient of(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag) {
        return new ForgeFluidIngredient(itemTag, fluidTag);
    }

    public static ForgeFluidIngredient of(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag, int amount) {
        return new ForgeFluidIngredient(itemTag, fluidTag, amount);
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        return this.fluidIngredient.test(input);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    protected void invalidate() {
        this.fluidIngredient.invalidate();
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(SERIALIZER).toString());
        SERIALIZER.write(json, this.fluidIngredient);
        return json;
    }

    @Override
    public ItemStack[] getItems() {
        return this.fluidIngredient.getMatchingStacks().toArray(new ItemStack[0]);
    }

    public static class Serializer extends LibFluidIngredient.Serializer<LibFluidIngredient> implements IIngredientSerializer<ForgeFluidIngredient> {

        @Override
        public ForgeFluidIngredient parse(JsonObject json) {
            return new ForgeFluidIngredient(this.read(json));
        }

        @Override
        public ForgeFluidIngredient parse(FriendlyByteBuf buffer) {
            return new ForgeFluidIngredient(this.read(buffer));
        }

        @Override
        public void write(FriendlyByteBuf buffer, ForgeFluidIngredient ingredient) {
            this.write(buffer, ingredient.fluidIngredient);
        }

        @Override
        protected LibFluidIngredient create(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag, long amount) {
            return new LibFluidIngredient(itemTag, fluidTag, amount);
        }
    }
}