package com.grim3212.assorted.lib.worldgen;

import com.grim3212.assorted.lib.LibConstants;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LibForgeWorldGen {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, LibConstants.MOD_ID);

    public static final RegistryObject<Codec<ForgeBiomeModifier>> BIOME_MODIFIER_CODEC = BIOME_MODIFIERS.register("lib_biome_modifier", () -> Codec.unit(ForgeBiomeModifier.INSTANCE));


}
