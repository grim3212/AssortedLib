package com.grim3212.assorted.lib.core.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Lets an item decide which enchantments it accepts, beyond the enchantments'
 * {@code supported_items} / {@code primary_items} tags. The methods match NeoForge's
 * {@code IItemExtension} signatures, so on NeoForge they override its defaults and every check
 * consults them; Fabric reaches them through {@code EnchantmentEvents.ALLOW_ENCHANTING}.
 * An implementation gives the whole answer; {@link #supportedByDefault} and
 * {@link #primaryByDefault} are what the item would have answered otherwise.
 */
public interface IItemEnchantmentCondition {

    /**
     * Whether the enchantment may be put on this stack at all - the anvil, loot, {@code /enchant}.
     */
    boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment);

    /**
     * Whether the enchantment may be rolled for this stack at an enchanting table or by a random
     * enchant. Should imply {@link #supportsEnchantment}.
     */
    boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment);

    // Read straight off the definition rather than through Enchantment#isSupportedItem/isPrimaryItem:
    // NeoForge deprecates those for ItemStack#supportsEnchantment/isPrimaryItemFor, which would call
    // straight back into the implementing item.
    static boolean supportedByDefault(ItemStack stack, Holder<Enchantment> enchantment) {
        return stack.is(enchantment.value().definition().supportedItems());
    }

    static boolean primaryByDefault(ItemStack stack, Holder<Enchantment> enchantment) {
        return supportedByDefault(stack, enchantment) && enchantment.value().definition().primaryItems().map(stack::is).orElse(true);
    }
}
