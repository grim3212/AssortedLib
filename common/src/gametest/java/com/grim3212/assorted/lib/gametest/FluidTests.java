package com.grim3212.assorted.lib.gametest;

import com.grim3212.assorted.lib.core.fluid.FluidInformation;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.gametest.LibTestSupport.*;

/**
 * The fluid manager reading, draining and filling containers.
 */
final class FluidTests {

    private FluidTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("fluid_manager_reads_bucket", FluidTests::fluidManagerReadsBucket);
        out.accept("fluid_manager_moves_fluid", FluidTests::fluidManagerMovesFluid);
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

        helper.assertTrue(bucket.is(Items.WATER_BUCKET), "reading a bucket's fluid consumed the bucket");
        helper.assertValueEqual(bucket.getCount(), 1, "reading a bucket's fluid changed the stack size");

        helper.assertFalse(Services.FLUIDS.getDisplayName(Fluids.WATER).getString().isBlank(), "water has no display name");
        helper.assertTrue(Services.FLUIDS.getVariantHandlerFor(Fluids.WATER).isPresent(), "no fluid variant handler for water");

        helper.succeed();
    }

    /**
     * Fluid moved in and out of vanilla buckets, which fill and empty by becoming another item -
     * the case a slot that cannot change the item silently gets wrong. Both directions, simulated
     * and real; the stack handed in is never touched and only one item of it is worked.
     */
    private static void fluidManagerMovesFluid(GameTestHelper helper) {
        long oneBucket = Services.FLUIDS.getBucketAmount();
        FluidInformation oneWater = new FluidInformation(Fluids.WATER, oneBucket);

        ItemStack water = new ItemStack(Items.WATER_BUCKET);
        helper.assertValueEqual(Services.FLUIDS.simulateExtract(water, oneBucket), oneBucket, "simulated extraction from a water bucket");
        helper.assertTrue(water.is(Items.WATER_BUCKET), "simulateExtract emptied the bucket");

        ItemStack drained = Services.FLUIDS.extractFrom(water, oneBucket);
        helper.assertTrue(drained.is(Items.BUCKET), "draining a water bucket gave back " + drained);
        helper.assertTrue(water.is(Items.WATER_BUCKET), "extractFrom changed the stack it was handed");

        ItemStack empty = new ItemStack(Items.BUCKET);
        helper.assertValueEqual(Services.FLUIDS.simulateExtract(empty, oneBucket), 0L, "simulated extraction from an empty bucket");
        helper.assertTrue(Services.FLUIDS.extractFrom(empty, oneBucket).is(Items.BUCKET), "draining an empty bucket did not give it back unchanged");

        helper.assertValueEqual(Services.FLUIDS.simulateInsert(empty, oneWater), oneBucket, "simulated insertion into an empty bucket");
        helper.assertTrue(empty.is(Items.BUCKET), "simulateInsert filled the bucket");

        ItemStack filled = Services.FLUIDS.insertInto(empty, oneWater);
        helper.assertTrue(filled.is(Items.WATER_BUCKET), "filling an empty bucket gave back " + filled);
        helper.assertTrue(empty.is(Items.BUCKET), "insertInto changed the stack it was handed");

        ItemStack threeEmpty = new ItemStack(Items.BUCKET, 3);
        ItemStack oneFilled = Services.FLUIDS.insertInto(threeEmpty, oneWater);
        helper.assertTrue(oneFilled.is(Items.WATER_BUCKET), "filling one of a stack of buckets gave back " + oneFilled);
        helper.assertValueEqual(oneFilled.getCount(), 1, "buckets filled out of a stack of three");
        helper.assertValueEqual(threeEmpty.getCount(), 3, "the stack of empty buckets after insertInto");

        helper.succeed();
    }
}
