package com.grim3212.assorted.lib.mixin.world.level;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MonsterRoomFeature.class)
public interface MonsterRoomFeatureAccessor {
    @Accessor
    static EntityType<?>[] getMOBS() {
        throw new UnsupportedOperationException();
    }
}
