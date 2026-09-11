package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Presents a foreign {@link ResourceHandler} as an {@link IItemStorageHandler}, the inverse of
 * {@link ForgeItemStorageHandler}. A {@code simulate} call runs in a transaction that is only
 * committed when the caller wants the change.
 */
public class ForgeWrappedItemHandler implements IItemStorageHandler {

    private final ResourceHandler<ItemResource> storage;
    private final BlockEntity entity;

    public ForgeWrappedItemHandler(@Nullable BlockEntity entity, @NotNull ResourceHandler<ItemResource> storage) {
        this.storage = storage;
        this.entity = entity;
    }

    /**
     * Nests inside whatever transaction is already running, so an operation performed while a
     * caller holds one is rolled back with it rather than committing independently.
     */
    private static Transaction openTransaction() {
        TransactionContext current = Transaction.getCurrentOpenedTransaction();
        return current == null ? Transaction.openRoot() : Transaction.open(current);
    }

    @Override
    public int getSlots() {
        return this.storage.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.storage.getResource(slot).toStack(this.storage.getAmountAsInt(slot));
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try (Transaction transaction = openTransaction()) {
            int inserted = this.storage.insert(slot, ItemResource.of(stack), stack.getCount(), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return stack.copyWithCount(stack.getCount() - inserted);
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemResource resource = this.storage.getResource(slot);
        if (resource.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        try (Transaction transaction = openTransaction()) {
            int extracted = this.storage.extract(slot, resource, amount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return extracted <= 0 ? ItemStack.EMPTY : resource.toStack(extracted);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.storage.getCapacityAsInt(slot, this.storage.getResource(slot));
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.storage.isValid(slot, ItemResource.of(stack));
    }

    /**
     * A {@link ResourceHandler} has no "overwrite this slot" operation - that was an artefact of
     * the old interface exposing its backing array. The nearest faithful equivalent is to empty
     * the slot and put the new contents in, done atomically so a partial write cannot be observed.
     */
    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        try (Transaction transaction = openTransaction()) {
            ItemResource current = this.storage.getResource(slot);
            if (!current.isEmpty()) {
                this.storage.extract(slot, current, this.storage.getAmountAsInt(slot), transaction);
            }
            if (!stack.isEmpty()) {
                this.storage.insert(slot, ItemResource.of(stack), stack.getCount(), transaction);
            }
            transaction.commit();
        }
    }

    @Override
    public void onContentsChanged(int slot) {
        if (this.entity != null)
            this.entity.setChanged();
    }
}
