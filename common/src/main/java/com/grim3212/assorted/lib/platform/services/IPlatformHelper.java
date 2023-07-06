package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.mixin.world.level.MonsterRoomFeatureAccessor;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    String getCommonTagPrefix();

    void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter);

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    boolean isFakePlayer(Player player);

    double getPlayerReachDistance(Player player);

    /**
     * The current distribution.
     *
     * @return The current distribution.
     */
    Dist getCurrentDistribution();

    boolean isPhysicalClient();

    /**
     * Indicates if the current platform is running in production mode or not.
     *
     * @return True if the current platform is running in production mode. False when not.
     */
    boolean isProduction();

    <T> ILoaderRegistry<T> getRegistry(ResourceKey<? extends Registry<T>> key);

    void modifyCreativeTab(final ResourceKey<CreativeModeTab> key, Supplier<List<ItemStack>> displayStacks);

    void addReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener);

    default EntityType<?> getRandomDungeonEntity(RandomSource random) {
        return Util.getRandom(MonsterRoomFeatureAccessor.getMOBS(), random);
    }

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> builder, Block... blocks);

    <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory);

    @FunctionalInterface
    interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int syncId, Inventory inventory, FriendlyByteBuf buf);
    }

    int getFuelTime(ItemStack stack);

    enum ToolType {
        PICKAXE,
        SHOVEL,
        AXE,
        HOE
    }

    default boolean isTieredTool(ItemStack stack, Tiers minTier, ToolType toolType) {
        if (stack.getItem() instanceof TieredItem itemTier) {
            if (itemTier.getTier().getLevel() >= minTier.getLevel()) {
                switch (toolType) {
                    case PICKAXE -> {
                        return itemTier instanceof PickaxeItem;
                    }
                    case SHOVEL -> {
                        return itemTier instanceof ShovelItem;
                    }
                    case AXE -> {
                        return itemTier instanceof AxeItem;
                    }
                    case HOE -> {
                        return itemTier instanceof HoeItem;
                    }
                }
            }
        }

        return false;
    }
}
