package com.grim3212.assorted.lib.events;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

public class FabricLootTableModificationContext implements LootTableModifyEvent.LootTableModificationContext {
    private final LootTable.Builder tableBuilder;

    public FabricLootTableModificationContext(LootTable.Builder tableBuilder) {
        this.tableBuilder = tableBuilder;
    }

    @Override
    public void addPool(LootPool pool) {
        tableBuilder.pool(pool);
    }

    @Override
    public void addPool(LootPool.Builder pool) {
        tableBuilder.withPool(pool);
    }
}
