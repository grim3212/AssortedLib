package com.grim3212.assorted.lib.core.inventory.locking;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface LockedItemHandler {

    ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode);

    ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode);
}
