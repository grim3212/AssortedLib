package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.config.ConfigBuilder;
import com.grim3212.assorted.lib.config.ForgeConfigUtil;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Consumer;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public String getCommonTagPrefix() {
        return "forge";
    }

    @Override
    public SoundType getSoundType(BlockState state, Level level, BlockPos pos, Player player) {
        return state.getSoundType(level, pos, player);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
        NetworkHooks.openScreen(player, provider, extraDataWriter);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public boolean isFakePlayer(Player player) {
        return player instanceof FakePlayer;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key) {
        return ForgeRegistryWrapper.getRegistry(key);
    }

    @Override
    public void setupCommonConfig(String modId, ConfigBuilder builder) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfigUtil.getConfigSpec(builder));
    }

    @Override
    public void setupClientConfig(String modId, ConfigBuilder builder) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfigUtil.getConfigSpec(builder));
    }
}
