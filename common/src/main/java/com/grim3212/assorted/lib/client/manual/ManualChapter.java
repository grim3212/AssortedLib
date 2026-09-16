package com.grim3212.assorted.lib.client.manual;

import com.grim3212.assorted.lib.client.manual.page.ManualTextCodecs;
import com.grim3212.assorted.lib.conditions.DisplayCondition;
import com.grim3212.assorted.lib.conditions.DisplayConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A run of pages under one heading, from {@code assets/<modId>/manual/chapters/<id>.json}. Section
 * and chapter id come from the path, so the file does not repeat them.
 */
public record ManualChapter(String section, String id, Component title, Optional<Component> description, int sortOrder,
                            List<DisplayCondition> conditions, List<ManualPageEntry> pages) {

    /** The file's own contents, before {@link Definition#bind} adds what the path says. */
    public record Definition(Optional<Component> title, Optional<Component> description, int sortOrder,
                            List<DisplayCondition> conditions, List<ManualPageEntry> pages) {

        public static final Codec<Definition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ManualTextCodecs.TRANSLATABLE.optionalFieldOf("title").forGetter(Definition::title),
                ManualTextCodecs.TRANSLATABLE.optionalFieldOf("description").forGetter(Definition::description),
                Codec.INT.optionalFieldOf("sort_order", 0).forGetter(Definition::sortOrder),
                DisplayConditions.LIST_CODEC.optionalFieldOf("conditions", List.of()).forGetter(Definition::conditions),
                ManualPageEntry.CODEC.listOf().fieldOf("pages").forGetter(Definition::pages)
        ).apply(instance, Definition::new));

        public ManualChapter bind(String section, String id) {
            return new ManualChapter(section, id, this.title.orElseGet(() -> defaultTitle(section, id)),
                    this.description, this.sortOrder, this.conditions, this.pages);
        }
    }

    /**
     * Whether every condition on this chapter passes. A chapter whose content is switched off is not
     * a chapter worth reading, so it leaves the index entirely.
     */
    public boolean isVisible() {
        return DisplayConditions.allMatch(this.conditions);
    }

    /**
     * The chapters that pass their conditions, each holding only the pages that pass theirs, and
     * none left empty. Conditions are asked here and nowhere else: everything downstream reads this
     * list, so a hidden page leaves no gap and drawing re-tests nothing.
     */
    public static List<ManualChapter> resolve(List<ManualChapter> all) {
        List<ManualChapter> visible = new ArrayList<>(all.size());
        for (ManualChapter chapter : all) {
            if (!chapter.isVisible()) {
                continue;
            }

            List<ManualPageEntry> pages = chapter.pages.stream().filter(ManualPageEntry::isVisible).toList();
            if (!pages.isEmpty()) {
                visible.add(chapter.withPages(pages));
            }
        }

        return List.copyOf(visible);
    }

    public ManualChapter withPages(List<ManualPageEntry> pages) {
        return new ManualChapter(this.section, this.id, this.title, this.description, this.sortOrder,
                this.conditions, pages);
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
