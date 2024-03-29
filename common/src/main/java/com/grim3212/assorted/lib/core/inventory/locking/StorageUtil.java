package com.grim3212.assorted.lib.core.inventory.locking;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class StorageUtil {
    public static void writeLock(CompoundTag nbt, String lock) {
        if (!lock.isEmpty()) {
            nbt.putString("Storage_Lock", lock);
        }
    }

    public static String readLock(CompoundTag nbt) {
        if (nbt == null) return "";

        return nbt.contains("Storage_Lock", 8) ? nbt.getString("Storage_Lock") : "";
    }

    public static ItemStack setCodeOnStack(String code, ItemStack stack) {
        ItemStack output = stack.copy();
        writeCodeToStack(code, output);
        return output;
    }

    public static void writeCodeToStack(String code, ItemStack stack) {
        if (stack.hasTag()) {
            writeLock(stack.getTag(), code);
        } else {
            CompoundTag tag = new CompoundTag();
            writeLock(tag, code);
            stack.setTag(tag);
        }
    }

    public static String getCode(BlockEntity te) {
        if (te instanceof ILockable) {
            return ((ILockable) te).getLockCode();
        }
        return "";
    }

    public static String getCode(ItemStack stack) {
        if (stack.hasTag()) {
            return readLock(stack.getTag());
        }
        return "";
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
