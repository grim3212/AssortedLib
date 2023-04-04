package com.grim3212.assorted.lib.core.inventory.locking;

public interface ILockable {
    ILockable EMPTY = new ILockable() {
        @Override
        public boolean isLocked() {
            return false;
        }

        @Override
        public String getLockCode() {
            return "";
        }

        @Override
        public StorageLockCode getStorageLockCode() {
            return StorageLockCode.EMPTY_CODE;
        }

        @Override
        public void setLockCode(String s) {
            // nope
        }
    };

    static ILockable CONSTANT(String code) {
        return new ILockable() {
            @Override
            public boolean isLocked() {
                return !code.isEmpty();
            }

            @Override
            public String getLockCode() {
                return code;
            }

            @Override
            public StorageLockCode getStorageLockCode() {
                return new StorageLockCode(code);
            }

            @Override
            public void setLockCode(String s) {
                // nope
            }
        };
    }


    boolean isLocked();

    String getLockCode();

    StorageLockCode getStorageLockCode();

    void setLockCode(String s);

}
