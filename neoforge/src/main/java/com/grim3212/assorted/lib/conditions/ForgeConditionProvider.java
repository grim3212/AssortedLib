package com.grim3212.assorted.lib.conditions;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.core.conditions.LibConditionProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * An opaque handle on one {@link ICondition}.
 * <p>
 * The 1.20.1 version reflected {@code CraftingHelper}'s serializer map so it could write the
 * condition into a recipe's json itself. Conditions are attached by wrapping the {@code RecipeOutput}
 * now (see {@code IConditionHelper#conditionalOutput}) and serialised by their registered
 * {@link com.mojang.serialization.MapCodec}, so all this has to do is carry the condition and be
 * able to name it.
 */
public class ForgeConditionProvider implements LibConditionProvider {

    private static final Identifier UNREGISTERED = Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, "unregistered_condition");

    public final ICondition condition;

    public ForgeConditionProvider(ICondition condition) {
        this.condition = condition;
    }

    public ICondition getCondition() {
        return this.condition;
    }

    @Override
    public Identifier getName() {
        final Identifier name = NeoForgeRegistries.CONDITION_SERIALIZERS.getKey(this.condition.codec());
        return name != null ? name : UNREGISTERED;
    }
}
