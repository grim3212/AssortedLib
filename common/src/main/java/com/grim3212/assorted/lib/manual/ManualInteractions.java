package com.grim3212.assorted.lib.manual;

import com.grim3212.assorted.lib.client.manual.ManualClient;
import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.dist.DistExecutor;
import com.grim3212.assorted.lib.events.EntityInteractEvent;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.InteractionResult;

/** What holding the manual changes about interacting with the world. */
public final class ManualInteractions {

    private ManualInteractions() {
    }

    /**
     * Takes an entity the book has something to say about before the entity's own interaction does,
     * which would otherwise put the book into an empty item frame or rotate a filled one, and mount
     * a horse rather than read about it. Anything with no page is left alone and falls through to
     * {@link InstructionManualItem#use}.
     */
    public static void register() {
        Services.EVENTS.registerEvent(EntityInteractEvent.class, (final EntityInteractEvent event) -> {
            if (!(event.getPlayer().getItemInHand(event.getHand()).getItem() instanceof InstructionManualItem)) {
                return;
            }

            ManualPageRef page = ManualLinks.pageFor(event.getTarget());
            if (page == null) {
                return;
            }

            event.setCanceled(true);
            event.setResult(InteractionResult.SUCCESS);

            if (event.getPlayer().level().isClientSide()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ManualClient.open(page));
            }
        });
    }
}
