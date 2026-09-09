package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public String getCommonTagPrefix() {
        // "c", not "forge". NeoForge unified on Fabric's common tag namespace: the 26.2.0.82 jar
        // ships 593 data/c/tags entries and no data/forge/tags at all. Returning "forge" compiled
        // and loaded fine but pointed every common tag at a namespace nothing populates.
        return "c";
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
        // NetworkHooks is gone; opening a menu with extra data is a player extension now. The buffer
        // it hands over is a RegistryFriendlyByteBuf, which is a FriendlyByteBuf.
        player.openMenu(provider, buf -> extraDataWriter.accept(buf));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }


    @Override
    public boolean isProduction() {
        return FMLEnvironment.isProduction();
    }

    @Override
    public boolean isFakePlayer(Player player) {
        return player instanceof FakePlayer;
    }

    @Override
    public double getPlayerReachDistance(Player player) {
        // Reach is an attribute now, split into a block and an entity range.
        return player.blockInteractionRange();
    }

    @Override
    public Dist getCurrentDistribution() {
        return switch (FMLEnvironment.getDist()) {
            case CLIENT -> Dist.CLIENT;
            case DEDICATED_SERVER -> Dist.DEDICATED_SERVER;
        };
    }

    @Override
    public boolean isPhysicalClient() {
        return this.getCurrentDistribution() == Dist.CLIENT;
    }


    @Override
    public <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key) {
        return ForgeRegistryWrapper.getRegistry(key);
    }

    public static Map<ResourceKey<CreativeModeTab>, Supplier<List<ItemStack>>> tabsToRegister = new HashMap<>();

    @Override
    public void modifyCreativeTab(ResourceKey<CreativeModeTab> key, Supplier<List<ItemStack>> displayStacks) {
        tabsToRegister.put(key, displayStacks);
    }

    @Override
    public void addReloadListener(Identifier identifier, PreparableReloadListener reloadListener) {
        // AddReloadListenerEvent was split per side and listeners are named now, so the identifier
        // this interface has always carried finally has a use.
        NeoForge.EVENT_BUS.addListener((AddServerReloadListenersEvent event) -> event.addListener(identifier, reloadListener));
    }

    // getRandomDungeonEntity is no longer overridden here. Forge's DungeonHooks is gone and there is
    // no modded dungeon mob list to consult, so IPlatformHelper's default - which reads vanilla's own
    // list off the monster room feature - is correct on both loaders.

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> builder, Block... blocks) {
        // BlockEntityType.Builder is gone; the type takes its factory and valid blocks directly.
        return new BlockEntityType<>(builder::apply, blocks);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return IMenuTypeExtension.create(factory::create);
    }

    // isTieredTool is no longer overridden here. Forge's ToolActions and TierSortingRegistry are
    // both gone, and 26.2 expresses tool type and mining tier entirely through vanilla item tags and
    // the TOOL data component, so the default implementation in IPlatformHelper is correct on both
    // loaders.

    // getFuelTime is no longer overridden here either. Burn times are data driven and resolved
    // through Level#fuelValues(), which both loaders share.
}
