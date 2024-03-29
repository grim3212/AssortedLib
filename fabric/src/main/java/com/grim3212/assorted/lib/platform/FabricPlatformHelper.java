package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public String getCommonTagPrefix() {
        return "c";
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
        player.openMenu(new ExtendedScreenHandlerImpl(provider, extraDataWriter));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isFakePlayer(Player player) {
        return false;
    }

    @Override
    public double getPlayerReachDistance(Player player) {
        return player.isCreative() ? 5.0F : 4.5F;
    }

    @Override
    public Dist getCurrentDistribution() {
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> Dist.CLIENT;
            case SERVER -> Dist.DEDICATED_SERVER;
        };
    }

    @Override
    public boolean isPhysicalClient() {
        return this.getCurrentDistribution() == Dist.CLIENT;
    }

    @Override
    public boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key) {
        return FabricRegistryWrapper.getRegistry(key);
    }

    @Override
    public void modifyCreativeTab(ResourceKey<CreativeModeTab> key, Supplier<List<ItemStack>> displayStacks) {
        ItemGroupEvents.modifyEntriesEvent(key).register(populator -> {
            populator.acceptAll(displayStacks.get());
        });
    }

    @Override
    public void addReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener) {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return identifier;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                return reloadListener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            }
        });
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> builder, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(builder::apply, blocks).build(null);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return new ExtendedScreenHandlerType(factory::create);
    }

    @Override
    public int getFuelTime(ItemStack stack) {
        Integer fuelTime = FuelRegistry.INSTANCE.get(stack.getItem());
        return fuelTime != null ? fuelTime : 0;
    }

    public static class ExtendedScreenHandlerImpl implements ExtendedScreenHandlerFactory {
        private final MenuProvider provider;
        private final Consumer<FriendlyByteBuf> extraDataWriter;

        public ExtendedScreenHandlerImpl(MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
            this.provider = provider;
            this.extraDataWriter = extraDataWriter;
        }

        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
            extraDataWriter.accept(buf);
        }

        @Override
        public Component getDisplayName() {
            return provider.getDisplayName();
        }

        @Override
        public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
            return provider.createMenu(windowId, inventory, player);
        }
    }
}