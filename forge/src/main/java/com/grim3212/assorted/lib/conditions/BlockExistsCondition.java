package com.grim3212.assorted.lib.conditions;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockExistsCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(LibConstants.MOD_ID, "block_exists");
    private final ResourceLocation block;

    public BlockExistsCondition(ResourceLocation block) {
        this.block = block;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return ForgeRegistries.BLOCKS.containsKey(this.block);
    }

    @Override
    public String toString() {
        return "block_exists(\"" + this.block + "\")";
    }

    public static class Serializer implements IConditionSerializer<BlockExistsCondition> {

        public static final BlockExistsCondition.Serializer INSTANCE = new BlockExistsCondition.Serializer();

        @Override
        public void write(JsonObject json, BlockExistsCondition value) {
            json.addProperty("block", value.block.toString());
        }

        @Override
        public BlockExistsCondition read(JsonObject json) {
            return new BlockExistsCondition(new ResourceLocation(GsonHelper.getAsString(json, "block")));
        }

        @Override
        public ResourceLocation getID() {
            return BlockExistsCondition.NAME;
        }
    }
}
