package com.grim3212.assorted.lib.mixin.item;

import net.minecraft.world.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmorMaterials.class)
public interface ArmorMaterialsMixin {

    @Accessor("HEALTH_PER_SLOT")
    static int[] assortedlib_getHealthperSlot() {
        throw new AssertionError();
    }
}
