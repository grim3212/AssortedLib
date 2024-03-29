package com.grim3212.assorted.lib.core.item;

import net.minecraft.world.item.ItemStack;

public class ExtraPropertyHelper {

    public static int getDamage(ItemStack stack) {
        return !stack.hasTag() ? 0 : stack.getTag().getInt("Damage");
    }

    public static int getMaxDamage(ItemStack stack) {
        return stack.getMaxDamage();
    }

    public static boolean isDamaged(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    public static void setDamage(ItemStack stack, int damage) {
        stack.getOrCreateTag().putInt("Damage", Math.max(0, damage));
    }
}
