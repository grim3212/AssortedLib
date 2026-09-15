package com.grim3212.assorted.lib.client.manual;

import com.grim3212.assorted.lib.client.manual.page.ManualTextCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

/**
 * A run of pages under one heading, from {@code assets/<modId>/manual/chapters/<id>.json}. Section
 * and chapter id come from the path, so the file does not repeat them.
 */
public record ManualChapter(String section, String id, Component title, Optional<Component> description, int sortOrder, List<ManualPageEntry> pages) {

    /** The file's own contents, before {@link Definition#bind} adds what the path says. */
    public record Definition(Optional<Component> title, Optional<Component> description, int sortOrder, List<ManualPageEntry> pages) {

        public static final Codec<Definition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ManualTextCodecs.TRANSLATABLE.optionalFieldOf("title").forGetter(Definition::title),
                ManualTextCodecs.TRANSLATABLE.optionalFieldOf("description").forGetter(Definition::description),
                Codec.INT.optionalFieldOf("sort_order", 0).forGetter(Definition::sortOrder),
                ManualPageEntry.CODEC.listOf().fieldOf("pages").forGetter(Definition::pages)
        ).apply(instance, Definition::new));

        public ManualChapter bind(String section, String id) {
            return new ManualChapter(section, id, this.title.orElseGet(() -> defaultTitle(section, id)), this.description, this.sortOrder, this.pages);
        }
    }

    public static Component defaultTitle(String section, String id) {
        return Component.translatable("manual." + section + ".chapter." + id);
    }

    public Optional<ManualPageEntry> page(int index) {
        return index >= 0 && index < this.pages.size() ? Optional.of(this.pages.get(index)) : Optional.empty();
    }

    /** Falls back to the first page, so a link survives the page it names being renamed. */
    public int indexOfPage(String page) {
        if (page.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < this.pages.size(); i++) {
            if (this.pages.get(i).hasId(page)) {
                return i;
            }
        }

        return 0;
    }

    public int pageCount() {
        return this.pages.size();
    }
}
