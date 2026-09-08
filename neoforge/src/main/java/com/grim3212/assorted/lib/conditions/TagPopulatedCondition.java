package com.grim3212.assorted.lib.conditions;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.function.Function;

public abstract class TagPopulatedCondition<T> implements ICondition {

    private final ResourceKey<? extends Registry<T>> registry;
    private final TagKey<T> tag;

    public TagPopulatedCondition(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag) {
        this.tag = TagKey.create(registry, tag);
        this.registry = registry;
    }

    @Override
    public boolean test(IContext context) {
        return !context.getTag(this.tag).isEmpty();
    }

    @Override
    public String toString() {
        return this.tag.registry().registry() + "_tag_populated(\"" + this.tag + "\")";
    }

    public record Serializer<T>(ResourceLocation name,
                                Function<ResourceLocation, TagPopulatedCondition<T>> factory) implements IConditionSerializer<TagPopulatedCondition<T>> {

        @Override
        public void write(JsonObject json, TagPopulatedCondition value) {
            json.addProperty("tag", value.tag.location().toString());
        }

        @Override
        public TagPopulatedCondition<T> read(JsonObject json) {
            return this.factory.apply(new ResourceLocation(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID() {
            return this.name;
        }
    }

    public static class ItemTagPopulatedCondition extends TagPopulatedCondition<Item> {

        public static final ResourceLocation NAME = new ResourceLocation(LibConstants.MOD_ID, "item_tag_populated");
        public static Serializer<Item> SERIALIZER = new Serializer<>(NAME, ItemTagPopulatedCondition::new);

        public ItemTagPopulatedCondition(ResourceLocation tag) {
            super(Registries.ITEM, tag);
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }

    public static class BlockTagPopulatedCondition extends TagPopulatedCondition<Block> {

        public static final ResourceLocation NAME = new ResourceLocation(LibConstants.MOD_ID, "block_tag_populated");
        public static Serializer<Block> SERIALIZER = new Serializer<>(NAME, BlockTagPopulatedCondition::new);

        public BlockTagPopulatedCondition(ResourceLocation tag) {
            super(Registries.BLOCK, tag);
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
