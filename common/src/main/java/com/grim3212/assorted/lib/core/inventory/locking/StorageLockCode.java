package com.grim3212.assorted.lib.core.inventory.locking;

import net.minecraft.nbt.CompoundTag;

public class StorageLockCode {
    // TODO: This is kinda dumb to have a class for this
    // Get rid of this and just add the tag stuff into StorageUtil
    public static final StorageLockCode EMPTY_CODE = new StorageLockCode("");
    private final String lock;

    public StorageLockCode(String code) {
        this.lock = code;
    }

    public String getLockCode() {
        return lock;
    }

    public void write(CompoundTag nbt) {
        if (!this.lock.isEmpty()) {
            nbt.putString("Storage_Lock", this.lock);
        }
    }

    public static StorageLockCode read(CompoundTag nbt) {
        if (nbt == null) return EMPTY_CODE;

        return nbt.contains("Storage_Lock", 8) ? new StorageLockCode(nbt.getString("Storage_Lock")) : EMPTY_CODE;
    }
}
