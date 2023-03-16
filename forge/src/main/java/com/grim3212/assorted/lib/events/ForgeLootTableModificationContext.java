package com.grim3212.assorted.lib.events;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

public class ForgeLootTableModificationContext implements LootTableModifyEvent.LootTableModificationContext {
    private final LootTable table;

    public ForgeLootTableModificationContext(LootTable table) {
        this.table = table;
    }

    @Override
    public void addPool(LootPool pool) {
        table.addPool(pool);
    }
}
