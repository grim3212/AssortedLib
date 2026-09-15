package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.IRecipeSyncHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * NeoForge asks by type at {@code OnDatapackSyncEvent}, so a caller's serializers go unused here and
 * {@code AssortedLibForge} hands it {@link #requestedTypes()}. Resolving then is also what lets the
 * request come from mod construction, where no config is loaded and no {@code DeferredRegister}
 * entry is bound.
 */
public class ForgeRecipeSyncHelper implements IRecipeSyncHelper {

    private static final List<Request> REQUESTED = new CopyOnWriteArrayList<>();

    @Override
    public void require(BooleanSupplier enabled, Supplier<? extends RecipeType<?>> type, RecipeSerializer<?>... serializers) {
        REQUESTED.add(new Request(enabled, type));
    }

    public static List<RecipeType<?>> requestedTypes() {
        return REQUESTED.stream()
                .filter(request -> request.enabled().getAsBoolean())
                .<RecipeType<?>>map(request -> request.type().get())
                .distinct()
                .toList();
    }

    private record Request(BooleanSupplier enabled, Supplier<? extends RecipeType<?>> type) {
    }
}
