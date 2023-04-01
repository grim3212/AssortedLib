package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IInventoryItem extends IInventory<Item> {
    IPlatformInventoryStorageHandler getStorageHandler(ItemStack stack);
}
