package com.grim3212.assorted.lib.client.manual;

import com.grim3212.assorted.lib.conditions.DisplayCondition;
import com.grim3212.assorted.lib.conditions.DisplayConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

/**
 * A page and the id {@link com.grim3212.assorted.lib.manual.ManualPageRef} links to it by. Optional,
 * but an id survives pages being added around it where a position would not.
 */
public record ManualPageEntry(Optional<String> id, List<DisplayCondition> conditions, ManualPage page) {

    public static final Codec<ManualPageEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("id").forGetter(ManualPageEntry::id),
            DisplayConditions.LIST_CODEC.optionalFieldOf("conditions", List.of()).forGetter(ManualPageEntry::conditions),
            ManualPageTypes.MAP_CODEC.forGetter(ManualPageEntry::page)
    ).apply(instance, ManualPageEntry::new));

    /** Whether every condition on this page passes; a page naming none always is shown. */
    public boolean isVisible() {
        return DisplayConditions.allMatch(this.conditions);
    }

    public boolean hasId(String candidate) {
        return this.id.filter(candidate::equals).isPresent();
    }
}
