package com.grim3212.assorted.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

/**
 * Helpers for the free form NBT that used to live directly on an ItemStack.
 * <p>
 * Stack NBT no longer exists, so every ItemStack based method here now reads and
 * writes the {@link DataComponents#CUSTOM_DATA} component instead, which is the
 * data component that carries arbitrary modded NBT.
 * <p>
 * Two behaviours had to change:
 * <ul>
 * <li>{@link CustomData} is immutable, so every read hands back a detached copy.
 * Mutating a CompoundTag returned from here no longer writes through to the
 * stack, it has to be handed back through the matching put method.</li>
 * <li>The getters no longer persist their fallback onto the stack when the key
 * is missing, they only return it. Writing a component changes stack equality
 * and would stop otherwise identical stacks from stacking together.</li>
 * </ul>
 */
public class NBTHelper {

    /**
     * Reads the custom data component off the given ItemStack as a detached
     * CompoundTag, an empty one if the stack carries no custom data at all
     *
     * @param itemStack The ItemStack to read the custom data of
     */
    private static CompoundTag customData(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    /**
     * Applies the given change to a copy of the ItemStack's custom data and
     * stores the result back onto the stack
     *
     * @param itemStack The ItemStack whose custom data is being modified
     * @param modifier  The change to apply
     */
    private static void updateCustomData(ItemStack itemStack, Consumer<CompoundTag> modifier) {
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, modifier);
    }

    public static boolean hasTag(ItemStack itemStack, String keyName) {
        return !itemStack.isEmpty() && customData(itemStack).contains(keyName);
    }

    public static void removeTag(ItemStack itemStack, String keyName) {
        updateCustomData(itemStack, (tag) -> tag.remove(keyName));
    }

    public static CompoundTag getTag(CompoundTag compound, String keyName) {
        if (compound == null) {
            return new CompoundTag();
        }

        return compound.getCompoundOrEmpty(keyName);
    }

    public static CompoundTag getTag(ItemStack stack, String keyName) {
        return customData(stack).getCompoundOrEmpty(keyName);
    }

    public static void putTag(ItemStack stack, String keyName, CompoundTag compound) {
        updateCustomData(stack, (tag) -> tag.put(keyName, compound));
    }

    // =============== STRING ===============
    public static String getString(CompoundTag compound, String keyName) {
        if (compound == null) {
            return "";
        }

        return compound.getStringOr(keyName, "");
    }

    public static void putString(CompoundTag compound, String keyName, String keyValue) {
        if (compound == null) {
            return;
        }

        compound.putString(keyName, keyValue);
    }

    public static String getString(ItemStack itemStack, String keyName) {
        return customData(itemStack).getStringOr(keyName, "");
    }

