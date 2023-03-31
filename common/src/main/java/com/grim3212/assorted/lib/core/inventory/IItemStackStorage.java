package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.world.item.ItemStack;

public interface IItemStackStorage {
    IItemStorageHandler getItemStorageHandler(ItemStack stack);
}
