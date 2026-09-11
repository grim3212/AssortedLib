package com.grim3212.assorted.lib.core.item;

import net.minecraft.world.item.ItemStack;

public interface IItemExtraProperties {

    /** The stack's current damage. */
    int getDamage(ItemStack stack);

    /** The stack's maximum damage. */
    int getMaxDamage(ItemStack stack);

    /** Whether the stack is damaged; only asked if {@link ItemStack#isDamageableItem()} is true. */
    boolean isDamaged(ItemStack stack);

    /** Sets the stack's damage. The implementation is responsible for checking it against zero. */
    void setDamage(ItemStack stack, int damage);
}
