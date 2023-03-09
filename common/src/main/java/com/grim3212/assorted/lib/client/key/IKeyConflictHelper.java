package com.grim3212.assorted.lib.client.key;

import net.minecraft.client.KeyMapping;

public interface IKeyConflictHelper {

    /**
     * @return true if conditions are met to activate {@link KeyMapping}s with this context
     */
    boolean isActive();

    /**
     * @return true if the other context can have {@link KeyMapping} conflicts with this one.
     * This will be called on both contexts to check for conflicts.
     */
    boolean conflicts(IKeyConflictHelper other);
}
