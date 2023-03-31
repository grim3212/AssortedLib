package com.grim3212.assorted.lib.util;

import net.minecraft.nbt.Tag;

public interface ITagSerializable<T extends Tag> {
    T serializeNBT();

    void deserializeNBT(T nbt);
}
