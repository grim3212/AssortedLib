package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.client.manual.ManualChapter;
import com.grim3212.assorted.lib.client.manual.ManualPage;
import com.grim3212.assorted.lib.client.manual.ManualPageEntry;
import com.grim3212.assorted.lib.client.manual.ManualPageTypes;
import com.grim3212.assorted.lib.client.manual.page.ImagePage;
import com.grim3212.assorted.lib.client.manual.page.ItemPage;
import com.grim3212.assorted.lib.client.manual.page.RecipePage;
import com.grim3212.assorted.lib.client.manual.page.TextPage;
import com.grim3212.assorted.lib.manual.ManualLinks;
import com.grim3212.assorted.lib.manual.ManualPageRef;
import com.grim3212.assorted.lib.manual.ManualSection;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * A mod's manual section, generated with its client assets: {@code section.json}, one file per
 * chapter, and the {@code links.json} that says what opens which page.
 * <p>
 * Everything is written through the same codecs the book reads, so a chapter that generates is a
 * chapter that loads. Links are declared on the page they open rather than in a list of their own,
 * which is what lets {@link #run} refuse to generate while any block or item of this mod has no
 * page at all.
 * <p>
 * A page's heading and body are translation keys, derived rather than passed:
 * {@code manual.<modId>.chapter.<chapter>.<page>} and the same with {@code .title}. A chapter's own
 * name is {@code manual.<modId>.chapter.<chapter>}.
 */
public abstract class LibManualProvider implements DataProvider {

    private final String modId;
    private final Path root;
    private final PackOutput.PathProvider chapterPath;

    private final List<ChapterBuilder> chapters = new ArrayList<>();
    private ManualSection.Definition section = new ManualSection.Definition(ManualSection.DEFAULT_SORT_ORDER, Optional.empty());

    protected LibManualProvider(PackOutput output, String modId) {
        this.modId = modId;
        this.root = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modId).resolve("manual");
        this.chapterPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "manual/chapters");

        // Datagen never runs client init, which is where the book's page types are normally named.
        ManualPageTypes.bootstrap();
    }

    /** Where the chapters are declared. Called once, when the provider runs. */
    protected abstract void addChapters();

    /**
     * This mod's place in the index. Lower sorts first; the icon is drawn beside it.
     */
    protected void section(int sortOrder, ItemLike icon) {
        Holder<Item> holder = BuiltInRegistries.ITEM.wrapAsHolder(icon.asItem());
        this.section = new ManualSection.Definition(sortOrder, Optional.of(holder));
    }

    /** A chapter, sorted by the order it is declared in. */
    protected ChapterBuilder chapter(String id) {
        return this.chapter(id, this.chapters.size() * 10);
    }

    protected ChapterBuilder chapter(String id, int sortOrder) {
        ChapterBuilder chapter = new ChapterBuilder(id, sortOrder);
        this.chapters.add(chapter);
        return chapter;
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput cache) {
        this.addChapters();
        this.verify();

        List<CompletableFuture<?>> writes = new ArrayList<>();
        writes.add(DataProvider.saveStable(cache, ManualSection.Definition.CODEC, this.section,
                this.root.resolve("section.json")));
        writes.add(DataProvider.saveStable(cache, ManualLinks.Group.FILE_CODEC, this.groups(),
                this.root.resolve("links.json")));

        for (ChapterBuilder chapter : this.chapters) {
            writes.add(DataProvider.saveStable(cache, ManualChapter.Definition.CODEC, chapter.build(),
                    this.chapterPath.json(Identifier.fromNamespaceAndPath(this.modId, chapter.id))));
        }

        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Manual: " + this.modId;
    }

    /** One group per page that something opens, in the order the chapters declare them. */
    private List<ManualLinks.Group> groups() {
        List<ManualLinks.Group> groups = new ArrayList<>();
        for (ChapterBuilder chapter : this.chapters) {
            for (PageBuilder page : chapter.pages) {
                if (page.opensNothing()) {
                    continue;
                }
                groups.add(new ManualLinks.Group(ManualPageRef.of(this.modId, chapter.id, page.id),
                        List.copyOf(page.blocks), List.copyOf(page.items), List.copyOf(page.entities)));
            }
        }
        return groups;
    }

    /**
     * Refuses to generate a section with a hole in it. Right clicking anything this mod adds is
     * expected to open something, so anything registered and unclaimed is a mistake, as is a link
     * to something that is not registered at all.
     */
    private void verify() {
        Set<Identifier> blocks = new LinkedHashSet<>();
        Set<Identifier> items = new LinkedHashSet<>();
        List<String> problems = new ArrayList<>();

        for (ChapterBuilder chapter : this.chapters) {
            Set<String> pageIds = new LinkedHashSet<>();
            for (PageBuilder page : chapter.pages) {
                if (!pageIds.add(page.id)) {
                    problems.add("chapter " + chapter.id + " has two pages called " + page.id);
                }
                for (Identifier id : page.blocks) {
                    if (!blocks.add(id)) {
                        problems.add("block " + id + " is opened by more than one page");
                    }
                }
                for (Identifier id : page.items) {
                    if (!items.add(id)) {
                        problems.add("item " + id + " is opened by more than one page");
                    }
                }
            }
        }

        // A block's item shares its id, so listing the block covers the item that places it.
        Set<Identifier> covered = new LinkedHashSet<>(items);
        covered.addAll(blocks);

        BuiltInRegistries.BLOCK.registryKeySet().stream()
                .map(ResourceKey::identifier)
                .filter(id -> id.getNamespace().equals(this.modId))
                .filter(id -> !blocks.contains(id))
                .forEach(id -> problems.add("block " + id + " opens no page"));

        BuiltInRegistries.ITEM.registryKeySet().stream()
                .map(ResourceKey::identifier)
                .filter(id -> id.getNamespace().equals(this.modId))
                .filter(id -> !covered.contains(id))
                .forEach(id -> problems.add("item " + id + " opens no page"));

        blocks.stream().filter(id -> !BuiltInRegistries.BLOCK.containsKey(id))
                .forEach(id -> problems.add(id + " is linked as a block but is not registered"));
        items.stream().filter(id -> !BuiltInRegistries.ITEM.containsKey(id))
                .forEach(id -> problems.add(id + " is linked as an item but is not registered"));

        if (!problems.isEmpty()) {
            throw new IllegalStateException(this.getName() + " has " + problems.size() + " problems:\n  "
                    + String.join("\n  ", problems));
        }
    }

    /** A run of pages under one heading. */
    public final class ChapterBuilder {

        private final String id;
        private final int sortOrder;
        private final List<PageBuilder> pages = new ArrayList<>();

        private ChapterBuilder(String id, int sortOrder) {
            this.id = id;
            this.sortOrder = sortOrder;
        }

        /** Text and nothing else. */
        public PageBuilder text(String page) {
            return this.add(page, interval -> new TextPage(this.title(page), this.body(page)));
        }

        /**
         * A picture with text under it.
         *
         * @param image full texture path, such as {@code mymod:textures/gui/manual/kiln.png}
         * @param width the texture's own size, which cannot be asked for at this point
         */
        public PageBuilder image(String page, Identifier image, int width, int height) {
            return this.add(page, interval ->
                    new ImagePage(this.title(page), image, width, height, Optional.of(this.body(page))));
        }

        /** One or more items shown large, cycling. */
        public PageBuilder items(String page, ItemLike... shown) {
            List<Holder<Item>> holders = new ArrayList<>();
            for (ItemLike item : shown) {
                holders.add(BuiltInRegistries.ITEM.wrapAsHolder(item.asItem()));
            }
            return this.add(page, interval ->
                    new ItemPage(this.title(page), List.copyOf(holders), interval, Optional.of(this.body(page))));
        }

        /** One or more recipes drawn on their station, cycling. Ids are this mod's own. */
        public PageBuilder recipes(String page, String... recipes) {
            List<ResourceKey<Recipe<?>>> keys = new ArrayList<>();
            for (String recipe : recipes) {
                keys.add(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(modId, recipe)));
            }
            return this.add(page, interval ->
                    new RecipePage(this.title(page), List.copyOf(keys), interval, Optional.of(this.body(page))));
        }

        private PageBuilder add(String page, IntFunction<ManualPage> build) {
            PageBuilder builder = new PageBuilder(page, build);
            this.pages.add(builder);
            return builder;
        }

        private Optional<Component> title(String page) {
            return Optional.of(Component.translatable(key(this.id, page) + ".title"));
        }

        private Component body(String page) {
            return Component.translatable(key(this.id, page));
        }

        private ManualChapter.Definition build() {
            List<ManualPageEntry> entries = new ArrayList<>();
            for (PageBuilder page : this.pages) {
                entries.add(new ManualPageEntry(Optional.of(page.id), page.build()));
            }
            return new ManualChapter.Definition(Optional.empty(), Optional.empty(), this.sortOrder, entries);
        }
    }

    /** One page, and whatever opens it. */
    public final class PageBuilder {

        /** What {@link ItemPage} and {@link RecipePage} cycle at when nothing says otherwise. */
        private static final int DEFAULT_INTERVAL = 40;

        private final String id;
        private final IntFunction<ManualPage> build;
        private final List<Identifier> blocks = new ArrayList<>();
        private final List<Identifier> items = new ArrayList<>();
        private final List<Identifier> entities = new ArrayList<>();
        private int interval = DEFAULT_INTERVAL;

        private PageBuilder(String id, IntFunction<ManualPage> build) {
            this.id = id;
            this.build = build;
        }

        /** How long each item or recipe is shown for, in ticks. Ignored by a page that draws one thing. */
        public PageBuilder every(int ticks) {
            this.interval = ticks;
            return this;
        }

        /** Right clicking any of these with the manual opens this page. A block covers its own item. */
        public PageBuilder opens(ItemLike... things) {
            for (ItemLike thing : things) {
                if (thing instanceof Block block) {
                    this.blocks.add(BuiltInRegistries.BLOCK.getKey(block));
                } else if (thing instanceof Item item) {
                    this.items.add(BuiltInRegistries.ITEM.getKey(item));
                } else {
                    throw new IllegalArgumentException("Not a block or an item: " + thing);
                }
            }
            return this;
        }

        public PageBuilder opens(EntityType<?>... types) {
            for (EntityType<?> type : types) {
                this.entities.add(BuiltInRegistries.ENTITY_TYPE.getKey(type));
            }
            return this;
        }

        /** For a block with no item of its own, which cannot be named as an {@link ItemLike}. */
        public PageBuilder opensBlocks(Identifier... ids) {
            this.blocks.addAll(List.of(ids));
            return this;
        }

        /**
         * Every item this mod registers whose id {@code match} accepts, for a family named by its
         * shape rather than listed: a new material joins its page without a line being added here.
         * Two pages matching the same item is an error, so a narrower family excludes the wider one.
         */
        public PageBuilder opensEveryItem(Predicate<Identifier> match) {
            this.items.addAll(matching(BuiltInRegistries.ITEM.registryKeySet(), match));
            return this;
        }

        /** As {@link #opensEveryItem}, for this mod's blocks. */
        public PageBuilder opensEveryBlock(Predicate<Identifier> match) {
            this.blocks.addAll(matching(BuiltInRegistries.BLOCK.registryKeySet(), match));
            return this;
        }

        private List<Identifier> matching(Set<? extends ResourceKey<?>> keys, Predicate<Identifier> match) {
            return keys.stream()
                    .map(ResourceKey::identifier)
                    .filter(id -> id.getNamespace().equals(modId))
                    .filter(match)
                    .sorted(Comparator.comparing(Identifier::getPath))
                    .toList();
        }

        private boolean opensNothing() {
            return this.blocks.isEmpty() && this.items.isEmpty() && this.entities.isEmpty();
        }

        private ManualPage build() {
            return this.build.apply(this.interval);
        }
    }

    /** The translation key a page's body reads, which a mod's language provider names. */
    public static String key(String modId, String chapter, String page) {
        return "manual." + modId + ".chapter." + chapter + "." + page;
    }

    private String key(String chapter, String page) {
        return key(this.modId, chapter, page);
    }
}
