package com.grim3212.assorted.lib.core.conditions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public interface LibCondition {
    boolean test(JsonObject json) throws JsonParseException;
}
