package com.grim3212.assorted.lib.crafting;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.IRecipeSyncHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Recipes the server sent this client, for a type opted in with {@link #require}. Cleared on
 * disconnect: they belong to the server that sent them.
 */
public final class SyncedRecipes {

    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();

    private static volatile RecipeMap recipes = RecipeMap.EMPTY;

    private SyncedRecipes() {
    }

    /** See {@link IRecipeSyncHelper#require} for why both a type and its serializers are needed. */
    public static void require(Supplier<? extends RecipeType<?>> type, RecipeSerializer<?>... serializers) {
        require(() -> true, type, serializers);
    }

    /**
     * Gated. {@code enabled} is read as late as each loader allows, so it may read a config value:
     * NeoForge has none loaded during mod construction.
     */
    public static void require(BooleanSupplier enabled, Supplier<? extends RecipeType<?>> type, RecipeSerializer<?>... serializers) {
        Services.RECIPE_SYNC.require(enabled, type, serializers);
    }

    /** Called by each loader's client on login and after {@code /reload}. */
    public static void set(RecipeMap map) {
        recipes = map;

        for (Runnable listener : LISTENERS) {
            listener.run();
        }
    }

    public static void clear() {
        set(RecipeMap.EMPTY);
    }

    public static RecipeMap recipes() {
        return recipes;
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> type) {
        return recipes.byType(type);
    }

    @Nullable
    public static RecipeHolder<?> byKey(ResourceKey<Recipe<?>> id) {
        return recipes.byKey(id);
    }

    /** Runs on the client thread whenever a new set arrives, including the empty set on disconnect. */
    public static void addUpdateListener(Runnable listener) {
        LISTENERS.add(listener);
    }
}
