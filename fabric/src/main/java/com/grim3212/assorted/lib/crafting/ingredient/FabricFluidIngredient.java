package com.grim3212.assorted.lib.crafting.ingredient;

import com.grim3212.assorted.lib.core.crafting.ingredient.LibFluidIngredient;
import com.grim3212.assorted.lib.platform.Services;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

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

    /**
     * The items this ingredient may ever match, for the recipe book display and the ingredient
     * index. The contained fluid is checked per stack in {@link #test(ItemStack)} and drawn by
     * {@code display()}, which is inherited from {@link LibFluidIngredient} - a superclass method
     * wins over the interface default, so both loaders draw the filled containers the same way.
     */
    @Override
    public Stream<Holder<Item>> items() {
        return getMatchingStacks().stream().map(ItemStack::typeHolder).distinct();
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    static class Serializer extends LibFluidIngredient.Serializer<FabricFluidIngredient> implements CustomIngredientSerializer<FabricFluidIngredient> {

        // The json/buffer pair the common serializer still carries is not a registration mechanism
        // anymore: Fabric asks for a MapCodec and a StreamCodec, so the same three fields are
        // described once here and both directions fall out of it.
        private static final MapCodec<FabricFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.ITEM).optionalFieldOf("item").forGetter(ingredient -> Optional.ofNullable(ingredient.getItemTag())),
                TagKey.codec(Registries.FLUID).fieldOf("fluid").forGetter(LibFluidIngredient::getFluidTag),
                Codec.LONG.optionalFieldOf("amount").forGetter(ingredient -> Optional.of(ingredient.getAmount()))
        ).apply(instance, Serializer::create));

        private static final StreamCodec<RegistryFriendlyByteBuf, FabricFluidIngredient> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(TagKey.streamCodec(Registries.ITEM)), ingredient -> Optional.ofNullable(ingredient.getItemTag()),
                TagKey.streamCodec(Registries.FLUID), LibFluidIngredient::getFluidTag,
                ByteBufCodecs.VAR_LONG, LibFluidIngredient::getAmount,
                (itemTag, fluidTag, amount) -> new FabricFluidIngredient(itemTag.orElse(null), fluidTag, amount)
        );

        private static FabricFluidIngredient create(final Optional<TagKey<Item>> itemTag, final TagKey<Fluid> fluidTag, final Optional<Long> amount) {
            return new FabricFluidIngredient(itemTag.orElse(null), fluidTag, amount.orElseGet(() -> Services.FLUIDS.getBucketAmount()));
        }

        @Override
        protected FabricFluidIngredient create(@Nullable TagKey itemTag, TagKey fluidTag, long amount) {
            return new FabricFluidIngredient(itemTag, fluidTag, amount);
        }

        @Override
        public Identifier getIdentifier() {
            return super.getIdentifier();
        }

        @Override
        public MapCodec<FabricFluidIngredient> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FabricFluidIngredient> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
