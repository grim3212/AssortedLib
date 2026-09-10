package com.grim3212.assorted.lib.gametest;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedLib.
 * <p>
 * Everything downstream sits on this module, so what these pin down is not a feature but a
 * <em>parity</em> claim: that the NeoForge and Fabric implementations of each
 * {@code lib/platform/services} interface answer the same way about the same world. The bodies live
 * in common and are byte-identical on both loaders; only the {@code Registries.TEST_FUNCTION}
 * registration differs, and {@code data/assortedlib/test_instance/*.json} pairs each one with the
 * shared {@code test_box} structure.
 * <p>
 * Client-only services ({@code ClientServices}) are deliberately untested here - a gametest run is a
 * dedicated server, where loading them would fail by design.
 */
public final class LibGameTests {

    private LibGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("services_all_resolve", LibGameTests::servicesAllResolve);
        out.accept("registry_round_trips", LibGameTests::registryRoundTrips);
        out.accept("tiered_tool_agrees_with_vanilla", LibGameTests::tieredToolAgreesWithVanilla);
        out.accept("storage_handler_insert_extract", LibGameTests::storageHandlerInsertExtract);
        out.accept("block_entity_handler_from_level", LibGameTests::blockEntityHandlerFromLevel);
        out.accept("level_properties_match_level", LibGameTests::levelPropertiesMatchLevel);
        out.accept("fluid_manager_reads_bucket", LibGameTests::fluidManagerReadsBucket);
        out.accept("ingredients_combine", LibGameTests::ingredientsCombine);
        out.accept("common_tags_are_bound", LibGameTests::commonTagsAreBound);
    }

    /** Somewhere central in the 9x9x9 test box, one block above the floor. */
    private static final BlockPos WORK = new BlockPos(4, 1, 4);

    /**
     * Every server-safe service resolves, and resolves to <em>this</em> loader's implementation.
     * <p>
     * A missing {@code META-INF/services} entry is silent until the first caller touches the
     * service, which in practice means a crash deep inside some downstream mod. This is the cheapest
     * possible tripwire for it, and the class-name check additionally catches the case where the
     * wrong loader's jar won the ServiceLoader lookup.
     */
    private static void servicesAllResolve(GameTestHelper helper) {
        String platform = Services.PLATFORM.getPlatformName();
        helper.assertTrue(platform.equals("Forge") || platform.equals("Fabric"),
                "unexpected platform name '" + platform + "'; LibCommonTagProvider branches on this exact string");

        assertLoaded(helper, platform, "IPlatformHelper", Services.PLATFORM);
        assertLoaded(helper, platform, "IConfigHelper", Services.CONFIG);
        assertLoaded(helper, platform, "IRegistryFactory", Services.REGISTRY_FACTORY);
        assertLoaded(helper, platform, "INetworkHelper", Services.NETWORK);
        assertLoaded(helper, platform, "ILevelPropertyAccessor", Services.LEVEL_PROPERTIES);
        assertLoaded(helper, platform, "IEventHelper", Services.EVENTS);
        assertLoaded(helper, platform, "IFluidManager", Services.FLUIDS);
        assertLoaded(helper, platform, "IConditionHelper", Services.CONDITIONS);
        assertLoaded(helper, platform, "IIngredientHelper", Services.INGREDIENTS);
        assertLoaded(helper, platform, "IWorldGenHelper", Services.WORLD_GEN);
        assertLoaded(helper, platform, "IInventoryHelper", Services.INVENTORY);

        // The environment queries, against a dedicated server running out of a gradle workspace.
        helper.assertFalse(Services.PLATFORM.isProduction(), "a gradle run reported itself as a production environment");
        helper.assertTrue(Services.PLATFORM.getCurrentDistribution() == Dist.DEDICATED_SERVER,
                "a gametest server reported distribution " + Services.PLATFORM.getCurrentDistribution());
        helper.assertFalse(Services.PLATFORM.isPhysicalClient(), "a gametest server reported itself as a physical client");

        helper.assertTrue(Services.PLATFORM.isModLoaded("assortedlib"), "assortedlib is not loaded, in its own gametest");
        helper.assertTrue(Services.PLATFORM.isModLoaded("minecraft"), "minecraft is not reported as a loaded mod");
        helper.assertFalse(Services.PLATFORM.isModLoaded("assortedlib_not_a_real_mod"), "an unknown mod id reported as loaded");

        // getRandomDungeonEntity reads MonsterRoomFeature.MOBS through an accessor mixin, so this is
        // also the cheapest check that the mixin config applied on this loader.
        EntityType<?> dungeonMob = Services.PLATFORM.getRandomDungeonEntity(RandomSource.create(1234L));
        helper.assertTrue(dungeonMob != null, "getRandomDungeonEntity returned null");
        helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getKey(dungeonMob) != null,
                "getRandomDungeonEntity returned an unregistered entity type");

        helper.succeed();
    }

    private static void assertLoaded(GameTestHelper helper, String platform, String service, Object impl) {
        helper.assertTrue(impl != null, service + " did not resolve");
        helper.assertTrue(impl.getClass().getSimpleName().startsWith(platform),
                service + " resolved to " + impl.getClass().getName() + ", which is not a " + platform + " implementation");
    }

    /** {@link ILoaderRegistry} is a straight view of a vanilla registry now, and must behave like one. */
    private static void registryRoundTrips(GameTestHelper helper) {
        ILoaderRegistry<Block> blocks = Services.PLATFORM.getRegistry(Registries.BLOCK);
        Identifier stoneId = Identifier.withDefaultNamespace("stone");

        helper.assertTrue(blocks.getValue(stoneId).orElse(null) == Blocks.STONE,
                "the block registry did not hand back the same Blocks.STONE instance");
        helper.assertTrue(stoneId.equals(blocks.getRegistryName(Blocks.STONE)), "minecraft:stone did not round-trip its id");
        helper.assertTrue(blocks.containsKey(stoneId), "the block registry does not contain minecraft:stone");
        helper.assertTrue(blocks.contains(Blocks.STONE), "the block registry does not contain Blocks.STONE");

        // Only containsKey is asserted for an unknown id. getValue disagrees across the loaders on a
        // DEFAULTED registry - NeoForge's Registry#getValue hands back minecraft:air, Fabric's
        // #getOptional hands back empty - and that divergence is reported rather than papered over.
        Identifier missing = Identifier.fromNamespaceAndPath("assortedlib", "not_a_real_block");
        helper.assertFalse(blocks.containsKey(missing), "the block registry claims to contain " + missing);

        ILoaderRegistry<Item> items = Services.PLATFORM.getRegistry(Registries.ITEM);
        helper.assertTrue(items.getValue(Identifier.withDefaultNamespace("stick")).orElse(null) == Items.STICK,
                "the item registry did not hand back the same Items.STICK instance");
        helper.assertTrue(items.getValues().count() == BuiltInRegistries.ITEM.keySet().size(),
                "the wrapped item registry and the vanilla one disagree on size");

        helper.succeed();
    }

    /**
     * {@code Tiers} and {@code TieredItem} are gone; a tier is expressed as the vanilla harvest level
     * a tool can actually reach. Anchored against {@code ItemStack#isCorrectToolForDrops} rather than
     * against a recomputed expectation, so the test cannot drift with the implementation.
     */
    private static void tieredToolAgreesWithVanilla(GameTestHelper helper) {
        assertPickaxeTier(helper, Items.WOODEN_PICKAXE);
        assertPickaxeTier(helper, Items.STONE_PICKAXE);
        assertPickaxeTier(helper, Items.IRON_PICKAXE);
        assertPickaxeTier(helper, Items.DIAMOND_PICKAXE);
        assertPickaxeTier(helper, Items.NETHERITE_PICKAXE);
        assertPickaxeTier(helper, Items.GOLDEN_PICKAXE);

        // Wrong tool type, right tier: the tag gate has to reject these outright.
        helper.assertFalse(Services.PLATFORM.isTieredTool(new ItemStack(Items.IRON_SWORD), IPlatformHelper.ToolTier.WOOD, IPlatformHelper.ToolType.PICKAXE),
                "an iron sword passed as a pickaxe");
        helper.assertFalse(Services.PLATFORM.isTieredTool(new ItemStack(Items.SHEARS), IPlatformHelper.ToolTier.WOOD, IPlatformHelper.ToolType.PICKAXE),
                "shears passed as a pickaxe");
        helper.assertFalse(Services.PLATFORM.isTieredTool(new ItemStack(Items.IRON_SHOVEL), IPlatformHelper.ToolTier.IRON, IPlatformHelper.ToolType.PICKAXE),
                "an iron shovel passed as a pickaxe");

        // Only the tag half is asserted for a non-pickaxe: harvestLevelOf() probes iron ore, diamond
        // ore and obsidian, and no shovel, axe or hoe is ever "correct for drops" on any of them, so
        // every one of them reads as tier WOOD whatever it is made of. Reported, not papered over -
        // the sole caller today (MachineUtil) asks about PICKAXE, so nothing is broken by it yet.
        helper.assertTrue(Services.PLATFORM.isTieredTool(new ItemStack(Items.IRON_SHOVEL), IPlatformHelper.ToolTier.WOOD, IPlatformHelper.ToolType.SHOVEL),
                "an iron shovel failed the shovel tag gate");
        helper.assertFalse(Services.PLATFORM.isTieredTool(new ItemStack(Items.IRON_PICKAXE), IPlatformHelper.ToolTier.WOOD, IPlatformHelper.ToolType.SHOVEL),
                "an iron pickaxe passed as a shovel");
        helper.assertFalse(Services.PLATFORM.isTieredTool(new ItemStack(Items.STICK), IPlatformHelper.ToolTier.WOOD, IPlatformHelper.ToolType.PICKAXE),
                "a stick passed as a pickaxe");

        helper.succeed();
    }

    /** Each tier gate on a pickaxe must match what vanilla lets that pickaxe harvest. */
    private static void assertPickaxeTier(GameTestHelper helper, Item pickaxe) {
        ItemStack stack = new ItemStack(pickaxe);
        assertTierMatches(helper, stack, IPlatformHelper.ToolTier.STONE, Blocks.IRON_ORE);
        assertTierMatches(helper, stack, IPlatformHelper.ToolTier.IRON, Blocks.DIAMOND_ORE);
        assertTierMatches(helper, stack, IPlatformHelper.ToolTier.DIAMOND, Blocks.OBSIDIAN);
    }

    private static void assertTierMatches(GameTestHelper helper, ItemStack stack, IPlatformHelper.ToolTier tier, Block gatedBlock) {
        boolean throughLib = Services.PLATFORM.isTieredTool(stack, tier, IPlatformHelper.ToolType.PICKAXE);
        boolean vanilla = stack.isCorrectToolForDrops(gatedBlock.defaultBlockState());
        helper.assertTrue(throughLib == vanilla, stack.getItem() + " at tier " + tier + ": isTieredTool said "
                + throughLib + " but vanilla harvests " + gatedBlock.getName().getString() + " = " + vanilla);
    }

    /**
     * The library's own handler: slot limits, {@code isItemValid}, and simulation leaving the
     * inventory alone. Pure common code, but every downstream inventory is built on it.
     */
    private static void storageHandlerInsertExtract(GameTestHelper helper) {
        // Slot 1 is deliberately narrow and picky, so limit and validity are exercised separately.
        IItemStorageHandler handler = new ItemStackStorageHandler(2) {
            @Override
            public int getSlotLimit(int slot) {
                return slot == 1 ? 4 : super.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot != 1 || stack.is(Items.STICK);
            }
        };

        helper.assertTrue(handler.isEmpty(), "a fresh handler was not empty");

        helper.assertTrue(handler.insertItem(0, new ItemStack(Items.STICK, 16), false).isEmpty(),
                "inserting 16 sticks into an empty 64 slot left a remainder");
        helper.assertValueEqual(handler.getStackInSlot(0).getCount(), 16, "slot 0 count after insert");

        helper.assertTrue(handler.insertItem(0, new ItemStack(Items.STICK, 8), true).isEmpty(),
                "a simulated insert of 8 more sticks reported a remainder");
        helper.assertValueEqual(handler.getStackInSlot(0).getCount(), 16, "slot 0 count after a SIMULATED insert");

        helper.assertFalse(handler.isItemValid(1, new ItemStack(Items.STONE)), "the picky slot accepted stone from isItemValid");
        helper.assertTrue(handler.isItemValid(1, new ItemStack(Items.STICK)), "the picky slot rejected sticks from isItemValid");
        helper.assertTrue(handler.insertItem(1, new ItemStack(Items.STONE), false).getCount() == 1,
                "inserting stone into the picky slot did not hand the whole stack back");
        helper.assertTrue(handler.getStackInSlot(1).isEmpty(), "the picky slot took stone anyway");

        ItemStack overLimit = handler.insertItem(1, new ItemStack(Items.STICK, 10), false);
        helper.assertValueEqual(overLimit.getCount(), 6, "remainder after inserting 10 sticks into a limit-4 slot");
        helper.assertValueEqual(handler.getStackInSlot(1).getCount(), 4, "slot 1 count against a limit of 4");

        helper.assertValueEqual(handler.extractItem(0, 8, true).getCount(), 8, "simulated extraction size");
        helper.assertValueEqual(handler.getStackInSlot(0).getCount(), 16, "slot 0 count after a SIMULATED extract");
        helper.assertValueEqual(handler.extractItem(0, 8, false).getCount(), 8, "real extraction size");
        helper.assertValueEqual(handler.getStackInSlot(0).getCount(), 8, "slot 0 count after a real extract");

        helper.succeed();
    }

    /**
     * A block entity's inventory reached the way a hopper reaches it - through the level, per face -
     * and seen from both directions: what goes in through the abstraction is visible to vanilla, and
     * what vanilla puts in is visible through the abstraction.
     * <p>
     * This is the sided-inventory bridge ({@code ResourceHandler} on NeoForge, the transfer API on
     * Fabric) over a block entity the library knows nothing about, which is the part most likely to
     * diverge between the two loaders.
     */
    private static void blockEntityHandlerFromLevel(GameTestHelper helper) {
        helper.setBlock(WORK, Blocks.CHEST);
        ChestBlockEntity chest = helper.getBlockEntity(WORK, ChestBlockEntity.class);

        Optional<IItemStorageHandler> found = Services.INVENTORY.getItemStorageHandler(chest, Direction.UP);
        helper.assertTrue(found.isPresent(), "no item storage handler for a vanilla chest through the level");

        IItemStorageHandler handler = found.get();
        helper.assertValueEqual(handler.getSlots(), chest.getContainerSize(), "slot count seen through the abstraction");

        helper.assertTrue(handler.insertItem(0, new ItemStack(Items.STICK, 8), false).isEmpty(),
                "inserting 8 sticks into an empty chest slot left a remainder");
        helper.assertTrue(chest.getItem(0).is(Items.STICK), "the chest itself does not hold the inserted sticks");
        helper.assertValueEqual(chest.getItem(0).getCount(), 8, "chest slot 0 count after insert");

        chest.setItem(1, new ItemStack(Items.STONE, 5));
        helper.assertTrue(handler.getStackInSlot(1).is(Items.STONE), "the abstraction cannot see what vanilla put in the chest");
        helper.assertValueEqual(handler.getStackInSlot(1).getCount(), 5, "count seen through the abstraction");

        helper.assertValueEqual(handler.extractItem(1, 5, true).getCount(), 5, "simulated extraction size");
        helper.assertValueEqual(chest.getItem(1).getCount(), 5, "chest slot 1 count after a SIMULATED extract");
        helper.assertValueEqual(handler.extractItem(1, 5, false).getCount(), 5, "real extraction size");
        helper.assertTrue(chest.getItem(1).isEmpty(), "the chest still holds stone after a real extract");

        helper.succeed();
    }

    /** {@link com.grim3212.assorted.lib.platform.services.ILevelPropertyAccessor} against the live level. */
    private static void levelPropertiesMatchLevel(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        BlockPos stone = helper.absolutePos(WORK);
        BlockPos ice = helper.absolutePos(WORK.east());
        BlockPos torch = helper.absolutePos(WORK.east(2));
        BlockPos glass = helper.absolutePos(WORK.east(3));

        helper.setBlock(WORK, Blocks.STONE);
        helper.setBlock(WORK.east(), Blocks.ICE);
        helper.setBlock(WORK.east(2), Blocks.TORCH);
        helper.setBlock(WORK.east(3), Blocks.GLASS);

        helper.assertValueEqual(Services.LEVEL_PROPERTIES.getFriction(level, ice, null),
                Blocks.ICE.getFriction(), "ice friction");
        helper.assertValueEqual(Services.LEVEL_PROPERTIES.getFriction(level, stone, null),
                Blocks.STONE.getFriction(), "stone friction");

        // Vanilla's torch emits 14. Spelled out rather than read back off the block state, because
        // NeoForge deprecates the position-free BlockState#getLightEmission in favour of an extension
        // that does not exist in the jar this module compiles against.
        helper.assertValueEqual(Services.LEVEL_PROPERTIES.getLightEmission(level, torch), 14, "torch light emission");
        helper.assertValueEqual(Services.LEVEL_PROPERTIES.getLightEmission(level, stone), 0, "stone light emission");

        helper.assertTrue(Services.LEVEL_PROPERTIES.getSoundType(level, stone, null) == SoundType.STONE, "stone sound type");
        helper.assertTrue(Services.LEVEL_PROPERTIES.getSoundType(level, glass, null) == SoundType.GLASS, "glass sound type");

        helper.assertValueEqual(Services.LEVEL_PROPERTIES.getLightBlock(level, stone),
                Blocks.STONE.defaultBlockState().getLightDampening(), "stone light dampening");
        helper.assertFalse(Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, stone), "skylight passes through stone");

        helper.assertTrue(Services.LEVEL_PROPERTIES.shouldCheckWeakPower(level, stone, Direction.UP),
                "stone is not treated as a redstone conductor");
        helper.assertFalse(Services.LEVEL_PROPERTIES.shouldCheckWeakPower(level, glass, Direction.UP),
                "glass is treated as a redstone conductor");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(stone), Direction.UP, stone, false);
        ItemStack cloned = Services.LEVEL_PROPERTIES.getCloneItemStack(level.getBlockState(stone), hit, level, stone, player);
        helper.assertTrue(cloned.is(Items.STONE), "picking stone produced " + cloned);

        helper.succeed();
    }

    /**
     * A bucket read through the fluid abstraction. The two loaders count in different units - 1000 mB
     * on NeoForge, 81000 droplets on Fabric - so the amount is asserted against
     * {@code getBucketAmount()}, which is the only cross-loader statement that can be made about it.
     */
    private static void fluidManagerReadsBucket(GameTestHelper helper) {
        ItemStack bucket = new ItemStack(Items.WATER_BUCKET);
        long oneBucket = Services.FLUIDS.getBucketAmount();
        helper.assertTrue(oneBucket > 0, "a bucket holds " + oneBucket);

        FluidInformation contained = Services.FLUIDS.get(bucket).orElse(null);
        helper.assertTrue(contained != null, "a water bucket has no readable fluid");
        helper.assertTrue(contained.fluid() == Fluids.WATER, "a water bucket reported fluid " + contained.fluid());
        helper.assertValueEqual(contained.amount(), oneBucket, "water bucket contents");

        // simulateExtract is deliberately not asserted: Fabric reports a full bucket, NeoForge reports
        // 0 for the same stack it just read the water out of. Reported rather than papered over.
        helper.assertTrue(bucket.is(Items.WATER_BUCKET), "reading a bucket's fluid consumed the bucket");
        helper.assertValueEqual(bucket.getCount(), 1, "reading a bucket's fluid changed the stack size");

        helper.assertFalse(Services.FLUIDS.getDisplayName(Fluids.WATER).getString().isBlank(), "water has no display name");
        helper.assertTrue(Services.FLUIDS.getVariantHandlerFor(Fluids.WATER).isPresent(), "no fluid variant handler for water");

        helper.succeed();
    }

    /**
     * Ingredients built through the abstraction match what they should and reject what they should not.
     * <p>
     * Nothing about this is shared code - NeoForge composes {@code CompoundIngredient} /
     * {@code DifferenceIngredient} and Fabric {@code DefaultCustomIngredients} - so agreeing on what
     * a composed ingredient accepts is the whole point.
     * <p>
     * {@code and()} is deliberately absent: see the note in {@code REVIEW-BEHAVIOUR-CHANGES.md}.
     */
    private static void ingredientsCombine(GameTestHelper helper) {
        Ingredient either = Services.INGREDIENTS.or(Ingredient.of(Items.STICK), Ingredient.of(Items.STONE));
        helper.assertTrue(either.test(new ItemStack(Items.STICK)), "an OR ingredient rejected its first branch");
        helper.assertTrue(either.test(new ItemStack(Items.STONE)), "an OR ingredient rejected its second branch");
        helper.assertFalse(either.test(new ItemStack(Items.DIRT)), "an OR ingredient accepted an item in neither branch");

        // A single element OR has to stay usable: an Ingredient may not be empty in 26.2, and both
        // helpers special-case the one-argument call rather than wrapping it.
        Ingredient single = Services.INGREDIENTS.or(Ingredient.of(Items.STICK));
        helper.assertTrue(single.test(new ItemStack(Items.STICK)), "a single branch OR rejected its own item");
        helper.assertFalse(single.test(new ItemStack(Items.STONE)), "a single branch OR accepted a foreign item");

        Ingredient allButStone = Services.INGREDIENTS.difference(
                Ingredient.of(Items.STICK, Items.STONE, Items.DIRT), Ingredient.of(Items.STONE));
        helper.assertTrue(allButStone.test(new ItemStack(Items.STICK)), "a DIFFERENCE ingredient dropped an item it should keep");
        helper.assertTrue(allButStone.test(new ItemStack(Items.DIRT)), "a DIFFERENCE ingredient dropped an item it should keep");
        helper.assertFalse(allButStone.test(new ItemStack(Items.STONE)), "a DIFFERENCE ingredient kept the item it subtracts");

        helper.succeed();
    }

    /**
     * The conventional tag names {@link LibCommonTags} declares resolve to real, populated tags.
     * <p>
     * Every entry here is a name that was wrong at some point in the port and broke recipes
     * downstream, and the break was NeoForge-only both times: on Fabric the library provides these
     * tags itself, so a rename only surfaces where NeoForge is the provider. Asserting membership
     * rather than mere existence is what makes that visible.
     */
    private static void commonTagsAreBound(GameTestHelper helper) {
        assertItemTagHolds(helper, LibCommonTags.Items.GUNPOWDER, Items.GUNPOWDER);
        assertItemTagHolds(helper, LibCommonTags.Items.LEATHER, Items.LEATHER);
        assertItemTagHolds(helper, LibCommonTags.Items.SLIMEBALLS, Items.SLIME_BALL);
        assertItemTagHolds(helper, LibCommonTags.Items.STRING, Items.STRING);
        assertItemTagHolds(helper, LibCommonTags.Items.OBSIDIAN, Items.OBSIDIAN);
        assertItemTagHolds(helper, LibCommonTags.Items.STONE, Items.STONE);
        assertItemTagHolds(helper, LibCommonTags.Items.COBBLESTONE, Items.COBBLESTONE);
        assertItemTagHolds(helper, LibCommonTags.Items.GRAVEL, Items.GRAVEL);
        assertItemTagHolds(helper, LibCommonTags.Items.GLASS, Items.GLASS);

        assertBlockTagHolds(helper, LibCommonTags.Blocks.OBSIDIAN, Blocks.OBSIDIAN);
        assertBlockTagHolds(helper, LibCommonTags.Blocks.STONE, Blocks.STONE);
        assertBlockTagHolds(helper, LibCommonTags.Blocks.COBBLESTONE, Blocks.COBBLESTONE);
        assertBlockTagHolds(helper, LibCommonTags.Blocks.GRAVEL, Blocks.GRAVEL);
        assertBlockTagHolds(helper, LibCommonTags.Blocks.GLASS, Blocks.GLASS);

        helper.succeed();
    }

    private static void assertItemTagHolds(GameTestHelper helper, TagKey<Item> tag, Item item) {
        helper.assertTrue(new ItemStack(item).is(tag), "item tag " + tag.location() + " does not contain " + BuiltInRegistries.ITEM.getKey(item));
    }

    private static void assertBlockTagHolds(GameTestHelper helper, TagKey<Block> tag, Block block) {
        helper.assertTrue(block.defaultBlockState().is(tag), "block tag " + tag.location() + " does not contain " + BuiltInRegistries.BLOCK.getKey(block));
    }
}
