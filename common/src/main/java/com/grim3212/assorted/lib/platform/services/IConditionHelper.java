package com.grim3212.assorted.lib.platform.services;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.core.conditions.LibCondition;
import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface IConditionHelper {
    void init();

    void write(JsonObject conditionalObject, LibConditionProvider... conditions);

    void register(Identifier name, LibCondition condition);

    boolean test(JsonObject json);

    String getConditionsKey();

    LibConditionProvider and(LibConditionProvider... values);

    LibConditionProvider not(LibConditionProvider value);

    LibConditionProvider or(LibConditionProvider... values);

    LibConditionProvider blockExists(Identifier block);

    LibConditionProvider itemExists(Identifier item);

    LibConditionProvider blockTagExists(TagKey<Block> tag);

    LibConditionProvider itemTagExists(TagKey<Item> tag);

    LibConditionProvider modLoaded(String modId);

    LibConditionProvider partEnabled(String partId);

    void registerPartCondition(String part, Supplier<Boolean> check);
}
