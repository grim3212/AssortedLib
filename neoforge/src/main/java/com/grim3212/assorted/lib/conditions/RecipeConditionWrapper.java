package com.grim3212.assorted.lib.conditions;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.core.conditions.LibCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class RecipeConditionWrapper implements ICondition {
    private final ResourceLocation name;
    private final String stringName;
    private final boolean result;

    public RecipeConditionWrapper(ResourceLocation name, String stringName, boolean result) {
        this.name = name;
        this.stringName = stringName;
        this.result = result;
    }

    @Override
    public ResourceLocation getID() {
        return name;
    }

    @Override
    public boolean test(IContext context) {
        return this.result;
    }

    @Override
    public String toString() {
        return stringName;
    }

    public static class Serializer implements IConditionSerializer<RecipeConditionWrapper> {

        private final ResourceLocation name;
        private final LibCondition condition;

        public Serializer(ResourceLocation name, LibCondition condition) {
            this.name = name;
            this.condition = condition;
        }

        @Override
        public void write(JsonObject json, RecipeConditionWrapper value) {
        }

        @Override
        public RecipeConditionWrapper read(JsonObject json) {
            return new RecipeConditionWrapper(this.name, this.condition.toString(), this.condition.test(json));
        }

        @Override
        public ResourceLocation getID() {
            return name;
        }

        public LibCondition getCondition() {
            return condition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Serializer that = (Serializer) o;
            return this.name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
