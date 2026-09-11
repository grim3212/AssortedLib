package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.core.block.IBlockCloneStack;
import com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition;
import com.grim3212.assorted.lib.platform.FabricConfigHelper;
import com.grim3212.assorted.lib.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AssortedLibFabric implements ModInitializer {


    @Override
    public void onInitialize() {
        LibConstants.LOG.info(LibConstants.MOD_NAME + " starting up...");

        Services.CONDITIONS.init();
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

        // TODO(26.2): pick block is resolved on the server (PlayerPickItemEvents.BLOCK), which
        //  gives no HitResult. IBlockCloneStack still asks for one, so a hit at the block centre is
        //  synthesised; the real face and hit vector are unavailable.
        PlayerPickItemEvents.BLOCK.register((player, pos, state, includeData) -> {
            if (state.getBlock() instanceof IBlockCloneStack extraProperties) {
                final BlockHitResult target = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
                return extraProperties.getCloneItemStack(state, target, player.level(), pos, player);
            }

            // null lets the next listener - and ultimately vanilla - handle the pick.
            return null;
        });
    }

}
