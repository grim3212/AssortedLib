package com.grim3212.assorted.lib.core.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class ExtraPropertyHelper {

    public static int getDamage(ItemStack stack) {
        return stack.getOrDefault(DataComponents.DAMAGE, 0);
    }

    public static int getMaxDamage(ItemStack stack) {
        return stack.getMaxDamage();
    }

    public static boolean isDamaged(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    public static void setDamage(ItemStack stack, int damage) {
        stack.set(DataComponents.DAMAGE, Math.max(0, damage));
    }
}
