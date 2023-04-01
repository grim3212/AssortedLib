package com.grim3212.assorted.lib.core.inventory.impl;

import com.grim3212.assorted.lib.core.inventory.locking.LockedItemHandler;
import com.grim3212.assorted.lib.core.inventory.locking.LockedWorldlyContainer;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LockedSidedStorageHandler extends SidedStorageHandler implements LockedItemHandler {


    public LockedSidedStorageHandler(LockedWorldlyContainer inv, @Nullable Direction side) {
        super(inv, side);
    }

    public LockedWorldlyContainer getLockedContainer() {
        return (LockedWorldlyContainer) inv;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, "");
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return stack;

        ItemStack stackInSlot = inv.getItem(slot1);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
                return stack;

            if (!Services.INVENTORY.canItemStacksStack(stack, stackInSlot))
                return stack;

            if (!getLockedContainer().canPlaceItemThroughFace(slot1, stack, side, inLockCode, false) || !inv.canPlaceItem(slot1, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(slot1, copy);
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(slot1, copy);
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (!getLockedContainer().canPlaceItemThroughFace(slot1, stack, side, inLockCode, false) || !inv.canPlaceItem(slot1, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    setInventorySlotContents(slot1, stack.split(m));
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate)
                    setInventorySlotContents(slot1, stack);
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, "");
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode) {
        if (amount == 0)
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = inv.getItem(slot1);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (!getLockedContainer().canTakeItemThroughFace(slot1, stackInSlot, side, inLockCode, false))
            return ItemStack.EMPTY;

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getCount(), amount);
            ItemStack ret = inv.removeItem(slot1, m);
            inv.setChanged();
            return ret;
        }
    }
}
