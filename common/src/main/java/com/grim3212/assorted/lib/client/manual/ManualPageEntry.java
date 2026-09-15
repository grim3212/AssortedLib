package com.grim3212.assorted.lib.client.manual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * A page and the id {@link com.grim3212.assorted.lib.manual.ManualPageRef} links to it by. Optional,
 * but an id survives pages being added around it where a position would not.
 */
public record ManualPageEntry(Optional<String> id, ManualPage page) {

    public static final Codec<ManualPageEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("id").forGetter(ManualPageEntry::id),
            ManualPageTypes.MAP_CODEC.forGetter(ManualPageEntry::page)
    ).apply(instance, ManualPageEntry::new));

    public boolean hasId(String candidate) {
        return this.id.filter(candidate::equals).isPresent();
    }
}
