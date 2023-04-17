package com.grim3212.assorted.lib.mixin.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;

@Mixin(ArmorMaterials.class)
public interface ArmorMaterialsMixin {

    @Accessor("HEALTH_FUNCTION_FOR_TYPE")
    static EnumMap<ArmorItem.Type, Integer> assortedlib_getHealthperSlot() {
        throw new AssertionError();
    }
}
