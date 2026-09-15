package com.grim3212.assorted.lib.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.lib.client.manual.ManualBookStyle;
import com.grim3212.assorted.lib.client.manual.ManualRecipeLayout;
import com.grim3212.assorted.lib.manual.LibItems;
import com.grim3212.assorted.lib.manual.ManualLinks;
import com.grim3212.assorted.lib.manual.ManualPageRef;
import com.grim3212.assorted.lib.manual.ManualRegistry;
import com.grim3212.assorted.lib.manual.ManualSection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;

/**
 * The parts of the manual a dedicated server can see: the page addresses, the section registry and
 * the lookups that decide where right clicking something lands. The book itself is client only and
 * is not covered here.
 */
final class ManualTests {

    /** The recipe layouts this mod ships, all for vanilla's own stations. */
    private static final List<String> LAYOUTS =
            List.of("crafting", "smelting", "blasting", "smoking", "campfire_cooking", "stonecutting");

    private static final ManualPageRef ANVIL_PAGE = ManualPageRef.of("assortedlibtest", "blocks", "anvil");
    private static final ManualPageRef APPLE_PAGE = ManualPageRef.of("assortedlibtest", "food", "apple");
    private static final ManualPageRef PIG_PAGE = ManualPageRef.of("assortedlibtest", "animals", "pig");

