package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FabricWrappedItemHandler implements IItemStorageHandler {

    private final InventoryStorage storage;

    public FabricWrappedItemHandler(@NotNull InventoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public int getSlots() {
        return this.storage.getSlots().size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.storage.getSlot(slot).getResource().toStack();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        if (!slotStorage.supportsInsertion() || stack.isEmpty()) {
            return stack;
        }

        if (!Services.INVENTORY.canItemStacksStack(stack, slotStorage.getResource().toStack())) {
            return stack;
        }

        ItemVariant toInsert = ItemVariant.of(stack);
        if (simulate) {
            int inserted = (int) slotStorage.simulateInsert(toInsert, stack.getCount(), null);
            if (inserted <= 0) {
                return stack;
            } else {
                int remainder = stack.getCount() - inserted;
                if (remainder > 0) {
                    return stack.copyWithCount(remainder);
                }
                return ItemStack.EMPTY;
            }
        }

        try (final Transaction context = Transaction.openOuter()) {
            int inserted = (int) slotStorage.insert(toInsert, stack.getCount(), context);
            if (inserted <= 0) {
                return stack;
            } else {
                int remainder = stack.getCount() - inserted;
                if (remainder > 0) {
                    return stack.copyWithCount(remainder);
                }
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        if (!slotStorage.supportsExtraction() || amount < 0) {
            return ItemStack.EMPTY;
        }

        ItemVariant variant = slotStorage.getResource();
        if (variant.isBlank()) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            int extracted = (int) slotStorage.simulateExtract(variant, amount, null);
            if (extracted <= 0) {
                return ItemStack.EMPTY;
            } else {
                return slotStorage.getResource().toStack().copyWithCount(extracted);
            }
        }

        try (final Transaction context = Transaction.openOuter()) {
            int extracted = (int) slotStorage.extract(variant, amount, context);
            if (extracted <= 0) {
                return ItemStack.EMPTY;
            } else {
                return slotStorage.getResource().toStack().copyWithCount(extracted);
            }
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int) this.storage.getSlot(slot).getCapacity();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        return slotStorage.supportsInsertion() && slotStorage.simulateInsert(ItemVariant.of(stack), stack.getCount(), null) > 0;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        SingleSlotStorage<ItemVariant> slotStorage = this.storage.getSlot(slot);
        if (slotStorage.supportsInsertion()) {
            try (final Transaction context = Transaction.openOuter()) {
                // Extract everything from the slot
                slotStorage.extract(slotStorage.getResource(), slotStorage.getCapacity(), context);
                // Then add the stack in to set
                slotStorage.insert(ItemVariant.of(stack), stack.getCount(), context);
            }
        }
    }

}
