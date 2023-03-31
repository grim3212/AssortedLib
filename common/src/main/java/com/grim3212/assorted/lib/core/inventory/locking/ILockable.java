package com.grim3212.assorted.lib.core.inventory.locking;

public interface ILockable {

    boolean isLocked();

    String getLockCode();

    StorageLockCode getStorageLockCode();

    void setLockCode(String s);

}
