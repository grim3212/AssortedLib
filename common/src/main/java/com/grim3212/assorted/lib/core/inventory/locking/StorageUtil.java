package com.grim3212.assorted.lib.core.inventory.locking;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StorageUtil {
    public static ItemStack setCodeOnStack(String code, ItemStack stack) {
        return setCodeOnStack(new StorageLockCode(code), stack);
    }

    public static ItemStack setCodeOnStack(StorageLockCode code, ItemStack stack) {
        ItemStack output = stack.copy();

        if (output.hasTag()) {
            code.write(output.getTag());
        } else {
            CompoundTag tag = new CompoundTag();
            code.write(tag);
            output.setTag(tag);
        }

        return output;
    }

    public static void writeCodeToStack(String code, ItemStack stack) {
        writeCodeToStack(new StorageLockCode(code), stack);
    }

    public static void writeCodeToStack(StorageLockCode code, ItemStack stack) {
        if (stack.hasTag()) {
            code.write(stack.getTag());
        } else {
            CompoundTag tag = new CompoundTag();
            code.write(tag);
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
            String code = stack.getTag().contains("Storage_Lock", 8) ? stack.getTag().getString("Storage_Lock") : "";
            return code;
        }
        return "";
    }

    public static boolean hasCodeWithMatch(ItemStack stack, String testCode) {
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
}
