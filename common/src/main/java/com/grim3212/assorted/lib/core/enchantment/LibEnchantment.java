package com.grim3212.assorted.lib.core.enchantment;

import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * TODO(26.2): Enchantments are fully data-driven now. {@code Enchantment} is a final record
 * loaded from JSON (description/definition/exclusiveSet/effects), so it can no longer be
 * extended and {@code EnchantmentCategory} no longer exists - what an enchantment may be
 * applied to is expressed with the {@code supported_items} / {@code primary_items} item
 * {@code HolderSet}s of {@code Enchantment.EnchantmentDefinition}.
 * <p>
 * The old subclass based extension point is therefore kept only as a duck typing interface so
 * the loader mixins have something to implement; nothing in vanilla can be an instance of it
 * unless a mixin makes it one. Prefer overriding the item side via
 * {@link com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition}.
 */
public interface LibEnchantment {

    /**
     * Override whether this enchantment can be applied to the given stack at an enchanting
     * table. An empty result falls back to the vanilla behaviour.
     *
     * @param stack the stack being enchanted
     * @return the override, or empty for vanilla behaviour
     */
    default Optional<Boolean> assortedlib_canApplyAtEnchantingTable(ItemStack stack) {
        return Optional.empty();
    }
}
