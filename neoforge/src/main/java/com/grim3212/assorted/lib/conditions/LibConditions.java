package com.grim3212.assorted.lib.conditions;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.core.conditions.LibCondition;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Owns the registration of this mod's {@link ICondition} codecs.
 * <p>
 * {@code CraftingHelper.register(IConditionSerializer)} is gone; a condition type is a
 * {@link com.mojang.serialization.MapCodec} in the {@code NeoForgeRegistries.CONDITION_SERIALIZERS}
 * registry, which can only be written to from a {@link RegisterEvent}. Conditions registered at mod
 * construction time through {@code IConditionHelper#register} are therefore collected here first and
 * flushed when that event fires.
 */
public final class LibConditions {

    /**
     * The parts a {@link PartEnabledCondition} can ask about. Populated by
     * {@code IConditionHelper#registerPartCondition}.
     */
    public static final Map<String, Supplier<Boolean>> REGISTERED_PARTS = new HashMap<>();

    private static final Map<Identifier, RecipeConditionWrapper> DYNAMIC_CONDITIONS = new LinkedHashMap<>();

    private LibConditions() {
    }

    /**
     * Registers a runtime supplied condition under the given name. The codec is not registered until
     * {@link #registerCodecs(RegisterEvent)} runs.
     */
    public static void register(Identifier name, LibCondition condition) {
        if (DYNAMIC_CONDITIONS.putIfAbsent(name, new RecipeConditionWrapper(name, condition)) != null)
            LibConstants.LOG.warn("Duplicate condition with id: " + name);
    }

    /**
     * Registers a part check that {@link PartEnabledCondition} can look up.
     */
    public static void registerPartCondition(String part, Supplier<Boolean> check) {
        if (REGISTERED_PARTS.containsKey(part)) {
            throw new IllegalArgumentException("Can't have registered part with the same name as another");
        }
        REGISTERED_PARTS.put(part, check);
    }

    /**
     * The condition wrapper registered under the given name, or {@code null} if there is none.
     */
    public static RecipeConditionWrapper getCondition(Identifier name) {
        return DYNAMIC_CONDITIONS.get(name);
    }

    /**
     * Flushes every condition codec into the condition serializer registry. Hook this up to the mod
     * event bus; it filters the event itself.
     */
    public static void registerCodecs(final RegisterEvent event) {
        if (!event.getRegistryKey().equals(NeoForgeRegistries.Keys.CONDITION_CODECS))
            return;

        event.register(NeoForgeRegistries.Keys.CONDITION_CODECS, registry -> {
            registry.register(BlockExistsCondition.NAME, BlockExistsCondition.CODEC);
            registry.register(PartEnabledCondition.NAME, PartEnabledCondition.CODEC);
            registry.register(TagPopulatedCondition.ItemTagPopulatedCondition.NAME, TagPopulatedCondition.ItemTagPopulatedCondition.CODEC);
            registry.register(TagPopulatedCondition.BlockTagPopulatedCondition.NAME, TagPopulatedCondition.BlockTagPopulatedCondition.CODEC);

            for (Map.Entry<Identifier, RecipeConditionWrapper> entry : DYNAMIC_CONDITIONS.entrySet()) {
                registry.register(entry.getKey(), entry.getValue().codec());
            }
        });
    }
}
