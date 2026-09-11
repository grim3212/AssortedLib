package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Serialises a storage handler through {@link ValueOutput} / {@link ValueInput}, which carry the
 * registry lookup item stacks need; a stack cannot be written to a bare {@code CompoundTag}.
 */
public interface IValueSerializable {

    void serialize(ValueOutput output);

    void deserialize(ValueInput input);
}
