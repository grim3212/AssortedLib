package com.grim3212.assorted.lib.conditions;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.lang.reflect.Field;
import java.util.Map;

public class ForgeConditionProvider implements LibConditionProvider {

    private static final Map<ResourceLocation, IConditionSerializer<?>> CONDITIONS;

    static {
        try {
            Field field = CraftingHelper.class.getDeclaredField("conditions");
            field.setAccessible(true);
            //noinspection unchecked
            CONDITIONS = (Map<ResourceLocation, IConditionSerializer<?>>) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load conditions", e);
        }
    }

    public final ICondition condition;

    public ForgeConditionProvider(ICondition condition) {
        this.condition = condition;
    }

    @Override
    public void write(JsonObject json) {
        write(json, this.condition);
    }

    @Override
    public ResourceLocation getName() {
        return this.condition.getID();
    }

    @SuppressWarnings("unchecked")
    private static <T extends ICondition> void write(JsonObject json, T condition) {
        IConditionSerializer<T> serializer = (IConditionSerializer<T>) CONDITIONS.get(condition.getID());
        serializer.write(json, condition);
    }
}
