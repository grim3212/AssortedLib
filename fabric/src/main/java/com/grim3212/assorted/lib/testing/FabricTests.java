package com.grim3212.assorted.lib.testing;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;
import java.util.function.Supplier;

public class FabricTests {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, LibConstants.MOD_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, LibConstants.MOD_ID);

    public static final IRegistryObject<BlockBreakCancelTest> blockBreakTest = register("test", () -> new BlockBreakCancelTest(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)));

    private static <T extends Block> IRegistryObject<T> register(String name, Supplier<? extends T> sup) {
        return register(name, sup, block -> item(block));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Supplier<? extends T> sup, Function<IRegistryObject<T>, Supplier<? extends Item>> itemCreator) {
        IRegistryObject<T> ret = registerNoItem(name, sup);
        ITEMS.register(name, itemCreator.apply(ret));
        return ret;
    }

    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
        return BLOCKS.register(name, sup);
    }

    private static Supplier<BlockItem> item(final IRegistryObject<? extends Block> block) {
        return () -> new BlockItem(block.get(), new Item.Properties());
    }

    public static void init() {
    }
}
