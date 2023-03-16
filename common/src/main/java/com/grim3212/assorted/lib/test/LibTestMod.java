package com.grim3212.assorted.lib.test;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.events.LootTableModifyEvent;
import com.grim3212.assorted.lib.events.OnDropStacksEvent;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.test.config.LibTestClientConfig;
import com.grim3212.assorted.lib.test.config.LibTestCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.List;

public class LibTestMod {

    public static final LibTestClientConfig clientConfig = new LibTestClientConfig();
    public static final LibTestCommonConfig commonConfig = new LibTestCommonConfig();

    public static void init() {
        Services.PLATFORM.registerCreativeTab(new ResourceLocation(LibConstants.MOD_ID, "tab"), Component.translatable("itemGroup.assortedlib"), () -> new ItemStack(Items.BARRIER), () -> ImmutableList.of(new ItemStack(Items.BARRIER)));
        Services.EVENTS.registerEvent(LootTableModifyEvent.class, (final LootTableModifyEvent event) -> {
            if (Blocks.DIRT.getLootTable().equals(event.getId())) {
                LootPool.Builder pool = LootPool.lootPool().add(LootItem.lootTableItem(Items.STICK));
                event.getContext().addPool(pool);
            }
        });
        Services.EVENTS.registerEvent(OnDropStacksEvent.class, (final OnDropStacksEvent event) -> {
            if (event.getState().getBlock() == Blocks.GRASS_BLOCK) {
                List<ItemStack> currentDrops = event.getDrops();
                currentDrops.add(new ItemStack(Items.DIAMOND));
            }
        });
    }

    public static void getConfig() {
        LibConstants.LOG.info("Test float list config {}", clientConfig.testFloatList.get());
        LibConstants.LOG.info("Test boolean config {}", clientConfig.testBoolean.get());
        LibConstants.LOG.info("Test int config {}", clientConfig.testInteger.get());
        LibConstants.LOG.info("Test string client config {}", clientConfig.testString.get());
        LibConstants.LOG.info("Test string common config {}", commonConfig.testString.get());
    }
}
