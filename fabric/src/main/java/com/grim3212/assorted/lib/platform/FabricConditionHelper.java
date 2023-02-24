package com.grim3212.assorted.lib.platform;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.core.conditions.LibCondition;
import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import com.grim3212.assorted.lib.platform.services.IConditionHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FabricConditionHelper implements IConditionHelper {
    public static final ResourceLocation ITEM_EXISTS = new ResourceLocation(LibConstants.MOD_ID, "item_exists");
    public static final ResourceLocation BLOCK_EXISTS = new ResourceLocation(LibConstants.MOD_ID, "block_exists");
    public static final ResourceLocation PART_ENABLED = new ResourceLocation(LibConstants.MOD_ID, "part_enabled");
    private static final Map<String, Supplier<Boolean>> REGISTERED_PARTS = new HashMap<>();

    @Override
    public void init() {
        ResourceConditions.register(ITEM_EXISTS, object -> registryKeyExists(object, "item", Registries.ITEM));
        ResourceConditions.register(BLOCK_EXISTS, object -> registryKeyExists(object, "block", Registries.BLOCK));
        ResourceConditions.register(PART_ENABLED, jsonObject -> {
            String foundPart = GsonHelper.getAsString(jsonObject, "part");
            if (!REGISTERED_PARTS.containsKey(foundPart)) {
                throw new IllegalArgumentException("Can't check part that doesn't exist!");
            }

            return REGISTERED_PARTS.get(foundPart).get();
        });
    }

    @Override
    public void write(JsonObject conditionalObject, LibConditionProvider... conditions) {
        ConditionJsonProvider.write(conditionalObject, conditions.length == 0 ? null : Arrays.stream(conditions).map(FabricConditionHelper::wrap).toArray(ConditionJsonProvider[]::new));
    }

    @Override
    public void register(ResourceLocation name, LibCondition condition) {
        ResourceConditions.register(name, condition::test);
    }

    @Override
    public boolean test(JsonObject json) {
        return ResourceConditions.objectMatchesConditions(json);
    }

    @Override
    public String getConditionsKey() {
        return ResourceConditions.CONDITIONS_KEY;
    }

    @Override
    public LibConditionProvider and(LibConditionProvider... values) {
        return wrap(DefaultResourceConditions.and(Arrays.stream(values).map(FabricConditionHelper::wrap).toArray(ConditionJsonProvider[]::new)));
    }

    @Override
    public LibConditionProvider not(LibConditionProvider value) {
        return wrap(DefaultResourceConditions.not(wrap(value)));
    }

    @Override
    public LibConditionProvider or(LibConditionProvider... values) {
        return wrap(DefaultResourceConditions.or(Arrays.stream(values).map(FabricConditionHelper::wrap).toArray(ConditionJsonProvider[]::new)));
    }

    @Override
    public LibConditionProvider blockExists(ResourceLocation block) {
        return wrap(registryKeyExistsProvider(BLOCK_EXISTS, "block", block));
    }

    @Override
    public LibConditionProvider itemExists(ResourceLocation item) {
        return wrap(registryKeyExistsProvider(ITEM_EXISTS, "item", item));
    }

    @Override
    public LibConditionProvider blockTagExists(TagKey<Block> tag) {
        return wrap(DefaultResourceConditions.tagsPopulated(tag));
    }

    @Override
    public LibConditionProvider itemTagExists(TagKey<Item> tag) {
        return wrap(DefaultResourceConditions.tagsPopulated(tag));
    }

    @Override
    public LibConditionProvider modLoaded(String modId) {
        return wrap(DefaultResourceConditions.allModsLoaded(modId));
    }

    @Override
    public LibConditionProvider partEnabled(String partId) {
        return wrap(partEnabledCondition(partId));
    }

    @Override
    public void registerPartCondition(String part, Supplier<Boolean> check) {
        if (REGISTERED_PARTS.containsKey(part)) {
            throw new IllegalArgumentException("Can't have registered part with the same name as another");
        }
        REGISTERED_PARTS.put(part, check);
    }

    public static LibConditionProvider wrap(ConditionJsonProvider condition) {
        return new LibConditionWrapper(condition);
    }

    public static ConditionJsonProvider wrap(LibConditionProvider condition) {
        return new ConditionJsonWrapper(condition);
    }

    private static ConditionJsonProvider registryKeyExistsProvider(ResourceLocation id, String jsonKey, ResourceLocation key) {
        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject json) {
                json.addProperty(jsonKey, key.toString());
            }
        };
    }

    private static ConditionJsonProvider partEnabledCondition(String part) {
        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return PART_ENABLED;
            }

            @Override
            public void writeParameters(JsonObject json) {
                json.addProperty("part", part);
            }
        };
    }

    private static <T> boolean registryKeyExists(JsonObject object, String jsonKey, ResourceKey<? extends Registry<T>> key) {
        return Services.PLATFORM.getRegistry(key).containsKey(new ResourceLocation(GsonHelper.getAsString(object, jsonKey)));
    }

    private static class ConditionJsonWrapper implements ConditionJsonProvider {

        private final LibConditionProvider provider;

        private ConditionJsonWrapper(LibConditionProvider provider) {
            this.provider = provider;
        }

        @Override
        public ResourceLocation getConditionId() {
            return this.provider.getName();
        }

        @Override
        public void writeParameters(JsonObject object) {
            this.provider.write(object);
        }
    }

    private static class LibConditionWrapper implements LibConditionProvider {

        private final ConditionJsonProvider condition;

        public LibConditionWrapper(ConditionJsonProvider condition) {
            this.condition = condition;
        }

        @Override
        public void write(JsonObject json) {
            this.condition.writeParameters(json);
        }

        @Override
        public ResourceLocation getName() {
            return this.condition.getConditionId();
        }
    }
}
