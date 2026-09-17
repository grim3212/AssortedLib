package com.grim3212.assorted.lib.platform.services;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

/**
 * Opts a recipe type into being sent to clients. Since 1.21.2 a client only receives the recipe book
 * displays of recipes it has unlocked, so anything reading whole recipes must ask.
 * <p>
 * The loaders key it differently - NeoForge by type at datapack sync, Fabric by serializer at mod
 * init - so callers pass both and each takes the half it uses.
 */
public interface IRecipeSyncHelper {

    /**
     * Call from common init, once per type. Late calls are honoured as long as they happen before a
     * world is joined.
     *
     * @param type        a supplier, not the type itself: this is called from common init, where a
     *                    type registered through a {@code DeferredRegister} is not yet bound
     * @param serializers every serializer that produces a recipe of that type
     */
    void require(Supplier<? extends RecipeType<?>> type, RecipeSerializer<?>... serializers);
}
