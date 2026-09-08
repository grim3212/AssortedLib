package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * TODO(26.2): {@link IItemHandlerModifiable} (and {@link net.neoforged.neoforge.items.IItemHandler}
 * as a whole) is deprecated for removal in favour of NeoForge's transfer API - the item handler
 * capabilities are {@code BlockCapability<ResourceHandler<ItemResource>, Direction>} now
 * ({@code Capabilities.Item.BLOCK} / {@code .ENTITY} / {@code .ITEM}), and there is only an adapter
 * in the {@code ResourceHandler -> IItemHandler} direction ({@code IItemHandler.of}), not back.
 * Exposing an {@link IItemStorageHandler} as a capability therefore has to go through a real
 * {@code ResourceHandler<ItemResource>} implementation with transaction support, which is a redesign
 * of {@code IItemStorageHandler} rather than a rename; the deprecated interface is kept here so the
 * existing behaviour is preserved until that happens.
 */
public class ForgeItemStorageHandler implements IItemHandlerModifiable {

    private final IItemStorageHandler storage;

    public ForgeItemStorageHandler(@NotNull IItemStorageHandler storage) {
        this.storage = storage;
    }

    public IItemStorageHandler getStorage() {
        return storage;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.storage.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.storage.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.storage.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.storage.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.storage.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.storage.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.storage.isItemValid(slot, stack);
    }
}
