package com.grim3212.assorted.lib.core.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ExtraPropertyItem extends Item implements IItemExtraProperties {
    public ExtraPropertyItem(Properties props) {
        super(props);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return ExtraPropertyHelper.getDamage(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ExtraPropertyHelper.getMaxDamage(stack);
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return ExtraPropertyHelper.isDamaged(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        ExtraPropertyHelper.setDamage(stack, damage);
    }

}
