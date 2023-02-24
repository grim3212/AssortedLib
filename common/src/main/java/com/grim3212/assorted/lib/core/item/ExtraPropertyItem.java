package com.grim3212.assorted.lib.core.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ExtraPropertyItem extends Item implements IItemExtraProperties {
    public ExtraPropertyItem(Properties props) {
        super(props);
    }

    public int getDamage(ItemStack stack) {
        return !stack.hasTag() ? 0 : stack.getTag().getInt("Damage");
    }


    public int getMaxDamage(ItemStack stack) {
        return this.getMaxDamage();
    }


    public boolean isDamaged(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    public void setDamage(ItemStack stack, int damage) {
        stack.getOrCreateTag().putInt("Damage", Math.max(0, damage));
    }
}
