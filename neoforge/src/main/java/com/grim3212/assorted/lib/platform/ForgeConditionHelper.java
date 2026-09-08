package com.grim3212.assorted.lib.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.conditions.*;
import com.grim3212.assorted.lib.core.conditions.LibCondition;
import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import com.grim3212.assorted.lib.platform.services.IConditionHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ForgeConditionHelper implements IConditionHelper {
    private static final Set<IConditionSerializer<?>> CONDITIONS = ConcurrentHashMap.newKeySet();
    public static final Map<String, Supplier<Boolean>> REGISTERED_PARTS = new HashMap<>();

    @Override
    public void init() {
        CONDITIONS.forEach(CraftingHelper::register);
        CraftingHelper.register(PartEnabledCondition.Serializer.INSTANCE);
        CraftingHelper.register(TagPopulatedCondition.ItemTagPopulatedCondition.SERIALIZER);
        CraftingHelper.register(TagPopulatedCondition.BlockTagPopulatedCondition.SERIALIZER);
    }

    @Override
    public void write(JsonObject conditionalObject, LibConditionProvider... conditions) {
        if (conditions.length == 0)
            return;

        if (conditionalObject.has("conditions"))
            throw new IllegalArgumentException("Object already has a condition entry: " + conditionalObject);

        JsonArray conditionsJson = new JsonArray();
        for (LibConditionProvider condition : conditions) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", condition.getName().toString());
            condition.write(jsonObject);
            conditionsJson.add(jsonObject);
        }
        conditionalObject.add("conditions", conditionsJson);
    }

    @Override
    public void register(ResourceLocation name, LibCondition condition) {
        if (!CONDITIONS.add(new RecipeConditionWrapper.Serializer(name, condition)))
            LibConstants.LOG.warn("Duplicate condition with id: " + name);
    }

    @Override
    public boolean test(JsonObject json) {
        return CraftingHelper.processConditions(json, "conditions", ICondition.IContext.EMPTY);
    }

    @Override
    public String getConditionsKey() {
        return "conditions";
    }

    @Override
    public LibConditionProvider and(LibConditionProvider... values) {
        return wrap(new AndCondition(Arrays.stream(values).map(x -> new ConditionWrapper((ForgeConditionProvider) x)).toArray(ICondition[]::new)));
    }

    @Override
    public LibConditionProvider not(LibConditionProvider value) {
        return wrap(new NotCondition(new ConditionWrapper((ForgeConditionProvider) value)));
    }

    @Override
    public LibConditionProvider or(LibConditionProvider... values) {
        return wrap(new OrCondition(Arrays.stream(values).map(x -> new ConditionWrapper((ForgeConditionProvider) x)).toArray(ICondition[]::new)));
    }

    @Override
    public LibConditionProvider blockExists(ResourceLocation block) {
        return wrap(new BlockExistsCondition(block));
    }

    @Override
    public LibConditionProvider itemExists(ResourceLocation item) {
        return wrap(new ItemExistsCondition(item));
    }

    @Override
    public LibConditionProvider blockTagExists(TagKey<Block> tag) {
        return wrap(new TagPopulatedCondition.BlockTagPopulatedCondition(tag.location()));
    }

    @Override
    public LibConditionProvider itemTagExists(TagKey<Item> tag) {
        return wrap(new TagPopulatedCondition.ItemTagPopulatedCondition(tag.location()));
    }

    @Override
    public LibConditionProvider modLoaded(String modId) {
        return wrap(new ModLoadedCondition(modId));
    }

    @Override
    public LibConditionProvider partEnabled(String partId) {
        return wrap(new PartEnabledCondition(partId));
    }

    @Override
    public void registerPartCondition(String part, Supplier<Boolean> check) {
        if (REGISTERED_PARTS.containsKey(part)) {
            throw new IllegalArgumentException("Can't have registered part with the same name as another");
        }
        REGISTERED_PARTS.put(part, check);
    }

    public static ForgeConditionProvider wrap(ICondition condition) {
        return new ForgeConditionProvider(condition);
    }

    public static class ConditionWrapper implements ICondition {

        public final ForgeConditionProvider provider;

        public ConditionWrapper(ForgeConditionProvider provider) {
            this.provider = provider;
        }

        public ConditionWrapper(ICondition provider) {
            this.provider = wrap(provider);
        }

        @Override
        public ResourceLocation getID() {
            return this.provider.getName();
        }

        @Override
        public boolean test(IContext context) {
            return provider.condition.test(context);
        }
    }
}
