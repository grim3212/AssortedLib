package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.data.ForgeLibBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeLibBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeLibItemTagProvider;
import com.grim3212.assorted.lib.events.*;
import com.grim3212.assorted.lib.platform.ForgePlatformHelper;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.concurrent.CompletableFuture;

@Mod(LibConstants.MOD_ID)
public class AssortedLibForge {
    public AssortedLibForge() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::gatherData);
        modBus.addListener(this::registerRecipeSerializers);
        modBus.addListener(this::registerCreativeTabs);

        Services.EVENTS.registerEventType(UseBlockEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final PlayerInteractEvent.RightClickBlock event) -> {
                final UseBlockEvent newEvent = new UseBlockEvent(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
                Services.EVENTS.handleEvents(newEvent);
                if (newEvent.isCanceled()) {
                    event.setCancellationResult(newEvent.getInteractionResult());
                    event.setCanceled(true);
                }
            });
        });

        Services.EVENTS.registerEventType(AnvilUpdatedEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final AnvilUpdateEvent event) -> {
                final AnvilUpdatedEvent newEvent = new AnvilUpdatedEvent(event.getLeft(), event.getRight(), event.getName(), event.getCost(), event.getPlayer());
                Services.EVENTS.handleEvents(newEvent);
                if (newEvent.isCanceled()) {
                    event.setCanceled(true);
                } else if (!newEvent.getOutput().isEmpty()) {
                    event.setOutput(newEvent.getOutput());
                    event.setCost(newEvent.getCost());
                    event.setMaterialCost(newEvent.getMaterialCost());
                }
            });
        });

        Services.EVENTS.registerEventType(EntityInteractEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final PlayerInteractEvent.EntityInteract event) -> {
                final EntityInteractEvent newEvent = new EntityInteractEvent(event.getEntity(), event.getHand(), event.getTarget());
                Services.EVENTS.handleEvents(newEvent);
                event.setCanceled(newEvent.isCanceled());
                event.setCancellationResult(newEvent.getInteractionResult());
            });
        });

        Services.EVENTS.registerEventType(LootTableModifyEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener((final LootTableLoadEvent event) -> {
                final LootTableModifyEvent newEvent = new LootTableModifyEvent(event.getLootTableManager(), event.getName(), new ForgeLootTableModificationContext(event.getTable()), true);
                Services.EVENTS.handleEvents(newEvent);
            });
        });

        Services.CONDITIONS.init();
    }

    private void registerRecipeSerializers(final RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            Services.INGREDIENTS.register();
        }
    }

    private void registerCreativeTabs(final CreativeModeTabEvent.Register event) {
        for (ForgePlatformHelper.TabRegister tab : ForgePlatformHelper.tabsToRegister.values()) {
            event.registerCreativeModeTab(tab.id(), builder -> builder.title(tab.title()).icon(tab.icon()).displayItems((enabledFlags, populator, hasPermissions) -> populator.acceptAll(tab.displayStacks().get())));
        }
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator datagenerator = event.getGenerator();
        PackOutput packOutput = datagenerator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeLibBlockTagProvider blockTagProvider = new ForgeLibBlockTagProvider(packOutput, lookupProvider, fileHelper);
        datagenerator.addProvider(event.includeServer(), blockTagProvider);
        datagenerator.addProvider(event.includeServer(), new ForgeLibItemTagProvider(packOutput, lookupProvider, blockTagProvider, fileHelper));
        datagenerator.addProvider(event.includeServer(), new ForgeLibBiomeTagProvider(packOutput, lookupProvider, fileHelper));
    }
}
