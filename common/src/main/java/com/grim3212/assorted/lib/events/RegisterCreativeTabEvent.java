package com.grim3212.assorted.lib.events;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class RegisterCreativeTabEvent extends GenericEvent {

    private final TabCreator creator;

    public RegisterCreativeTabEvent(TabCreator creator) {
        this.creator = creator;
    }

    public TabCreator getCreator() {
        return creator;
    }

    @FunctionalInterface
    public interface TabCreator {
        void create(ResourceLocation id, Component title, Supplier<ItemStack> icon, Supplier<List<ItemStack>> displayStacks);
    }
}
