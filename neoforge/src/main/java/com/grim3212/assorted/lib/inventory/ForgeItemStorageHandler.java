package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes an {@link IItemStorageHandler} as NeoForge's {@link ResourceHandler}, the type behind
 * {@code Capabilities.Item.*}.
 * <p>
 * {@code IItemHandler} is deprecated for removal and is deliberately not used anywhere here. The
 * two models differ in more than naming: a {@link ResourceHandler} works in terms of an
 * {@link ItemResource} (an item plus its component patch, with no count) and an explicit amount,
 * and every mutation takes part in a {@link TransactionContext} that may later be rolled back,
 * where the old interface used a {@code simulate} flag.
 * <p>
 * Rollback is provided by snapshotting the backing slots through a {@link SnapshotJournal}. The
 * whole slot list is copied per transaction rather than tracking individual slots; the handlers
 * this wraps are machine inventories of a few slots, so the simplicity is worth more than the
 * saved copies.
 */
public class ForgeItemStorageHandler implements ResourceHandler<ItemResource> {

    private final IItemStorageHandler storage;
    private final SlotJournal journal = new SlotJournal();

    public ForgeItemStorageHandler(@NotNull IItemStorageHandler storage) {
        this.storage = storage;
    }

    public IItemStorageHandler getStorage() {
        return storage;
    }

    @Override
    public int size() {
        return this.storage.getSlots();
    }

    @Override
    public ItemResource getResource(int index) {
        return ItemResource.of(this.storage.getStackInSlot(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        return this.storage.getStackInSlot(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return this.storage.getSlotLimit(index);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return this.storage.isItemValid(index, resource.toStack(1));
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }

        this.journal.updateSnapshots(transaction);
        ItemStack remainder = this.storage.insertItem(index, resource.toStack(amount), false);
        return amount - remainder.getCount();
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0 || !resource.matches(this.storage.getStackInSlot(index))) {
            return 0;
        }

        this.journal.updateSnapshots(transaction);
        return this.storage.extractItem(index, amount, false).getCount();
    }

    /**
     * Copies every slot on the first mutation inside a transaction and restores them if that
     * transaction is rolled back.
     */
    private final class SlotJournal extends SnapshotJournal<List<ItemStack>> {

        @Override
        protected List<ItemStack> createSnapshot() {
            List<ItemStack> snapshot = new ArrayList<>(storage.getSlots());
            for (int slot = 0; slot < storage.getSlots(); slot++) {
                snapshot.add(storage.getStackInSlot(slot).copy());
            }
            return snapshot;
        }

        @Override
        protected void revertToSnapshot(List<ItemStack> snapshot) {
            for (int slot = 0; slot < snapshot.size(); slot++) {
                storage.setStackInSlot(slot, snapshot.get(slot));
            }
        }
    }
}
