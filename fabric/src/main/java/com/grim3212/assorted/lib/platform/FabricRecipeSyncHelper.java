package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IRecipeSyncHelper;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

/**
 * Fabric syncs by serializer, declared up front rather than per join, so a caller's type goes unused.
 */
public class FabricRecipeSyncHelper implements IRecipeSyncHelper {

    @Override
    public void require(Supplier<? extends RecipeType<?>> type, RecipeSerializer<?>... serializers) {

        for (RecipeSerializer<?> serializer : serializers) {
            RecipeSynchronization.synchronizeRecipeSerializer(serializer);
        }
    }
}
