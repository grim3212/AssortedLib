package com.grim3212.assorted.lib.conditions;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.ForgeConditionHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class PartEnabledCondition implements ICondition {
    public static final ResourceLocation NAME = new ResourceLocation(LibConstants.MOD_ID, "part_enabled");
    private final String part;

    public PartEnabledCondition(String part) {
        this.part = part;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        if (!ForgeConditionHelper.REGISTERED_PARTS.containsKey(this.part)) {
            throw new IllegalArgumentException("Can't check part that doesn't exist!");
        }

        return ForgeConditionHelper.REGISTERED_PARTS.get(this.part).get();
    }

    @Override
    public String toString() {
        return "part_enabled(\"" + this.part + "\")";
    }

    public static class Serializer implements IConditionSerializer<PartEnabledCondition> {

        public static Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, PartEnabledCondition value) {
            json.addProperty("part", value.part);
        }

        @Override
        public PartEnabledCondition read(JsonObject json) {
            return new PartEnabledCondition(GsonHelper.getAsString(json, "part"));
        }

        @Override
        public ResourceLocation getID() {
            return PartEnabledCondition.NAME;
        }
    }
}