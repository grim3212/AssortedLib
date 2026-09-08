package com.grim3212.assorted.lib.conditions;

import com.grim3212.assorted.lib.core.conditions.LibCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Wraps a loader agnostic {@link LibCondition} registered at runtime through
 * {@code IConditionHelper#register(Identifier, LibCondition)}.
 * <p>
 * The 1.20.1 version needed a separate {@code IConditionSerializer} which re-evaluated the condition
 * while reading a recipe's json. A {@link LibCondition} has no parameters any more, so a wrapper is
 * a singleton per name and its codec is a unit codec of itself; the test happens where every other
 * condition's does, in {@link #test(IContext)}.
 */
public class RecipeConditionWrapper implements ICondition {

    private final Identifier name;
    private final LibCondition condition;
    private final MapCodec<RecipeConditionWrapper> codec;

    public RecipeConditionWrapper(Identifier name, LibCondition condition) {
        this.name = name;
        this.condition = condition;
        this.codec = MapCodec.unit(() -> this);
    }

    public Identifier getName() {
        return this.name;
    }

    public LibCondition getCondition() {
        return this.condition;
    }

    @Override
    public boolean test(IContext context) {
        return this.condition.test();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return this.codec;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeConditionWrapper that = (RecipeConditionWrapper) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
