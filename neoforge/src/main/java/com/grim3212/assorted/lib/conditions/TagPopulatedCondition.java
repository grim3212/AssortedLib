package com.grim3212.assorted.lib.conditions;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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

    public TagPopulatedCondition(ResourceKey<? extends Registry<T>> registry, Identifier tag) {
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

    public record Serializer<T>(Identifier name,
                                Function<Identifier, TagPopulatedCondition<T>> factory) implements IConditionSerializer<TagPopulatedCondition<T>> {

        @Override
        public void write(JsonObject json, TagPopulatedCondition value) {
            json.addProperty("tag", value.tag.location().toString());
        }

        @Override
        public TagPopulatedCondition<T> read(JsonObject json) {
            return this.factory.apply(Identifier.parse(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public Identifier getID() {
            return this.name;
        }
    }

    public static class ItemTagPopulatedCondition extends TagPopulatedCondition<Item> {

        public static final Identifier NAME = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "item_tag_populated");
        public static Serializer<Item> SERIALIZER = new Serializer<>(NAME, ItemTagPopulatedCondition::new);

        public ItemTagPopulatedCondition(Identifier tag) {
            super(Registries.ITEM, tag);
        }

        @Override
        public Identifier getID() {
            return NAME;
        }
    }

    public static class BlockTagPopulatedCondition extends TagPopulatedCondition<Block> {

        public static final Identifier NAME = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "block_tag_populated");
        public static Serializer<Block> SERIALIZER = new Serializer<>(NAME, BlockTagPopulatedCondition::new);

        public BlockTagPopulatedCondition(Identifier tag) {
            super(Registries.BLOCK, tag);
        }

        @Override
        public Identifier getID() {
            return NAME;
        }
    }
}
