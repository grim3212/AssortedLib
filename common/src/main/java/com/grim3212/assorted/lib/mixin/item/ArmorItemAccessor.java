package com.grim3212.assorted.lib.mixin.item;

import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public interface ArmorItemAccessor {
    @Accessor("ARMOR_MODIFIER_UUID_PER_TYPE")
    static EnumMap<ArmorItem.Type, UUID> assortedlib_getArmorModPerSlot() {
        throw new AssertionError();
    }
}
