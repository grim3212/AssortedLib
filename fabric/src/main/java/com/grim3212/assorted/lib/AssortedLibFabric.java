package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.core.block.IBlockCloneStack;
import com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition;
import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.platform.FabricConfigHelper;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;

public class AssortedLibFabric implements ModInitializer {


    @Override
    public void onInitialize() {
        LibConstants.LOG.info(LibConstants.MOD_NAME + " starting up...");

        Services.CONDITIONS.init();
        LibDataComponents.init();
        Services.INGREDIENTS.register();

        FabricConfigHelper.init();

        // On NeoForge an IItemEnchantmentCondition's methods are IItemExtension overrides already;
        // Fabric asks through this event at the same four places - table, anvil, loot, /enchant.
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, stack, context) -> {
            if (stack.getItem() instanceof IItemEnchantmentCondition condition) {
                return TriState.of(context == EnchantingContext.PRIMARY ? condition.isPrimaryItemFor(stack, enchantment) : condition.supportsEnchantment(stack, enchantment));
            }

            return TriState.DEFAULT;
        });

        // Pick block is resolved on the server here, as it is on NeoForge; the event carries the
        // block and its position and no hit result, which is why IBlockCloneStack does not ask for one.
        PlayerPickItemEvents.BLOCK.register((player, pos, state, includeData) -> {
            if (state.getBlock() instanceof IBlockCloneStack extraProperties) {
                return extraProperties.getCloneItemStack(state, player.level(), pos, player);
            }

            // null lets the next listener - and ultimately vanilla - handle the pick.
            return null;
        });
    }

}
