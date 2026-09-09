package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    // TODO(26.2): IPlatformHelper still passes the extra menu opening data as a raw
    //  FriendlyByteBuf (openMenu's Consumer<FriendlyByteBuf> and MenuFactory's buf parameter), but
    //  Fabric's ExtendedMenuType is codec driven and NeoForge's payload based menu opening is too, so
    //  both loaders now want a typed D plus a StreamCodec<RegistryFriendlyByteBuf, D>. Until
    //  IPlatformHelper carries that type, the buffer's contents travel as a byte array and are handed
    //  back as a RegistryFriendlyByteBuf so registry aware reads keep working.
    private static final StreamCodec<RegistryFriendlyByteBuf, FriendlyByteBuf> EXTRA_DATA_CODEC = StreamCodec.of(
            (buf, data) -> {
                final byte[] bytes = new byte[data.readableBytes()];
                data.getBytes(data.readerIndex(), bytes);
                buf.writeByteArray(bytes);
            },
            buf -> new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(buf.readByteArray()), buf.registryAccess())
    );

    @Override
    public String getPlatformName() {
        return "Fabric";
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
        CreativeModeTabEvents.modifyOutputEvent(key).register(output -> {
            output.acceptAll(displayStacks.get());
        });
    }

    @Override
    public void addReloadListener(Identifier identifier, PreparableReloadListener reloadListener) {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(identifier, reloadListener);
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> builder, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(builder::apply, blocks).build();
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return new ExtendedMenuType<>(factory::create, EXTRA_DATA_CODEC);
    }

    public static class ExtendedScreenHandlerImpl implements ExtendedMenuProvider<FriendlyByteBuf> {
        private final MenuProvider provider;
        private final Consumer<FriendlyByteBuf> extraDataWriter;

        public ExtendedScreenHandlerImpl(MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
            this.provider = provider;
            this.extraDataWriter = extraDataWriter;
        }

        @Override
        public FriendlyByteBuf getScreenOpeningData(ServerPlayer player) {
            final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            extraDataWriter.accept(buf);
            return buf;
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