    public static void putString(ItemStack itemStack, String keyName, String keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putString(keyName, keyValue));
    }

    public static ItemStack putStringItemStack(ItemStack itemStack, String keyName, String keyValue) {
        putString(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END STRING ===============

    // =============== BOOLEAN ===============
    public static boolean getBoolean(ItemStack itemStack, String keyName) {
        return customData(itemStack).getBooleanOr(keyName, false);
    }

    public static void putBoolean(ItemStack itemStack, String keyName, boolean keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putBoolean(keyName, keyValue));
    }

    public static ItemStack putBooleanItemStack(ItemStack itemStack, String keyName, boolean keyValue) {
        putBoolean(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END BOOLEAN ===============

    // =============== BYTE ===============
    public static byte getByte(ItemStack itemStack, String keyName) {
        return customData(itemStack).getByteOr(keyName, (byte) 0);
    }

    public static void putByte(ItemStack itemStack, String keyName, byte keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putByte(keyName, keyValue));
    }

    public static ItemStack putByteItemStack(ItemStack itemStack, String keyName, byte keyValue) {
        putByte(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END BYTE ===============

    // =============== SHORT ===============
    public static short getShort(ItemStack itemStack, String keyName) {
        return customData(itemStack).getShortOr(keyName, (short) 0);
    }

    public static void putShort(ItemStack itemStack, String keyName, short keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putShort(keyName, keyValue));
    }

    public static ItemStack putShortItemStack(ItemStack itemStack, String keyName, short keyValue) {
        putShort(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END SHORT ===============

    // =============== INTEGER ===============
    public static int getInt(CompoundTag compound, String keyName) {
        if (compound == null) {
            return 0;
        }

        return compound.getIntOr(keyName, 0);
    }

    public static void putInt(CompoundTag compound, String keyName, int keyValue) {
        if (compound == null) {
            return;
        }

        compound.putInt(keyName, keyValue);
    }

    public static int getInt(ItemStack itemStack, String keyName) {
        return getInt(itemStack, keyName, 0);
    }

    public static int getInt(ItemStack itemStack, String keyName, int fallback) {
        return customData(itemStack).getIntOr(keyName, fallback);
    }

    public static void putInt(ItemStack itemStack, String keyName, int keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putInt(keyName, keyValue));
    }

    public static ItemStack putIntItemStack(ItemStack itemStack, String keyName, int keyValue) {
        putInt(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END INTEGER ===============

    // =============== LONG ===============
    public static long getLong(CompoundTag compound, String keyName) {
        if (compound == null) {
            return 0;
        }

        return compound.getLongOr(keyName, 0);
    }

    public static void putLong(CompoundTag compound, String keyName, long keyValue) {
        if (compound == null) {
            return;
        }

        compound.putLong(keyName, keyValue);
    }

    public static long getLong(ItemStack itemStack, String keyName) {
        return customData(itemStack).getLongOr(keyName, 0);
    }

    public static void putLong(ItemStack itemStack, String keyName, long keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putLong(keyName, keyValue));
    }

    public static ItemStack putLongItemStack(ItemStack itemStack, String keyName, long keyValue) {
        putLong(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END LONG ===============

    // =============== FLOAT ===============
    public static float getFloat(ItemStack itemStack, String keyName) {
        return customData(itemStack).getFloatOr(keyName, 0);
    }

    public static void putFloat(ItemStack itemStack, String keyName, float keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putFloat(keyName, keyValue));
    }

    public static ItemStack putFloatItemStack(ItemStack itemStack, String keyName, float keyValue) {
        putFloat(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END FLOAT ===============

    // =============== DOUBLE ===============
    public static double getDouble(ItemStack itemStack, String keyName) {
        return customData(itemStack).getDoubleOr(keyName, 0);
    }

    public static void putDouble(ItemStack itemStack, String keyName, double keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putDouble(keyName, keyValue));
    }

    public static ItemStack putDoubleItemStack(ItemStack itemStack, String keyName, double keyValue) {
        putDouble(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END DOUBLE ===============

    // =============== BLOCK POS ===============
    public static BlockPos getBlockPos(ItemStack itemStack, String keyName) {
        return readBlockPos(customData(itemStack), keyName);
    }

    public static BlockPos getBlockPos(CompoundTag tag, String keyName) {
        if (tag == null) {
            return BlockPos.ZERO;
        }

        return readBlockPos(tag, keyName);
    }

    private static BlockPos readBlockPos(CompoundTag tag, String keyName) {
        int[] pos = tag.getIntArray(keyName).orElse(null);
        if (pos == null || pos.length < 3) {
            return BlockPos.ZERO;
        }

        return new BlockPos(pos[0], pos[1], pos[2]);
    }

    public static void putBlockPos(CompoundTag tag, String keyName, BlockPos keyValue) {
        if (tag == null) {
            return;
        }

        tag.putIntArray(keyName, new int[]{keyValue.getX(), keyValue.getY(), keyValue.getZ()});
    }

    public static void putBlockPos(ItemStack itemStack, String keyName, BlockPos keyValue) {
        updateCustomData(itemStack, (tag) -> tag.putIntArray(keyName, new int[]{keyValue.getX(), keyValue.getY(), keyValue.getZ()}));
    }

    public static ItemStack putBlockPosItemStack(ItemStack itemStack, String keyName, BlockPos keyValue) {
        putBlockPos(itemStack, keyName, keyValue);

        return itemStack;
    }
    // =============== END BLOCK POS ===============
}
