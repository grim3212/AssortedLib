package com.grim3212.assorted.lib.core.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Lets an item decide for itself which enchantments it accepts, beyond what the enchantments'
 * {@code supported_items} / {@code primary_items} tags say.
 * <p>
 * Both methods deliberately have exactly the signatures of NeoForge's
 * {@code IItemExtension#supportsEnchantment} and {@code IItemExtension#isPrimaryItemFor}. Common
 * compiles against vanilla, where they are only this interface's methods; when the NeoForge module
 * recompiles common, an implementing item's methods override NeoForge's defaults as well, so
 * NeoForge's own anvil, enchanting table, loot and {@code /enchant} checks consult them with no
 * further wiring. Fabric reaches them through {@code EnchantmentEvents.ALLOW_ENCHANTING}.
 * <p>
 * An implementation gives the whole answer, not just an override of it; {@link #supportedByDefault}
 * and {@link #primaryByDefault} are the answer the item would have had otherwise.
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
