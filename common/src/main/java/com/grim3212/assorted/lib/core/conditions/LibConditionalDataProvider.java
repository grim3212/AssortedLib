package com.grim3212.assorted.lib.core.conditions;

import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;

public interface LibConditionalDataProvider extends DataProvider {

    void addConditions(Identifier id, LibConditionProvider... providers);
}
