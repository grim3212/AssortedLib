package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.events.FabricLootTableModificationContext;
import com.grim3212.assorted.lib.events.LootTableModifyEvent;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.FabricConfigHelper;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class AssortedLibFabric implements ModInitializer {


    @Override
    public void onInitialize() {
        Services.EVENTS.registerEventType(UseBlockEvent.class, () -> UseBlockCallback.EVENT.register((Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
            final UseBlockEvent event = new UseBlockEvent(player, level, hand, hitResult);
            Services.EVENTS.handleEvents(event);
            return event.getInteractionResult();
        }));

        Services.EVENTS.registerEventType(LootTableModifyEvent.class, () -> LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            final LootTableModifyEvent event = new LootTableModifyEvent(lootManager, id, new FabricLootTableModificationContext(tableBuilder), source.isBuiltin());
            Services.EVENTS.handleEvents(event);
        }));

        Services.CONDITIONS.init();
        Services.INGREDIENTS.register();

        FabricConfigHelper.init();
    }

}
