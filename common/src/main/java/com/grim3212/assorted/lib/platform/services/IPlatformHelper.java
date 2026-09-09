package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.mixin.world.level.MonsterRoomFeatureAccessor;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
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

    void addReloadListener(Identifier identifier, PreparableReloadListener reloadListener);

    default EntityType<?> getRandomDungeonEntity(RandomSource random) {
        return Util.getRandom(MonsterRoomFeatureAccessor.getMOBS(), random);
    }

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> builder, Block... blocks);

    <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory);

    @FunctionalInterface
    interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int syncId, Inventory inventory, FriendlyByteBuf buf);
    }

    /**
     * Burn times are data-driven in 26.x and resolved per level, so this needs a level to look
     * against. Both loaders now feed the vanilla fuel registry, so there is no platform-specific
     * behaviour left here.
     * <p>
     * NeoForge deprecates {@code FuelValues#burnDuration} in favour of an {@code ItemStack} extension
     * that also takes the recipe type; that extension only exists in its patched jar, while this module
     * builds against vanilla, so the vanilla lookup is the only one available here.
     */
    @SuppressWarnings("deprecation")
    default int getFuelTime(Level level, ItemStack stack) {
        return level.fuelValues().burnDuration(stack);
    }

    enum ToolType {
        PICKAXE(ItemTags.PICKAXES),
        SHOVEL(ItemTags.SHOVELS),
        AXE(ItemTags.AXES),
        HOE(ItemTags.HOES);

        private final TagKey<Item> tag;

        ToolType(TagKey<Item> tag) {
            this.tag = tag;
        }

        public TagKey<Item> getTag() {
            return this.tag;
        }
    }

    /**
     * Vanilla no longer exposes a scalar tool tier. {@code Tiers} and {@code TieredItem} were
     * removed, and mining capability now lives in the {@link DataComponents#TOOL} component as a
     * list of per-block rules. What is still testable is whether a tool can correctly harvest the
     * blocks vanilla gates behind a given tier, so a tier is expressed as that harvest level.
     */
    enum ToolTier {
        WOOD(0),
        GOLD(0),
        STONE(1),
        // Copper sits between stone and iron, but vanilla ships no NEEDS_COPPER_TOOL tag, so it is
        // only distinguishable down to the stone level.
        COPPER(1),
        IRON(2),
        // Netherite shares diamond's harvest level in vanilla; it differs in durability, not tier.
        DIAMOND(3),
        NETHERITE(3);

        private final int harvestLevel;

        ToolTier(int harvestLevel) {
            this.harvestLevel = harvestLevel;
        }

        public int getHarvestLevel() {
            return this.harvestLevel;
        }
    }

    default boolean isTieredTool(ItemStack stack, ToolTier minTier, ToolType toolType) {
        if (!stack.is(toolType.getTag())) {
            return false;
        }

        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) {
            return false;
        }

        return harvestLevelOf(tool) >= minTier.getHarvestLevel();
    }

    /**
     * Probes a tool against one representative block per vanilla harvest tier. These are the blocks
     * behind NEEDS_DIAMOND_TOOL, NEEDS_IRON_TOOL and NEEDS_STONE_TOOL respectively.
     */
    private static int harvestLevelOf(Tool tool) {
        if (tool.isCorrectForDrops(Blocks.OBSIDIAN.defaultBlockState())) {
            return 3;
        }
        if (tool.isCorrectForDrops(Blocks.DIAMOND_ORE.defaultBlockState())) {
            return 2;
        }
        if (tool.isCorrectForDrops(Blocks.IRON_ORE.defaultBlockState())) {
            return 1;
        }
        return 0;
    }
}
