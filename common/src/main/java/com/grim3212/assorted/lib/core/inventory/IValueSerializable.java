package com.grim3212.assorted.lib.core.inventory;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Replaces raw {@code CompoundTag} round-tripping for storage handlers.
 * <p>
 * 26.x moved block entity and container state onto {@link ValueOutput} / {@link ValueInput}, which
 * carry the {@code HolderLookup.Provider} that item stack serialization now requires. There is no
 * longer a way to write an {@code ItemStack} to a bare {@code CompoundTag}, so handlers take the
 * value IO objects directly rather than returning a tag.
 */
public interface IValueSerializable {

    void serialize(ValueOutput output);

    void deserialize(ValueInput input);
}
