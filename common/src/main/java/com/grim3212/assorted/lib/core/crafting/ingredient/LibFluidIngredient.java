package com.grim3212.assorted.lib.core.crafting.ingredient;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibFluidIngredient {

    protected final TagKey<Item> itemTag;
    protected final TagKey<Fluid> fluidTag;
    protected final long amount;

    private List<ItemStack> itemStacks;

    protected LibFluidIngredient(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag) {
        this(itemTag, fluidTag, Services.FLUIDS.getBucketAmount());
    }

    public LibFluidIngredient(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag, long amount) {
        this.itemTag = itemTag;
        this.fluidTag = fluidTag;
        this.amount = amount;
    }

    public TagKey<Item> getItemTag() {
        return itemTag;
    }

    public TagKey<Fluid> getFluidTag() {
        return fluidTag;
    }

    public long getAmount() {
        return amount;
    }

    public boolean test(ItemStack stack) {
        if (stack == null)
            return false;
        // If the itemTag is null just check the fluid
        // If the itemTag isn't null check both the tag and the fluid
        return ((itemTag != null && stack.is(itemTag)) || itemTag == null) && doesInputMatchFluid(stack);
    }

    private boolean doesInputMatchFluid(ItemStack stack) {
        // Can we drain at least the amount given from this stack
        Optional<FluidInformation> fluidInformation = Services.FLUIDS.get(stack);
        if (fluidInformation.isPresent()) {
            long extracted = Services.FLUIDS.simulateExtract(stack, this.amount);
            return extracted == this.amount && fluidInformation.get().fluid().is(this.fluidTag);
        }
        return false;
    }

    public List<ItemStack> getMatchingStacks() {
        if (this.itemStacks == null) {
            this.itemStacks = new ArrayList<>();

            List<Fluid> fluids = Lists.newArrayList();
            BuiltInRegistries.FLUID.getTagOrEmpty(this.fluidTag).forEach(f -> fluids.add(f.value()));

            List<Item> items = Lists.newArrayList();
            BuiltInRegistries.ITEM.getTagOrEmpty(LibCommonTags.Items.FLUID_CONTAINERS).forEach(i -> items.add(i.value()));

            for (Fluid fluid : fluids) {
                for (Item itm : items) {
                    ItemStack stack = new ItemStack(itm);
                    Services.FLUIDS.get(stack).ifPresent((itemFluid) -> {
                        if (itemFluid.fluid().isSame(fluid) && itemFluid.amount() >= Services.FLUIDS.getBucketAmount()) {
                            if (this.itemStacks.stream().noneMatch(i -> ItemStack.matches(i, stack))) {
                                this.itemStacks.add(stack);
                            }
                        }
                    });
                }
            }
        }
        return this.itemStacks;
    }

    public void invalidate() {
        this.itemStacks = null;
    }

    public static abstract class Serializer<T extends LibFluidIngredient> {

        public ResourceLocation getIdentifier() {
            return new ResourceLocation(LibConstants.MOD_ID, "stored_fluid_ingredient");
        }

        public T read(JsonObject json) {
            TagKey<Item> itemTag = null;
            if (json.has("item")) {
                ResourceLocation itemLocation = new ResourceLocation(GsonHelper.getAsString(json, "item"));
                itemTag = TagKey.create(Registries.ITEM, itemLocation);
            }

            ResourceLocation fluidLocation = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
            TagKey<Fluid> fluidTag = TagKey.create(Registries.FLUID, fluidLocation);

            long amount = Services.FLUIDS.getBucketAmount();
            if (json.has("amount")) {
                amount = GsonHelper.getAsInt(json, "amount");
            }

            if (fluidTag == null) {
                throw new JsonSyntaxException("Must set 'fluid'");
            }

            return create(itemTag, fluidTag, amount);
        }

        public void write(JsonObject output, T ingredient) {
            if (ingredient.itemTag != null)
                output.addProperty("item", ingredient.itemTag.location().toString());
            output.addProperty("fluid", ingredient.fluidTag.location().toString());
            if (ingredient.amount > 0)
                output.addProperty("amount", ingredient.amount);
        }

        public T read(FriendlyByteBuf buf) {
            boolean hasItemTag = buf.readBoolean();
            TagKey<Item> itemTag = null;

            if (hasItemTag) {
                itemTag = TagKey.create(Registries.ITEM, buf.readResourceLocation());
            }
            TagKey<Fluid> fluidTag = TagKey.create(Registries.FLUID, buf.readResourceLocation());
            long amount = buf.readLong();
            return create(itemTag, fluidTag, amount);
        }

        public void write(FriendlyByteBuf buf, T ingredient) {
            if (ingredient.itemTag != null) {
                buf.writeBoolean(true);
                buf.writeResourceLocation(ingredient.itemTag.location());
            } else {
                buf.writeBoolean(false);
            }
            buf.writeResourceLocation(ingredient.fluidTag.location());
            buf.writeLong(ingredient.amount);
        }

        protected abstract T create(@Nullable TagKey<Item> itemTag, TagKey<Fluid> fluidTag, long amount);
    }
}
