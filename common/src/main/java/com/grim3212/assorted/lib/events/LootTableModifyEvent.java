package com.grim3212.assorted.lib.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTables;

public class LootTableModifyEvent extends GenericEvent {
    private final LootTables lootTables;
    private final ResourceLocation id;
    private final LootTableModificationContext context;
    private final boolean builtin;

    public LootTableModifyEvent(LootTables lootTables, ResourceLocation id, LootTableModificationContext context, boolean builtin) {
        this.lootTables = lootTables;
        this.id = id;
        this.context = context;
        this.builtin = builtin;
    }

    public LootTables getLootTables() {
        return lootTables;
    }

    public ResourceLocation getId() {
        return id;
    }

    public LootTableModificationContext getContext() {
        return context;
    }

    public boolean isBuiltin() {
        return builtin;
    }

    public interface LootTableModificationContext {
        /**
         * Adds a pool to the loot table.
         *
         * @param pool the pool to add
         */
        void addPool(LootPool pool);

        /**
         * Adds a pool to the loot table.
         *
         * @param pool the pool to add
         */
        default void addPool(LootPool.Builder pool) {
            addPool(pool.build());
        }
    }
}
