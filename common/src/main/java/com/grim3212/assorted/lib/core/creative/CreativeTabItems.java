package com.grim3212.assorted.lib.core.creative;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class CreativeTabItems {
    List<ItemStack> items = new ArrayList<>();

    public void add(ItemLike item) {
        items.add(new ItemStack(item));
    }

    public void add(ItemStack item) {
        items.add(item);
    }

    public List<ItemStack> getItems() {
        return this.items;
    }
}
