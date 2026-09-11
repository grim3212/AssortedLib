package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemStorageHandler {

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    int getSlots();

    /**
     * The stack in a slot, empty if the slot is empty; its count may exceed its max stack size.
     * <strong>Never modify the returned stack</strong>: this is not a way to change the inventory.
     */
    @NotNull
    ItemStack getStackInSlot(int slot);

    /**
     * Inserts a stack into a slot without modifying it.
     *
     * @param simulate if true, only simulates the insertion
     * @return the remainder not inserted: empty if all of it was, possibly the input itself if
     * nothing changed. Safe for the caller to modify.
     */
    @NotNull
    ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

    /**
     * Extracts up to {@code amount} from a slot; {@code amount} may exceed the max stack size.
     *
     * @param simulate if true, only simulates the extraction
     * @return a new stack of at most {@code amount} and its max stack size, empty if nothing could
     * be extracted. Safe for the caller to modify.
     */
    @NotNull
    ItemStack extractItem(int slot, int amount, boolean simulate);

    /** The largest stack allowed in the slot. */
    int getSlotLimit(int slot);

    /**
     * The equivalent of {@link Container#canPlaceItem(int, ItemStack)}: false if the stack can
     * never go in this slot. Ignores the current contents and fullness, so true still needs a
     * simulated insert.
     */
    boolean isItemValid(int slot, @NotNull ItemStack stack);

    /**
     * Overwrites the stack in a slot. For the library's own helpers, not general use; a handler may
     * throw if it is called unexpectedly.
     */
    void setStackInSlot(int slot, @NotNull ItemStack stack);

    default void startOpen(Player player) {
    }

    default void stopOpen(Player player) {
    }

    default boolean stillValid(Player player) {
        return !player.isDeadOrDying();
    }

    default boolean isEmpty() {
        for (int slot = 0; slot < this.getSlots(); slot++) {
            if (this.getStackInSlot(slot) != ItemStack.EMPTY) {
                return false;
            }
        }
        return true;
    }

    void onContentsChanged(int slot);
}
