package com.grim3212.assorted.lib.conditions;

import com.grim3212.assorted.lib.LibConstants;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Conditions are codec based in 26.2, so the old {@code IConditionSerializer} inner class is gone;
 * the {@link MapCodec} below is what identifies the condition on both sides and is registered
 * against {@code NeoForgeRegistries.CONDITION_SERIALIZERS} by {@link LibConditions}.
 */
public class BlockExistsCondition implements ICondition {

    public static final Identifier NAME = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "block_exists");
    public static final MapCodec<BlockExistsCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Identifier.CODEC.fieldOf("block").forGetter(condition -> condition.block))
            .apply(instance, BlockExistsCondition::new));

    private final Identifier block;

    public BlockExistsCondition(Identifier block) {
        this.block = block;
    }

    public Identifier getBlock() {
        return this.block;
    }

    @Override
    public boolean test(IContext context) {
        return BuiltInRegistries.BLOCK.containsKey(this.block);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "block_exists(\"" + this.block + "\")";
    }
}
