package com.grim3212.assorted.lib.inventory;

import com.grim3212.assorted.lib.core.inventory.ISerializableItemStorageHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeItemStackStorageWrapper implements ICapabilitySerializable<Tag> {

    private final ISerializableItemStorageHandler inventory;
    private final LazyOptional<IItemHandler> optional;

    public ForgeItemStackStorageWrapper(ISerializableItemStorageHandler handler) {
        inventory = handler;
        optional = LazyOptional.of(() -> new ForgeItemStorageHandler(handler));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
        this.inventory.save();
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        this.inventory.load();
    }
}
