package com.grim3212.assorted.lib.worldgen;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.ForgeWorldGenHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

public class LibForgeWorldGen {
    @Nullable
    private static Codec<ForgeBiomeModifier> libBiomeModifierCodec = null;

    public static void init(IEventBus modEventBus) {
        modEventBus.<RegisterEvent>addListener(event -> {
            event.register(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, registry -> {
                registry.register(new ResourceLocation(LibConstants.MOD_ID, "lib_biome_modifier"), libBiomeModifierCodec = Codec.unit(ForgeBiomeModifier.INSTANCE));
            });
        });
    }

    private static class ForgeBiomeModifier implements BiomeModifier {

        private static final ForgeBiomeModifier INSTANCE = new ForgeBiomeModifier();

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            ForgeWorldGenHelper.modifyBiome(biome, phase, builder);
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            if (libBiomeModifierCodec != null) {
                return libBiomeModifierCodec;
            } else {
                return Codec.unit(INSTANCE);
            }
        }

    }
}
