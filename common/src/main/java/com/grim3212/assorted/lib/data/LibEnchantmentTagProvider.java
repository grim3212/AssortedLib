package com.grim3212.assorted.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Enchantment tags for a mod's own enchantments.
 * <p>
 * Whether an enchantment is offered at the enchanting table, sold by librarians or rolled into loot
 * is only these vanilla tags in 26.2 - the 1.20.1 {@code isDiscoverable()} / {@code isTradeable()}
 * overrides went with the enchantment classes. Every entry written through {@link #obtainable} is
 * optional, so an enchantment switched off by a condition on its definition (see
 * {@link LibDatapackRegistryProvider#conditions()}) drops out of the tag instead of failing vanilla's tag with it.
 */
public abstract class LibEnchantmentTagProvider extends TagsProvider<Enchantment> {

    private static final List<TagKey<Enchantment>> OBTAINABLE = List.of(EnchantmentTags.IN_ENCHANTING_TABLE, EnchantmentTags.TRADEABLE, EnchantmentTags.ON_RANDOM_LOOT);

    // NeoForge deprecates this constructor in favour of one that also takes a mod id; that overload
    // does not exist in vanilla, which this module builds against.
    @SuppressWarnings("deprecation")
    public LibEnchantmentTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Registries.ENCHANTMENT, lookup);
    }

    /**
     * Makes the enchantments obtainable the way vanilla's own are: the enchanting table, librarian
     * trades and random loot.
     */
    protected void obtainable(List<ResourceKey<Enchantment>> enchantments) {
        for (TagKey<Enchantment> tag : OBTAINABLE) {
            TagAppender<Enchantment> appender = this.tag(tag);
            enchantments.forEach(appender::addOptional);
        }
    }
}