    private ManualTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("manual_page_refs_round_trip", ManualTests::pageRefsRoundTrip);
        out.accept("manual_page_refs_reject_nonsense", ManualTests::pageRefsRejectNonsense);
        out.accept("manual_links_resolve_by_kind", ManualTests::linksResolveByKind);
        out.accept("manual_links_read_framed_items", ManualTests::linksReadFramedItems);
        out.accept("manual_takes_the_click_off_a_frame", ManualTests::manualTakesTheClickOffAFrame);
        out.accept("manual_section_is_registered", ManualTests::sectionIsRegistered);
        out.accept("manual_codecs_read_the_shipped_data", ManualTests::codecsReadTheShippedData);
    }

    /** A reference survives being written out and read back, with and without a page. */
    private static void pageRefsRoundTrip(GameTestHelper helper) {
        ManualPageRef page = ManualPageRef.of("assortedcore", "machines", "alloy_forge");
        assertEquals(helper, "assortedcore:machines/alloy_forge", page.toString(), "page reference text");
        assertEquals(helper, page, ManualPageRef.parse(page.toString()), "page reference round trip");

        ManualPageRef chapter = ManualPageRef.ofChapter("assortedcore", "machines");
        assertEquals(helper, "assortedcore:machines", chapter.toString(), "chapter reference text");
        assertEquals(helper, chapter, ManualPageRef.parse(chapter.toString()), "chapter reference round trip");
        if (!chapter.isChapterOnly()) {
            helper.fail("A reference with no page should read as chapter only");
        }

        helper.succeed();
    }

    /** Anything that is not {@code section:chapter[/page]} is refused rather than half parsed. */
    private static void pageRefsRejectNonsense(GameTestHelper helper) {
        for (String bad : new String[]{"", "nocolon", ":chapter", "section:", "section:/page", "section:chapter/"}) {
            if (ManualPageRef.read(bad).result().isPresent()) {
                helper.fail("Expected '" + bad + "' to be refused as a page reference");
            }
        }

        helper.succeed();
    }

    /** A block, an item and an entity each find the page they were linked to, and nothing else does. */
    private static void linksResolveByKind(GameTestHelper helper) {
        ManualLinks.link(Blocks.ANVIL, ANVIL_PAGE);
        ManualLinks.link(Items.APPLE, APPLE_PAGE);
        ManualLinks.link(EntityTypes.PIG, PIG_PAGE);

        BlockState anvil = Blocks.ANVIL.defaultBlockState();
        assertEquals(helper, ANVIL_PAGE, ManualLinks.pageFor(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), anvil), "block link");
        assertEquals(helper, APPLE_PAGE, ManualLinks.pageFor(new ItemStack(Items.APPLE)), "item link");

        var pig = helper.spawn(EntityTypes.PIG, BlockPos.ZERO);
        assertEquals(helper, PIG_PAGE, ManualLinks.pageFor(pig), "entity link");

        if (ManualLinks.pageFor(new ItemStack(Items.STONE)) != null) {
            helper.fail("An unlinked item should have no page");
        }
        if (ManualLinks.pageFor(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), Blocks.STONE.defaultBlockState()) != null) {
            helper.fail("An unlinked block should have no page");
        }

        helper.succeed();
    }

    /** A frame reads the item on display, and an empty one reads as nothing. */
    private static void linksReadFramedItems(GameTestHelper helper) {
        ManualLinks.link(Items.APPLE, APPLE_PAGE);

        ItemFrame frame = frameHolding(helper, ItemStack.EMPTY);
        if (ManualLinks.pageFor(frame) != null) {
            helper.fail("An empty frame should have no page");
        }

        frame.setItem(new ItemStack(Items.APPLE));
        assertEquals(helper, APPLE_PAGE, ManualLinks.pageFor(frame), "framed item link");
        helper.succeed();
    }

    /**
     * The interaction the book takes before the frame's own: a frame holding something with a page
     * is read rather than rotated, so the click reaches the book at all.
     */
    private static void manualTakesTheClickOffAFrame(GameTestHelper helper) {
        ManualLinks.link(Items.APPLE, APPLE_PAGE);

        ItemFrame frame = frameHolding(helper, new ItemStack(Items.APPLE));
        ServerPlayer player = survivalPlayer(helper, new ItemStack(LibItems.INSTRUCTION_MANUAL.get()));

        InteractionResult result = player.interactOn(frame, InteractionHand.MAIN_HAND, Vec3.ZERO);
        helper.assertTrue(result.consumesAction(), "the manual did not take the interaction, got " + result);
        helper.assertValueEqual(frame.getRotation(), 0, "frame rotation after the manual was used on it");

        // A frame with nothing to read is left to vanilla, which is what lets the book be framed.
        ItemFrame empty = frameHolding(helper, ItemStack.EMPTY);
        player.interactOn(empty, InteractionHand.MAIN_HAND, Vec3.ZERO);
        helper.assertTrue(empty.getItem().is(LibItems.INSTRUCTION_MANUAL.get()),
                "an empty frame should still take the book, it holds " + empty.getItem());

        helper.succeed();
    }

    private static ItemFrame frameHolding(GameTestHelper helper, ItemStack held) {
        BlockPos pos = helper.absolutePos(BlockPos.ZERO.above());
        ItemFrame frame = new ItemFrame(helper.getLevel(), pos, Direction.NORTH);
        frame.setItem(held);
        helper.getLevel().addFreshEntity(frame);
        return frame;
    }

    /**
     * The code path a mod can use instead of shipping a {@code section.json}. The library's own
     * section is data, so this registers one of its own rather than looking for it.
     */
    private static void sectionIsRegistered(GameTestHelper helper) {
        if (LibItems.INSTRUCTION_MANUAL.get() == null) {
            helper.fail("The instruction manual item should be registered");
        }

        String modId = "assortedlibtest";
        ManualRegistry.register(ManualSection.of(modId, () -> new ItemStack(LibItems.INSTRUCTION_MANUAL.get())));

        ManualSection registered = ManualRegistry.section(modId).orElse(null);
        if (registered == null) {
            helper.fail("A section registered in code should be readable back");
        } else if (!ManualRegistry.sections().contains(registered)) {
            helper.fail("A registered section should appear in the index order");
        }

        helper.succeed();
    }

    /**
     * The codecs the book is described with, run over the files the library ships. As much about
     * the codecs themselves as about the data: one built from a static field declared below it
     * reads that field as null and takes the whole class down with it, which on a client shows up
     * only as a resource reload dying half way.
     */
    private static void codecsReadTheShippedData(GameTestHelper helper) {
        parse(helper, ManualBookStyle.CODEC, "{}", "an empty book style");
        parse(helper, ManualBookStyle.CODEC,
                "{\"width\": 400, \"colors\": {\"text\": \"#112233\", \"title\": \"#FF112233\"}}",
                "a book style overriding some fields");

        ManualBookStyle style = ManualBookStyle.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{}")).getOrThrow();
        if (style.textColor() != ManualBookStyle.DEFAULT.textColor() || style.width() != ManualBookStyle.DEFAULT.width()) {
            helper.fail("An empty book style should read as the defaults");
        }

        for (String bad : new String[]{"{\"colors\": {\"text\": \"nonsense\"}}", "{\"colors\": {\"text\": \"#12\"}}"}) {
            if (ManualBookStyle.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(bad)).result().isPresent()) {
                helper.fail("Expected '" + bad + "' to be refused as a book style");
            }
        }

        parse(helper, ManualSection.Definition.CODEC, resource(helper, "/assets/assortedlib/manual/section.json"), "the library's section");

        for (String layout : LAYOUTS) {
            String path = "/assets/minecraft/manual/recipe_layouts/" + layout + ".json";
            parse(helper, ManualRecipeLayout.CODEC, resource(helper, path), path);
        }

        helper.succeed();
    }

    private static <T> void parse(GameTestHelper helper, Codec<T> codec, String json, String what) {
        DataResult<T> result = codec.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
        if (result.error().isPresent()) {
            helper.fail("Could not read " + what + ": " + result.error().get().message());
        }
    }

    private static String resource(GameTestHelper helper, String path) {
        try (InputStream in = ManualTests.class.getResourceAsStream(path)) {
            if (in == null) {
                throw helper.assertionException(path + " is not on the classpath");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw helper.assertionException("could not read " + path + ": " + e);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String what) {
        if (!expected.equals(actual)) {
            helper.fail("Wrong " + what + ": expected " + expected + " but got " + actual);
        }
    }
}
