package com.grim3212.assorted.lib.core.item;

import net.minecraft.world.item.ItemStack;

public interface IItemExtraProperties {

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the Damage
     * entry in the stack NBT, but can be overridden here for other sources.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    int getDamage(ItemStack stack);

    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in
     * this item, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    int getMaxDamage(ItemStack stack);

    /**
     * Return if this itemstack is damaged. Note only called if
     * {@link ItemStack#isDamageableItem()} is true.
     *
     * @param stack the stack
     * @return if the stack is damaged
     */
    boolean isDamaged(ItemStack stack);

    /**
     * Set the damage for this itemstack. Note, this method is responsible for zero
     * checking.
     *
     * @param stack  the stack
     * @param damage the new damage value
     */
    void setDamage(ItemStack stack, int damage);
}
