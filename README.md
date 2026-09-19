# Assorted Lib

The library mod that all of the Assorted Mods use for easier development and code reuse.

Minecraft 26.2, on both NeoForge and Fabric from a single source tree. Branches are per Minecraft
version; `26.2` is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* Loader and its version — NeoForge, or Fabric Loader together with Fabric API
* Assorted Lib version
* Which Assorted mod you were using it with
* The full `latest.log`, plus the crash report if the game crashed

## Using it in a mod

Assorted Lib publishes three artifacts per version:

```
com.grim3212.assorted.lib:assortedlib-common-26.2:<version>
com.grim3212.assorted.lib:assortedlib-fabric-26.2:<version>
com.grim3212.assorted.lib:assortedlib-neoforge-26.2:<version>
```

Depend on `common` from your common module and on the matching loader artifact from each loader
module. The Minecraft version is part of the artifactId, so each version of the game gets its
own artifact tree. They are published to `https://maven.grimoid.com/mods`; to test a local build
instead, install it to your own Maven and resolve from `mavenLocal()`:

```bash
./gradlew publishToMavenLocal
```

## The instruction manual

Assorted Lib adds an in-game book, `assortedlib:instruction_manual`. Everything in it is data, read
from resource packs, so a mod adds its part by shipping files and a pack can change any of it
without touching the mod:

```
assets/assortedlib/manual/book.json                 what the book itself looks like
assets/<modid>/manual/section.json                  that mod's place in the index
assets/<modid>/manual/links.json                    what right clicking its content opens
assets/<modid>/manual/chapters/<chapter>.json       a chapter of it
assets/<ns>/manual/recipe_layouts/<path>.json       how <ns>:<path> recipes are drawn
```

A mod gets a section in the index by shipping `section.json`; one that ships nothing is simply
absent. `sort_order` is what orders the index, so a resource pack can move a mod by shipping its
own. Its chapters are the files beside it. Every `title` and `text` is a translation key, never
the words themselves.

```json
{ "sort_order": 0, "icon": "mymod:kiln" }
```

```json
{
  "sort_order": 0,
  "pages": [
    { "id": "intro", "type": "assortedlib:text", "title": "...", "text": "..." },
    { "id": "kiln", "type": "assortedlib:recipe", "text": "...", "recipes": ["mymod:kiln"] }
  ]
}
```

The page types are `assortedlib:text`, `assortedlib:image`, `assortedlib:item` (one or more items
shown large, cycling) and `assortedlib:recipe` (one or more recipes, cycling). A mod can add its own
with `ManualPageTypes.register` from client init.

### Recipe layouts

A recipe is drawn on the screen of the container that makes it, so crafting looks like a crafting
table and smelting like a furnace. A layout names the part of a container texture to draw and where
the items sit on it. The same positions that container's menu already gives its slots.

```json
{
  "texture": "mymod:textures/gui/container/kiln.png",
  "u": 28, "v": 20, "width": 118, "height": 60,
  "corner_radius": 3,
  "columns": 2,
  "inputs": [[4, 7], [28, 7]],
  "result": [87, 7],
  "extras": [
    { "position": [52, 42], "display": { "type": "minecraft:any_fuel" } },
    { "position": [34, 4], "display": { "type": "minecraft:tag", "tag": "mymod:kiln_tools" } }
  ]
}
```

`columns` is how many of `inputs` make a row, which puts a recipe smaller than the station in its
top left corner the way the recipe book does. `extras` are slots the station always has but no one
recipe fills, like a furnace's fuel or a mill's tool. Their contents are a vanilla `SlotDisplay`, so
`any_fuel` cycles through everything that burns and `tag` through everything in a tag; both are
ringed in gold and say what they take in their tooltip.

`corner_radius` rounds off the corners of the region so a screen cut out of its own frame does not
sit on the page as a hard rectangle; 0 leaves it square. A recipe whose inputs go in any arrangement
is marked with three loose pieces, in the region's top right corner unless `shapeless_marker` names
somewhere else.

Anything that cycles blocks/items like a recipe page with several recipes, a slot with several stacks, or an item page
with several items holds still while the cursor rests on it, so it can be read.

A page whose text outgrows its box scrolls rather than losing the tail of it. A bar appears down the
side of that page: drag it, click anywhere on its track to jump, or use the wheel over the page.

Assorted Lib ships layouts for crafting, the furnace family and the stonecutter under
`assets/minecraft/manual/recipe_layouts`. A pack can replace any of them by writing the same path,
and a type with no layout falls back to the crafting table.

### Pointing at things

Right clicking something with the manual opens its page. Name what opens what in
`assets/<modid>/manual/links.json`, grouped by the page they open. Listing a block covers the item
that places it; vanilla content can be listed too.

