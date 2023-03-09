package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.data.ForgeLibBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeLibItemTagProvider;
import com.grim3212.assorted.lib.data.LibCommonRecipeProvider;
import com.grim3212.assorted.lib.events.AnvilUpdatedEvent;
import com.grim3212.assorted.lib.events.ClientTickEvent;
import com.grim3212.assorted.lib.events.EntityInteractEvent;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
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

        Services.EVENTS.registerEventType(ClientTickEvent.class, () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (final TickEvent.ClientTickEvent event) -> {
                ClientTickEvent newEvent;
                if (event.phase == net.minecraftforge.event.TickEvent.Phase.START) {
                    newEvent = new ClientTickEvent.StartClientTickEvent();
                } else {
                    newEvent = new ClientTickEvent.EndClientTickEvent();
                }

                Services.EVENTS.handleEvents(newEvent);
                if (newEvent.isCanceled()) {
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

        Services.CONDITIONS.init();
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator datagenerator = event.getGenerator();
        PackOutput packOutput = datagenerator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeLibBlockTagProvider blockTagProvider = new ForgeLibBlockTagProvider(packOutput, lookupProvider, fileHelper);
        datagenerator.addProvider(event.includeServer(), blockTagProvider);
        datagenerator.addProvider(event.includeServer(), new ForgeLibItemTagProvider(packOutput, lookupProvider, blockTagProvider, fileHelper));

        datagenerator.addProvider(event.includeServer(), new LibCommonRecipeProvider(packOutput));
    }

    private void registerRecipeSerializers(final RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            Services.INGREDIENTS.register();
        }
    }
}
