package com.grim3212.assorted.lib.conditions;

import com.grim3212.assorted.lib.LibConstants;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * The shared {@code Serializer} record is gone with {@code IConditionSerializer}; every concrete
 * subclass owns the {@link MapCodec} that identifies it instead.
 */
public abstract class TagPopulatedCondition<T> implements ICondition {

    protected final TagKey<T> tag;

    public TagPopulatedCondition(ResourceKey<? extends Registry<T>> registry, Identifier tag) {
        this.tag = TagKey.create(registry, tag);
    }

    public TagKey<T> getTag() {
        return this.tag;
    }

    /**
     * Asks whether the tag is declared, not what is in it, because that is the question the Fabric
     * half of this condition asks. {@code fabric:tags_populated} is named for contents but tests
     * {@code HolderGetter#get(TagKey).isEmpty()}, and that {@code Optional} is empty only when no
     * pack declares the tag at all; {@code IContext#getTag} hands back the contents, so testing it
     * the way {@code neoforge:tag_empty} does answers a stricter question. Both are right on their
     * own loader - but one datagen writes both files, so they have to be asked the same thing, and
     * a full mod set differed by 451 recipes until they were.
     */
    @Override
    public boolean test(IContext context) {
        return context.isTagLoaded(this.tag);
    }

    @Override
    public String toString() {
        return this.tag.registry().identifier() + "_tag_populated(\"" + this.tag.location() + "\")";
    }

    public static class ItemTagPopulatedCondition extends TagPopulatedCondition<Item> {

        public static final Identifier NAME = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "item_tag_populated");
        public static final MapCodec<ItemTagPopulatedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Identifier.CODEC.fieldOf("tag").forGetter(condition -> condition.tag.location()))
                .apply(instance, ItemTagPopulatedCondition::new));

        public ItemTagPopulatedCondition(Identifier tag) {
            super(Registries.ITEM, tag);
        }

        @Override
        public MapCodec<? extends ICondition> codec() {
            return CODEC;
        }
    }

    public static class BlockTagPopulatedCondition extends TagPopulatedCondition<Block> {

        public static final Identifier NAME = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "block_tag_populated");
        public static final MapCodec<BlockTagPopulatedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Identifier.CODEC.fieldOf("tag").forGetter(condition -> condition.tag.location()))
                .apply(instance, BlockTagPopulatedCondition::new));

        public BlockTagPopulatedCondition(Identifier tag) {
            super(Registries.BLOCK, tag);
        }

        @Override
        public MapCodec<? extends ICondition> codec() {
            return CODEC;
        }
    }
}
