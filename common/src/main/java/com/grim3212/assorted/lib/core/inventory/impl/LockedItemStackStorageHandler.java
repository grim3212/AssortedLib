package com.grim3212.assorted.lib.core.inventory.impl;

import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.LockedStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LockedItemStackStorageHandler extends ItemStackStorageHandler implements LockedStorageHandler {

    private final ILockable lockable;

    public LockedItemStackStorageHandler(ILockable lockable, int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.lockable = lockable;
    }

    public LockedItemStackStorageHandler(ILockable lockable, NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
        this.lockable = lockable;
    }

    private boolean codeMatches(String s) {
        return !this.lockable.isLocked() || this.lockable.getLockCode().equals(s);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, "", false);
    }

    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode, boolean ignoreLock) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack) || (!codeMatches(inLockCode) && !ignoreLock))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!Services.INVENTORY.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? Services.INVENTORY.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? Services.INVENTORY.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, "", false);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode, boolean ignoreLock) {
        if (amount == 0 || (!codeMatches(inLockCode) && !ignoreLock))
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, Services.INVENTORY.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return Services.INVENTORY.copyStackWithSize(existing, toExtract);
        }
    }
}
