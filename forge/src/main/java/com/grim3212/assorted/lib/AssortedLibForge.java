package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.data.ForgeLibBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeLibItemTagProvider;
import com.grim3212.assorted.lib.data.LibCommonRecipeProvider;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
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
