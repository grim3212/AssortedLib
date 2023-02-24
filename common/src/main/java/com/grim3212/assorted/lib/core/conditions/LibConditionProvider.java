package com.grim3212.assorted.lib.core.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public interface LibConditionProvider {
    void write(JsonObject json);

    ResourceLocation getName();
}
