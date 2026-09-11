package com.grim3212.assorted.lib.core.inventory.locking;

import net.minecraft.core.component.DataComponentGetter;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class StorageUtil {
    private static final String LOCK_KEY = "Storage_Lock";

    public static void writeLock(CompoundTag nbt, String lock) {
        if (!lock.isEmpty()) {
            nbt.putString(LOCK_KEY, lock);
        }
    }

    public static String readLock(CompoundTag nbt) {
        if (nbt == null) return "";

        return nbt.getStringOr(LOCK_KEY, "");
    }

    // Block entities save through ValueOutput / ValueInput, which cannot be bridged to a
    // CompoundTag, so the lock has these overloads beside the tag ones.
    public static void writeLock(ValueOutput output, String lock) {
        if (!lock.isEmpty()) {
            output.putString(LOCK_KEY, lock);
        }
    }

    public static String readLock(ValueInput input) {
        return input.getStringOr(LOCK_KEY, "");
    }

    public static ItemStack setCodeOnStack(String code, ItemStack stack) {
        ItemStack output = stack.copy();
        writeCodeToStack(code, output);
        return output;
    }

    // Stacks no longer carry a free-form tag; the equivalent is the CUSTOM_DATA component, which
    // holds an immutable CompoundTag that has to be replaced rather than mutated in place.
    public static void writeCodeToStack(String code, ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> writeLock(tag, code));
    }

    /**
     * Sets the lock cached on a stack, or removes it when the code is empty. {@link #writeCodeToStack}
     * only ever writes, so a lock that has been taken off would otherwise stay in the component.
     */
    public static void setLockOnStack(ItemStack stack, String lock) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (lock.isEmpty()) {
                tag.remove(LOCK_KEY);
            } else {
                tag.putString(LOCK_KEY, lock);
            }
        });
    }

    public static String getCode(BlockEntity te) {
        if (te instanceof ILockable) {
            return ((ILockable) te).getLockCode();
        }
        return "";
    }

    public static String getCode(ItemStack stack) {
        return getCode((DataComponentGetter) stack);
    }

    /** The lock on anything with components: a stack, or the getter a {@code TooltipProvider} is handed. */
    public static String getCode(DataComponentGetter components) {
        return readLock(components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
    }

    public static boolean hasCodeWithMatch(ItemStack stack, String testCode) {
        // We are checking for an invalid code so any stack will match
        if (testCode == null || testCode.isEmpty()) {
            return true;
        }

        String code = getCode(stack);
        return !code.isEmpty() && code.equals(testCode);
    }

    public static boolean hasCode(ItemStack stack) {
        String code = getCode(stack);
        return !code.isEmpty();
    }

    public static void dropContents(Level level, BlockPos pos, IItemStorageHandler storageHandler) {
        for (int slot = 0; slot < storageHandler.getSlots(); slot++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), storageHandler.getStackInSlot(slot));
        }
    }

    public static int getRedstoneSignalFromContainer(@Nullable ItemStackStorageHandler itemHandler) {
        if (itemHandler == null) {
            return 0;
        } else {
            int $$1 = 0;
            float $$2 = 0.0F;

            for (int slot = 0; slot < itemHandler.getSlots(); ++slot) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    $$2 += (float) stack.getCount() / (float) Math.min(itemHandler.getSlotLimit(slot), stack.getMaxStackSize());
                    ++$$1;
                }
            }

            $$2 /= (float) itemHandler.getSlots();
            return Mth.floor($$2 * 14.0F) + ($$1 > 0 ? 1 : 0);
        }
    }
}
