package com.grim3212.assorted.lib.worldgen;

import com.grim3212.assorted.lib.platform.ForgeWorldGenHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class ForgeBiomeModifier implements BiomeModifier {

    public static final ForgeBiomeModifier INSTANCE = new ForgeBiomeModifier();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        ForgeWorldGenHelper.modifyBiome(biome, phase, builder);
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return LibForgeWorldGen.BIOME_MODIFIER_CODEC.get();
    }

}