```json
{
  "links": [
    { "page": "mymod:machines/kiln", "blocks": ["mymod:kiln"] },
    { "page": "mymod:metals/steel", "items": ["mymod:steel_ingot"], "entities": ["mymod:slag_golem"] }
  ]
}
```

A page that depends on the thing's state like a multiblock that reads its own block state, comes
from implementing `IManualEntry` on the block, item or entity instead. `ManualLinks.linkBlock` and
friends register a link from code, for a link that has to be computed. They are asked in that
order, `IManualEntry`, then `links.json`, then the code registry so a pack can re-point any link
a mod registered in code.

An item frame reads the item on display, so a wall of framed samples doubles as an index. The book
takes that click before the frame does, or the frame would rotate instead; an empty frame has
nothing to read and still takes the book as any frame would.

While the manual is in hand a green check mark sits beside the crosshair whenever what it is on has
a page, so a link is visible before it is clicked. Turn the mark off with
`manual.showPageIndicator` in `assortedlib-client`.

### Generating a section

Nothing above has to be written by hand. `LibManualProvider` generates the whole section with a
mod's other client assets, through the same codecs the book reads, so a chapter that generates is
a chapter that loads:

```java
public class KilnManualProvider extends LibManualProvider {

    public KilnManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        this.section(0, MyBlocks.KILN.get());

        ChapterBuilder machines = this.chapter("machines");
        machines.recipes("kiln", "kiln").opens(MyBlocks.KILN.get());
        machines.items("tiers", MyBlocks.KILN.get(), MyBlocks.BIG_KILN.get()).every(50)
                .opens(MyBlocks.BIG_KILN.get());
        machines.text("firing");
    }
}
```

A recipe is named by what it makes, so another mod's recipe is named by another mod's item;
`recipesById` takes the id itself for a smelting or stonecutting variant that is not named after
its result. Links are declared on the page they open rather than in a list of their own, so the two
cannot disagree. Titles and bodies are the derived keys above, never passed. `opensEveryItem` and
`opensEveryBlock` take a predicate over this mod's ids, for a family named by its shape rather
than listed, so a new material joins its page on its own.

The provider refuses to generate while any block or item of the mod opens nothing, which is what
keeps a section complete as content is added.

### Showing a chapter conditionally

A chapter or page can carry conditions, written the way a recipe's load conditions are:

```json
{
  "conditions": [
    { "type": "assortedlib:mod_loaded", "mod": "jei" },
    { "type": "assortedlib:not", "value": { "type": "assortedlib:part_enabled", "part": "cage" } }
  ],
  "pages": [ ... ]
}
```

The library provides `part_enabled` (a piece of a mod its config can switch off), `mod_loaded`,
`item_exists`, `block_exists`, and `all_of` / `any_of` / `not` to combine them. A mod with a
question of its own registers a type for it with `DisplayConditions.register` rather than making
one of these fit.

From the provider:

```java
ChapterBuilder colorizer = this.chapter("colorizer").whenPartEnabled(Parts.COLORIZER);
lights.recipes("fluro", "fluro_white").whenPartEnabled(Parts.FLURO);
hanging.recipes("plaque", "plaque").when(modLoaded("jei"), itemExists(SOME_ITEM));
```

Conditions are applied when the book's data loads, when a world is joined and on every `/reload`.

### Recipes on the client

Recipe pages read whole recipes on the client. Vanilla crafting, smelting and stonecutting
are always synced by the library itself; a mod's own recipe type or serializer has to ask for
itself, from common init:

```java
SyncedRecipes.require(MyRecipes.KILN_TYPE, KilnRecipeSerializer.INSTANCE);
```

A recipe with no `RecipeDisplay` like a machine recipe kept out of the recipe book can say how it should
be drawn by implementing `IManualRecipeProvider`.

## Building

JDK 25 and the bundled Gradle wrapper. `common/` holds the loader-agnostic code; both loader
modules compile those sources inline rather than depending on a common jar, so there is nothing to
install between them.

How the build works - the Minecraft and loader versions, the runs, the tests, publishing - lives in
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild), pinned by `assortedbuild_version` in
`gradle.properties`. This repository only says what the mod is.

```bash
./gradlew build                        # every module; jars land in <module>/build/libs
./gradlew :neoforge:runClient
./gradlew :fabric:runClient
./gradlew :neoforge:runGameTestServer  # headless gametests, non-zero exit on failure
./gradlew :fabric:runGameTest
./gradlew :neoforge:runClientData      # datagen
./gradlew :neoforge:runServerData
```

Generated resources are committed. The NeoForge datagen writes them for both loaders; they are
regenerated, never hand-edited.

## License

[LGPL-3.0-only](LICENSE).
